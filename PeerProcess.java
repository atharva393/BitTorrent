import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class PeerProcess {

	Peer peer;
	boolean hasFile;

	public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub
		int thisPeerId = Integer.parseInt(args[0]);
		PeerProcess p = new PeerProcess();
		p.initializeMyPeer(thisPeerId, "PeerInfo.cfg");
		// start connecting

	}

	private void initializeMyPeer(int thisPeerId, String peerInfoFile) {
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
					peer = new Peer(peerInfo, peersToConnect);
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
