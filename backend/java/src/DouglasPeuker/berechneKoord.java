package DouglasPeuker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class berechneKoord {
	
	String node, nextLine = "";
	int startIndex, endIndex;
	Hashtable<String, double[]> allNodes = new Hashtable<String, double[]>();
	
	berechneKoord(String file) throws IOException{
		
		String nodeID;
		double lat, lon;
		double[] help = new double[1];
		
		BufferedReader br = new BufferedReader(new FileReader(file));

		while (((nextLine = br.readLine()) != null)) {
			if (nextLine.trim().startsWith("<node")){
				startIndex = nextLine.indexOf(" id=") + 5;
				endIndex = nextLine.indexOf("\"", startIndex);
				nodeID = nextLine.substring(startIndex, endIndex);
				startIndex = nextLine.indexOf(" lat=") + 6;
				endIndex = nextLine.indexOf("\"", startIndex);
				lat = Double.parseDouble(nextLine.substring(startIndex, endIndex));
				startIndex = nextLine.indexOf(" lon=") + 6;
				endIndex = nextLine.indexOf("\"", startIndex);
				lon = Double.parseDouble(nextLine.substring(startIndex, endIndex));
				
				
				help[0] = lat;
				help[1] = lon;
				
				allNodes.put(nodeID, help);
			}
		}
	}
	
	
	public Punkt[] berechneLaenge(ArrayList<String> list) throws IOException {
		int anzNodes = list.size();
		Punkt[] point = new Punkt[anzNodes];
		int index = 0;

		
		for (String nodeInList: list){
			if (allNodes.contains(nodeInList)){
				point[index] = new Punkt(allNodes.get(nodeInList)[0], allNodes.get(nodeInList)[1], nodeInList);
				index++;
			}
		}
		return point;
		
	}
}
