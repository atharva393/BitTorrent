import java.io.IOException;
import java.net.Socket;

import messages.BitFieldMessage;

public class Neighbor {
	private PeerInfo peerInfo;
	private int noPrevPiecesRcvd;
	private BitFieldMessage bitField;	
	//private PeerConn connection;
	//private SocketDetails socketDetails;
	private Socket requestSocket;

	private boolean isChokingMe;
	private boolean isChokedbyMe;
	private boolean isInterested;
	private boolean amInterested;
	

	public PeerInfo getPeerInfo() {
		return peerInfo;
	}
	
	public void setPeerInfo(PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
	}
	
	public Neighbor(PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
		//this.setSocketDetails(new SocketDetails(this.peerInfo));
		try {
			this.requestSocket = new Socket(peerInfo.name, peerInfo.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.amInterested = false;
		this.isChokedbyMe = false;
		this.isChokingMe = true;
		this.isInterested = false;
	}
	
	public Neighbor(PeerInfo peerInfo, Socket connectionSocket) {
		this.peerInfo = peerInfo;
		//this.setSocketDetails(new SocketDetails(this.peerInfo));
		this.requestSocket = connectionSocket;
		this.amInterested = false;
		this.isChokedbyMe = false;
		this.isChokingMe = true;
		this.isInterested = false;
	}
	
/*	public SocketDetails getSocketDetails() {
		return socketDetails;
	}
	public void setSocketDetails(SocketDetails socketDetails) {
		this.socketDetails = socketDetails;
	}*/
	
	public boolean isChokingMe() {
		return isChokingMe;
	}
	public void setChokingMe(boolean isChokingMe) {
		this.isChokingMe = isChokingMe;
	}
	public boolean isChokedbyMe() {
		return isChokedbyMe;
	}
	public void setChokedbyMe(boolean isChokedbyMe) {
		this.isChokedbyMe = isChokedbyMe;
	}
	public boolean isInterested() {
		return isInterested;
	}
	public void setInterested(boolean isInterested) {
		this.isInterested = isInterested;
	}
	public boolean isAmInterested() {
		return amInterested;
	}
	public void setAmInterested(boolean amInterested) {
		this.amInterested = amInterested;
	}
	public BitFieldMessage getBitField() {
		return bitField;
	}
	public void setBitField(BitFieldMessage bitField) {
		this.bitField = bitField;
	}
	public int getNoPrevPiecesRcvd() {
		return noPrevPiecesRcvd;
	}
	public void setNoPrevPiecesRcvd(int noPrevPiecesRcvd) {
		this.noPrevPiecesRcvd = noPrevPiecesRcvd;
	}
	public Socket getRequestSocket() {
		return requestSocket;
	}
	public void setRequestSocket(Socket requestSocket) {
		this.requestSocket = requestSocket;
	}
}
