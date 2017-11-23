package messages;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class HandshakeMessage {
	
	final static String header = "P2PFILESHARINGPROJ";
	
	public static byte[] createHandshakeMessage(int peerId) throws IOException{
		ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
		msgStream.write(header.getBytes());
		msgStream.write(new byte[10]);
		byte[] peerIdField = Helper.getBytesOfGivenSizeAndMessage(peerId, 4); 
		msgStream.write(peerIdField);
		return msgStream.toByteArray();
	}
	
	public static int getPeerID_Handshake_Message(byte[] message){
	    return ByteBuffer.wrap(message, 28, 4).getInt();
	}
	
	public static int validateHandshakeMsg(byte [] msg) {
		System.out.println(new String(msg, 0, 18));
		if (header.equals(new String(msg, 0, 18))) {
			System.out.println(getPeerID_Handshake_Message(msg));
			return getPeerID_Handshake_Message(msg);
		}
		return -1;
	}
}
