import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.BitSet;

import messages.BitFieldMessage;

public class MessageReceiver implements Runnable {
	private Socket socket;
	InputStream inputStream;
	OutputStream outputStream;
	FileManager fileManager;
	
	MessageReceiver(Socket connectionSocket, FileManager fileManager) {
		socket = connectionSocket;
		this.fileManager = fileManager;
	}
	
	@Override
	public void run() {
		
		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			
			byte[] bitFieldMessage;
			if(!this.fileManager.getCustomBitField().getBitSet().isEmpty()) {
				bitFieldMessage = BitFieldMessage.createBitFieldMessage(this.fileManager.getCustomBitField().getBitSet().toByteArray());
				outputStream.write(bitFieldMessage);
			}
			
			byte[] bitFieldMsgLengthArray = new byte[4];
			int bitFieldMsgLength;
			inputStream.read(bitFieldMsgLengthArray, 0, 4);
			bitFieldMsgLength = ByteBuffer.wrap(bitFieldMsgLengthArray, 0, 4).getInt();
			System.out.println(bitFieldMsgLength);
			byte[] inputStreamByte = new byte[bitFieldMsgLength];
			System.out.println(inputStream.read(inputStreamByte, 0, 1));
			inputStream.read(inputStreamByte, 0, bitFieldMsgLength);
			System.out.println("received bitfield "+ BitSet.valueOf(inputStreamByte));
	
			//experimental
			/*byte[] test = new byte[10];
			inputStream.read(test, 0, 10);
			System.out.println("received test msg " + new String(test, "US-ASCII"));
			outputStream.write(new String("sent msg to you").getBytes());*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
