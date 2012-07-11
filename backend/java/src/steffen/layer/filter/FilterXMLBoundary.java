
package steffen.layer.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import steffen.Constants;

public class FilterXMLBoundary {
	private static String	fileSource			= "bawu.xml";
	private static String	fileTarget			= "bawu boundary.xml";
	private static String[]	neededTags			= { "k=\"boundary\" v=\"administrative\"" };
	private static int		admin_level_min	= 5;
	private static int		admin_level_max	= 6;
	
	// admin_level gibt Grenzart an:
	// 2: Staaten
	// 4: Bundesländer
	// 5: Regierungsbezirke
	// 6: Kreise
	// 7-9: Ortsgrenzen
	// 9-11: Ortsteile
	
	public static void main(String[] args) throws IOException {
		fileSource = Constants.pathToExternXMLs + fileSource;
		fileTarget = Constants.pathToExternXMLs + fileTarget;
		Hashtable<Integer, Integer> nodeIDs = new Hashtable<Integer, Integer>();
		
		// 1 Save needed nodes in file
		// 1 Create
		File sourceFile = new File(FilterXMLBoundary.fileSource);
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
				boolean needed2 = false;
				String zeile = line + "\n";
				do {
					line = reader.readLine();
					if (line.indexOf("<tag") >= 0) {
						for (String neededTag : FilterXMLBoundary.neededTags) {
							if (line.indexOf(neededTag) >= 0) {
								needed1 = true;
							}
						}
						if (line.indexOf("k=\"admin_level\"") >= 0) {
							int levelbegin = line.indexOf("v=\"");
							if (levelbegin >= 0) {
								int levelend = line.indexOf("\"", levelbegin + 3);
								int level = Integer.valueOf(line.substring(levelbegin + 3, levelend));
								if ((level >= FilterXMLBoundary.admin_level_min) && (level <= FilterXMLBoundary.admin_level_max)) {
									needed2 = true;
								}
							}
						}
					}
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
		reader.close();
		writer.close();
		
		System.out.println("Nodes: " + nodeIDs.size());
		System.out.println("Step 1");
		
		// 2 Add nodes to target file
		// 2 Create
		reader = new BufferedReader(new FileReader(sourceFile));
		File targetFile = new File(FilterXMLBoundary.fileTarget);
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
						if (line.indexOf("/>") < 0) {
							line = line.replaceFirst(">", "/>");
							writer.write(line + "\n");
							do {
								if (reader.ready()) {
									line = reader.readLine();
								} else {
									line = "</node";
								}
							} while (line.indexOf("</node") < 0);
						} else {
							writer.write(line + "\n");
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
		while (reader.ready()) {
			line = reader.readLine();
			writer.write(line + "\n");
		}
		writer.write("</osm>\n");
		
		// 3 Destroy
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
}
