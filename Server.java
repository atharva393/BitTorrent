import java.net.*;
import java.util.HashMap;

import messages.HandshakeMessage;

import java.io.*;

public class Server implements Runnable{
	private ServerSocket welcomingSocket;
	HashMap<Integer, Neighbor> connectionMap;
	int peerId;
	public Server(int peerId, int sPort, HashMap<Integer, Neighbor> connectionMap){
		try{
			this.peerId = peerId;
			this.connectionMap = connectionMap;
			welcomingSocket = new ServerSocket(sPort);
		}
		catch(Exception e){
			System.out.println("error creating server");
		}
	}

//	//send a message to the output stream
//	public void sendMessage(String msg)
//	{
//		try{
//			out.writeObject(msg);
//			out.flush();
//			System.out.println("Send message: " + msg + " to Client ");
//		}
//		catch(IOException ioException){
//			ioException.printStackTrace();
//		}
//	}

	@Override
	public void run() {
		Socket connectionSocket = null;
		System.out.println("server is running");
		byte[] message;
		try{
			while(true)
			{
				connectionSocket = welcomingSocket.accept();
				OutputStream out = connectionSocket.getOutputStream();
				//out.flush();
				InputStream in = connectionSocket.getInputStream();
								
				byte[] msg = new byte[32];	
				in.read(msg, 0, 32);
				
				int peerId = HandshakeMessage.validateHandshakeMsg(msg);
				System.out.println("Receive message: " + new String(msg, "US-ASCII") + " from client ");
				//send hand shake msg
				out.write(HandshakeMessage.createHandshakeMessage(peerId));
				Thread t = new Thread(new MessageReceiver(connectionSocket));
				t.start();

			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Error at server running");
		}
	}

}
