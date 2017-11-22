import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketDetails {
	OutputStream out;        //stream write to the socket
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
