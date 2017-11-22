package messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PieceMessage {

	final int messageType = 7;
	final int messageTypeLen = 1;

	byte[] create_Piece_Message(int pieceIndex, byte[] content) throws IOException {
		ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
		byte[] pieceIndexField = Helper.getBytesOfGivenSizeAndMessage(pieceIndex, 4);
		int messageTotalLen = pieceIndexField.length + content.length + messageTypeLen;
		byte[] messageLenField = Helper.getBytesOfGivenSizeAndMessage(messageTotalLen, 4);
		msgStream.write(messageLenField);
		msgStream.write(messageType);
		msgStream.write(pieceIndexField);
		msgStream.write(content);
		return msgStream.toByteArray();
	}
}
