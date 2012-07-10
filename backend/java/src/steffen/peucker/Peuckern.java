
package steffen.peucker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import steffen.FilePath;

public class Peuckern {
	private static String								fileSource			= "bawu motorways.xml";
	private static Hashtable<Integer, Node>		nodes					= new Hashtable<Integer, Node>();
	private static Hashtable<Integer, Boolean>	neededNodes			= new Hashtable<Integer, Boolean>();
	private static double								peuckerDistance	= 0.05;
	
	public static void main(String[] args) throws IOException {
		Hashtable<Integer, Integer> wayPoints = new Hashtable<Integer, Integer>();
		DecimalFormat format = new DecimalFormat("0.##");
		String fileTarget = FilePath.path
				+ Peuckern.fileSource.replaceFirst(".xml", " p" + format.format(Peuckern.peuckerDistance) + ".xml");
		fileSource = FilePath.path + fileSource;
		BufferedReader reader = new BufferedReader(new FileReader(new File(Peuckern.fileSource)));
		
		// Save data of the nodes in Hashtables and check for needed nodes
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				int begin = line.indexOf("id=\"");
				if (begin >= 0) {
					int end = line.indexOf("\"", begin + 4);
					Integer id = Integer.valueOf(line.substring(begin + 4, end));
					begin = line.indexOf("lat=\"");
					if (begin >= 0) {
						end = line.indexOf("\"", begin + 5);
						Double lat = Double.valueOf(line.substring(begin + 5, end));
						begin = line.indexOf("lon=\"");
						if (begin >= 0) {
							end = line.indexOf("\"", begin + 5);
							Double lon = Double.valueOf(line.substring(begin + 5, end));
							Peuckern.nodes.put(id, new Node(lon, lat));
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
						line = reader.readLine();
						while (line.indexOf("</way") < 0) {
							if (line.indexOf("<nd") >= 0) {
								String str = "ref=\"";
								int begin = line.indexOf(str);
								if (begin >= 0) {
									int end = line.indexOf("\"", begin + str.length());
									wayPoints.put(Integer.valueOf(line.substring(begin + str.length(), end)), new Integer(0));
								}
							}
							line = reader.readLine();
						}
						if (wayPoints.size() > 0) {
							Integer[] refs2 = new Integer[wayPoints.size()];
							Integer[] refs = wayPoints.keySet().toArray(refs2);
							refs2 = null;
							Peuckern.markRelevantNodes(refs, 0, refs.length - 1);
						}
						wayPoints.clear();
					}
				}
			}
		}
		reader.close();
		Peuckern.nodes.clear();
		System.out.println("Nodes: " + neededNodes.size());
		
		System.out.println("Step 1");
		
		// Write only needed nodes in the new file
		reader = new BufferedReader(new FileReader(new File(Peuckern.fileSource)));
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
					if (Peuckern.neededNodes.containsKey(id)) {
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
								if (Peuckern.neededNodes.containsKey(ref)) {
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
		Peuckern.neededNodes.clear();
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
	
	private static void markRelevantNodes(Integer[] refs, int begin, int end) {
		switch (end - begin) {
			case 0: {
				Peuckern.neededNodes.put(refs[begin], new Boolean(true));
				break;
			}
			case 1: {
				Peuckern.neededNodes.put(refs[begin], new Boolean(true));
				Peuckern.neededNodes.put(refs[end], new Boolean(true));
				break;
			}
			default: {
				Peuckern.neededNodes.put(refs[begin], new Boolean(true));
				Peuckern.neededNodes.put(refs[end], new Boolean(true));
				Node beginNode = Peuckern.nodes.get(refs[begin]);
				Node endNode = Peuckern.nodes.get(refs[end]);
				Line iLine = new Line(beginNode.lon, beginNode.lat, endNode.lon, endNode.lat);
				int most_distant = -1;
				double largest_distance = -1.0;
				double distance;
				for (int i = begin + 1; i < end - 1; i++) {
					Node node = Peuckern.nodes.get(refs[i]);
					distance = iLine.getDistanceOfPoint(node.lon, node.lat);
					if (largest_distance < distance) {
						largest_distance = distance;
						most_distant = i;
					}
				}
				if (largest_distance > Peuckern.peuckerDistance) {
					Peuckern.markRelevantNodes(refs, begin, most_distant);
					Peuckern.markRelevantNodes(refs, most_distant, end);
				} else {
					// Peuckern.neededNodes.put(refs[begin], new Boolean(true));
					// Peuckern.neededNodes.put(refs[end], new Boolean(true));
				}
				break;
			}
		}
	}
}
