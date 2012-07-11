
package steffen.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import steffen.Constants;

public class GetLine {
	private static String	fileSource		= "bawu.xml";
	private static String	searchedString	= "1785840335";
	
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToExternXMLs + GetLine.fileSource)));
		
		int i = 0;
		int occurrences = 0;
		String line = null;
		String lineLower = null;
		while (reader.ready()) {
			line = reader.readLine();
			lineLower = line.toLowerCase();
			if (lineLower.indexOf(GetLine.searchedString.toLowerCase()) >= 0) {
				occurrences++;
				System.out.println(i);
				System.out.println(line);
			}
			i++;
		}
		reader.close();
		
		System.out.println(occurrences + " occurrences!");
		System.out.println("Done");
	}
}
