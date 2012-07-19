
package steffen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TurnPolyline {
	
	private static String	fileSource	= "new_federal2.svg";
	
	public static void main(String[] args) throws IOException {
		System.out.println("Begin turning way...");
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToExternXMLs
				+ TurnPolyline.fileSource)));
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Constants.pathToExternXMLs + "turned_"
				+ TurnPolyline.fileSource)));
		
		while (reader.ready()) {
			String line = reader.readLine();
			String newline = null;
			
			if (line.indexOf("<polyline") >= 0) {
				String pointsString = "points=\"";
				int begin = line.indexOf(pointsString) + pointsString.length();
				if (begin >= 0) {
					newline = line.substring(0, begin);
					int end = line.indexOf("\"", begin);
					String allPoints = line.substring(begin, end);
					StringBuffer points = new StringBuffer();
					
					String[] pointArray = allPoints.split(" ");
					
					for (int i = pointArray.length - 1; i >= 0; i--) {
						points.append(pointArray[i] + " ");
					}
					
					newline += points.toString() + line.substring(end);
				}
			} else {
				newline = line;
			}
			
			writer.write(newline + Constants.lineSeparator);
		}
		writer.close();
		
		System.out.println("Done");
	}
}
