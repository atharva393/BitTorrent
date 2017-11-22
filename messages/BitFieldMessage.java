package messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitFieldMessage {

	final static int messageType = 5;
	final static int messageTypeLen = 1;

	public static byte[] createBitFieldMessage(byte[] payload) throws IOException {
		ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
		byte[] messageLenField = Helper.getBytesOfGivenSizeAndMessage(payload.length + messageTypeLen, 4);
		msgStream.write(messageLenField);
		msgStream.write(messageType);
		msgStream.write(payload);
		return msgStream.toByteArray();
	}
}
