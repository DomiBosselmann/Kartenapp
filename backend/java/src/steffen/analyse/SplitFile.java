
package steffen.analyse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class SplitFile {
	private static String	fileSource	= "bawu.xml";
	private static int		beginLine	= 25470022;
	private static int		endLine		= 25470422;
	
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToExternXMLs + SplitFile.fileSource)));
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				new File(Constants.pathToExternXMLs
						+ SplitFile.fileSource.replaceFirst(".xml", " split " + SplitFile.beginLine + "-" + SplitFile.endLine
								+ " .xml"))));
		
		int i = 0;
		String line = null;
		while (reader.ready() && i < SplitFile.endLine) {
			line = reader.readLine() + Constants.lineSeparator;
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
