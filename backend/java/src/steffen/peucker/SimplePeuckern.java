
package steffen.peucker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import steffen.Constants;

public class SimplePeuckern {
	private static String	fileSource			= "bawu boundary.xml";
	private static double	peuckerDistance	= 2.0;
	
	public static void main(String[] args) throws IOException {
		DecimalFormat format = new DecimalFormat("0.#");
		String fileTarget = Constants.pathToExternXMLs
				+ SimplePeuckern.fileSource.replaceFirst(".xml", " sp" + format.format(SimplePeuckern.peuckerDistance) + ".xml");
		fileSource = Constants.pathToExternXMLs + fileSource;
		
		Hashtable<Integer, Node> nodes = new Hashtable<Integer, Node>();
		Hashtable<Integer, Boolean> neededNodes = new Hashtable<Integer, Boolean>();
		Hashtable<Integer, Integer> wayPoints = new Hashtable<Integer, Integer>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(SimplePeuckern.fileSource)));
		
		// Save data of the nodes in Hashtables and check for needed nodes
		String line = null;
		int begin;
		int end;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				begin = line.indexOf("id=\"");
				if (begin >= 0) {
					end = line.indexOf("\"", begin + 4);
					Integer id = Integer.valueOf(line.substring(begin + 4, end));
					begin = line.indexOf("lat=\"");
					if (begin >= 0) {
						end = line.indexOf("\"", begin + 5);
						Double lat = Double.valueOf(line.substring(begin + 5, end));
						begin = line.indexOf("lon=\"");
						if (begin >= 0) {
							end = line.indexOf("\"", begin + 5);
							Double lon = Double.valueOf(line.substring(begin + 5, end));
							nodes.put(id, new Node(lon, lat));
						}
					}
				}
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
								begin = line.indexOf("ref=\"");
								if (begin >= 0) {
									end = line.indexOf("\"", begin + 5);
									wayPoints.put(Integer.valueOf(line.substring(begin + 5, end)), 0);
								}
							}
						} while (line.indexOf("</way") < 0);
						if (wayPoints.size() > 0) {
							Integer[] refs2 = new Integer[wayPoints.size()];
							Integer[] refs = wayPoints.keySet().toArray(refs2);
							refs2 = null;
							neededNodes.put(refs[0], new Boolean(true));
							neededNodes.put(refs[refs.length - 1], new Boolean(true));
							for (int i = 1; i < refs.length - 1; i++) {
								Node lastNode = nodes.get(refs[i - 1]);
								Node node = nodes.get(refs[i]);
								Node nextNode = nodes.get(refs[i + 1]);
								if (SimplePeuckern.peuckerDistance < SimplePeuckern.getDistanceToLine(node.lon, node.lat, lastNode.lon,
										lastNode.lat, nextNode.lon, nextNode.lat)) {
									neededNodes.put(refs[i], new Boolean(true));
								}
							}
							refs = null;
						}
						wayPoints.clear();
					}
				}
			}
		}
		
		reader.close();
		nodes.clear();
		System.out.println("Nodes: " + neededNodes.size());
		
		System.out.println("Step 1");
		
		// Write only needed nodes in the new file
		reader = new BufferedReader(new FileReader(new File(SimplePeuckern.fileSource)));
		FileWriter writer = new FileWriter(new File(fileTarget));
		String zeile = null;
		int ndcount;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				begin = line.indexOf("id=\"");
				if (begin >= 0) {
					end = line.indexOf("\"", begin + 4);
					Integer id = Integer.valueOf(line.substring(begin + 4, end));
					if (neededNodes.containsKey(id)) {
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
							begin = line.indexOf("ref=\"");
							if (begin >= 0) {
								end = line.indexOf("\"", begin + 5);
								Integer ref = Integer.valueOf(line.substring(begin + 5, end));
								if (neededNodes.containsKey(ref)) {
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
		neededNodes.clear();
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
	
	private static Double getDistanceToLine(Double lon, Double lat, Double lon1, Double lat1, Double lon2, Double lat2) {
		Double a = Math.sqrt(Math.pow((lon - lon1) * Constants.xRatio, 2) + Math.pow((lat - lat1) * Constants.yRatio, 2));
		Double b = Math.sqrt(Math.pow((lon - lon2) * Constants.xRatio, 2) + Math.pow((lat - lat2) * Constants.yRatio, 2));
		Double c = Math.sqrt(Math.pow((lon1 - lon2) * Constants.xRatio, 2) + Math.pow((lat1 - lat2) * Constants.yRatio, 2));
		return Math.sqrt(2 * (Math.pow(a, 2) * Math.pow(b, 2) + Math.pow(b, 2) * Math.pow(c, 2) + Math.pow(c, 2) * Math.pow(a, 2))
				- (Math.pow(a, 4) + Math.pow(b, 4) + Math.pow(c, 4)))
				/ (2 * c);
	}
}
