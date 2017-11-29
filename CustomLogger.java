import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is custom logger class to write logs into file.
 * 
 * @author Aman, Aneesh & Atharva
 *
 */

public class CustomLogger {
	
	private File logFile;
	
	public CustomLogger(int peerId) {
		
		this.logFile = new File("log_peer_" + peerId + ".log");
		
	}
	
	public void changeInPreferredNeighbors(int peerId, String neighbors){
		
		 try
         {
             FileWriter fw = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fw);
             String log = getFormattedDateTime()+": Peer " + peerId + " has the Preferred neighbours " + neighbors+".";
             bw.write(log);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}

	private String getFormattedDateTime() {
		// TODO Auto-generated method stub
		
		Date today = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
		String date = format.format(today);
		return date;
		
	}
	
	public void optimUnchokedNeighbor(int peerId, int optimUnchokedNeighbor) {
		
		 try
         {
             FileWriter fileWriter = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fileWriter);
             String log = getFormattedDateTime()+": Peer " + peerId + " has the optimistically unchoked neighbor " + optimUnchokedNeighbor + ".";
             bw.write(log);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void unchoke(int unchokedNeighbor,int peerId ) {
		
		 try
         {
             FileWriter fileWriter = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fileWriter);
             String log = getFormattedDateTime()+": Peer " + unchokedNeighbor + " is unchoked by " + peerId + ".";
             bw.write(log);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void choke(int peerId, int chokedNeighbor) {
		
		 try
         {
             FileWriter fileWriter = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fileWriter);
             String str = getFormattedDateTime()+": Peer " + peerId + " is choked by " + chokedNeighbor + ".";
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void receivedHave(int peerId, int senderPeer, int pieceIndex) {
		
		 try
         {
             FileWriter fw = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fw);
             String str = getFormattedDateTime()+": Peer " + peerId + " received the \'have\' message from " 
            		 + senderPeer + " for the piece " + pieceIndex + ".";
             
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void receivedInterested(int peerId, int senderPeer) {
		
		 try
         {
             FileWriter fw = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fw);
             String str = getFormattedDateTime()+": Peer " + peerId + " received the \'interested\' message from "+ senderPeer +  ".";
             
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void receivedNotInterested(int peerId, int senderPeer) {
		
		 try
         {
             FileWriter fw = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fw);
             String str = getFormattedDateTime()+": Peer " + peerId + " received the \'not interested\' message from "+ senderPeer +  ".";
             
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void downloadedPiece(int peerId, int pieceIndex, int senderPeer, int currentPieceNumber) {
		
		 try
         {
             FileWriter fw = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fw);
             String str = getFormattedDateTime()+": Peer " + peerId + " has downloaded the piece " + pieceIndex + " from " + senderPeer +  "."
            		 + "Now the number of pieces it has is " + currentPieceNumber + ".";
             
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	
	public void downloadeComplete(int peerId) {
		
		 try
         {
             FileWriter fw = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fw);
             String str = getFormattedDateTime()+": Peer "+peerId+" has downloaded the complete file.";
             
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void incomingTcpConn(int peerId, int requestingPeer) {
		
		 try
         {
             FileWriter fw = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fw);
             String str = getFormattedDateTime()+": Peer " + peerId + " is connected from Peer " + requestingPeer + ".";
             
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void outgoingTcpConn(int peerId, int requestedPeer) {
		
		 try
         {
             FileWriter fw = new FileWriter(logFile,true);
             BufferedWriter bw = new BufferedWriter(fw);
             String str = getFormattedDateTime()+": Peer " + peerId + " makes a connection to Peer " + requestedPeer + ".";
             
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
}
