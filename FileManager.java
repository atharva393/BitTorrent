import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

	private CustomBitField customBitField;
	private BitSet requestBitset;
	private Map<Integer, Integer> requestPieceMap;

	public CustomBitField getCustomBitField() {
		return customBitField;
	}

	FileManager(boolean hasFile){
		
		CommonConfig commonConfig = CommonConfig.getCommonProperties();
		this.setRequestPieceMap(new HashMap<>());
		customBitField = new CustomBitField(commonConfig.getFileSize()/commonConfig.getPieceSize());
		
		if(hasFile) {
			this.customBitField.setAll();
		}
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

}
