import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.BitSet;

public class MessageReceiver implements Runnable {
	private Socket socket;
	MessageReceiver(Socket connectionSocket) {
		socket = connectionSocket;
	}
	
	@Override
	public void run() {
		
		try {
			InputStream inputStream = socket.getInputStream();
			
			byte[] inputStreamByte = new byte[32];
			inputStream.read(inputStreamByte,0,32);
			
			System.out.println("received bitfield "+ BitSet.valueOf(inputStreamByte));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
