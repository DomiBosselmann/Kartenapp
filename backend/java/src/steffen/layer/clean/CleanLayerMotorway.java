
package steffen.layer.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CleanLayerMotorway {
	private static String	sourceFile	= "bawu motorway.xml";
	private static String[]	tagsToKeep	= { "k=\"highway\"", "k=\"name\"", "k=\"int_ref\"", "k=\"ref\"", "k=\"lanes\"", "k=\"oneway\"" };
	
	public static void main(String[] args) throws IOException {
		String targetFile = CleanLayerMotorway.sourceFile.replaceFirst(".xml", "2.xml");
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayerMotorway.sourceFile)));
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
					if (CleanLayerMotorway.keepTag(line)) {
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
		for (String tag : CleanLayerMotorway.tagsToKeep) {
			if (input.indexOf(tag) >= 0) {
				return true;
			}
		}
		return false;
	}
}
