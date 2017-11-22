import java.net.*;
import java.util.HashMap;

import messages.BitFieldMessage;
import messages.HandshakeMessage;

import java.io.*;

public class Server implements Runnable{
	
	private ServerSocket welcomingSocket;
	HashMap<Integer, Neighbor> connectionMap;
	FileManager fileManager;
	int peerId;
	
	public Server(int peerId, int sPort, HashMap<Integer, Neighbor> connectionMap, FileManager fileManager){
		try{
			this.peerId = peerId;
			this.connectionMap = connectionMap;
			welcomingSocket = new ServerSocket(sPort);
			this.fileManager = fileManager;
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

		try{
			while(true)
			{
				connectionSocket = welcomingSocket.accept();
				OutputStream outputStream = connectionSocket.getOutputStream();
				InputStream inputStream = connectionSocket.getInputStream();
							
				byte[] msg = new byte[32];	
				inputStream.read(msg, 0, 32);
				
				int peerId = HandshakeMessage.validateHandshakeMsg(msg);
				System.out.println("Receive message: " + new String(msg, "US-ASCII") + " from client ");
				//send hand shake msg
				outputStream.write(HandshakeMessage.createHandshakeMessage(peerId));
				Thread t = new Thread(new MessageHandler(connectionSocket, fileManager, peerId, connectionMap));
				t.start();
				byte[] bitFieldMsg;
				if(!this.fileManager.getCustomBitField().getBitSet().isEmpty()) {
					bitFieldMsg = BitFieldMessage.createBitFieldMessage(this.fileManager.getCustomBitField().getBitSet().toByteArray());
					outputStream.write(bitFieldMsg);
				}

			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Error at server running");
		}
	}

}
