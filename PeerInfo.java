/**
 * PeerInfo class is used to store name and port of peer. 
 * It also has infomation about whether peer has file or not.  
 * 
 * @author Aman, Aneesh & Atharva
 *
 */
public class PeerInfo {
	
	int id;
	String name;
	int port;
	boolean hasFile;
	
	public PeerInfo() {
		
	}
	
	public PeerInfo(int id, String name, int port, boolean hasFile){
		this.id = id;
		this.name = name;
		this.port = port;
		this.hasFile = hasFile;
	}
	
}
