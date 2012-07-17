
package steffen.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import steffen.Constants;

public class GetLineCount {
	
	public static Integer getLineCount(String fileSource) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToExternXMLs + fileSource)));
		
		Integer i = 0;
		@SuppressWarnings("unused")
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			i++;
		}
		reader.close();
		return i;
	}
	
	public static void main(String[] args) throws IOException {
		String fileSource = "bawu.xml";
		Integer count = GetLineCount.getLineCount(fileSource);
		
		System.out.println("Lines: " + count);
		System.out.println("Done");
	}
}
