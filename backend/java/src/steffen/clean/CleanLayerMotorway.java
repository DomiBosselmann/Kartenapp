
package steffen.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.WrapNodes;

public class CleanLayerMotorway {
	private static String	sourceFile	= "bawu motorwaytest.xml";
	private static String	targetFile	= "bawu motorwaytest2.xml";
	private static String[]	tagsToKeep	= { "k=\"highway\"", "k=\"name\"", "k=\"int_ref\"", "k=\"ref\"", "k=\"lanes\"", "k=\"oneway\"" };
	
	public static void main(String[] args) throws IOException {
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayerMotorway.sourceFile)));
		FileWriter writer = new FileWriter(new File(CleanLayerMotorway.targetFile));
		
		// actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<tag") >= 0) {
				if (CleanLayerMotorway.keepTag(line)) {
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
		String[] args2 = { CleanLayerMotorway.sourceFile.replaceAll(".xml", "2.xml"), CleanLayerMotorway.targetFile.replaceAll(".xml", "2.xml") };
		WrapNodes.main(args2);
	}
	
	private static boolean keepTag(String input) {
		for (String tag : CleanLayerMotorway.tagsToKeep) {
			if (input.indexOf(tag) >= 0) {
				return true;
			}
		}
		return false;
	}
}
