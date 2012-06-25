package DouglasPeuker;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class berechneKoord {
	
	String node, nextLine = "";
	double lat, lon;
	int startIndex, endIndex;
	
	
	public Punkt[] berechneLaenge(ArrayList<String> list) throws IOException {
		int anzNodes = list.size();
		Punkt[] point;
		point = new Punkt[anzNodes];
		int index = 0;
		File myDir = new File("C:\\Users\\PBernhardt\\Desktop\\Projektmanagement\\osm germany");
		myDir.mkdir();
		File data = new File(myDir, "baden-wuerttemberg_final.osm");
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\PBernhardt\\Desktop\\Projektmanagement\\osm germany\\Cities.osm"));
		
		while (((nextLine = br.readLine()) != null)) {
			if (nextLine.trim().startsWith("<node")){
			startIndex = nextLine.indexOf(" id=") + 5;
			endIndex = nextLine.indexOf("\"", startIndex);
			node = nextLine.substring(startIndex, endIndex);
			
			
			for (String nodeInList: list){
				if (node.equals(nodeInList)){
					startIndex = nextLine.indexOf(" lat=") + 6;
					endIndex = nextLine.indexOf("\"", startIndex);
					lat = Double.parseDouble(nextLine.substring(startIndex, endIndex));
					startIndex = nextLine.indexOf(" lon=") + 6;
					endIndex = nextLine.indexOf("\"", startIndex);
					lon = Double.parseDouble(nextLine.substring(startIndex, endIndex));
					/*point[index].setX(lat);
					point[index].setX(lon);
					point[index].setID(nodeInList);*/
					point[index] = new Punkt(lat, lon, nodeInList);
					index++;
				}
			}
			
			}
		}
		br.close();
		return point;
		
	}
}
