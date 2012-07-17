
package steffen.layer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class CleanNodesLayer {
	
	private static String	myFileSource	= "";
	private static Layer		myLayer			= Layer.Cities;
	private static String[]	tagsToKeep		= null;
	
	public static void cleanLayer(String fileSource, Layer layer, boolean deleteOldFile) throws IOException {
		System.out.println("Begin cleaning layer " + layer.name + "...");
		
		if (layer == Layer.Cities || layer == Layer.Towns || layer == Layer.Villages || layer == Layer.Hamlets
				|| layer == Layer.Suburbs) {
			CleanNodesLayer.tagsToKeep = new String[2];
			CleanNodesLayer.tagsToKeep[0] = "k=\"name\"";
			CleanNodesLayer.tagsToKeep[1] = "k=\"place\"";
		} else {
			System.exit(1);
		}
		
		File oldFile = new File(Constants.pathToExternXMLs + fileSource);
		BufferedReader reader = new BufferedReader(new FileReader(oldFile));
		File newFile = new File(Constants.pathToExternXMLs + fileSource.replaceFirst(".xml", "2.xml"));
		BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<tag") >= 0) {
				if (CleanNodesLayer.keepTag(line)) {
					writer.write(line + Constants.lineSeparator);
				}
			} else {
				writer.write(line + Constants.lineSeparator);
			}
		}
		reader.close();
		writer.close();
		
		if (deleteOldFile) {
			oldFile.delete();
			newFile.renameTo(oldFile);
		}
		
		System.out.println("Done");
	}
	
	public static void main(String[] args) throws Exception {
		CleanNodesLayer.cleanLayer(CleanNodesLayer.myFileSource, CleanNodesLayer.myLayer, false);
	}
	
	private static boolean keepTag(String input) {
		for (String tag : CleanNodesLayer.tagsToKeep) {
			if (input.indexOf(tag) >= 0) {
				return true;
			}
		}
		return false;
	}
}
