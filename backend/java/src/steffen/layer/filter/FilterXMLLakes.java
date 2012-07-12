
package steffen.layer.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import steffen.Constants;

public class FilterXMLLakes {
	private static String	fileSource			= "bawu.xml";
	private static String	fileTarget			= "bawu lakes.xml";
	
	private static boolean	onlyNamed			= false;
	private static String[]	neededKeys			= { "k=\"natural\"" };
	private static String[]	neededValues		= { "v=\"water\"" };
	private static String[]	permittedKeys		= { "k=\"golf\"", "k=\"waterway\"" };
	private static String[]	permittedValues	= { "", "" };
	
	public static void main(String[] args) throws IOException {
		fileSource = Constants.pathToExternXMLs + fileSource;
		fileTarget = Constants.pathToExternXMLs + fileTarget;
		Hashtable<Integer, Integer> nodeIDs = new Hashtable<Integer, Integer>();
		if (onlyNamed) {
			String[] oldNeededKeys = neededKeys;
			String[] oldNeededValues = neededValues;
			neededKeys = new String[oldNeededKeys.length + 1];
			neededValues = new String[oldNeededValues.length + 1];
			for (int i = 0; i < oldNeededKeys.length; i++) {
				neededKeys[i] = oldNeededKeys[i];
				neededValues[i] = oldNeededValues[i];
			}
			neededKeys[oldNeededKeys.length] = "k=\"name\"";
			neededValues[oldNeededValues.length] = "";
		}
		
		// 1 Save needed ways in file and needed nodes in hashtable
		// 1 Create
		File sourceFile = new File(FilterXMLLakes.fileSource);
		BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
		File tempFile = new File("lines_temp.xml");
		FileWriter writer = new FileWriter(tempFile);
		tempFile.deleteOnExit();
		
		// 1 Actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<way") >= 0) {
				boolean[] needed = new boolean[neededKeys.length];
				boolean[] permitted = new boolean[permittedKeys.length];
				String zeile = line + "\n";
				do {
					line = reader.readLine();
					if (line.indexOf("<tag") >= 0) {
						int i = 0;
						for (String neededTag : FilterXMLLakes.neededKeys) {
							if (line.indexOf(neededTag) >= 0) {
								if (line.indexOf(FilterXMLLakes.neededValues[i]) >= 0) {
									needed[i] = true;
								}
							}
							i++;
						}
						i = 0;
						for (String permittedTag : FilterXMLLakes.permittedKeys) {
							if (line.indexOf(permittedTag) >= 0) {
								if (line.indexOf(FilterXMLLakes.permittedValues[i]) >= 0) {
									permitted[i] = true;
								}
							}
							i++;
						}
					}
					// place for adding additional checks
					zeile += line + "\n";
				} while (line.indexOf("</way") < 0);
				boolean needed1 = true;
				boolean needed2 = true;
				for (boolean need : needed) {
					if (!need) {
						needed1 = false;
					}
				}
				for (boolean permit : permitted) {
					if (permit) {
						needed2 = false;
					}
				}
				if (needed1 && needed2) {
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
		File targetFile = new File(FilterXMLLakes.fileTarget);
		writer = new FileWriter(targetFile);
		
		// 2 Actions
		writer.write("<?xml version='1.0' encoding='UTF-8'?>\n");
		writer.write("<osm>\n");
		line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				String str = "id=\"";
				int idbegin = line.indexOf(str);
				if (idbegin >= 0) {
					int idend = line.indexOf("\"", idbegin + str.length());
					if (nodeIDs.containsKey(Integer.valueOf(line.substring(idbegin + str.length(), idend)))) {
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
		nodeIDs.clear();
		reader.close();
		
		System.out.println("Step 2");
		
		// 3 Add ways to target file
		// 3 Create
		reader = new BufferedReader(new FileReader(tempFile));
		
		// 3 Actions
		while (reader.ready()) {
			writer.write(reader.readLine() + "\n");
		}
		writer.write("</osm>\n");
		
		// 3 Destroy
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
}
