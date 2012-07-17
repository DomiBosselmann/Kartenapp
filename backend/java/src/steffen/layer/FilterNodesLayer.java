
package steffen.layer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class FilterNodesLayer {
	
	private static String	myFileSource	= "bawu.xml";
	private static Layer		myLayer			= Layer.Cities;
	
	public static void main(String[] args) throws IOException {
		filterNodesLayer(myFileSource, myLayer, true);
	}
	
	public static void filterNodesLayer(String fileSource, Layer layer, boolean deleteOldFile) throws IOException {
		System.out.println("Begin filtering layer " + layer.name + "...");
		
		long nodes = 0L;
		String[] neededKeys = new String[2];
		String[] neededValues = new String[neededKeys.length];
		neededKeys[0] = "k=\"place\"";
		neededKeys[1] = "k=\"name\"";
		switch (layer) {
			case Cities: {
				neededValues[0] = "v=\"city\"";
				break;
			}
			case Towns: {
				neededValues[0] = "v=\"town\"";
				break;
			}
			case Villages: {
				neededValues[0] = "v=\"village\"";
				break;
			}
			case Hamlets: {
				neededValues[0] = "v=\"hamlet\"";
				break;
			}
			case Suburbs: {
				neededValues[0] = "v=\"suburb\"";
				break;
			}
			default: {
				System.exit(1);
				break;
			}
		}
		neededValues[1] = "";
		String fileTargetName = layer.name;
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToExternXMLs + fileSource)));
		String fileTarget = fileSource.replaceFirst(".xml", " " + fileTargetName + ".xml");
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Constants.pathToExternXMLs + fileTarget)));
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				if (line.indexOf("/>") < 0) {
					boolean[] needed = new boolean[neededKeys.length];
					String zeile = line + Constants.lineSeparator;
					do {
						line = reader.readLine();
						if (line.indexOf("<tag") >= 0) {
							int i = 0;
							for (String neededTag : neededKeys) {
								if (line.indexOf(neededTag) >= 0) {
									if (line.indexOf(neededValues[i]) >= 0) {
										needed[i] = true;
									}
								}
								i++;
							}
						}
						zeile += line + Constants.lineSeparator;
					} while (line.indexOf("</node") < 0);
					boolean needed1 = true;
					for (boolean need : needed) {
						if (!need) {
							needed1 = false;
						}
					}
					if (needed1) {
						writer.write(zeile);
						nodes++;
					}
				}
			} else {
				if (line.indexOf("<?xml") >= 0) {
					writer.write(line + Constants.lineSeparator);
				} else {
					if (line.indexOf("<osm") >= 0) {
						writer.write("<osm>" + Constants.lineSeparator);
					} else {
						if (line.indexOf("</osm") >= 0) {
							writer.write("</osm>");
						}
					}
				}
			}
		}
		reader.close();
		writer.close();
		
		System.out.println("Nodes: " + nodes);
		System.out.println("Done");
		
		CleanNodesLayer.cleanLayer(fileTarget, layer, deleteOldFile);
	}
}
