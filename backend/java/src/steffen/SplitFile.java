
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SplitFile {
	private static String	sourceFilePath	= "bawu4.xml";
	private static int		beginLine		= 24186221;
	private static int		endLine			= 24196221;
	
	public static void main(String[] args) throws IOException {
		String targetFilePath = SplitFile.sourceFilePath.replaceFirst(".xml", " split .xml");
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(SplitFile.sourceFilePath)));
		FileWriter writer = new FileWriter(new File(targetFilePath));
		
		// actions
		int i = 0;
		String line = null;
		while (reader.ready() && i < SplitFile.endLine) {
			line = reader.readLine() + "\n";
			if (i > SplitFile.beginLine) {
				writer.write(line);
			}
			i++;
		}
		
		// destroy
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
}
