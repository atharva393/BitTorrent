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
	
	public CustomLogger() {
		
	}
	
	public void createLogFile(String peerId){
		
		File directory = new File("peer_" + peerId);
		
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("Directory " + directory + " is created!");
            } else {
                System.out.println("Failed to create directory!" + directory);
            }
        }
        
        File logFile = new File("peer_" + peerId + "/log_peer_" + peerId + ".log");
        
        if(!logFile.exists()) {
        	
        	try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error while creating log file.");
				e.printStackTrace();
			}
        } 
        	
        FileWriter fw;
		try {
			fw = new FileWriter(logFile.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Log File for Peer "+ peerId +".");
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void changeInPreferredNeighbors(String peerId, String neighbors){
		String fileName = "peer_" + peerId+"/log_peer_"+peerId+".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fw = new FileWriter(fileName,true);
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
	
	public void optimUnchokedNeighbor(String peerId, String optimUnchokedNeighbor) {
		String fileName = "peer_" + peerId+"/log_peer_"+peerId+".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fileWriter = new FileWriter(fileName,true);
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
	
	public void unchoke(String peerId, String unchokedNeighbor) {
		String fileName = "peer_" + peerId +"/log_peer_"+ peerId + ".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fileWriter = new FileWriter(fileName,true);
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
	
	public void choke(String peerId, String chokedNeighbor) {
		String fileName = "peer_" + peerId +"/log_peer_"+ peerId + ".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fileWriter = new FileWriter(fileName,true);
             BufferedWriter bw = new BufferedWriter(fileWriter);
             String str = getFormattedDateTime()+": Peer " + chokedNeighbor + " is choked by " + peerId + ".";
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void receivedHave(String peerId, String senderPeer, int pieceIndex) {
		String fileName = "peer_" + peerId +"/log_peer_"+ peerId + ".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fw = new FileWriter(fileName,true);
             BufferedWriter bw = new BufferedWriter(fw);
             String str = getFormattedDateTime()+": Peer " + peerId + " received the \'have\' message from " 
            		 + senderPeer + "for the piece " + pieceIndex + ".";
             
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void receivedInterested(String peerId, String senderPeer) {
		String fileName = "peer_" + peerId +"/log_peer_"+ peerId + ".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fw = new FileWriter(fileName,true);
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
	
	public void receivedNotInterested(String peerId, String senderPeer) {
		String fileName = "peer_" + peerId +"/log_peer_"+ peerId + ".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fw = new FileWriter(fileName,true);
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
	
	public void downloadedPiece(String peerId, int pieceIndex, String senderPeer, int currentPieceNumber) {
		String fileName = "peer_" + peerId +"/log_peer_"+ peerId + ".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fw = new FileWriter(fileName,true);
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
	
	
	public void downloadeComplete(String peerId) {
		String fileName = "peer_" + peerId +"/log_peer_"+ peerId + ".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fw = new FileWriter(fileName,true);
             BufferedWriter bw = new BufferedWriter(fw);
             String str = getFormattedDateTime()+": Peer " + " has downloaded the complete file.";
             
             bw.write(str);
             bw.newLine();
             bw.close();
          }
          catch(IOException e)
          {
              e.printStackTrace();
          }
	}
	
	public void incomingTcpConn(String peerId, String requestingPeer) {
		String fileName = "peer_" + peerId +"/log_peer_"+ peerId + ".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fw = new FileWriter(fileName,true);
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
	
	public void outgoingTcpConn(String peerId, String requestedPeer) {
		String fileName = "peer_" + peerId +"/log_peer_"+ peerId + ".log";
		
		if (!new File(fileName).exists()) {
			createLogFile(peerId);
        }
		
		 try
         {
             FileWriter fw = new FileWriter(fileName,true);
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
