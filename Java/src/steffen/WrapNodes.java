
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class WrapNodes {
	private static String	sourceFile	= "bawu highway motorway2.xml";
	private static String	targetFile	= "bawu highway motorway3.xml";
	
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			WrapNodes.sourceFile = args[0];
			WrapNodes.targetFile = args[1];
		}
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(WrapNodes.sourceFile)));
		FileWriter writer = new FileWriter(new File(WrapNodes.targetFile));
		
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
						line = line.replaceAll(">", "/>");
						writer.write(line + "\n");
					} else {
						writer.write(line + "\n");
						writer.write(zeile + "\n");
					}
				}
			} else {
				writer.write(line + "\n");
			}
		}
		
		// destroy
		if (reader != null) {
			reader.close();
		}
		if (writer != null) {
			writer.close();
		}
	}
}
