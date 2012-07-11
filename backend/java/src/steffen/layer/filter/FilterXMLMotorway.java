
package steffen.layer.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import steffen.Constants;

public class FilterXMLMotorway {
	private static String	fileSource	= "bawu.xml";
	private static String	fileTarget	= "bawu motorways.xml";
	private static String	neededTag1	= "k=\"highway\" v=\"motorway\"";
	private static String	neededTag2	= "k=\"ref\"";
	
	public static void main(String[] args) throws IOException {
		fileSource = Constants.pathToExternXMLs + fileSource;
		fileTarget = Constants.pathToExternXMLs + fileTarget;
		Hashtable<Integer, Integer> nodeIDs = new Hashtable<Integer, Integer>();
		
		// 1 Save needed nodes in file
		// 1 Create
		File sourceFile = new File(FilterXMLMotorway.fileSource);
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
						if (line.indexOf(neededTag1) >= 0) {
							needed1 = true;
						} else {
							if (line.indexOf(neededTag2) >= 0) {
								needed2 = true;
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
		
		System.out.println(nodeIDs.size());
		System.out.println("Step 1");
		
		// 2 Add nodes to target file
		// 2 Create
		reader = new BufferedReader(new FileReader(sourceFile));
		writer = new FileWriter(new File(FilterXMLMotorway.fileTarget));
		
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
