/**
 * PeerInfo class is used to store name and port of peer. 
 * It also has infomation about whether peer has file or not.  
 * 
 * @author Aman, Aneesh & Atharva
 *
 */
public class PeerInfo {
	
	private int id;
	private String name;
	private int port;
	private boolean hasFile;
	
	public PeerInfo(int id, String name, int port, boolean hasFile){
		this.id = id;
		this.name = name;
		this.port = port;
		this.hasFile = hasFile;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isHasFile() {
		return hasFile;
	}

	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}
	
}
