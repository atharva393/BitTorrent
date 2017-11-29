import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import messages.BitFieldMessage;
import messages.HandshakeMessage;

public class Peer {

	private HashMap<Integer, Neighbor> connectionMap;
	private PeerInfo peerInfo;
	private FileManager fileManager;
	private Map<Integer, PeerInfo> peerInfoMap;
	private UnchokeCycle unchokeCycle;
	private Vector<Neighbor> interestedNeighbors;
	private Vector<Integer> currentlyUnchokedNeighborIds;
	private CustomLogger logger;
	private Neighbor optimisticallyUnchokedNeighbor;
	
	Peer(PeerInfo peerInfo, ArrayList<PeerInfo> peersToConnect, Map<Integer, PeerInfo> peerInfoMap) {
		this.peerInfo = peerInfo;
		this.connectionMap = new HashMap<>();
		this.fileManager = new FileManager(peerInfo);
		this.peerInfoMap = peerInfoMap;
		this.interestedNeighbors = new Vector<Neighbor>();
		this.unchokeCycle = new UnchokeCycle(this);
		this.currentlyUnchokedNeighborIds = new Vector<Integer>();
		this.logger = new CustomLogger(this.peerInfo.getId());
		startServer(peerInfo);
		createConnections(peersToConnect);
	}

	private void startServer(PeerInfo peerInfo) {
		Thread server = new Thread(new Server(this));
		server.start();
		//start unchokecycle
		unchokeCycle.beginCycle();
	}

	public PeerInfo getPeerInfo() {
		return peerInfo;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public Map<Integer, PeerInfo> getPeerInfoMap() {
		return peerInfoMap;
	}

	public UnchokeCycle getUnchokeCycle() {
		return unchokeCycle;
	}

	void createConnections(ArrayList<PeerInfo> peersToConnectTo) {
		for (PeerInfo peerInfo : peersToConnectTo) {
			connectionMap.put(peerInfo.getId(), new Neighbor(peerInfo));
			sendHandshakeMsg(connectionMap.get(peerInfo.getId()));
		}
	}
	
	public HashMap<Integer, Neighbor> getConnectionMap() {
		return connectionMap;
	}

	public void setConnectionMap(HashMap<Integer, Neighbor> connectionMap) {
		this.connectionMap = connectionMap;
	}
	
	private void sendHandshakeMsg(Neighbor neighborPeer) {
		Socket socket = neighborPeer.getRequestSocket();
		OutputStream outputStream = null;
		try {
			outputStream = socket.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		byte[] handshakeMessage;
		byte[] bitFieldMessage;
		try {
			handshakeMessage = HandshakeMessage.createHandshakeMessage(peerInfo.getId());
			outputStream.write(handshakeMessage);
			byte[] response = new byte[32];
			socket.getInputStream().read(response);

			System.out.println(new String(response));

			if (HandshakeMessage.getPeerID_Handshake_Message(response) == neighborPeer.getPeerInfo().getId()) {
				
				logger.outgoingTcpConn(peerInfo.getId(), neighborPeer.getPeerInfo().getId());
				
				Thread t = new Thread(new MessageHandler(socket, this, neighborPeer.getPeerInfo().getId()));
				t.start();
				if (!this.fileManager.getCustomBitField().getBitSet().isEmpty()) {
					bitFieldMessage = BitFieldMessage
							.createBitFieldMessage(this.fileManager.getCustomBitField().getBitSet().toByteArray());
					outputStream.write(bitFieldMessage);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error while sending hand shake msg to" + neighborPeer.getPeerInfo().getId());
		}
	}

	public Vector<Neighbor> getInterestedNeighbors() {
		return interestedNeighbors;
	}

	public void setInterestedNeighbors(Vector<Neighbor> interestedNeighbors) {
		this.interestedNeighbors = interestedNeighbors;
	}

	public Vector<Integer> getCurrentlyUnchokedNeighborIds() {
		return currentlyUnchokedNeighborIds;
	}

	public void setCurrentlyUnchokedNeighborIds(Vector<Integer> currentlyUnchokedNeighborIds) {
		this.currentlyUnchokedNeighborIds = currentlyUnchokedNeighborIds;
	}

	public CustomLogger getLogger() {
		return logger;
	}

	public Neighbor getOptimisticallyUnchokedNeighbor() {
		return optimisticallyUnchokedNeighbor;
	}

	public void setOptimisticallyUnchokedNeighbor(Neighbor optimisticallyUnchokedNeighbor) {
		this.optimisticallyUnchokedNeighbor = optimisticallyUnchokedNeighbor;
	}
}
