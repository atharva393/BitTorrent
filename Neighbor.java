import java.io.IOException;
import java.net.Socket;

public class Neighbor {
	private PeerInfo peerInfo;
	private int noPrevPiecesRcvd;
	private Socket requestSocket;
	private CustomBitField neighborBitField;
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
		try {
			this.requestSocket = new Socket(peerInfo.name, peerInfo.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.amInterested = false;
		this.isChokedbyMe = false;
		this.isChokingMe = true;
		this.isInterested = false;
	}

	public Neighbor(PeerInfo peerInfo, Socket connectionSocket) {
		this.peerInfo = peerInfo;
		this.requestSocket = connectionSocket;
		this.amInterested = false;
		this.isChokedbyMe = false;
		this.isChokingMe = true;
		this.isInterested = false;
	}

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

	public CustomBitField getNeighborBitField() {
		return neighborBitField;
	}

	public void setNeighborBitField(CustomBitField neighborBitField) {
		this.neighborBitField = neighborBitField;
	}
}
