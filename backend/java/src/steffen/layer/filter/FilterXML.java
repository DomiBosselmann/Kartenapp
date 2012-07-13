
package steffen.layer.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import steffen.Constants;
import steffen.layer.clean.CleanLayer;

public class FilterXML {
	private static String	fileSource		= "bawu.xml";
	private static String	fileTarget		= "bawu test.xml";
	private static String[]	neededKeys		= { "k=\"highway\"" };
	private static String[]	neededValues	= { "v=\"motorway\"" };
	
	public static void main(String[] args) throws IOException {
		System.out.println("Begin filtering layer...");
		Hashtable<Integer, Integer> nodeIDs = new Hashtable<Integer, Integer>();
		
		// 1 Save needed ways in file and needed nodes in hashtable
		// 1 Create
		File sourceFile = new File(Constants.pathToExternXMLs + FilterXML.fileSource);
		BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
		File tempFile = new File("lines_temp.xml");
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		tempFile.deleteOnExit();
		
		// 1 Actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<way") >= 0) {
				boolean[] needed = new boolean[FilterXML.neededKeys.length];
				String zeile = line + Constants.lineSeparator;
				do {
					line = reader.readLine();
					if (line.indexOf("<tag") >= 0) {
						int i = 0;
						for (String neededTag : FilterXML.neededKeys) {
							if (line.indexOf(neededTag) >= 0) {
								if (line.indexOf(FilterXML.neededValues[i]) >= 0) {
									needed[i] = true;
								}
							}
							i++;
						}
					}
					// place for adding additional checks
					zeile += line + Constants.lineSeparator;
				} while (line.indexOf("</way") < 0);
				boolean needed1 = true;
				for (boolean need : needed) {
					if (!need) {
						needed1 = false;
					}
				}
				if (needed1) {
					String str = "ref=\"";
					int refbegin = zeile.indexOf(str);
					while (refbegin >= 0) {
						int refend = zeile.indexOf("\"", refbegin + str.length());
						Integer ref = Integer.valueOf(zeile.substring(refbegin + str.length(), refend));
						nodeIDs.put(ref, ref);
						refbegin = zeile.indexOf(str, refend);
					}
					writer.write(zeile);
				}
			}
		}
		
		// 1 Destroy
		reader.close();
		writer.close();
		
		System.out.println("Nodes: " + nodeIDs.size());
		System.out.println("Step 1");
		
		// 2 Add nodes to target file
		// 2 Create
		reader = new BufferedReader(new FileReader(sourceFile));
		File targetFile = new File(Constants.pathToExternXMLs + FilterXML.fileTarget);
		writer = new BufferedWriter(new FileWriter(targetFile));
		
		// 2 Actions
		writer.write("<?xml version='1.0' encoding='UTF-8'?>" + Constants.lineSeparator);
		writer.write("<osm>" + Constants.lineSeparator);
		line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				String str = "id=\"";
				int idbegin = line.indexOf(str);
				if (idbegin >= 0) {
					int idend = line.indexOf("\"", idbegin + str.length());
					if (nodeIDs.containsKey(Integer.valueOf(line.substring(idbegin + str.length(), idend)))) {
						writer.write(line + Constants.lineSeparator);
						if (line.indexOf("/>") < 0) {
							do {
								if (reader.ready()) {
									line = reader.readLine();
									writer.write(line + Constants.lineSeparator);
								} else {
									line = "</node";
								}
							} while (line.indexOf("</node") < 0);
						}
					} else {
						if (line.indexOf("/>") < 0) {
							do {
								if (reader.ready()) {
									line = reader.readLine();
								} else {
									line = "</node";
								}
							} while (line.indexOf("</node") < 0);
						}
					}
				}
			}
		}
		
		// 2 Destroy (except writer)
		reader.close();
		
		System.out.println("Step 2");
		
		// 3 Add ways to target file
		// 3 Create
		reader = new BufferedReader(new FileReader(tempFile));
		
		// 3 Actions
		line = null;
		while (reader.ready()) {
			line = reader.readLine();
			writer.write(line + Constants.lineSeparator);
		}
		writer.write("</osm>");
		
		// 3 Destroy
		reader.close();
		writer.close();
		
		System.out.println("Done");
		
		CleanLayer.cleanLayer(FilterXML.fileTarget);
	}
}
