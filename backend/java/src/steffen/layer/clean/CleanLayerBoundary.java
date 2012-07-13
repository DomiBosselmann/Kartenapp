
package steffen.layer.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class CleanLayerBoundary {
	private static String	fileSource	= "bawu bounds.xml";
	private static String[]	tagsToKeep	= { "k=\"admin_level\"", "k=\"boundary\"", "k=\"border_type\"" };
	
	public static void main(String[] args) throws IOException {
		String fileTarget = Constants.pathToExternXMLs + CleanLayerBoundary.fileSource.replaceFirst(".xml", "2.xml");
		fileSource = Constants.pathToExternXMLs + fileSource;
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayerBoundary.fileSource)));
		FileWriter writer = new FileWriter(new File(fileTarget));
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<tag") >= 0) {
				if (CleanLayerBoundary.keepTag(line)) {
					writer.write(line + "\n");
				}
			} else {
				writer.write(line + "\n");
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
