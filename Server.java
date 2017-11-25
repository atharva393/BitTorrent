import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import messages.BitFieldMessage;
import messages.HandshakeMessage;

import java.io.*;

public class Server implements Runnable {

	private ServerSocket welcomingSocket;
	Map<Integer, Neighbor> connectionMap;
	FileManager fileManager;
	int myPeerId;
	Map<Integer, PeerInfo> peerInfoMap;
	private List<Neighbor> interestedNeighbors;
	private UnchokeCycle unchokeCycle;
	
	public Server(int peerId, int sPort, HashMap<Integer, Neighbor> connectionMap, FileManager fileManager,
			Map<Integer, PeerInfo> peerInfoMap, List<Neighbor> interestedNeighbors, UnchokeCycle unchokeCycle) {
		try {
			this.myPeerId = peerId;
			this.connectionMap = connectionMap;
			this.welcomingSocket = new ServerSocket(sPort);
			this.fileManager = fileManager;
			this.peerInfoMap = peerInfoMap;
			this.interestedNeighbors = interestedNeighbors;
			this.unchokeCycle = unchokeCycle;
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
				OutputStream outputStream = connectionSocket.getOutputStream();
				InputStream inputStream = connectionSocket.getInputStream();

				byte[] msg = new byte[32];
				inputStream.read(msg, 0, 32);

				int neighborPeerId = HandshakeMessage.validateHandshakeMsg(msg);
				connectionMap.put(neighborPeerId, new Neighbor(peerInfoMap.get(neighborPeerId), connectionSocket));
				System.out.println("Receive message: " + new String(msg, "US-ASCII") + " from client ");

				outputStream.write(HandshakeMessage.createHandshakeMessage(myPeerId));
				
				if(unchokeCycle.isCycleStopped()){
					unchokeCycle.setCycleStopped(false);
					unchokeCycle.beginCycle();
				}
				
				Thread t = new Thread(new MessageHandler(connectionSocket, fileManager, neighborPeerId, connectionMap, interestedNeighbors, unchokeCycle));
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
