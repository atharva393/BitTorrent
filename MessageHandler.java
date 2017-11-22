import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.HashMap;

import messages.InterestedMessage;
import messages.NotInterestedMessage;

public class MessageHandler implements Runnable {
	private Socket socket;
	InputStream inputStream;
	OutputStream outputStream;
	FileManager fileManager;
	int neighborPeerId;
	HashMap<Integer,Neighbor> connectionMap;
	
	MessageHandler(Socket connectionSocket, FileManager fileManager, int peerId,  HashMap<Integer,Neighbor> connectionMap) {
		socket = connectionSocket;
		this.fileManager = fileManager;
		this.neighborPeerId = peerId;
		this.connectionMap = connectionMap;
	}
	
	@Override
	public void run() {
		
		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			
			while(true) {
				byte[] bitFieldMsgLengthArray = new byte[4];
				int msgLength;
				int messageType;
				
				inputStream.read(bitFieldMsgLengthArray, 0, 4);
				msgLength = ByteBuffer.wrap(bitFieldMsgLengthArray, 0, 4).getInt();
				byte[] inputStreamByte = new byte[msgLength];
				messageType = inputStream.read(inputStreamByte, 0, 1);
				inputStream.read(inputStreamByte, 0, msgLength);
				
				switch(messageType) {
				case 0:
					handleChokeMsg(inputStreamByte);
					break;
				case 1:
					break;
				case 2:
					System.out.println("received interested msg from " + neighborPeerId);
					break;
				case 3:
					System.out.println("received not interested msg from " + neighborPeerId);
					break;
				case 4:
					break;
				case 5:
					System.out.println("calling bitfield msg handler method");
					handleBitFieldMsg(BitSet.valueOf(inputStreamByte));
					break;
				}
			}
			
			
			
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleBitFieldMsg(BitSet neighborBitSet) {
		boolean interested = hasMissingPieces(neighborBitSet);
		System.out.println("I am interested " + interested);
		try {
			if(interested){
				connectionMap.get(neighborPeerId).equals(true);
				byte[] interestedMessage = InterestedMessage.createInterestedMessage();
				System.out.println("sending interestd msg");
				outputStream.write(interestedMessage);
			} else {
				connectionMap.get(neighborPeerId).equals(false);
				byte[] notInterestedMessage = NotInterestedMessage.createNotInterestedMessage();
				System.out.println("sending not interestd msg");
				outputStream.write(notInterestedMessage);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void handleChokeMsg(byte[] inputStreamByte) {
		
		
	}
	
	private boolean hasMissingPieces(BitSet otherBitSet) {
			
			BitSet XORbits = (BitSet) fileManager.getCustomBitField().getBitSet().clone();
			XORbits.xor(otherBitSet);
			
			BitSet andBits = (BitSet) (XORbits.clone());
			andBits.and(fileManager.getCustomBitField().getBitSet());
			
			
			andBits.xor(XORbits);
			
			return !andBits.isEmpty();
					
			
	}
}
