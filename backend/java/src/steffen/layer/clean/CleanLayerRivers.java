
package steffen.layer.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class CleanLayerRivers {
	private static String	sourceFile	= "bawu rivers.xml";
	private static String[]	tagsToKeep	= { "k=\"name\"", "k=\"waterway\"", "k=\"natural\"" };
	
	public static void main(String[] args) throws IOException {
		String targetFile = Constants.pathToExternXMLs + CleanLayerRivers.sourceFile.replaceFirst(".xml", "2.xml");
		sourceFile = Constants.pathToExternXMLs + sourceFile;
		
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayerRivers.sourceFile)));
		FileWriter writer = new FileWriter(new File(targetFile));
		
		// actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				if (line.indexOf("/>") < 0) {
					line = line.replaceFirst(">", "/>");
					writer.write(line + "\n");
					do {
						line = reader.readLine();
					} while ((line.indexOf("</node") < 0));
				} else {
					writer.write(line + "\n");
				}
			} else {
				if (line.indexOf("<tag") >= 0) {
					if (CleanLayerRivers.keepTag(line)) {
						writer.write(line + "\n");
					}
				} else {
					writer.write(line + "\n");
				}
			}
		}
		
		// destroy
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
	
	private static boolean keepTag(String input) {
		for (String tag : CleanLayerRivers.tagsToKeep) {
			if (input.indexOf(tag) >= 0) {
				return true;
			}
		}
		return false;
	}
}
