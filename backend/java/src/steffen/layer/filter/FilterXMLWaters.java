
package steffen.layer.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

public class FilterXMLWaters {
	private static String	fileSource		= "bawu2.xml";
	private static String	fileTarget		= "bawu rivers.xml";
//	private static String[]	neededKeys		= { "k=\"waterway\"", "k=\"waterway\"", "k=\"waterway\"", "k=\"natural\"" };
//	private static String[]	neededValues	= { "v=\"river\"", "v=\"canal\"", "v=\"riverbank\"", "v=\"water\"" };
	private static String[]	neededKeys		= { "k=\"waterway\"" };
	private static String[]	neededValues	= { "v=\"river\"" };
	
	public static void main(String[] args) throws IOException {
		Hashtable<Integer, Integer> nodeIDs = new Hashtable<Integer, Integer>();
		
		// 1 Save needed ways in file and needed nodes in hashtable
		// 1 Create
		File sourceFile = new File(FilterXMLWaters.fileSource);
		BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
		File tempFile = new File("lines_temp.xml");
		FileWriter writer = new FileWriter(tempFile);
		tempFile.deleteOnExit();
		
		// 1 Actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<way") >= 0) {
				boolean needed1 = false;
				boolean needed2 = true;
				String zeile = line + "\n";
				do {
					line = reader.readLine();
					if (line.indexOf("<tag") >= 0) {
						int i = 0;
						for (String neededTag : FilterXMLWaters.neededKeys) {
							if (line.indexOf(neededTag) >= 0) {
								if (line.indexOf(FilterXMLWaters.neededValues[i]) >= 0) {
									needed1 = true;
								}
								i++;
							}
						}
					}
					// place for adding additional checks
					zeile += line + "\n";
				} while (line.indexOf("</way") < 0);
				if (needed1 && needed2) {
					int refbegin = zeile.indexOf("ref=\"");
					int refend = -1;
					while (refbegin >= 0) {
						refend = zeile.indexOf("\"", refbegin + 5);
						Integer ref = Integer.valueOf(zeile.substring(refbegin + 5, refend));
						nodeIDs.put(ref, ref);
						refbegin = zeile.indexOf("ref=\"", refend);
					}
					writer.write(zeile);
				}
			}
		}
		
		// 1 Destroy
		if (reader != null) {
			reader.close();
		}
		if (writer != null) {
			writer.close();
		}
		
		System.out.println(nodeIDs.size());
		System.out.println("Step 1");
		
		// 2 Add nodes to target file
		// 2 Create
		reader = new BufferedReader(new FileReader(sourceFile));
		File targetFile = new File(FilterXMLWaters.fileTarget);
		writer = new FileWriter(targetFile);
		
		// 2 Actions
		writer.write("<?xml version='1.0' encoding='UTF-8'?>\n");
		writer.write("<osm>\n");
		line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				int idbegin = line.indexOf("id=\"");
				if (idbegin >= 0) {
					int idend = line.indexOf("\"", idbegin + 4);
					if (nodeIDs.containsKey(Integer.valueOf(line.substring(idbegin + 4, idend)))) {
						writer.write(line + "\n");
						if (line.indexOf("/>") < 0) {
							do {
								if (reader.ready()) {
									line = reader.readLine();
									writer.write(line + "\n");
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
		if (reader != null) {
			reader.close();
		}
		
		System.out.println("Step 2");
		
		// 3 Add ways to target file
		// 3 Create
		reader = new BufferedReader(new FileReader(tempFile));
		
		// 3 Actions
		line = null;
		while (reader.ready()) {
			line = reader.readLine();
			writer.write(line + "\n");
		}
		writer.write("</osm>\n");
		
		// 3 Destroy
		if (reader != null) {
			reader.close();
		}
		if (writer != null) {
			writer.close();
		}
		
		System.out.println("Done");
	}
}
