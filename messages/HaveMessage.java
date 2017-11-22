package messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HaveMessage {
	
	final int messageType = 4;
	final int messageTypeLen = 1;
	
	byte[] create_Have_Message(int pieceIndex) throws IOException{
		ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
		byte[] pieceIndexField = Helper.getBytesOfGivenSizeAndMessage(pieceIndex, 4);
		byte[] messageLenField = Helper.getBytesOfGivenSizeAndMessage(pieceIndexField.length+messageTypeLen, 4);
		msgStream.write(messageLenField);
		msgStream.write(messageType);
		msgStream.write(pieceIndexField);
		return msgStream.toByteArray();
	}
}
