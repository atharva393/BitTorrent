import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import messages.BitFieldMessage;
import messages.HandshakeMessage;

public class Peer {

	HashMap<Integer, Neighbor> connectionMap;
	PeerInfo peerInfo;
	FileManager fileManager;
	Map<Integer, PeerInfo> peerInfoMap;

	Peer(PeerInfo peerInfo, ArrayList<PeerInfo> peersToConnect, Map<Integer, PeerInfo> peerInfoMap) {
		this.peerInfo = peerInfo;
		connectionMap = new HashMap<>();
		fileManager = new FileManager(peerInfo.hasFile);
		this.peerInfoMap = peerInfoMap;
		startServer(peerInfo);
		createConnections(peersToConnect);
	}

	private void startServer(PeerInfo peerInfo) {
		Thread server = new Thread(new Server(peerInfo.id, peerInfo.port, connectionMap, fileManager, peerInfoMap));
		server.start();
	}

	void createConnections(ArrayList<PeerInfo> peersToConnectTo) {
		for (PeerInfo peerInfo : peersToConnectTo) {
			connectionMap.put(peerInfo.id, new Neighbor(peerInfo));
			sendHandshakeMsg(connectionMap.get(peerInfo.id));
		}
	}

	private void sendHandshakeMsg(Neighbor peer) {
		Socket socket = peer.getRequestSocket();
		OutputStream outputStream = null;
		try {
			outputStream = socket.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		byte[] handshakeMessage;
		byte[] bitFieldMessage;
		try {
			handshakeMessage = HandshakeMessage.createHandshakeMessage(peerInfo.id);
			outputStream.write(handshakeMessage);
			byte[] response = new byte[32];
			socket.getInputStream().read(response);

			System.out.println(new String(response));

			if (HandshakeMessage.getPeerID_Handshake_Message(response) == peer.getPeerInfo().id) {

				Thread t = new Thread(new MessageHandler(socket, fileManager, peer.getPeerInfo().id, connectionMap));
				t.start();
				if (!this.fileManager.getCustomBitField().getBitSet().isEmpty()) {
					bitFieldMessage = BitFieldMessage
							.createBitFieldMessage(this.fileManager.getCustomBitField().getBitSet().toByteArray());
					outputStream.write(bitFieldMessage);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error while sending hand shake msg to" + peer.getPeerInfo().id);
		}
	}
}
