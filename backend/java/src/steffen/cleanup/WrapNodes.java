
package steffen.cleanup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class WrapNodes {
	private static String	fileSource	= "motorways.xml";
	
	public static void main(String[] args) throws IOException {
		String fileTarget = Constants.pathToExternXMLs + WrapNodes.fileSource.replaceFirst(".xml", "2.xml");
		fileSource = Constants.pathToExternXMLs + fileSource;
		
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(WrapNodes.fileSource)));
		FileWriter writer = new FileWriter(new File(fileTarget));
		
		// actions
		String line = null;
		String zeile = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				if (line.indexOf("/>") >= 0 || line.indexOf("</node") >= 0) {
					writer.write(line + "\n");
				} else {
					zeile = reader.readLine();
					if (zeile.indexOf("</node") >= 0) {
						line = line.replaceFirst(">", "/>");
						writer.write(line + "\n");
					} else {
						writer.write(line + "\n");
						writer.write(zeile + "\n");
						while (zeile.indexOf("</node") < 0) {
							zeile = reader.readLine();
							writer.write(zeile + "\n");
						}
					}
				}
			} else {
				writer.write(line + "\n");
			}
		}
		
		// destroy
		reader.close();
		writer.close();
	}
}
