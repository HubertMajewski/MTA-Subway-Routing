/**
 * @author Hubert
 */
public class FileReader {

	private final String[] fileText;

	public FileReader(String fileName) throws java.io.IOException {
		String temp = "";
		java.io.FileReader fileReader;
		fileReader = new java.io.FileReader(fileName);
		for (int i = fileReader.read(); i != -1; i = fileReader.read())
			temp += (char) i;
		fileReader.close();
		fileText = temp.split("\\n");
		length = fileText.length;
	}

	private int lineNumber = -1;
	public final int length;

	public String readNextLine() throws EndOfFileException {
		try {
			return fileText[++lineNumber];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new EndOfFileException("Reached end of file.");
		}
	}

}

class EndOfFileException extends Exception {

	public EndOfFileException(String s) {
		super(s);
	}

}
