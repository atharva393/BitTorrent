import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Common config class is used to store parameters in common.cfg file. This
 * should be accessible globally.
 * 
 * @author Aman, Aneesh & Atharva
 *
 */
public class CommonConfig {
	private int numberOfPreferredNeighbors;
	private int unchokingInterval;
	private int optimisticUnchokingInterval;
	private int fileSize;
	private int pieceSize;
	private String fileName;

	private static CommonConfig commonConfig = null;

	private CommonConfig() {

	}

	public int getNumberOfPreferredNeighbors() {
		return numberOfPreferredNeighbors;
	}

	private void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) {
		this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
	}

	public int getUnchokingInterval() {
		return unchokingInterval;
	}

	private void setUnchokingInterval(int unchokingInterval) {
		this.unchokingInterval = unchokingInterval;
	}

	public int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}

	private void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
		this.optimisticUnchokingInterval = optimisticUnchokingInterval;
	}

	public int getFileSize() {
		return fileSize;
	}

	private void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getPieceSize() {
		return pieceSize;
	}

	private void setPieceSize(int pieceSize) {
		this.pieceSize = pieceSize;
	}

	public String getFileName() {
		return fileName;
	}

	private void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public static CommonConfig getCommonProperties() {

		if (commonConfig == null) {
			commonConfig = new CommonConfig();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader("Common.cfg"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String line;
			String propertyName;
			String propertyValue;

			try {
				while ((line = br.readLine()) != null) {

					String[] splittedLine = line.split(" ");
					propertyName = splittedLine[0].toLowerCase();
					propertyValue = splittedLine[1];

					switch (propertyName) {

					case (Constants.FileName):
						commonConfig.setFileName(propertyValue);
						break;

					case (Constants.FileSize):
						commonConfig.setFileSize(Integer.parseInt(propertyValue));
						break;

					case (Constants.NumberOfPreferredNeighbors):
						commonConfig.setNumberOfPreferredNeighbors(Integer.parseInt(propertyValue));
						break;

					case (Constants.PieceSize):
						commonConfig.setPieceSize(Integer.parseInt(propertyValue));
						break;

					case (Constants.UnchokingInterval):
						commonConfig.setUnchokingInterval(Integer.parseInt(propertyValue));
						break;

					case (Constants.OptimisticUnchokingInterval):
						commonConfig.setOptimisticUnchokingInterval(Integer.parseInt(propertyValue));
						break;

					default:
						System.out.println("Unexpected property found.");
						break;
					}

				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return commonConfig;

	}

}
