
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SplitFile {
	private static String	sourceFilePath	= "./berlin4.xml";
	private static String	targetFilePath	= "./berlin5.xml";
	private static int		beginLine		= 5000000;
	private static int		endLine			= 5527980;
	
	public static void main(String[] args) {
		// create
		File file1 = new File(sourceFilePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file1));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		File file2 = new File(targetFilePath);
		FileWriter writer = null;
		try {
			writer = new FileWriter(file2);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// actions
		int i = 0;
		String line = null;
		try {
			while ((reader.ready()) && (i < endLine)) {
				line = reader.readLine() + "\n";
				if (i > beginLine) {
					writer.write(line);
					System.out.println(i);
				} else {
					System.out.println("_" + i);
				}
				i++;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println(line);
		
		// destroy
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
