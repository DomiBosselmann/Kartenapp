
package steffen.layer.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.FilePath;

public class CleanLayerMotorway {
	private static String	fileSource	= "bawu motorways.xml";
	private static String[]	tagsToKeep	= { "k=\"highway\"", "k=\"name\"", "k=\"int_ref\"", "k=\"ref\"", "k=\"lanes\"", "k=\"oneway\"" };
	
	public static void main(String[] args) throws IOException {
		String fileTarget = FilePath.path + CleanLayerMotorway.fileSource.replaceFirst(".xml", "2.xml");
		fileSource = FilePath.path + fileSource;
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayerMotorway.fileSource)));
		FileWriter writer = new FileWriter(new File(fileTarget));
		
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
						if (line.indexOf("k=\"ref\"") >= 0) {
							int refbegin = line.indexOf("v=\"");
							if (refbegin >= 0) {
								int refend = line.indexOf("\"", refbegin + 3);
								String ref1 = line.substring(refbegin, refend + 3);
								String ref2 = ref1.replaceAll(" ", "");
								line = line.replaceFirst(ref1, ref2);
							}
						} else {
							if (line.indexOf("k=\"int_ref\"") >= 0) {
								int refbegin = line.indexOf("v=\"");
								if (refbegin >= 0) {
									int refend = line.indexOf("\"", refbegin + 3);
									String ref1 = line.substring(refbegin, refend + 3);
									String ref2 = ref1.replaceAll(" ", "");
									line = line.replaceFirst(ref1, ref2);
								}
							}
						}
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
