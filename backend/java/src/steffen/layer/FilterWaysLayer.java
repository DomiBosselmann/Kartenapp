
package steffen.layer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import steffen.Constants;

public class FilterWaysLayer {
	
	private static String	myFileSource	= "bawu.xml";
	private static Layer		myLayer			= Layer.Tertiaries;
	
	public static void filterWaysLayer(String fileSource, Layer layer, boolean deleteOldFile) throws IOException {
		System.out.println("Begin filtering layer " + layer.name + "...");
		
		long ways = 0L;
		String[] neededKeys = null;
		String[] neededValues = null;
		
		// Begin layered filter configuration
		boolean permitEnabled = false;
		String[] permittedKeys = null;
		String[] permittedValues = null;
		boolean admin_level = false;
		Integer admin_level_min = null;
		Integer admin_level_max = null;
		if (layer == Layer.Federal || layer == Layer.Counties) {
			neededKeys = new String[1];
			neededValues = new String[neededKeys.length];
			neededKeys[0] = "k=\"boundary\"";
			neededValues[0] = "v=\"administrative\"";
			// admin_level gibt Grenzart an:
			// 2: Staaten
			// 4: Bundesländer
			// 5: Regierungsbezirke
			// 6: Kreise
			// 7-9: Ortsgrenzen
			// 9-11: Ortsteile
			admin_level = true;
			switch (layer) {
				case Federal: {
					admin_level_min = 0;
					admin_level_max = 4;
					break;
				}
				case Counties: {
					admin_level_min = 5;
					admin_level_max = 6;
					break;
				}
			}
		} else {
			if (layer == Layer.Rivers || layer == Layer.Canals) {
				neededKeys = new String[2];
				neededValues = new String[neededKeys.length];
				neededKeys[0] = "k=\"waterway\"";
				neededKeys[1] = "k=\"name\"";
				switch (layer) {
					case Rivers: {
						neededValues[0] = "v=\"river\"";
						break;
					}
					case Canals: {
						neededValues[0] = "v=\"canal\"";
						break;
					}
				}
				neededValues[1] = "";
			} else {
				if (layer == Layer.UnnamedLakes || layer == Layer.NamedLakes) {
					permitEnabled = true;
					switch (layer) {
						case NamedLakes: {
							neededKeys = new String[2];
							neededValues = new String[neededKeys.length];
							neededKeys[1] = "k=\"name\"";
							neededValues[1] = "";
							permittedKeys = new String[2];
							permittedValues = new String[permittedKeys.length];
							break;
						}
						case UnnamedLakes: {
							neededKeys = new String[1];
							neededValues = new String[neededKeys.length];
							permittedKeys = new String[3];
							permittedValues = new String[permittedKeys.length];
							permittedKeys[2] = "k=\"name\"";
							permittedValues[2] = "";
							break;
						}
					}
					neededKeys[0] = "k=\"natural\"";
					neededValues[0] = "v=\"water\"";
					permittedKeys[0] = "k=\"golf\"";
					permittedKeys[1] = "k=\"waterway\"";
					permittedValues[0] = "";
					permittedValues[1] = "";
				} else {
					if (layer == Layer.Motorways || layer == Layer.Primaries || layer == Layer.Secondaries
							|| layer == Layer.Tertiaries) {
						neededKeys = new String[2];
						neededValues = new String[neededKeys.length];
						neededKeys[0] = "k=\"highway\"";
						neededKeys[1] = "k=\"ref\"";
						switch (layer) {
							case Motorways: {
								neededValues[0] = "v=\"motorway\"";
								break;
							}
							case Primaries: {
								neededValues[0] = "v=\"primary\"";
								break;
							}
							case Secondaries: {
								neededValues[0] = "v=\"secondary\"";
								break;
							}
							case Tertiaries: {
								neededValues[0] = "v=\"tertiary\"";
								break;
							}
						}
						neededValues[1] = "";
					} else {
						System.exit(1);
					}
				}
			}
		}
		String fileTargetName = layer.name;
		// End layered filter configuration
		
		Hashtable<Integer, Integer> nodeIDs = new Hashtable<Integer, Integer>();
		
		File sourceFile = new File(Constants.pathToExternXMLs + fileSource);
		BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
		File tempFile = new File("ways_filter.temp");
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		tempFile.deleteOnExit();
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<way") >= 0) {
				boolean[] needed = null;
				if (layer == Layer.Federal || layer == Layer.Counties) {
					needed = new boolean[neededKeys.length + 1];
				} else {
					needed = new boolean[neededKeys.length];
				}
				boolean[] permitted = null;
				if (permitEnabled) {
					permitted = new boolean[permittedKeys.length];
				}
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
							}
							i++;
						}
						if (permitEnabled) {
							i = 0;
							for (String permittedTag : permittedKeys) {
								if (line.indexOf(permittedTag) >= 0) {
									if (line.indexOf(permittedValues[i]) >= 0) {
										permitted[i] = true;
									}
								}
								i++;
							}
						}
						if (admin_level) {
							if (line.indexOf("k=\"admin_level\"") >= 0) {
								int levelbegin = line.indexOf("v=\"");
								if (levelbegin >= 0) {
									int levelend = line.indexOf("\"", levelbegin + 3);
									try {
										int level = Integer.valueOf(line.substring(levelbegin + 3, levelend));
										if (level >= admin_level_min && level <= admin_level_max) {
											needed[needed.length - 1] = true;
										}
									} catch (NumberFormatException ex) {
										
									}
								}
							}
						}
					}
					zeile += line + Constants.lineSeparator;
				} while (line.indexOf("</way") < 0);
				boolean needed1 = true;
				boolean needed2 = true;
				for (boolean need : needed) {
					if (!need) {
						needed1 = false;
					}
				}
				if (permitEnabled) {
					for (boolean permit : permitted) {
						if (permit) {
							needed2 = false;
						}
					}
				}
				if (needed1 && needed2) {
					String str = "ref=\"";
					int refbegin = zeile.indexOf(str);
					while (refbegin >= 0) {
						int refend = zeile.indexOf("\"", refbegin + str.length());
						Integer ref = Integer.valueOf(zeile.substring(refbegin + str.length(), refend));
						nodeIDs.put(ref, ref);
						refbegin = zeile.indexOf(str, refend);
					}
					writer.write(zeile);
					ways++;
				}
			}
		}
		reader.close();
		writer.close();
		
		System.out.println("Ways: " + ways);
		System.out.println("Nodes: " + nodeIDs.size());
		System.out.println("Step 1");
		
		reader = new BufferedReader(new FileReader(sourceFile));
		String fileTarget = fileSource.replaceFirst(".xml", " " + fileTargetName + ".xml");
		File targetFile = new File(Constants.pathToExternXMLs + fileTarget);
		writer = new BufferedWriter(new FileWriter(targetFile));
		
		writer.write("<?xml version='1.0' encoding='UTF-8'?>" + Constants.lineSeparator);
		writer.write("<osm>" + Constants.lineSeparator);
		line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				String str = "id=\"";
				int idbegin = line.indexOf(str);
				if (idbegin >= 0) {
					int idend = line.indexOf("\"", idbegin + str.length());
					if (nodeIDs.containsKey(Integer.valueOf(line.substring(idbegin + str.length(), idend)))) {
						writer.write(line + Constants.lineSeparator);
						if (line.indexOf("/>") < 0) {
							do {
								if (reader.ready()) {
									line = reader.readLine();
									writer.write(line + Constants.lineSeparator);
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
		reader.close();
		
		System.out.println("Step 2");
		
		reader = new BufferedReader(new FileReader(tempFile));
		
		line = null;
		while (reader.ready()) {
			line = reader.readLine();
			writer.write(line + Constants.lineSeparator);
		}
		writer.write("</osm>");
		
		reader.close();
		writer.close();
		
		System.out.println("Done");
		
		CleanWaysLayer.cleanLayer(fileTarget, layer, deleteOldFile);
	}
	
	public static void main(String[] args) throws IOException {
		FilterWaysLayer.filterWaysLayer(FilterWaysLayer.myFileSource, FilterWaysLayer.myLayer, true);
	}
}
