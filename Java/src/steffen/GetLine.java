
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GetLine {
	private static String	sourceFilePath	= "bawu.xml";
	private static String	searchedString	= "v=\"city\"";
	
	public static void main(String[] args) {
		// create
		File file1 = new File(sourceFilePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file1));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// actions
		int i = 0;
		String line = null;
		try {
			while (reader.ready()) {
				line = reader.readLine();
				if (line.indexOf(searchedString) >= 0) {
					System.out.println(i);
					System.out.println(line);
				}
				// System.out.println(i);
				i++;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println(i - 1);
		System.out.println(line);
		
		// destroy
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
