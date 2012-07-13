
package steffen.layer.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;
import steffen.layer.clean.CleanLayerCities;

public class FilterXMLCites {
	private enum PlacesType {
		Cities, Towns, Villages, Hamlets, Suburbs;
	}
	
	private static String		fileSource	= "bawu.xml";
	private static String		fileTarget	= "bawu cities.xml";
	private static PlacesType	placesType	= PlacesType.Cities;
	
	public static void main(String[] args) throws IOException {
		System.out.println("Begin filtering layer...");
		String[] neededKeys = { "k=\"place\"", "k=\"name\"" };
		String[] neededValues = new String[neededKeys.length];
		neededValues[1] = "";
		switch (placesType) {
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
				System.exit(0);
				break;
			}
		}
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToExternXMLs + FilterXMLCites.fileSource)));
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Constants.pathToExternXMLs + FilterXMLCites.fileTarget)));
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
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
								i++;
							}
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
					writer.write(zeile);
				}
			} else {
				if (line.indexOf("<?xml") >= 0) {
					writer.write(line + Constants.lineSeparator);
				} else {
					if (line.indexOf("<osm") >= 0) {
						writer.write("<osm>" + Constants.lineSeparator);
					} else {
						if (line.indexOf("</osm") >= 0) {
							writer.write("</osm>" + Constants.lineSeparator);
						}
					}
				}
			}
		}
		reader.close();
		writer.close();
		
		System.out.println("Done");
		
		CleanLayerCities.cleanLayer(fileTarget);
	}
}
