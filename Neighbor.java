import java.io.IOException;
import java.net.Socket;

public class Neighbor {
	private PeerInfo peerInfo;
	private Socket requestSocket;
	private CustomBitField neighborBitField;
	private boolean isChokingMe;
	private boolean isChokedbyMe;
	private boolean isInterested;
	private boolean amInterested;

	private long unchokedAt;
	private double downloadRate;
	private int numberOfReceivedPieces;
	
	public PeerInfo getPeerInfo() {
		return peerInfo;
	}

	public void setPeerInfo(PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
	}

	public Neighbor(PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
		try {
			this.requestSocket = new Socket(peerInfo.getName(), peerInfo.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.amInterested = false;
		this.isChokedbyMe = false;
		this.isChokingMe = true;
		this.isInterested = false;
		this.neighborBitField = new CustomBitField();
	}

	public Neighbor(PeerInfo peerInfo, Socket connectionSocket) {
		this.peerInfo = peerInfo;
		this.requestSocket = connectionSocket;
		this.amInterested = false;
		this.isChokedbyMe = true;
		this.isChokingMe = true;
		this.isInterested = false;
		this.neighborBitField = new CustomBitField();
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

	public int getNumberOfReceivedPieces() {
		return numberOfReceivedPieces;
	}

	public void setNumberOfReceivedPieces(int numberOfReceivedPieces) {
		this.numberOfReceivedPieces = numberOfReceivedPieces;
	}

	public void incrementNumberOfReceivedPieces(){
		this.numberOfReceivedPieces += 1;	
	}
	public long getUnchokedAt() {
		return unchokedAt;
	}

	public void setUnchokedAt(long unchokedAt) {
		this.unchokedAt = unchokedAt;
	}

	public double getDownloadRate() {
		return downloadRate;
	}

	public void setDownloadRate(double downloadRate) {
		this.downloadRate = downloadRate;
	}
}
