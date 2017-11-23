import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketDetails {
	OutputStream out;
 	InputStream in;
 	Socket requestSocket;
 	
	public SocketDetails(PeerInfo peerInfo){
		try {
			requestSocket = new Socket(peerInfo.name, peerInfo.port);
			System.out.println("Connected to " + peerInfo.name + " on port " + peerInfo.port);
			out = requestSocket.getOutputStream();
			out.flush();
			in = requestSocket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
