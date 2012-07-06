
package steffen.peucker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;

public class Peuckern {
	private static String								fileSource			= "xml/bawu boundary.xml";
	private static Hashtable<Integer, Double>		latitudes			= new Hashtable<Integer, Double>();
	private static Hashtable<Integer, Double>		longitudes			= new Hashtable<Integer, Double>();
	private static Hashtable<Integer, Boolean>	neededNodes			= new Hashtable<Integer, Boolean>();
	private static Hashtable<Integer, Integer>	wayPoints			= new Hashtable<Integer, Integer>();
	private static int									logint				= 0;
	private static double								peuckerDistance	= 50.0;
	
	public static void main(String[] args) throws IOException {
		DecimalFormat format = new DecimalFormat("0.#");
		String fileTarget = Peuckern.fileSource.replaceFirst(".xml", " p" + format.format(Peuckern.peuckerDistance) + ".xml");
		BufferedReader reader = new BufferedReader(new FileReader(new File(Peuckern.fileSource)));
		
		// Save data of the nodes in Hashtables and check for needed nodes
		String line = null;
		int begin;
		int end;
		int l = 0;
		while (reader.ready()) {
			line = reader.readLine();
			l++;
			if (l > Peuckern.logint) {
				System.out.println(l);
				Peuckern.logint += 500;
			}
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
							Peuckern.latitudes.put(id, lat);
							Peuckern.longitudes.put(id, lon);
						}
					}
				}
				if (line.indexOf("/>") < 0) {
					do {
						line = reader.readLine();
						l++;
					} while (line.indexOf("</node") < 0);
				}
			} else {
				if (line.indexOf("<way") >= 0) {
					if (line.indexOf("/>") < 0) {
						line = reader.readLine();
						l++;
						while (line.indexOf("</way") < 0) {
							if (line.indexOf("<nd") >= 0) {
								begin = line.indexOf("ref=\"");
								if (begin >= 0) {
									end = line.indexOf("\"", begin + 5);
									Peuckern.wayPoints.put(Integer.valueOf(line.substring(begin + 5, end)), 0);
								}
							}
							line = reader.readLine();
							l++;
						}
						if (Peuckern.wayPoints.size() > 0) {
							Integer[] refs2 = new Integer[Peuckern.wayPoints.size()];
							Integer[] refs = Peuckern.wayPoints.keySet().toArray(refs2);
							refs2 = null;
							Peuckern.markRelevantNodes(refs, 0, refs.length - 1);
						}
					}
				}
			}
		}
		reader.close();
		Peuckern.latitudes.clear();
		Peuckern.longitudes.clear();
		
		System.out.println("Step 1");
		
		// Write only needed nodes in the new file
		reader = new BufferedReader(new FileReader(new File(Peuckern.fileSource)));
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
					if (Peuckern.neededNodes.containsKey(id)) {
						writer.write(line + System.getProperty("line.separator", "\n"));
					}
				}
				if (line.indexOf("/>") < 0) {
					do {
						line = reader.readLine();
						l++;
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
				Line iLine = new Line(Peuckern.longitudes.get(refs[begin]), Peuckern.latitudes.get(refs[begin]),
						Peuckern.longitudes.get(refs[end]), Peuckern.latitudes.get(refs[end]));
				int most_distant = -1;
				double largest_distance = -1.0;
				double distance;
				for (int i = begin + 1; i < end - 1; i++) {
					distance = iLine.getDistanceOfPoint(Peuckern.longitudes.get(refs[i]), Peuckern.latitudes.get(refs[i]));
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
