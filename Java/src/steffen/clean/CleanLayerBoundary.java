
package steffen.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.WrapNodes;

public class CleanLayerBoundary {
	private static String	sourceFile	= "bawu boundary2.xml";
	private static String	targetFile	= "bawu boundary3.xml";
	private static String[]	tagsToKeep	= { "k=\"admin_level\"", "k=\"boundary\"", "k=\"border_type\"" };
	
	public static void main(String[] args) throws IOException {
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayerBoundary.sourceFile)));
		FileWriter writer = new FileWriter(new File(CleanLayerBoundary.targetFile));
		
		// actions
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
		
		// destroy
		if (reader != null) {
			reader.close();
		}
		if (writer != null) {
			writer.close();
		}
		String[] args2 = { CleanLayerBoundary.targetFile, CleanLayerBoundary.targetFile.replaceAll(".xml", "2.xml") };
		WrapNodes.main(args2);
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
