import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PeerProcess {

	Peer peer;
	boolean hasFile;

	public static void main(String[] args) throws Exception {

		int thisPeerId = Integer.parseInt(args[0]);
		PeerProcess p = new PeerProcess();
		Map<Integer, PeerInfo> peerInfoMap = p.populatePeerInfoMap("PeerInfo.cfg");
		p.initializeMyPeer(thisPeerId, "PeerInfo.cfg", peerInfoMap);

	}

	private Map<Integer, PeerInfo> populatePeerInfoMap(String peerInfoFile) {
		Map<Integer, PeerInfo> peerInfoMap = new HashMap<>();
		Scanner sc;
		try {
			sc = new Scanner(new File(peerInfoFile));
			
			while (sc.hasNextLine()) {
				String[] splittedLine = sc.nextLine().split(" ");
				PeerInfo peerInfo = new PeerInfo(Integer.parseInt(splittedLine[0]), splittedLine[1],
						Integer.parseInt(splittedLine[2]), splittedLine[3].equals("1"));
				peerInfoMap.put(peerInfo.id, peerInfo);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return peerInfoMap;
	}

	private void initializeMyPeer(int thisPeerId, String peerInfoFile, Map<Integer, PeerInfo> peerInfoMap) {
		ArrayList<PeerInfo> peersToConnect = new ArrayList<>();
		try {
			Scanner sc = new Scanner(new File(peerInfoFile));
			while (sc.hasNextLine()) {
				String[] splittedLine = sc.nextLine().split(" ");
				PeerInfo peerInfo = new PeerInfo(Integer.parseInt(splittedLine[0]), splittedLine[1],
						Integer.parseInt(splittedLine[2]), splittedLine[3].equals("1"));
				if (peerInfo.id != thisPeerId) {
					peersToConnect.add(peerInfo);
				} else {
					peer = new Peer(peerInfo, peersToConnect,peerInfoMap);
					break;
				}
			}

			sc.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error when reading from peer file");
		}
	}

}
