import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import messages.HaveMessage;
import messages.InterestedMessage;
import messages.NotInterestedMessage;
import messages.PieceMessage;
import messages.RequestMessage;

public class MessageHandler implements Runnable {
	
	private Peer peer;
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private FileManager fileManager;
	private int neighborPeerId;
	private Map<Integer, Neighbor> connectionMap;
	private Vector<Neighbor> interestedNeighbors;
	private UnchokeCycle unchokeCycle;
	private Map<Integer, PeerInfo> peerInfoMap;
	
	MessageHandler(Socket connectionSocket, Peer peer, int neighborPeerId) {
		this.peer = peer;
		this.socket = connectionSocket;
		this.fileManager = peer.getFileManager();
		this.neighborPeerId = neighborPeerId;
		this.connectionMap = peer.getConnectionMap();
		this.interestedNeighbors = peer.getInterestedNeighbors();
		this.unchokeCycle = peer.getUnchokeCycle();
		this.peerInfoMap = peer.getPeerInfoMap();
	}

	@Override
	public void run() {

		try {
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());

			while (true) {
				int msgLength;
				int messageType;

				byte[] bitFieldMsgLengthArray = new byte[4];
				inputStream.readFully(bitFieldMsgLengthArray, 0, 4);
				msgLength = ByteBuffer.wrap(bitFieldMsgLengthArray, 0, 4).getInt();

				byte[] msgType = new byte[1];
				inputStream.readFully(msgType, 0, 1);
				messageType = ByteBuffer.wrap(msgType, 0, 1).get();

				byte[] inputStreamByte = null;

				if (msgLength > 1) {
					inputStreamByte = new byte[msgLength-1];
					inputStream.readFully(inputStreamByte, 0, msgLength-1);
				}

				switch (messageType) {
				case 0:
					if(msgLength == 1){
						handleChokeMsg();
					} else{
						System.out.println("reached here");
						socket.close();
						if(connectionMap.size() == peerInfoMap.size()-1)
							System.exit(0);
					}
						
					break;
				case 1:
					handleUnChokeMsg();
					break;
				case 2:
					handleInterestedMsg();
					break;
				case 3:
					handleNotInterstedMsg();
					break;
				case 4:
					handleHaveMsg(ByteBuffer.wrap(inputStreamByte, 0, 4).getInt());
					break;
				case 5:
					handleBitFieldMsg(BitSet.valueOf(inputStreamByte));
					break;
				case 6:
					//System.out.println("Request msg received by " + neighborPeerId);
					handleRequestPieceMsg(ByteBuffer.wrap(inputStreamByte, 0, 4).getInt());
					break;
				case 7:
					handlePieceMsg(inputStreamByte);
					break;
				}
			}

		} catch (Exception e) {
			return;
		}
	}

	private void handleRequestPieceMsg(int index) {
		if(!fileManager.getCustomBitField().get(index))
			return;
		//System.out.println("Request msg received by " + neighborPeerId + " for " + index);
		try {
			byte[] piece = fileManager.readPieceFromFile(index);
			outputStream.write(PieceMessage.createPieceMessage(index, piece));
			
		} catch(FileNotFoundException e){
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Something went wrong while retrieving the piece.");
		}
	}

	private void handleNotInterstedMsg() {
		connectionMap.get(neighborPeerId).setInterested(false);
		
		peer.getLogger().receivedNotInterested(peer.getPeerInfo().getId(), neighborPeerId);
		
		if(interestedNeighbors.contains(connectionMap.get(neighborPeerId))){
			interestedNeighbors.remove(connectionMap.get(neighborPeerId));
		}
	}

	private void handleInterestedMsg() {
		connectionMap.get(neighborPeerId).setInterested(true);
		
		peer.getLogger().receivedInterested(peer.getPeerInfo().getId(), neighborPeerId);
		
		if(!interestedNeighbors.contains(connectionMap.get(neighborPeerId)))
			interestedNeighbors.add(connectionMap.get(neighborPeerId));
	}

	private void handlePieceMsg(byte[] pieceInfo) {
		//delete entry from requestmap
		int index = ByteBuffer.wrap(pieceInfo, 0, 4).getInt();
		
		fileManager.getRequestPieceMap().remove(neighborPeerId);
		
		connectionMap.get(neighborPeerId).incrementNumberOfReceivedPieces();
		fileManager.writePieceToFile(index, Arrays.copyOfRange(pieceInfo, 4, pieceInfo.length));
		
		peer.getLogger().downloadedPiece(peer.getPeerInfo().getId(), index, neighborPeerId, fileManager.getCustomBitField().getTotalPiecesReceived());
		
		sendHaveMsgToAll(index);
				
		for(Map.Entry<Integer, Neighbor> neighbor : connectionMap.entrySet()) {
			if(neighbor.getValue().isAmInterested() && 
					hasMissingPieces(neighbor.getValue().getNeighborBitField().getBitSet()).isEmpty()) {
				neighbor.getValue().setAmInterested(false);
				try {
					new DataOutputStream(neighbor.getValue().getRequestSocket().getOutputStream()).write(NotInterestedMessage.createNotInterestedMessage());
					
					neighbor.getValue().setAmInterested(false);
				} catch (IOException e) {
					System.out.println("Error occurred while sending not interested msg from handlePiecemsg method");
					e.printStackTrace();
				}
			}
		}

		if(fileManager.getCustomBitField().hasAll()){
			peer.getPeerInfo().setHasFile(true);
			peer.getLogger().downloadeComplete(peer.getPeerInfo().getId());
			System.out.println("last have "+index);
			checkIfEveryoneHasFile();			
		} else{
			sendPieceRequest();
		}
	}

	private void sendHaveMsgToAll(int index) {

		for(Map.Entry<Integer, Neighbor> entry : connectionMap.entrySet()){
			try {
				System.out.println("piece "+index+". have sent to "+entry.getKey());
				new DataOutputStream(entry.getValue().getRequestSocket().getOutputStream()).write(HaveMessage.createHaveMessage(index));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void handleHaveMsg(int index) {
		
		connectionMap.get(neighborPeerId).getNeighborBitField().set(index);
		
		peer.getLogger().receivedHave(peer.getPeerInfo().getId(), neighborPeerId, index);
		
		if(!fileManager.getCustomBitField().get(index)){
			byte[] interestedMessage;
			try {
				interestedMessage = InterestedMessage.createInterestedMessage();
				outputStream.write(interestedMessage);
				
				//this line was missing. Mandatory because we check this flag b4 requesting piece after getting unchoked.
				connectionMap.get(neighborPeerId).setAmInterested(true);
				if(!connectionMap.get(neighborPeerId).isChokingMe())
					sendPieceRequest(index);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(connectionMap.get(neighborPeerId).getNeighborBitField().hasAll()){
			//set interested flag to false because neighbor anyways has the complete file. 
			/*connectionMap.get(neighborPeerId).setInterested(false);
			if(interestedNeighbors.contains(connectionMap.get(neighborPeerId)))
				interestedNeighbors.remove(connectionMap.get(neighborPeerId));
			*/checkIfEveryoneHasFile();
		}
	}

	private void handleUnChokeMsg() {
		connectionMap.get(neighborPeerId).setChokingMe(false);
		
		peer.getLogger().unchoke(peer.getPeerInfo().getId(), neighborPeerId);
		
		connectionMap.get(neighborPeerId).setUnchokedAt(System.currentTimeMillis());
		
		if(connectionMap.get(neighborPeerId).isAmInterested()) {
			sendPieceRequest();
		}
	}

	private void sendPieceRequest() {
		if(connectionMap.get(neighborPeerId).isChokingMe())
			return;
		BitSet missingPieces = hasMissingPieces(connectionMap.get(neighborPeerId).getNeighborBitField().getBitSet());
		if(missingPieces.isEmpty())
			return;
		int index = getRandomPieceIndex(missingPieces);
		sendPieceRequest(index);
	}

	private void sendPieceRequest(int index){
		try {
			if((!fileManager.getRequestPieceMap().containsKey(neighborPeerId)) && (!fileManager.getRequestBitset().get(index))) {
				fileManager.getRequestBitset().set(index);
				fileManager.getRequestPieceMap().put(neighborPeerId, index);
				byte[] requestMsg = RequestMessage.createRequestMessage(index);
				outputStream.write(requestMsg);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private int getRandomPieceIndex(BitSet missingPieces) {
		Random random = new Random();
		
		int index = -1;
		
		if(!missingPieces.isEmpty()) {
			while(index == -1 || fileManager.getRequestBitset().get(index)) {
				index = missingPieces.nextSetBit(random.nextInt(missingPieces.size()));
			}
		}
		return index;
	}

	private void handleBitFieldMsg(BitSet neighborBitSet) {
		BitSet interested = hasMissingPieces(neighborBitSet);
		connectionMap.get(neighborPeerId).getNeighborBitField().setBitSet(neighborBitSet);
		
		try {
			if (!interested.isEmpty()) {
				connectionMap.get(neighborPeerId).setAmInterested(true);
				byte[] interestedMessage = InterestedMessage.createInterestedMessage();
				outputStream.write(interestedMessage);
				
			} else {
				connectionMap.get(neighborPeerId).setAmInterested(false);
				byte[] notInterestedMessage = NotInterestedMessage.createNotInterestedMessage();
				outputStream.write(notInterestedMessage);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleChokeMsg() {
		
		connectionMap.get(neighborPeerId).setChokingMe(true);
		
		peer.getLogger().choke(peer.getPeerInfo().getId(), neighborPeerId);
		
		Neighbor neighbor = connectionMap.get(neighborPeerId);
		neighbor.setDownloadRate(neighbor.getNumberOfReceivedPieces() * 1.0 / (System.currentTimeMillis() - neighbor.getUnchokedAt()));
		neighbor.setNumberOfReceivedPieces(0);
		
		if(fileManager.getRequestPieceMap().containsKey(neighborPeerId)) {
			int indexToClear = fileManager.getRequestPieceMap().get(neighborPeerId);
			fileManager.getRequestBitset().clear(indexToClear);
			fileManager.getRequestPieceMap().remove(neighborPeerId);
		}
		
	}

	private BitSet hasMissingPieces(BitSet otherBitSet) {

		BitSet XORbits = (BitSet) fileManager.getCustomBitField().getBitSet().clone();
		XORbits.xor(otherBitSet);

		BitSet andBits = (BitSet) (XORbits.clone());
		andBits.and(fileManager.getCustomBitField().getBitSet());

		andBits.xor(XORbits);

		return andBits;

	}
	
	public void checkIfEveryoneHasFile(){
		if(fileManager.getCustomBitField().hasAll()){
			for(Map.Entry<Integer, Neighbor> entry : connectionMap.entrySet()){
				if(!entry.getValue().getNeighborBitField().hasAll()){
					System.out.println("bacha hai "+entry.getKey());
					return;
				}
			}

			System.out.println("Stopping the unchoking cycles.");
			unchokeCycle.setCycleStopped(true);
						
			for(Map.Entry<Integer, Neighbor> entry : connectionMap.entrySet()){
				try {
					if(!entry.getValue().getRequestSocket().isClosed())
					{
						System.out.println("Closing the socket for " + entry.getKey());
						entry.getValue().getRequestSocket().close();
						interestedNeighbors.remove(entry.getValue());
						peer.getCurrentlyUnchokedNeighborIds().remove(entry.getKey());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(connectionMap.size() == peerInfoMap.size()-1)
				System.exit(0);
		}
	}
}
