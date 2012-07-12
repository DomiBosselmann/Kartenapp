
package steffen.layer.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class CleanLayer {
	private static String	fileSource	= "bawu boundary.xml";
	private static String[]	tagsToKeep	= { "k=\"admin_level\"", "k=\"boundary\"", "k=\"border_type\"" };
	
	public static void main(String[] args) throws IOException {
		String fileTarget = Constants.pathToExternXMLs + CleanLayer.fileSource.replaceFirst(".xml", "2.xml");
		fileSource = Constants.pathToExternXMLs + fileSource;
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayer.fileSource)));
		FileWriter writer = new FileWriter(new File(fileTarget));
		
		// actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			/*
			 * ((If nodes shall not have tags)) if (line.indexOf("<node") >= 0) { if (line.indexOf("/>") < 0) { line =
			 * line.replaceFirst(">", "/>"); writer.write(line + "\n"); do { line = reader.readLine(); } while ((line.indexOf("</node") <
			 * 0)); } else { writer.write(line + "\n"); } } else {
			 */
			if (line.indexOf("<tag") >= 0) {
				if (CleanLayer.keepTag(line)) {
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
		for (String tag : CleanLayer.tagsToKeep) {
			if (input.indexOf(tag) >= 0) {
				return true;
			}
		}
		return false;
	}
}
