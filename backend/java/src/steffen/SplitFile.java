
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SplitFile {
	private static String	fileSource	= "bawu.xml";
	private static int		beginLine	= 46840151;
	private static int		endLine		= 46840251;
	
	public static void main(String[] args) throws IOException {
		String fileTarget = Constants.pathToExternXMLs
				+ SplitFile.fileSource.replaceFirst(".xml", " split " + beginLine + "-" + endLine + " .xml");
		fileSource = Constants.pathToExternXMLs + fileSource;
		
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(SplitFile.fileSource)));
		FileWriter writer = new FileWriter(new File(fileTarget));
		
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
