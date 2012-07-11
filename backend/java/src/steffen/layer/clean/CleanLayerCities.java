
package steffen.layer.clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class CleanLayerCities {
	private static String	fileSource	= "bawu cities.xml";
	private static String[]	tagsToKeep	= { "k=\"name\"", "k=\"place\"" };
	
	public static void main(String[] args) throws IOException {
		String fileTarget = Constants.pathToExternXMLs + CleanLayerCities.fileSource.replaceFirst(".xml", "2.xml");
		fileSource = Constants.pathToExternXMLs + fileSource;
		
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(CleanLayerCities.fileSource)));
		FileWriter writer = new FileWriter(new File(fileTarget));
		
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
		reader.close();
		writer.close();
		
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
