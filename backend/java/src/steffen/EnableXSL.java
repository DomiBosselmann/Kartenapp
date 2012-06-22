
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EnableXSL {
	private static String	sourceFilePath	= "bawu highway motorway.xml";
	private static String	targetFilePath	= "bawu highway motorway xsl.xml";
	private static String	xslFileName		= "highway.xsl";
	 
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
			reader.readLine();
			writer.write("<?xml version='1.0' encoding='UTF-8' ?>\n");
			writer.write("<?xml-stylesheet type=\"text/xml\" href=\"" + xslFileName + "\" ?>\n");
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			while (reader.ready()) {
				line = reader.readLine();
				writer.write(line + "\n");
				System.out.println(i);
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
