
package steffen.peucker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

public class SimpliestPeuckern {
	private static String								fileSource	= "xml/bawu boundary.xml";
	private static Hashtable<Integer, Boolean>	neededNodes	= new Hashtable<Integer, Boolean>();
	private static Hashtable<Integer, Integer>	wayPoints	= new Hashtable<Integer, Integer>();
	
	public static void main(String[] args) throws IOException {
		String fileTarget = SimpliestPeuckern.fileSource.replaceFirst(".xml", " ssp.xml");
		BufferedReader reader = new BufferedReader(new FileReader(new File(SimpliestPeuckern.fileSource)));
		
		// Check for needed nodes in the ways
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				if (line.indexOf("/>") < 0) {
					do {
						line = reader.readLine();
					} while (line.indexOf("</node") < 0);
				}
			} else {
				if (line.indexOf("<way") >= 0) {
					if (line.indexOf("/>") < 0) {
						do {
							line = reader.readLine();
							if (line.indexOf("<nd") >= 0) {
								String str = "ref=\"";
								int begin = line.indexOf(str);
								if (begin >= 0) {
									int end = line.indexOf("\"", begin + str.length());
									SimpliestPeuckern.wayPoints.put(Integer.valueOf(line.substring(begin + str.length(), end)), new Integer(0));
								}
							}
						} while (line.indexOf("</way") < 0);
						if (SimpliestPeuckern.wayPoints.size() > 0) {
							Integer[] refs2 = new Integer[SimpliestPeuckern.wayPoints.size()];
							Integer[] refs = SimpliestPeuckern.wayPoints.keySet().toArray(refs2);
							refs2 = null;
							SimpliestPeuckern.neededNodes.put(refs[0], new Boolean(true));
							SimpliestPeuckern.neededNodes.put(refs[refs.length - 1], new Boolean(true));
							refs = null;
						}
						SimpliestPeuckern.wayPoints.clear();
					}
				}
			}
		}
		reader.close();
		System.out.println("Nodes: " + neededNodes.size());
		
		System.out.println("Step 1");
		
		// Write only needed nodes in the new file
		reader = new BufferedReader(new FileReader(new File(SimpliestPeuckern.fileSource)));
		FileWriter writer = new FileWriter(new File(fileTarget));
		String zeile = null;
		int ndcount;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				String str = "id=\"";
				int begin = line.indexOf(str);
				if (begin >= 0) {
					int end = line.indexOf("\"", begin + str.length());
					Integer id = Integer.valueOf(line.substring(begin + str.length(), end));
					if (SimpliestPeuckern.neededNodes.containsKey(id)) {
						writer.write(line + System.getProperty("line.separator", "\n"));
					}
				}
				if (line.indexOf("/>") < 0) {
					do {
						line = reader.readLine();
					} while (line.indexOf("</node") < 0);
				}
			} else {
				if (line.indexOf("<way") >= 0) {
					zeile = line + System.getProperty("line.separator", "\n");
					ndcount = 0;
					do {
						line = reader.readLine();
						if (line.indexOf("<nd") >= 0) {
							String str = "ref=\"";
							int begin = line.indexOf(str);
							if (begin >= 0) {
								int end = line.indexOf("\"", begin + str.length());
								Integer ref = Integer.valueOf(line.substring(begin + str.length(), end));
								if (SimpliestPeuckern.neededNodes.containsKey(ref)) {
									zeile += line + System.getProperty("line.separator", "\n");
									ndcount++;
								}
							}
						} else {
							zeile += line + System.getProperty("line.separator", "\n");
						}
					} while (line.indexOf("</way") < 0);
					if (ndcount >= 2) {
						writer.write(zeile);
					}
				} else {
					writer.write(line + System.getProperty("line.separator", "\n"));
				}
			}
		}
		SimpliestPeuckern.neededNodes.clear();
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
}
