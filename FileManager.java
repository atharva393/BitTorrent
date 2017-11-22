public class FileManager {

	private CustomBitField customBitField;

	public CustomBitField getCustomBitField() {
		return customBitField;
	}

	
	FileManager(boolean hasFile){
		
		CommonConfig commonConfig = CommonConfig.getCommonProperties();
		
		customBitField = new CustomBitField(commonConfig.getFileSize()/commonConfig.getPieceSize());
		
		if(hasFile) {
			this.customBitField.setAll();
		}
	}

}
