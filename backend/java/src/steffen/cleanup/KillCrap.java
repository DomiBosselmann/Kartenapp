
package steffen.cleanup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class KillCrap {
	private static String	fileSource	= "xml/bawu.xml";
	private static String	fileTarget	= "xml/bawu2.xml";
	
	public static void main(String[] args) throws IOException {
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(KillCrap.fileSource)));
		FileWriter writer = new FileWriter(new File(KillCrap.fileTarget));
		
		// actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<relation") < 0) {
				if (line.indexOf("<tag") < 0) {
					if (line.indexOf("<osm") >= 0) {
						writer.write("<osm>\n");
					} else {
						line = KillCrap.deleteAttribute("user", line);
						line = KillCrap.deleteAttribute("uid", line);
						line = KillCrap.deleteAttribute("timestamp", line);
						line = KillCrap.deleteAttribute("visible", line);
						line = KillCrap.deleteAttribute("version", line);
						line = KillCrap.deleteAttribute("changeset", line);
						line = KillCrap.cleanLine(line);
						writer.write(line + "\n");
					}
				} else {
					// Tag checks
					if (line.indexOf("k=\"TMC") < 0) {
						if (line.indexOf("k=\"created_by\"") < 0) {
							line = KillCrap.cleanLine(line);
							writer.write(line + "\n");
						}
					}
				}
			} else {
				if (line.indexOf("/>") < 0) {
					do {
						line = reader.readLine();
					} while (line.indexOf("</relation") < 0);
				}
			}
		}
		
		// destroy
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
	
	private static String cleanLine(String input) {
		input = input.trim();
		while (input.indexOf("  ") >= 0) {
			input = input.replaceAll("  ", " ");
		}
		input = input.replaceAll(" >", ">");
		input = input.replaceAll(" />", "/>");
		return input;
	}
	
	private static String deleteAttribute(String attribute, String input) {
		attribute += "=\"";
		int first = input.indexOf(attribute);
		int second = input.indexOf("\"", first + attribute.length());
		if (first >= 0 && second >= first) {
			input = input.substring(0, first) + input.substring(second + 1, input.length());
		}
		return input;
	}
}
