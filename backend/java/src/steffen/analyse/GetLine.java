
package steffen.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import steffen.FilePath;

public class GetLine {
	private static String	fileSource		= "bawu.xml";
	private static String	searchedString	= "1785840335";
	
	public static void main(String[] args) throws IOException {
		fileSource = FilePath.path + fileSource;
		
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(GetLine.fileSource)));
		
		// actions
		int i = 0;
		String line = null;
		String lineLower = null;
		while (reader.ready()) {
			line = reader.readLine();
			lineLower = line.toLowerCase();
			if (lineLower.indexOf(GetLine.searchedString.toLowerCase()) >= 0) {
				System.out.println(i);
				System.out.println(line);
			}
			i++;
		}
		
		// destroy
		reader.close();
		
		System.out.println("Done");
	}
}
