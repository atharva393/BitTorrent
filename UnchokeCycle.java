import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

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
			while (!isCycleStopped()) {
				if ((System.currentTimeMillis() - previousUnchokeTime) >= unchokingTimeInterval * 1000
						&& peer.getInterestedNeighbors().size() > 0) {
					List<Neighbor> newUnchokedNeighbors = selectNewUnchokedNeighbors();
					List<Integer> currentlyUnchokedNeighbors = peer.getCurrentlyUnchokedNeighborIds();
					
					List<Integer> toChokeList = new ArrayList<>(currentlyUnchokedNeighbors);
					
					List<Integer> sendUnchokeMessageTo = new ArrayList<>();
					
					boolean isChanged = false;
					
					for (Neighbor n : newUnchokedNeighbors) {
						
						if(!currentlyUnchokedNeighbors.contains(n.getPeerInfo().getId()))
							isChanged = true;
						if (toChokeList.contains(n.getPeerInfo().getId())) {
							toChokeList.remove(new Integer(n.getPeerInfo().getId()));
						} else {
							// send unchoked msg
							/*try {
								outputStream = peer.getConnectionMap().get(n.getPeerInfo().id).getRequestSocket()
										.getOutputStream();
								outputStream.write(UnchokeMessage.createUnchokeMessage());
							} catch (IOException e) {
								e.printStackTrace();
							}*/
							
							sendUnchokeMessageTo.add(n.getPeerInfo().getId());
							
							currentlyUnchokedNeighbors.add(n.getPeerInfo().getId());
						}
					}

					currentlyUnchokedNeighbors.removeAll(toChokeList);
					
					for (int i : toChokeList) {
						// send choked msg
						try {
							outputStream = peer.getConnectionMap().get(i).getRequestSocket().getOutputStream();
							outputStream.write(ChokeMessage.createChokeMessage());
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
					
					if(isChanged) {
						peer.getLogger().changeInPreferredNeighbors(peer.getPeerInfo().getId(), currentlyUnchokedNeighbors.toString());
					}
					
					for(int i : sendUnchokeMessageTo) {
						try {
							outputStream = peer.getConnectionMap().get(i).getRequestSocket()
									.getOutputStream();
							outputStream.write(UnchokeMessage.createUnchokeMessage());
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

		public List<Neighbor> selectNeighborRandomly() {
			List<Neighbor> interestedNeighborsList = peer.getInterestedNeighbors();
			Collections.shuffle(interestedNeighborsList);
			return interestedNeighborsList.subList(0, Math.min(interestedNeighborsList.size(),
					CommonConfig.getCommonProperties().getNumberOfPreferredNeighbors()));
		}

		private List<Neighbor> selectUnchokedNeighborsBasedOnDownloadingRate() {
			List<Neighbor> interestedNeighborsList = peer.getInterestedNeighbors();

			PriorityQueue<Neighbor> newUnchokedNeighbors = new PriorityQueue<Neighbor>(new Comparator<Neighbor>() {
				public int compare(Neighbor n1, Neighbor n2) {
					return (int) (n1.getDownloadRate() - n2.getDownloadRate());
				}
			});

			for (Neighbor n : interestedNeighborsList) {
				newUnchokedNeighbors.add(n);
			}

			int removeNeighbors = newUnchokedNeighbors.size()
					- CommonConfig.getCommonProperties().getNumberOfPreferredNeighbors();

			while (removeNeighbors > 0) {
				newUnchokedNeighbors.remove();
			}
			return new ArrayList<>(newUnchokedNeighbors);
		}
	}

	class OptimisticUnchokingCycle implements Runnable {
		public void run() {

			OutputStream outputstream = null;

			while (!isCycleStopped()) {
				if ((System.currentTimeMillis() - previousOptimisticUnchokeTime) >= optimisticUnchokingTimeInterval * 1000) {

					Neighbor neighborToUnchoke = getChokedNeighborRandomly(peer.getInterestedNeighbors());

					if (neighborToUnchoke != null) {

						try {
							outputstream = peer.getConnectionMap().get(neighborToUnchoke.getPeerInfo().getId())
									.getRequestSocket().getOutputStream();
							
							peer.getLogger().optimUnchokedNeighbor(peer.getPeerInfo().getId(), neighborToUnchoke.getPeerInfo().getId());
							
							outputstream.write(UnchokeMessage.createUnchokeMessage());

						} catch (IOException e) {
							e.printStackTrace();
						}

						previousOptimisticUnchokeTime = System.currentTimeMillis();

					}
					
				}
			}
		}

		private Neighbor getChokedNeighborRandomly(List<Neighbor> interestedNeighbors) {

			List<Neighbor> interestedChokedNeighbors = new ArrayList<>(interestedNeighbors);

			for (Neighbor neighbor : interestedNeighbors) {
				if (!neighbor.isChokedbyMe()) {
					interestedChokedNeighbors.remove(neighbor);
				}
			}
			
			if(interestedChokedNeighbors.size()==0)
				return null;
			
			return interestedChokedNeighbors.get(new Random().nextInt(interestedChokedNeighbors.size()));
		}
	}
}
