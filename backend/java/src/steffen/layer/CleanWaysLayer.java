
package steffen.layer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import steffen.Constants;

public class CleanWaysLayer {
	
	private static Layer		layer				= Layer.Counties;
	private static String	myFileSource	= "bawu counties.xml";
	private static String[]	tagsToKeep		= null;
	
	public static void cleanLayer(String fileSource, Layer layer, boolean deleteOldFile) throws IOException {
		System.out.println("Begin cleaning layer " + layer.name + "...");
		
		// Begin layered cleaner configuration
		boolean noNodeTags = false;
		boolean optimizeRefs = false;
		if (layer == Layer.Federal || layer == Layer.Counties) {
			noNodeTags = true;
			CleanWaysLayer.tagsToKeep = new String[3];
			CleanWaysLayer.tagsToKeep[0] = "k=\"admin_level\"";
			CleanWaysLayer.tagsToKeep[1] = "k=\"boundary\"";
			CleanWaysLayer.tagsToKeep[2] = "k=\"border_type\"";
		} else {
			if (layer == Layer.Rivers || layer == Layer.Canals) {
				noNodeTags = true;
				CleanWaysLayer.tagsToKeep = new String[3];
				CleanWaysLayer.tagsToKeep[0] = "k=\"natural\"";
				CleanWaysLayer.tagsToKeep[1] = "k=\"waterway\"";
				CleanWaysLayer.tagsToKeep[2] = "k=\"name\"";
			} else {
				if (layer == Layer.AllLakes || layer == Layer.NamedLakes) {
					noNodeTags = true;
					CleanWaysLayer.tagsToKeep = new String[3];
					CleanWaysLayer.tagsToKeep[0] = "k=\"natural\"";
					CleanWaysLayer.tagsToKeep[1] = "k=\"water\"";
					CleanWaysLayer.tagsToKeep[2] = "k=\"name\"";
				} else {
					if (layer == Layer.Motorways || layer == Layer.Primaries || layer == Layer.Secondaries
							|| layer == Layer.Tertiaries) {
						noNodeTags = true;
						optimizeRefs = true;
						CleanWaysLayer.tagsToKeep = new String[6];
						CleanWaysLayer.tagsToKeep[0] = "k=\"highway\"";
						CleanWaysLayer.tagsToKeep[1] = "k=\"name\"";
						CleanWaysLayer.tagsToKeep[2] = "k=\"int_ref\"";
						CleanWaysLayer.tagsToKeep[3] = "k=\"ref\"";
						CleanWaysLayer.tagsToKeep[4] = "k=\"lanes\"";
						CleanWaysLayer.tagsToKeep[5] = "k=\"oneway\"";
					} else {
						System.exit(1);
					}
				}
			}
		}
		// End layered cleaner configuration
		
		File oldFile = new File(Constants.pathToExternXMLs + fileSource);
		BufferedReader reader = new BufferedReader(new FileReader(oldFile));
		File tempFile = new File("ways_cleaner.temp");
		tempFile.deleteOnExit();
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				if (noNodeTags) {
					if (line.indexOf("/>") < 0) {
						line = line.replaceFirst(">", "/>");
						writer.write(line + Constants.lineSeparator);
						do {
							line = reader.readLine();
						} while (line.indexOf("</node") < 0);
					} else {
						writer.write(line + Constants.lineSeparator);
					}
				} else {
					writer.write(line + Constants.lineSeparator);
				}
			} else {
				if (line.indexOf("<tag") >= 0) {
					if (CleanWaysLayer.keepTag(line)) {
						if (optimizeRefs) {
							if (line.indexOf("k=\"ref\"") >= 0) {
								int refbegin = line.indexOf("v=\"");
								if (refbegin >= 0) {
									int refend = line.indexOf("\"", refbegin + 3);
									String ref1 = line.substring(refbegin, refend + 3);
									String ref2 = ref1.replaceAll(" ", "");
									line = line.replaceFirst(ref1, ref2);
								}
							} else {
								if (line.indexOf("k=\"int_ref\"") >= 0) {
									int refbegin = line.indexOf("v=\"");
									if (refbegin >= 0) {
										int refend = line.indexOf("\"", refbegin + 3);
										String ref1 = line.substring(refbegin, refend + 3);
										String ref2 = ref1.replaceAll(" ", "");
										line = line.replaceFirst(ref1, ref2);
									}
								}
							}
						}
						writer.write(line + Constants.lineSeparator);
					}
				} else {
					writer.write(line + Constants.lineSeparator);
				}
			}
		}
		reader.close();
		writer.close();
		
		System.out.println("Step 1");
		
		reader = new BufferedReader(new FileReader(tempFile));
		File newFile = new File(Constants.pathToExternXMLs + fileSource.replaceFirst(".xml", "2.xml"));
		writer = new BufferedWriter(new FileWriter(newFile));
		
		line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				if (line.indexOf("/>") >= 0) {
					writer.write(line + Constants.lineSeparator);
				} else {
					String line2 = reader.readLine();
					if (line2.indexOf("</node") >= 0) {
						line = line.replaceFirst(">", "/>");
						writer.write(line + Constants.lineSeparator);
					} else {
						line += line2;
						do {
							line2 = reader.readLine();
						} while (line2.indexOf("</node") < 0);
						writer.write(line + Constants.lineSeparator);
					}
				}
			} else {
				writer.write(line + Constants.lineSeparator);
			}
		}
		reader.close();
		writer.close();
		
		if (deleteOldFile) {
			oldFile.delete();
			newFile.renameTo(oldFile);
		}
		
		System.out.println("Done");
	}
	
	public static void main(String[] args) throws Exception {
		CleanWaysLayer.cleanLayer(myFileSource, layer, false);
	}
	
	private static boolean keepTag(String input) {
		for (String tag : CleanWaysLayer.tagsToKeep) {
			if (input.indexOf(tag) >= 0) {
				return true;
			}
		}
		return false;
	}
}
