import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import messages.BitFieldMessage;
import messages.HandshakeMessage;

public class Peer {

	HashMap<Integer, Neighbor> connectionMap;
	PeerInfo peerInfo;
	FileManager fileManager;
	
	Peer(PeerInfo peerInfo, ArrayList<PeerInfo> peersToConnect){
		this.peerInfo = peerInfo;
		connectionMap = new HashMap<>();
		fileManager = new FileManager(peerInfo.hasFile);
		startServer(peerInfo);	
		createConnections(peersToConnect);
	}

	private void startServer(PeerInfo peerInfo) {
		Thread server = new Thread(new Server(peerInfo.id, peerInfo.port, connectionMap, fileManager));
		server.start();
	}
	
	void createConnections(ArrayList<PeerInfo> peersToConnectTo)
	{
		for(PeerInfo peerInfo: peersToConnectTo) {
			connectionMap.put(peerInfo.id, new Neighbor(peerInfo));
			sendHandshakeMsg(connectionMap.get(peerInfo.id));
		}
	}
	
	private void sendHandshakeMsg(Neighbor peer) {
		SocketDetails sd = peer.getSocketDetails(); 
		OutputStream outputStream = sd.out;
		byte[] handshakeMessage;
		byte[] bitFieldMessage;
		try {
			handshakeMessage = HandshakeMessage.createHandshakeMessage(peer.getPeerInfo().id);
			outputStream.write(handshakeMessage);
			byte[] response = new byte[32];
			sd.in.read(response);
			System.out.println(new String(response));
			if(HandshakeMessage.getPeerID_Handshake_Message(response) == peer.getPeerInfo().id){

				Thread t = new Thread(new MessageHandler(sd.requestSocket, fileManager, peer.getPeerInfo().id, connectionMap));
				t.start();
				if(!this.fileManager.getCustomBitField().getBitSet().isEmpty()) {
					bitFieldMessage = BitFieldMessage.createBitFieldMessage(this.fileManager.getCustomBitField().getBitSet().toByteArray());
					outputStream.write(bitFieldMessage);
				}
			}
			
			System.out.println("hand shake msg sent" + peer.getPeerInfo().id);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error while sending hand shake msg to" + peer.getPeerInfo().id);
		}	
	}
}
