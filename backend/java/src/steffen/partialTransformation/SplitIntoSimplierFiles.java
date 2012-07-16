
package steffen.partialTransformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import steffen.Constants;
import steffen.layer.Layer;

public class SplitIntoSimplierFiles {
	
	private static boolean	fileLog	= false;
	
	public static void main(String[] args) throws Exception {
		String fileSource = "bawu boundary p0.05.xml";
		Layer layer = Layer.Federal;
		
		SplitIntoSimplierFiles.splitThisFile(fileSource, layer);
	}
	
	public static void splitThisFile(String fileSource, Layer layer) throws IOException {
		System.out.println("Begin splitting " + layer.name + " file...");
		
		int splitCount = 20;
		if (layer.nodeLayer) {
			splitCount = 250;
		}
		
		String fileTarget = Constants.pathToExternXMLs + fileSource.replaceFirst(".xml", " splitted");
		fileSource = Constants.pathToExternXMLs + fileSource;
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileSource)));
		Hashtable<Integer, Boolean> nodes = null;
		
		int file = 1;
		String line = null;
		if (layer.nodeLayer) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileTarget + file + ".xml")));
			writer.write("<?xml version='1.0' encoding='UTF-8'?>" + Constants.lineSeparator);
			writer.write("<osm>" + Constants.lineSeparator);
			int nodeCount = 0;
			while (reader.ready()) {
				line = reader.readLine();
				if (line.indexOf("<node") >= 0) {
					nodeCount++;
					writer.write(line + Constants.lineSeparator);
					if (line.indexOf("/>") < 0) {
						while (line.indexOf("</node") < 0) {
							line = reader.readLine();
							writer.write(line + Constants.lineSeparator);
						}
					}
					if (nodeCount >= splitCount) {
						writer.write("</osm>");
						writer.close();
						nodeCount = 0;
						if (fileLog) {
							System.out.println("File " + file);
						}
						file++;
						writer = new BufferedWriter(new FileWriter(new File(fileTarget + file + ".xml")));
						writer.write("<?xml version='1.0' encoding='UTF-8'?>" + Constants.lineSeparator);
						writer.write("<osm>" + Constants.lineSeparator);
					}
				}
			}
			writer.write("</osm>");
			writer.close();
		} else {
			nodes = new Hashtable<Integer, Boolean>();
			int ways = 0;
			while (reader.ready()) {
				line = reader.readLine();
				if (line.indexOf("<way") >= 0) {
					if (line.indexOf("/>") < 0) {
						ways++;
						do {
							line = reader.readLine();
							if (line.indexOf("<nd") >= 0) {
								String str = "ref=\"";
								int begin = line.indexOf(str);
								if (begin >= 0) {
									int end = line.indexOf("\"", begin + str.length());
									Integer ref = Integer.valueOf(line.substring(begin + str.length(), end));
									nodes.put(ref, new Boolean(true));
								}
							}
						} while (line.indexOf("</way") < 0);
						if (ways >= splitCount) {
							SplitIntoSimplierFiles.writeIntoSplitFile(fileSource, fileTarget, file, splitCount, nodes);
							ways = 0;
							if (fileLog) {
								System.out.println("File " + file);
							}
							file++;
							nodes.clear();
						}
					}
				}
			}
			SplitIntoSimplierFiles.writeIntoSplitFile(fileSource, fileTarget, file, splitCount, nodes);
			nodes.clear();
		}
		reader.close();
		
		System.out.println(file + " files");
		System.out.println("Done");
	}
	
	private static void writeIntoSplitFile(String fileSource, String fileTarget, int file, int wayCount,
			Hashtable<Integer, Boolean> nodes) throws IOException {
		BufferedReader reader2 = new BufferedReader(new FileReader(new File(fileSource)));
		File myFile = new File(fileTarget + file + ".xml");
		BufferedWriter writer = new BufferedWriter(new FileWriter(myFile));
		writer.write("<?xml version='1.0' encoding='UTF-8'?>" + Constants.lineSeparator);
		writer.write("<osm>" + Constants.lineSeparator);
		String line2 = null;
		int ways2 = 0;
		boolean reading = true;
		while (reader2.ready() && reading) {
			line2 = reader2.readLine();
			if (line2.indexOf("<node") >= 0) {
				String str = "id=\"";
				int begin = line2.indexOf(str);
				if (begin >= 0) {
					int end = line2.indexOf("\"", begin + str.length());
					Integer id = Integer.valueOf(line2.substring(begin + str.length(), end));
					if (nodes.containsKey(id)) {
						writer.write(line2 + Constants.lineSeparator);
						if (line2.indexOf("/>") < 0) {
							do {
								line2 = reader2.readLine();
								writer.write(line2 + Constants.lineSeparator);
							} while (line2.indexOf("</node") >= 0);
						}
					}
				}
			} else {
				if (line2.indexOf("<way") >= 0) {
					if (line2.indexOf("/>") < 0) {
						if (ways2 >= (file - 1) * wayCount) {
							ways2++;
							writer.write(line2 + Constants.lineSeparator);
							do {
								line2 = reader2.readLine();
								writer.write(line2 + Constants.lineSeparator);
							} while (line2.indexOf("</way") < 0);
							if (ways2 >= file * wayCount) {
								reading = false;
							}
						} else {
							ways2++;
							do {
								line2 = reader2.readLine();
							} while (line2.indexOf("</way") < 0);
						}
					}
				}
			}
		}
		writer.write("</osm>");
		reader2.close();
		writer.close();
	}
}
