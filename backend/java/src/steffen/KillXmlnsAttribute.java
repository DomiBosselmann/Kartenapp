
package steffen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

public class KillXmlnsAttribute {
	
	private static String	fileName	= "federal";
	
	public static void main(String[] args) throws IOException {
		System.out.println("Begin killing xmlns attribute...");
		
		File[] files = new File(Constants.pathToExternXMLs).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.indexOf(KillXmlnsAttribute.fileName) >= 0 && name.indexOf(".svg") >= 0) {
					return true;
				}
				return false;
			}
		});
		
		for (File file : files) {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Constants.pathToExternXMLs + "new_"
					+ file.getName())));
			
			String line = null;
			while (reader.ready()) {
				line = reader.readLine();
				line = line.replaceAll("xmlns=\"\"", "");
				writer.write(line + Constants.lineSeparator);
			}
			writer.close();
		}
		
		System.out.println("Done");
	}
}
