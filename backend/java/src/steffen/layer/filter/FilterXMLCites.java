
package steffen.layer.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class FilterXMLCites {
	private static String	fileSource		= "bawu.xml";
	private static String	fileTarget		= "bawu cities.xml";
	private static String	neededKey		= "k=\"place\"";
	// private static String[] possibleValues = { "v=\"city\"", "v=\"town\"", "v=\"village\"", "v=\"hamlet\"", "v=\"suburb\"" };
	private static String[]	possibleValues	= { "v=\"city\"" };
	
	public static void main(String[] args) throws IOException {
		fileSource = Constants.pathToExternXMLs + fileSource;
		fileTarget = Constants.pathToExternXMLs + fileTarget;
		
		// 1 Save needed nodes in file
		// 1 Create
		BufferedReader reader = new BufferedReader(new FileReader(new File(FilterXMLCites.fileSource)));
		FileWriter writer = new FileWriter(new File(FilterXMLCites.fileTarget));
		
		// 1 Actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				boolean needed = false;
				String zeile = line + "\n";
				if (line.indexOf("/>") < 0) {
					do {
						line = reader.readLine();
						if (line.indexOf("<tag") >= 0) {
							if (line.indexOf(FilterXMLCites.neededKey) >= 0) {
								for (String value : FilterXMLCites.possibleValues) {
									if (line.indexOf(value) >= 0) {
										needed = true;
										break;
									}
								}
							}
						}
						// place for adding additional checks
						zeile += line + "\n";
					} while (line.indexOf("</node") < 0);
				}
				if (needed) {
					writer.write(zeile);
				}
			} else {
				if (line.indexOf("<?xml") >= 0) {
					writer.write(line + "\n");
				} else {
					if (line.indexOf("<osm") >= 0) {
						writer.write("<osm>\n");
					} else {
						if (line.indexOf("</osm") >= 0) {
							writer.write("</osm>\n");
						}
					}
				}
			}
		}
		
		// 1 Destroy
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
}
