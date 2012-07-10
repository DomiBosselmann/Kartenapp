
package steffen.layer.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.FilePath;

public class CleanLayerBoundary {
	private static String	fileSource	= "bawu boundary.xml";
	private static String[]	tagsToKeep	= { "k=\"admin_level\"", "k=\"boundary\"", "k=\"border_type\"" };
	
	public static void main(String[] args) throws IOException {
		String fileTarget = FilePath.path + CleanLayerBoundary.fileSource.replaceFirst(".xml", "2.xml");
		fileSource = FilePath.path + fileSource;
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayerBoundary.fileSource)));
		FileWriter writer = new FileWriter(new File(fileTarget));
		
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
