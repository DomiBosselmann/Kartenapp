
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

public class SplitIntoSimplierFiles {
	
	private static String	fileSource	= "bawu lakes p0.05.xml";
	private static int		wayCount		= 150;
	
	public static void main(String[] args) throws IOException {
		String fileTarget = FilePath.path + fileSource.replaceFirst(".xml", " splitted ");
		fileSource = FilePath.path + fileSource;
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileSource)));
		Hashtable<Integer, Boolean> nodes = new Hashtable<Integer, Boolean>();
		
		// 1 Gather x times the information of the next "wayCount" ways in the hashtable
		String line = null;
		int ways = 0;
		int file = 1;
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
					if (ways >= wayCount) {
						BufferedReader reader2 = new BufferedReader(new FileReader(new File(fileSource)));
						File myFile = new File(fileTarget + file + ".xml");
						FileWriter writer = new FileWriter(myFile);
						writer.write("<?xml version='1.0' encoding='UTF-8'?>\n");
						writer.write("<?xml-stylesheet type=\"text/xml\" href=\"transform.xsl\"?>\n");
						writer.write("<osm>\n");
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
										writer.write(line2 + "\n");
										if (line2.indexOf("/>") < 0) {
											do {
												line2 = reader2.readLine();
												writer.write(line2 + "\n");
											} while (line2.indexOf("</node") >= 0);
										}
									}
								}
							} else {
								if (line2.indexOf("<way") >= 0) {
									if (line2.indexOf("/>") < 0) {
										if (ways2 >= (file - 1) * wayCount) {
											ways2++;
											writer.write(line2 + "\n");
											do {
												line2 = reader2.readLine();
												writer.write(line2 + "\n");
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
						ways = 0;
						file++;
						nodes.clear();
					}
				}
			}
		}
		reader.close();
		
		System.out.println("Done");
	}
}
