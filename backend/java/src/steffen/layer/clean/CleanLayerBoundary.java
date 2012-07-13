
package steffen.layer.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class CleanLayerBoundary {
	private static String[]	tagsToKeep	= { "k=\"admin_level\"", "k=\"boundary\"", "k=\"border_type\"" };
	
	public static void main(String[] args) throws Exception {
		String fileSource = "bawu bounds.xml";
		cleanLayer(fileSource);
	}
	
	public static void cleanLayer(String fileSource) throws IOException {
		System.out.println("Begin cleaning layer...");
		String fileTarget = Constants.pathToExternXMLs + fileSource.replaceFirst(".xml", "2.xml");
		fileSource = Constants.pathToExternXMLs + fileSource;
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileSource)));
		FileWriter writer = new FileWriter(new File(fileTarget));
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<tag") >= 0) {
				if (CleanLayerBoundary.keepTag(line)) {
					writer.write(line + Constants.lineSeparator);
				}
			} else {
				writer.write(line + Constants.lineSeparator);
			}
		}
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
	
	private static boolean keepTag(String input) {
		for (String tag : CleanLayerBoundary.tagsToKeep) {
			if (input.indexOf(tag) >= 0) {
				return true;
			}
		}
		return false;
	}
}
