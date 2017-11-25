import java.util.BitSet;
/**
 * CustomBitfield class to use only limited elements in BitSet.
 * @author Aman, Aneesh and Atharva
 *
 */
public class CustomBitField {
	private BitSet bitSet = new BitSet();
	private int numberOfPieces = (int) Math.ceil(CommonConfig.getCommonProperties().getFileSize() / CommonConfig.getCommonProperties().getPieceSize());
	
	public BitSet getBitSet() {
		return bitSet;
	}

	public void setBitSet(BitSet bitSet) {
		this.bitSet = bitSet;
	}

	
	public CustomBitField() {
		
	}
	
	public CustomBitField(int numberOfPieces) {
		this.bitSet = new BitSet(numberOfPieces);
		this.numberOfPieces = numberOfPieces;
	}
	
	public synchronized void set(int index) {
		bitSet.set(index);
	}

	public synchronized void setAll() {
		for (int index = 0; index < numberOfPieces; index++) {
			bitSet.set(index);
		}
	}
	
	public synchronized boolean hasAll() {
		for (int index = 0; index < numberOfPieces; index++) {
			if (!bitSet.get(index))
				return false;
		}
		return true;
	}
}
