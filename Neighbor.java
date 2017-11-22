import messages.BitFieldMessage;

public class Neighbor {
	private PeerInfo peerInfo;
	public PeerInfo getPeerInfo() {
		return peerInfo;
	}
	public void setPeerInfo(PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
	}
	private int noPrevPiecesRcvd;
	private BitFieldMessage bitField;	
	//private PeerConn connection;
	private SocketDetails socketDetails;
	private boolean isChokingMe;
	private boolean isChokedbyMe;
	private boolean isInterested;
	private boolean amInterested;
	
	public Neighbor(PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
		this.setSocketDetails(new SocketDetails(this.peerInfo));
	}
	public SocketDetails getSocketDetails() {
		return socketDetails;
	}
	public void setSocketDetails(SocketDetails socketDetails) {
		this.socketDetails = socketDetails;
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
}
