
package steffen.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import steffen.FilePath;

public class KoordinatenRange {
	private static String	fileSource	= "bawu boundary.xml";
	
	public static void main(String[] args) throws IOException {
		fileSource = FilePath.path + fileSource;
		
		// create
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileSource)));
		
		// actions
		String line = null;
		double lat1 = 9999.0;
		double lat2 = -9999.0;
		double lon1 = 9999.0;
		double lon2 = -9999.0;
		while (reader.ready()) {
			line = reader.readLine();
			int latBegin = line.indexOf("lat=\"");
			if (latBegin >= 0) {
				int latEnd = line.indexOf("\"", latBegin + 5);
				double lat = Double.valueOf(line.substring(latBegin + 5, latEnd));
				if (lat < lat1) {
					lat1 = lat;
				}
				if (lat > lat2) {
					lat2 = lat;
				}
			}
			int lonBegin = line.indexOf("lon=\"");
			if (lonBegin >= 0) {
				int lonEnd = line.indexOf("\"", lonBegin + 5);
				double lon = Double.valueOf(line.substring(lonBegin + 5, lonEnd));
				if (lon < lon1) {
					lon1 = lon;
				}
				if (lon > lon2) {
					lon2 = lon;
				}
			}
		}
		
		// destroy
		reader.close();
		
		System.out.println("Links: " + lon1);
		System.out.println("Rechts: " + lon2);
		System.out.println("Oben: " + lat2);
		System.out.println("Unten: " + lat1);
	}
}
