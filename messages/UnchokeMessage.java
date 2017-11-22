package messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UnchokeMessage {

	final int messageType = 1;
	final int messageTypeLen = 1;

	byte[] create_Unchoke_Message() throws IOException {
		ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
		byte[] messageLenField = Helper.getBytesOfGivenSizeAndMessage(messageTypeLen, 4);
		msgStream.write(messageLenField);
		msgStream.write(messageType);
		return msgStream.toByteArray();
	}
}
