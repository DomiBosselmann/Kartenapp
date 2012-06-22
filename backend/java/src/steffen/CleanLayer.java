
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CleanLayer {
	private static String	sourceFile	= "bawu bounfdary.xml";
	private static String	targetFile	= "bawu boundfary2.xml";
	private static String[]	tagsToKeep	= { "k=\"admin_level\"", "k=\"boundary\"", "k=\"border_type\"" };
	
	public static void main(String[] args) throws IOException {
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayer.sourceFile)));
		FileWriter writer = new FileWriter(new File(CleanLayer.targetFile));
		
		// actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<tag") >= 0) {
				if (CleanLayer.keepTag(line)) {
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
		String[] args2 = { CleanLayer.sourceFile.replaceAll(".xml", "2.xml"), CleanLayer.targetFile.replaceAll(".xml", "2.xml") };
		WrapNodes.main(args2);
	}
	
	private static boolean keepTag(String input) {
		for (String tag : CleanLayer.tagsToKeep) {
			if (input.indexOf(tag) >= 0) {
				return true;
			}
		}
		return false;
	}
}
