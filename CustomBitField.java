import java.util.BitSet;
/**
 * CustomBitfield class to use only limited elements in BitSet.
 * @author Aman, Aneesh and Atharva
 *
 */
public class CustomBitField {
	
	private int numberOfPieces = (int) Math.ceil((CommonConfig.getCommonProperties().getFileSize() * 1.0) / CommonConfig.getCommonProperties().getPieceSize());
	private BitSet bitSet = new BitSet(numberOfPieces);

	public void setBitSet(BitSet bitSet) {
		this.bitSet = bitSet;
	}
	
	public BitSet getBitSet() {
		return bitSet;
	}
	
	public synchronized void set(int index) {
		bitSet.set(index);
	}
	
	public synchronized boolean get(int index) {
		return bitSet.get(index);
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
	
	public synchronized int getTotalPiecesReceived() {
		return bitSet.cardinality();
	}
}
