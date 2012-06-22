
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class KillCrap {
	private static String	sourceFile	= "LayerGrenzen.xml";
	private static String	targetFile	= "LayerGrenzen2.xml";
	
	public static void main(String[] args) throws IOException {
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(KillCrap.sourceFile)));
		FileWriter writer = new FileWriter(new File(KillCrap.targetFile));
		
		// actions
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (!KillCrap.uselessNode(line)) {
				if (line.indexOf("<osm") >= 0) {
					writer.write("<osm>\n");
				} else {
					line = KillCrap.deleteAttribute("user", line);
					line = KillCrap.deleteAttribute("uid", line);
					line = KillCrap.deleteAttribute("timestamp", line);
					line = KillCrap.deleteAttribute("visible", line);
					line = KillCrap.deleteAttribute("version", line);
					line = KillCrap.deleteAttribute("changeset", line);
					line = line.trim();
					writer.write(line + "\n");
				}
			}
		}
		System.out.println(line);
		
		// destroy
		if (reader != null) {
			reader.close();
		}
		if (writer != null) {
			writer.close();
		}
		String[] args2 = { KillCrap.sourceFile.replaceAll(".xml", "2.xml"), KillCrap.targetFile.replaceAll(".xml", "2.xml") };
		WrapNodes.main(args2);
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
	
	private static boolean uselessNode(String input) {
		return input.indexOf("<relation") >= 0 || false;
	}
}
