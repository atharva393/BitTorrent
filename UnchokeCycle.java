import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import messages.ChokeMessage;

public class UnchokeCycle {
	private int unchokingTimeInterval;
	private long previousUnchokeTime;
	private int optimisticUnchokingTimeInterval;
	private long previousOptimisticUnchokeTime;
	private boolean cycleStopped;
	private Peer peer;
	
	public UnchokeCycle(Peer peer) {
		this.unchokingTimeInterval = CommonConfig.getCommonProperties().getUnchokingInterval();
		this.optimisticUnchokingTimeInterval = CommonConfig.getCommonProperties().getOptimisticUnchokingInterval();
		this.peer = peer;
	}
	
	public void beginCycle() {
		this.previousUnchokeTime = System.currentTimeMillis();
		this.previousOptimisticUnchokeTime = previousUnchokeTime;
		Thread unchokingThread = new Thread(new UnchokingCycle());
		Thread OptimisticUnchokingThread = new Thread(new OptimisticUnchokingCycle());
		unchokingThread.start();
		OptimisticUnchokingThread.start();
		
	}
	
	public boolean isCycleStopped() {
		return cycleStopped;
	}

	public void setCycleStopped(boolean cycleStopped) {
		this.cycleStopped = cycleStopped;
	}

	class UnchokingCycle implements Runnable {
		
		public void run() {
			OutputStream outputStream;
			while(!isCycleStopped()) {
				if((System.currentTimeMillis() - previousUnchokeTime) >= unchokingTimeInterval * 1000) {
					List<Neighbor> newUnchokedNeighbors = selectNewUnchokedNeighbors();
					List<Integer> currentlyUnchokedNeighbors = peer.getCurrentlyUnchokedNeighborIds();
					List<Integer> toChokeList = new ArrayList<>(currentlyUnchokedNeighbors);
					
					for(Neighbor n : newUnchokedNeighbors) {
						if(toChokeList.contains(n.getPeerInfo().id)){
							toChokeList.remove(new Integer(n.getPeerInfo().id));
						} else{
							//send unchoked msg
							try {
								outputStream = peer.getConnectionMap().get(n.getPeerInfo().id).getRequestSocket().getOutputStream();
								outputStream.write(ChokeMessage.createChokeMessage());
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							currentlyUnchokedNeighbors.add(n.getPeerInfo().id);
						}
					}
					newUnchokedNeighbors.removeAll(toChokeList);
					for(int i: toChokeList) {
						//send choked msg
						try {
							outputStream = peer.getConnectionMap().get(i).getRequestSocket().getOutputStream();
							outputStream.write(ChokeMessage.createChokeMessage());
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					
					previousUnchokeTime = System.currentTimeMillis();
					
				}
				
			}
		}
		
		public List<Neighbor> selectNewUnchokedNeighbors(){
			if(!peer.peerInfo.hasFile) return selectUnchokedNeighborsBasedOnDownloadingRate();
			return selcteNeighborRandomly();
		}
		
		public List<Neighbor> selcteNeighborRandomly(){
			List<Neighbor> interestedNeighborsList = peer.getInterestedNeighbors();
			Collections.shuffle(interestedNeighborsList);
			return interestedNeighborsList.subList(0, CommonConfig.getCommonProperties().getNumberOfPreferredNeighbors());
		}

		private List<Neighbor> selectUnchokedNeighborsBasedOnDownloadingRate() {
			List<Neighbor> interestedNeighborsList = peer.getInterestedNeighbors();
			
			PriorityQueue<Neighbor> newUnchokedNeighbors = new PriorityQueue<Neighbor>(new Comparator<Neighbor>() {
				public int compare(Neighbor n1, Neighbor n2) {
					return (int) (n1.getDownloadRate() - n2.getDownloadRate());
				}
			});
			
			for(Neighbor n: interestedNeighborsList){
				newUnchokedNeighbors.add(n);
			}
			
			int removeNeighbors = newUnchokedNeighbors.size() - CommonConfig.getCommonProperties().getNumberOfPreferredNeighbors();
			
			while(removeNeighbors>0){
				newUnchokedNeighbors.remove();
			}
			return new ArrayList<>(newUnchokedNeighbors);
		}
	}
	
	class OptimisticUnchokingCycle implements Runnable{
		public void run() {
			while(!isCycleStopped()) {
				if((System.currentTimeMillis() - previousOptimisticUnchokeTime) >= optimisticUnchokingTimeInterval * 1000) {
					
					previousOptimisticUnchokeTime = System.currentTimeMillis();
				}
			}
		}
	}
}
