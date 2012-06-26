
package steffen.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GetLine {
	private static String	sourceFilePath	= "bawu.xml";
	private static String	searchedString	= "<relation";
	
	public static void main(String[] args) throws IOException {
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(GetLine.sourceFilePath)));
		
		// actions
		int i = 0;
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf(GetLine.searchedString) >= 0) {
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
