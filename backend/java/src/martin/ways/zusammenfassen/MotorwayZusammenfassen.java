
package martin.ways.zusammenfassen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import steffen.Constants;

public class MotorwayZusammenfassen {
	
	public static void main(String[] args) throws IOException {
		
		String file = Constants.pathToExternXMLs + "bawu counties.xml";
		File myDir = new File(Constants.pathToExternXMLs);
		String outputFile = "bawu_motorways_zusammen.xml";
		
		String nextLine;
		int anzahlMotorways = 0;
		Hashtable<String, Integer> motorways = new Hashtable<String, Integer>();
		int startIndex;
		String motorway;
		ArrayList<String> motorwaysName = new ArrayList<String>();
		String ndID;
		ArrayList<String> newWay = new ArrayList<String>();
		List<String>[] ways;
		int anzahlWays;
		boolean waysAdded;
		String startID;
		String endID;
		String startID2;
		String endID2;
		int neuAnzahl = 0;
		int alteAnzahl = 0;
		
		myDir.mkdir();
		File data = new File(myDir, "help.osm");
		BufferedReader br = new BufferedReader(new FileReader(file));
		FileWriter writer = new FileWriter(data);
		
		writer.write(br.readLine());
		writer.write(System.getProperty("line.separator"));
		writer.write(br.readLine());
		writer.write(System.getProperty("line.separator"));
		writer.flush();
		
		while (((nextLine = br.readLine()) != null)) {
			if ((nextLine.trim().startsWith("<tag")) && (nextLine.contains("\"ref"))) {
				startIndex = nextLine.indexOf(" v=") + 4;
				motorway = nextLine.substring(startIndex, nextLine.indexOf("\"", startIndex));
				if (motorways.containsKey(motorway)) {
					motorways.put(motorway, motorways.get(motorway) + 1);
				} else {
					motorways.put(motorway, 1);
					motorwaysName.add(motorway);
				}
				anzahlMotorways++;
				alteAnzahl++;
			} else
				if (nextLine.trim().startsWith("<node")) {
					writer.write(nextLine);
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
		}
		
		for (int l = 0; l < motorwaysName.size(); l++) {
			
			ways = new List[motorways.get(motorwaysName.get(l))];
			anzahlWays = 0;
			
			br = new BufferedReader(new FileReader(file));
			while (((nextLine = br.readLine()) != null)) {
				if (nextLine.trim().startsWith("<nd")) {
					startIndex = nextLine.indexOf(" ref=") + 6;
					ndID = nextLine.substring(startIndex, nextLine.indexOf("\"", startIndex));
					newWay.add(ndID);
				} else
					if ((nextLine.trim().startsWith("<tag")) && (nextLine.contains("\"ref"))) {
						startIndex = nextLine.indexOf(" v=") + 4;
						motorway = nextLine.substring(startIndex, nextLine.indexOf("\"", startIndex));
						if (motorway.equalsIgnoreCase(motorwaysName.get(l))) {
							ways[anzahlWays] = newWay;
							anzahlWays++;
						}
						newWay = new ArrayList<String>();
					}
			}
			
			for (int i = 0; i < ways.length; i++) {
				if (!(ways[i] == null)) {
					for (int j = 0; j < ways.length; j++) {
						if (!(ways[j] == null)) {
							if (i != j) {
								waysAdded = false;
								startID = ways[i].get(0);
								endID = ways[i].get(ways[i].size() - 1);
								
								startID2 = ways[j].get(0);
								endID2 = ways[j].get(ways[j].size() - 1);
								
								newWay = new ArrayList<String>();
								
								if ((startID.equalsIgnoreCase(startID2) && (endID.equalsIgnoreCase(endID2)))) {
									for (int k = 0; k < ways[j].size(); k++) {
										newWay.add(ways[j].get(k));
									}
									for (int k = ways[i].size() - 1; k > 0; k--) {
										newWay.add(ways[i].get(k));
									}
									waysAdded = true;
								} else
									if ((startID.equalsIgnoreCase(endID2)) && (endID.equalsIgnoreCase(startID2))) {
										for (int k = 0; k < ways[j].size(); k++) {
											newWay.add(ways[j].get(k));
										}
										for (int k = 1; k < ways[i].size(); k++) {
											newWay.add(ways[i].get(k));
										}
										waysAdded = true;
									} else
										if (startID.equalsIgnoreCase(startID2)) {
											for (int k = ways[j].size() - 1; k > 0; k--) {
												newWay.add(ways[j].get(k));
											}
											for (int k = 0; k < ways[i].size(); k++) {
												newWay.add(ways[i].get(k));
											}
											waysAdded = true;
										} else
											if (startID.equalsIgnoreCase(endID2)) {
												for (int k = 0; k < ways[j].size(); k++) {
													newWay.add(ways[j].get(k));
												}
												for (int k = 1; k < ways[i].size(); k++) {
													newWay.add(ways[i].get(k));
												}
												waysAdded = true;
											} else
												if (endID.equalsIgnoreCase(endID2)) {
													for (int k = 0; k < ways[i].size(); k++) {
														newWay.add(ways[i].get(k));
													}
													for (int k = (ways[j].size() - 2); k > (-1); k--) {
														newWay.add(ways[j].get(k));
													}
													waysAdded = true;
												} else
													if (endID.equalsIgnoreCase(startID2)) {
														for (int k = 0; k < ways[i].size(); k++) {
															newWay.add(ways[i].get(k));
														}
														for (int k = 1; k < ways[j].size(); k++) {
															newWay.add(ways[j].get(k));
														}
														waysAdded = true;
													}
								if (waysAdded) {
									ways[i] = newWay;
									ways[j] = null;
									if (i != 0) {
										i--;
									}
									break;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < ways.length; i++) {
				if (!(ways[i] == null)) {
					writer.write("<way>");
					writer.write(System.getProperty("line.separator"));
					for (int j = 0; j < ways[i].size(); j++) {
						writer.write("<nd ref=\"" + ways[i].get(j) + "\"/>");
						writer.write(System.getProperty("line.separator"));
					}
					writer.write("<tag k=\"ref\" v=\"" + motorways.get(motorwaysName.get(l)) + "\"/>");
					writer.write("</way>");
					writer.write(System.getProperty("line.separator"));
					writer.flush();
					neuAnzahl++;
				}
			}
			System.out.println((int) (l + 1) + " von " + motorways.size() + "fertig");
			
		}
		writer.write("</osm>");
		writer.flush();
		writer.close();
		System.out.println(alteAnzahl + " auf " + neuAnzahl + " veringert");
		
	}
}
