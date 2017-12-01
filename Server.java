import java.net.*;
import java.util.Map;

import messages.BitFieldMessage;
import messages.HandshakeMessage;

import java.io.*;

public class Server implements Runnable {

	private Peer peer;
	private ServerSocket welcomingSocket;
	private Map<Integer, Neighbor> connectionMap;
	private FileManager fileManager;
	private int myPeerId;
	private Map<Integer, PeerInfo> peerInfoMap;
	private UnchokeCycle unchokeCycle;
	private CustomLogger logger;
	
	public Server(Peer peer) {
		try {
			this.peer = peer;
			this.myPeerId = peer.getPeerInfo().getId();
			this.connectionMap = peer.getConnectionMap();
			this.welcomingSocket = new ServerSocket(peer.getPeerInfo().getPort());
			this.fileManager = peer.getFileManager();
			this.peerInfoMap = peer.getPeerInfoMap();
			this.unchokeCycle = peer.getUnchokeCycle();
			this.logger = peer.getLogger();
		} catch (Exception e) {
			System.out.println("error creating server");
		}
	}

	@Override
	public void run() {
		Socket connectionSocket = null;
		System.out.println("server is running");

		try {
			while (true) {
				connectionSocket = welcomingSocket.accept();
				DataOutputStream outputStream = new DataOutputStream(connectionSocket.getOutputStream());
				DataInputStream inputStream = new DataInputStream(connectionSocket.getInputStream());

				byte[] msg = new byte[32];
				inputStream.read(msg, 0, 32);

				int neighborPeerId = HandshakeMessage.validateHandshakeMsg(msg);
				connectionMap.put(neighborPeerId, new Neighbor(peerInfoMap.get(neighborPeerId), connectionSocket));
				
				logger.incomingTcpConn(myPeerId, neighborPeerId);
				
				outputStream.write(HandshakeMessage.createHandshakeMessage(myPeerId));
				
				if(unchokeCycle.isCycleStopped()){
					unchokeCycle.setCycleStopped(false);
					unchokeCycle.beginCycle();
				}
				
				Thread t = new Thread(new MessageHandler(connectionSocket, peer, neighborPeerId));
				t.start();
				byte[] bitFieldMsg;
				
				if (!this.fileManager.getCustomBitField().getBitSet().isEmpty()) {
					bitFieldMsg = BitFieldMessage
							.createBitFieldMessage(this.fileManager.getCustomBitField().getBitSet().toByteArray());
					outputStream.write(bitFieldMsg);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error at server running");
		}
	}

}
