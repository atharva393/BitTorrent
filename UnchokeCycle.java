import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;

import messages.ChokeMessage;
import messages.UnchokeMessage;

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
		this.cycleStopped = true;
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
			DataOutputStream outputStream;
			while (!isCycleStopped()) {
				if ((System.currentTimeMillis() - previousUnchokeTime) >= unchokingTimeInterval * 1000
						&& peer.getInterestedNeighbors().size() > 0) {
					
					List<Neighbor> newUnchokedNeighbors = selectNewUnchokedNeighbors();
					Vector<Integer> currentlyUnchokedNeighbors = peer.getCurrentlyUnchokedNeighborIds();
					
					List<Integer> toChokeList = new ArrayList<>(currentlyUnchokedNeighbors);
					
					List<Integer> sendUnchokeMessageTo = new ArrayList<>();
					
					boolean isChanged = false;
					
					for (Neighbor n : newUnchokedNeighbors) {
						
						if(!currentlyUnchokedNeighbors.contains(n.getPeerInfo().getId()))
							isChanged = true;
						if (toChokeList.contains(n.getPeerInfo().getId())) {
							toChokeList.remove(new Integer(n.getPeerInfo().getId()));
						} else {							
							sendUnchokeMessageTo.add(n.getPeerInfo().getId());
							
							currentlyUnchokedNeighbors.add(n.getPeerInfo().getId());
						}
					}

					currentlyUnchokedNeighbors.removeAll(toChokeList);
					Neighbor oldOptimNeighbor = peer.getOptimisticallyUnchokedNeighbor();
					for (int i : toChokeList) {
						// send choked msg
						if(oldOptimNeighbor != null && oldOptimNeighbor.getPeerInfo().getId() == i) {
							continue;
						}
						try {
							if(!peer.getConnectionMap().get(i).getRequestSocket().isClosed()){
									//&& (oldOptimNeighbor != null && oldOptimNeighbor.getPeerInfo().getId() != i)) {
								outputStream = new DataOutputStream(peer.getConnectionMap().get(i).getRequestSocket().getOutputStream());
								outputStream.write(ChokeMessage.createChokeMessage());
								
								peer.getConnectionMap().get(i).setChokedbyMe(true);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
					
					if(isChanged) {
						peer.getLogger().changeInPreferredNeighbors(peer.getPeerInfo().getId(), currentlyUnchokedNeighbors.toString());
					}
					
					for(int i : sendUnchokeMessageTo) {
						try {
							if(!peer.getConnectionMap().get(i).getRequestSocket().isClosed() && peer.getConnectionMap().get(i).isChokedbyMe()){
								outputStream = new DataOutputStream(peer.getConnectionMap().get(i).getRequestSocket()
									.getOutputStream());
								outputStream.write(UnchokeMessage.createUnchokeMessage());
								
								peer.getConnectionMap().get(i).setChokedbyMe(false);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					previousUnchokeTime = System.currentTimeMillis();

				}

			}
		}

		public List<Neighbor> selectNewUnchokedNeighbors() {
			if (!peer.getPeerInfo().isHasFile())
				return selectUnchokedNeighborsBasedOnDownloadingRate();
			return selectNeighborRandomly();
		}

		public synchronized List<Neighbor> selectNeighborRandomly() {
			List<Neighbor> interestedNeighborsList = new ArrayList<>(peer.getInterestedNeighbors());
			Collections.shuffle(interestedNeighborsList);
			return interestedNeighborsList.subList(0, Math.min(interestedNeighborsList.size(),
					CommonConfig.getCommonProperties().getNumberOfPreferredNeighbors()));
		}

		private List<Neighbor> selectUnchokedNeighborsBasedOnDownloadingRate() {
			PriorityQueue<Neighbor> newUnchokedNeighbors = new PriorityQueue<Neighbor>(new Comparator<Neighbor>() {
				public int compare(Neighbor n1, Neighbor n2) {
					return (int) (n1.getDownloadRate() - n2.getDownloadRate());
				}
			});

			for (Neighbor n : peer.getInterestedNeighbors()) {
					newUnchokedNeighbors.add(n);
			}

			int removeNeighbors = newUnchokedNeighbors.size()
					- CommonConfig.getCommonProperties().getNumberOfPreferredNeighbors();

			while (removeNeighbors-- > 0) {
				newUnchokedNeighbors.remove();
			}
			return new ArrayList<>(newUnchokedNeighbors);
		}
	}

	class OptimisticUnchokingCycle implements Runnable {
		public void run() {
			
			DataOutputStream outputstream = null;

			while (!isCycleStopped()) {
				if ((System.currentTimeMillis() - previousOptimisticUnchokeTime) >= optimisticUnchokingTimeInterval * 1000) {

					Neighbor neighborToUnchoke = getChokedNeighborRandomly(new Vector<>(peer.getInterestedNeighbors()));
					
					if (neighborToUnchoke != null) {
						Neighbor temp = peer.getOptimisticallyUnchokedNeighbor();
						if(temp != null){
							try {
								if(!peer.getConnectionMap().get(temp.getPeerInfo().getId()).getRequestSocket().isClosed()
										&& !peer.getCurrentlyUnchokedNeighborIds().contains(temp.getPeerInfo().getId())){
									outputstream = new DataOutputStream(peer.getConnectionMap().get(temp.getPeerInfo().getId()).getRequestSocket().getOutputStream());
									outputstream.write(ChokeMessage.createChokeMessage());
									
									peer.getConnectionMap().get(temp.getPeerInfo().getId()).setChokedbyMe(true);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						peer.setOptimisticallyUnchokedNeighbor(neighborToUnchoke);
						System.out.println("Optimistically unchoked - " + neighborToUnchoke.getPeerInfo().getId());
						try {
							if(!peer.getConnectionMap().get(neighborToUnchoke.getPeerInfo().getId()).getRequestSocket().isClosed()) {
								outputstream = new DataOutputStream(peer.getConnectionMap().get(neighborToUnchoke.getPeerInfo().getId()).getRequestSocket().getOutputStream());
							
								peer.getLogger().optimUnchokedNeighbor(peer.getPeerInfo().getId(), neighborToUnchoke.getPeerInfo().getId());
							
								outputstream.write(UnchokeMessage.createUnchokeMessage());
								
								peer.getConnectionMap().get(neighborToUnchoke.getPeerInfo().getId()).setChokedbyMe(false);
								//peer.getCurrentlyUnchokedNeighborIds().add(neighborToUnchoke.getPeerInfo().getId());
							}

						} catch (IOException e) {
							e.printStackTrace();
						}

						previousOptimisticUnchokeTime = System.currentTimeMillis();

					}
					
				}
			}
		}

		//synchronized really needed?
		private Neighbor getChokedNeighborRandomly(Vector<Neighbor> interestedNeighbors) {
			List<Neighbor> interestedChokedNeighbors = new ArrayList<>();
			//can not remove integers from the list of neighbors
			//interestedChokedNeighbors.removeAll(peer.getCurrentlyUnchokedNeighborIds());
			
			Iterator<Map.Entry<Integer, Neighbor>> it = peer.getConnectionMap().entrySet().iterator();
			
			
				while(it.hasNext()){
					try{
						Map.Entry<Integer, Neighbor> neighbor = it.next();
						if(neighbor.getValue().isInterested() && neighbor.getValue().isChokedbyMe()) {
							interestedChokedNeighbors.add(neighbor.getValue());
						}
					} catch(Exception e){
						
					}
					
				}
			
			
			//throws concurrent modification exception
			/*for(Map.Entry<Integer, Neighbor> neighbor : peer.getConnectionMap().entrySet()) {
				
				if(neighbor.getValue().isInterested() && neighbor.getValue().isChokedbyMe()) {
					interestedChokedNeighbors.add(neighbor.getValue());
				}
				
			}*/

			if(interestedChokedNeighbors.size()==0)
				return null;
			
			return interestedChokedNeighbors.get(new Random().nextInt(interestedChokedNeighbors.size()));
		}
	}
}
