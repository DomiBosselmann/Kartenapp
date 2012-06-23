
package steffen.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.WrapNodes;

public class CleanLayerCities {
	private static String	sourceFile	= "bawu hugecities.xml";
	private static String[]	tagsToKeep	= { "k=\"name\"", "k=\"place\"" };
	
	public static void main(String[] args) throws IOException {
		String targetFile = sourceFile.replaceAll(".xml", "2.xml");
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayerCities.sourceFile)));
		FileWriter writer = new FileWriter(new File(targetFile));
		
		// actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<tag") >= 0) {
				if (CleanLayerCities.keepTag(line)) {
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
		
		System.out.println("Step 1");
		
		String[] args2 = { targetFile, targetFile.replaceAll(".xml", "2.xml") };
		WrapNodes.main(args2);
		
		System.out.println("Done");
	}
	
	private static boolean keepTag(String input) {
		for (String tag : CleanLayerCities.tagsToKeep) {
			if (input.indexOf(tag) >= 0) {
				return true;
			}
		}
		return false;
	}
}
