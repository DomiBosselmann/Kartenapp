
package steffen.layer.clean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class CleanLayerCities {
	private static String[]	tagsToKeep	= { "k=\"name\"", "k=\"place\"" };
	
	public static void main(String[] args) throws Exception {
		cleanLayer("bawu cities.xml");
	}
	
	public static void cleanLayer(String fileSource) throws IOException {
		System.out.println("Begin cleaning layer...");
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToExternXMLs + fileSource)));
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Constants.pathToExternXMLs
				+ fileSource.replaceFirst(".xml", "2.xml"))));
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<tag") >= 0) {
				if (CleanLayerCities.keepTag(line)) {
					writer.write(line + Constants.lineSeparator);
				}
			} else {
				writer.write(line + Constants.lineSeparator);
			}
		}
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
