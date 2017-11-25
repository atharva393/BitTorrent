import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

	private CustomBitField customBitField;
	private BitSet requestBitset = new BitSet();
	private Map<Integer, Integer> requestPieceMap = new HashMap<>();
	private int fileSize;
	private int pieceSize;
	private String fileName;
	private String directoryName;
	private File file;
	
	FileManager(PeerInfo peerInfo){
		CommonConfig commonConfig = CommonConfig.getCommonProperties();
		customBitField = new CustomBitField(commonConfig.getFileSize()/commonConfig.getPieceSize());
		if(peerInfo.hasFile) {
			this.customBitField.setAll();
		}
		this.fileName = commonConfig.getFileName();
		this.fileSize = commonConfig.getFileSize();
		this.pieceSize = commonConfig.getPieceSize();
		this.directoryName = "peer_" + peerInfo.id + "/";
		File parentDirectory = new File(directoryName);
		
		if(!parentDirectory.exists())
			parentDirectory.mkdirs();
		
		file = new File(directoryName + fileName);
	}

	public CustomBitField getCustomBitField() {
		return customBitField;
	}

	public BitSet getRequestBitset() {
		return requestBitset;
	}

	public void setRequestBitset(BitSet requestBitset) {
		this.requestBitset = requestBitset;
	}

	public Map<Integer, Integer> getRequestPieceMap() {
		return requestPieceMap;
	}

	public void setRequestPieceMap(Map<Integer, Integer> requestPieceMap) {
		this.requestPieceMap = requestPieceMap;
	}
	
	public void writePieceToFile(int index, byte[] pieceInfo){
		
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			randomAccessFile.seek(index * pieceSize);
			randomAccessFile.write(new String(pieceInfo, StandardCharsets.UTF_8).getBytes());
			randomAccessFile.close();
			getCustomBitField().set(index);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] readPieceFromFile(int index) throws FileNotFoundException, IOException{
		FileInputStream fileInputStream = new FileInputStream(file);
		fileInputStream.skip(index * pieceSize);
		byte[] piece = new byte[Math.min(fileSize - index * pieceSize, pieceSize)];
		fileInputStream.read(piece);
		//System.out.println("Read from file:" + new String(piece));
		fileInputStream.close();
		return piece;

	}
}
