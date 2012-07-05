
package steffen.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GetLineCount {
	private static String	sourceFilePath	= "xml/bawu boundary.xml";
	
	public static void main(String[] args) throws IOException {
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(GetLineCount.sourceFilePath)));
		
		// actions
		int i = 0;
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			i++;
		}
		
		// destroy
		reader.close();
		
		System.out.println(i);
		System.out.println(line);
		System.out.println("Done");
	}
}
