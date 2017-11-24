import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import messages.InterestedMessage;
import messages.NotInterestedMessage;
import messages.RequestMessage;

public class MessageHandler implements Runnable {
	private Socket socket;
	InputStream inputStream;
	OutputStream outputStream;
	FileManager fileManager;
	int neighborPeerId;
	Map<Integer, Neighbor> connectionMap;
	private List<Neighbor> interestedNeighbors;
	
	MessageHandler(Socket connectionSocket, FileManager fileManager, int peerId, Map<Integer, Neighbor> connectionMap, List<Neighbor> interestedNeighbors) {
		socket = connectionSocket;
		this.fileManager = fileManager;
		this.neighborPeerId = peerId;
		this.connectionMap = connectionMap;
		this.interestedNeighbors = interestedNeighbors;
	}

	@Override
	public void run() {

		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();

			while (true) {
				int msgLength;
				int messageType;

				byte[] bitFieldMsgLengthArray = new byte[4];
				inputStream.read(bitFieldMsgLengthArray, 0, 4);
				msgLength = ByteBuffer.wrap(bitFieldMsgLengthArray, 0, 4).getInt();

				byte[] msgType = new byte[1];
				inputStream.read(msgType, 0, 1);
				messageType = ByteBuffer.wrap(msgType, 0, 1).get();

				byte[] inputStreamByte = null;

				if (msgLength > 1) {
					inputStreamByte = new byte[msgLength];
					inputStream.read(inputStreamByte, 0, msgLength);
				}

				switch (messageType) {
				case 0:
					System.out.println("choking msg received from " + neighborPeerId);
					handleChokeMsg(inputStreamByte);
					break;
				case 1:
					System.out.println("unchoked msg received from " + neighborPeerId);
					handleUnChokeMsg();
					break;
				case 2:
					System.out.println("received interested msg from " + neighborPeerId);
					handleInterestedMsg();
					break;
				case 3:
					System.out.println("received not interested msg from " + neighborPeerId);
					break;
				case 4:
					handleHaveMsg(ByteBuffer.wrap(inputStreamByte, 0, 4).getInt());
					break;
				case 5:
					System.out.println("calling bitfield msg handler method");
					handleBitFieldMsg(BitSet.valueOf(inputStreamByte));
					break;
				case 6:
					System.out.println("Request msg received by " + neighborPeerId);
					break;
				case 7:
					System.out.println("Piece received from " + neighborPeerId);
					handlePieceMsg(inputStreamByte);
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleInterestedMsg() {
		connectionMap.get(neighborPeerId).setInterested(true);
		interestedNeighbors.add(connectionMap.get(neighborPeerId));
	}

	private void handlePieceMsg(byte[] inputStreamByte) {
		//request map se delete krna hai
		
	}

	private void handleHaveMsg(int index) {
		connectionMap.get(neighborPeerId).getNeighborBitField().getBitSet().set(index);
		
		if(!fileManager.getCustomBitField().getBitSet().get(index)){
			byte[] interestedMessage;
			try {
				interestedMessage = InterestedMessage.createInterestedMessage();
				System.out.println("sending interestd msg");
				outputStream.write(interestedMessage);
				if(!connectionMap.get(neighborPeerId).isChokingMe())
					sendPieceRequest(index);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleUnChokeMsg() {
		connectionMap.get(neighborPeerId).setChokingMe(false);
		
		if(connectionMap.get(neighborPeerId).isAmInterested()) {
			sendPieceRequest();
		}
	}

	private void sendPieceRequest() {
		if(connectionMap.get(neighborPeerId).isChokingMe())
			return;
		BitSet missingPieces = hasMissingPieces(connectionMap.get(neighborPeerId).getNeighborBitField().getBitSet());
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
		int index = missingPieces.nextSetBit(random.nextInt(missingPieces.size()));
		if(!missingPieces.isEmpty()) {
			while(!fileManager.getRequestBitset().get(index)) {
				index = missingPieces.nextSetBit(random.nextInt(missingPieces.size()));
			}
		}
		return index;
	}

	private void handleBitFieldMsg(BitSet neighborBitSet) {
		BitSet interested = hasMissingPieces(neighborBitSet);
		System.out.println("I am interested " + interested);
		connectionMap.get(neighborPeerId).getNeighborBitField().setBitSet(neighborBitSet);
		
		try {
			if (!interested.isEmpty()) {
				connectionMap.get(neighborPeerId).setAmInterested(true);
				byte[] interestedMessage = InterestedMessage.createInterestedMessage();
				System.out.println("sending interestd msg");
				outputStream.write(interestedMessage);
			} else {
				connectionMap.get(neighborPeerId).setAmInterested(false);
				byte[] notInterestedMessage = NotInterestedMessage.createNotInterestedMessage();
				System.out.println("sending not interestd msg");
				outputStream.write(notInterestedMessage);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleChokeMsg(byte[] inputStreamByte) {
		//request map se delete krna hai
	}

	private BitSet hasMissingPieces(BitSet otherBitSet) {

		BitSet XORbits = (BitSet) fileManager.getCustomBitField().getBitSet().clone();
		XORbits.xor(otherBitSet);

		BitSet andBits = (BitSet) (XORbits.clone());
		andBits.and(fileManager.getCustomBitField().getBitSet());

		andBits.xor(XORbits);

		return andBits;

	}
	
	
}
