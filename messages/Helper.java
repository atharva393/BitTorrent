package messages;
import java.nio.ByteBuffer;

public class Helper {
	
	static byte[] getBytesOfGivenSizeAndMessage(int message, int size)
	{
		ByteBuffer bb = ByteBuffer.allocate(size); 
		bb.putInt(message); 
		return bb.array();
	}
}
