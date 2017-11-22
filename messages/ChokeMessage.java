package messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ChokeMessage {

	final int messageType = 0;
	final int messageTypeLen = 1;

	byte[] create_Handshake_Message() throws IOException {
		ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
		byte[] messageLenField = Helper.getBytesOfGivenSizeAndMessage(messageTypeLen, 4);
		msgStream.write(messageLenField);
		msgStream.write(messageType);
		return msgStream.toByteArray();
	}
}
