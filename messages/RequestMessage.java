package messages;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RequestMessage {
	
	final int messageType = 6;
	final int messageTypeLen = 1;
	
	byte[] create_Request_Message(int pieceIndex) throws IOException{
		ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
		byte[] pieceIndexField = Helper.getBytesOfGivenSizeAndMessage(pieceIndex, 4);
		byte[] messageLenField = Helper.getBytesOfGivenSizeAndMessage(pieceIndexField.length+messageTypeLen, 4);
		msgStream.write(messageLenField);
		msgStream.write(messageType);
		msgStream.write(pieceIndexField);
		return msgStream.toByteArray();
	}
}
