package messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RequestMessage {

	final static int messageType = 6;
	final static int messageTypeLen = 1;

	public static byte[] createRequestMessage(int pieceIndex) throws IOException {
		ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
		byte[] pieceIndexField = Helper.getBytesOfGivenSizeAndMessage(pieceIndex, 4);
		byte[] messageLenField = Helper.getBytesOfGivenSizeAndMessage(pieceIndexField.length + messageTypeLen, 4);
		msgStream.write(messageLenField);
		msgStream.write(messageType);
		msgStream.write(pieceIndexField);
		return msgStream.toByteArray();
	}
}
