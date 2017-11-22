package messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NotInterestedMessage {

	final static int messageType = 3;
	final static int messageTypeLen = 1;

	public static byte[] createNotInterestedMessage() throws IOException {
		ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
		byte[] messageLenField = Helper.getBytesOfGivenSizeAndMessage(messageTypeLen, 4);
		msgStream.write(messageLenField);
		msgStream.write(messageType);
		return msgStream.toByteArray();
	}
}
