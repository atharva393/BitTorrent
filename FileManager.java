import java.util.BitSet;

public class FileManager {

	private BitSet bitSet;

	public BitSet getBitSet() {
		return bitSet;
	}

	public void setBitSet(BitSet bitSet) {
		this.bitSet = bitSet;
	}
	
	FileManager(boolean hasFile){
		
		CommonConfig commonConfig = CommonConfig.getCommonProperties();
		
		bitSet = new BitSet(commonConfig.getFileSize()/commonConfig.getPieceSize());
		
		if(hasFile) {
			this.bitSet.set(0, bitSet.size());
		}
	}

}
