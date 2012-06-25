package DouglasPeuker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class WaysEinlesen {
	
	
	public static void main(String[] args) throws IOException{
		
		String file = "C:\\Users\\Dolle\\Desktop\\projektKarte\\osmarender\\stylesheets\\data.Roh.osm";
		File myDir = new File("C:\\Users\\Dolle\\Desktop\\projektKarte");
		String outputFile = "gepeukert.osm";
		
		double maxAbweichung = 500;
		String nextLine;
		List<String> punkteIDs = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();
		Punkt[] punkte;
		DouglasPeuker DouglasPeuker = new DouglasPeuker(maxAbweichung);
		Hashtable<String, String> nodeIDs = new Hashtable<String, String>();
		int startIndex;
		int endIndex;
		String nodeID;
		
		myDir.mkdir();
		File data = new File(myDir, "help.osm");
		BufferedReader br = new BufferedReader(new FileReader(file));
		FileWriter writer = new FileWriter(data);
	
		while (((nextLine = br.readLine()) != null)) {
			if(nextLine.trim().startsWith("<way")){
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();
			}else if(nextLine.trim().startsWith("<nd")){
				punkteIDs.add(nextLine.substring(nextLine.indexOf(" ref=") + 6, nextLine.length()-3));
			}else if(nextLine.trim().startsWith("<tag")){
				tags.add(nextLine);
			}else if(nextLine.trim().startsWith("</way")){
				punkte = punkteBerechnen(punkteIDs);
				punkte = DouglasPeuker.linienGlaetten(punkte);
				
				for (int i=0; i<punkte.length; i++){
					if (!(punkte[i] == null)){
						writer.write("    <nd ref=\"" + punkte[i].getID() + "\"\\>");
						writer.write(System.getProperty("line.separator"));
						writer.flush();
						
						nodeIDs.put(punkte[i].getID(), punkte[i].getID());
					}
				}
				
				for (int i=0; i<tags.size(); i++){
					writer.write(tags.get(i));
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
				
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();
				tags.clear();
				punkteIDs.clear();
			}else if(nextLine.trim().startsWith("</node")){
				//die nodes sollen erst in einem zweiten durchgang geschrieben werden, 
				//wenn man weiß, ob man sie überhaupt noch braucht.
			}else{
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();
			}
		}
		
		br.close();
		writer.close();
		
		data = new File(myDir, outputFile);
		BufferedReader wayReader = new BufferedReader(new FileReader(myDir + "\\help.osm")); 
		BufferedReader nodeReader = new BufferedReader(new FileReader(file));
		writer = new FileWriter(data);
		
		writer.write(wayReader.readLine());
		writer.write(System.getProperty("line.separator"));
		writer.write(wayReader.readLine());
		writer.write(System.getProperty("line.separator"));
		writer.flush();
		
		while (((nextLine = nodeReader.readLine()) != null)){
			if (nextLine.trim().startsWith("<node")){
				startIndex = nextLine.indexOf(" id=") + 5;
				endIndex = nextLine.indexOf("\"", startIndex);
				nodeID = nextLine.substring(startIndex, endIndex);
				
				if (nodeIDs.contains(nodeID)){
					writer.write(nextLine);
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
			}
		}
		
		while (((nextLine = wayReader.readLine()) != null)){
			writer.write(nextLine);
			writer.write(System.getProperty("line.separator"));
			writer.flush();
		}
		
		wayReader.close();
		nodeReader.close();
		writer.close();
	}
}
