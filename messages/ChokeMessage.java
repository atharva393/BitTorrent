package messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ChokeMessage {

	final static int messageType = 0;
	final static int messageTypeLen = 1;

	public static byte[] createChokeMessage() throws IOException {
		ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
		byte[] messageLenField = Helper.getBytesOfGivenSizeAndMessage(messageTypeLen, 4);
		msgStream.write(messageLenField);
		msgStream.write(messageType);
		return msgStream.toByteArray();
	}
}
