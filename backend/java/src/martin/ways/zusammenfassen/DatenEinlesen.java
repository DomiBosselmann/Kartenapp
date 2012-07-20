package martin.ways.zusammenfassen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatenEinlesen {
	
	public static void main(String[] args) throws IOException{
		
		String file = "C:\\Users\\Dolle\\Desktop\\projektKarte\\bawu_counties.xml";
		File myDir = new File("C:\\Users\\Dolle\\Desktop\\projektKarte");
		String outputFile = "bawu_counties_zusammen2.xml";
		
		String nextLine;
		List<String>[] ways;
		int anzahlWays = 0;
		String ndID;
		int startIndex;
		String startID;
		String endID;
		String startID2;
		String endID2;
		boolean waysAdded;
		List<String> newWay = new ArrayList<String>(); 
		
		myDir.mkdir();
		File data = new File(myDir, outputFile);
		BufferedReader br = new BufferedReader(new FileReader(file));
		FileWriter writer = new FileWriter(data);
		
		while (((nextLine = br.readLine()) != null)) {
			if(nextLine.trim().startsWith("<way")){
				anzahlWays++;
			}
		}
		System.out.println(anzahlWays);
		br = new BufferedReader(new FileReader(file));
		ways = new List[anzahlWays];
		anzahlWays = 0;
		
		while (((nextLine = br.readLine()) != null)) {
			if(nextLine.trim().startsWith("<way")){
				ways[anzahlWays] = new ArrayList<String>(); 
			}else if(nextLine.trim().startsWith("<nd")){
				startIndex = nextLine.indexOf(" ref=") + 6;
				ndID = nextLine.substring(startIndex, nextLine.indexOf("\"", startIndex));
				ways[anzahlWays].add(ndID);
			}else if(nextLine.trim().startsWith("</way")){
				anzahlWays++;
			}
		}
		
		for (int i=0; i < ways.length; i++){
			if (!(ways[i] == null)){
				for (int j=0; j < ways.length; j++){
					if (!(ways[j] == null)){
						if (i != j){
							waysAdded = false;
							startID = ways[i].get(0);
							endID = ways[i].get(ways[i].size()-1);
							
							startID2 = ways[j].get(0);
							endID2 = ways[j].get(ways[j].size()-1);
							
							newWay = new ArrayList<String>(); 
							
							if ((startID.equalsIgnoreCase(startID2) && (endID.equalsIgnoreCase(endID2)))){
								for (int k = 0; k < ways[j].size(); k++){
									newWay.add(ways[j].get(k)); 
								}
								for (int k = ways[i].size()-1; k > 0; k--){
									newWay.add(ways[i].get(k));
								}
								waysAdded = true;
							}else if((startID.equalsIgnoreCase(endID2)) && (endID.equalsIgnoreCase(startID2))){
								for (int k = 0; k < ways[j].size(); k++){
									newWay.add(ways[j].get(k)); 
								}
								for (int k = 1; k < ways[i].size(); k++){
									newWay.add(ways[i].get(k));
								}
								waysAdded = true;
							}else if(startID.equalsIgnoreCase(startID2)){
								for (int k = ways[j].size()-1; k > 0; k--){
									newWay.add(ways[j].get(k));
								}
								for (int k = 0; k < ways[i].size(); k++){
									newWay.add(ways[i].get(k));
								}
								waysAdded = true;
							}else if(startID.equalsIgnoreCase(endID2)){
								for (int k = 0; k < ways[j].size(); k++){
									newWay.add(ways[j].get(k));
								}
								for (int k = 1; k < ways[i].size(); k++){
									newWay.add(ways[i].get(k));
								}
								waysAdded = true;
							}else if(endID.equalsIgnoreCase(endID2)){
								for (int k = 0; k < ways[i].size(); k++){
									newWay.add(ways[i].get(k));
								}
								for (int k = (ways[j].size()-2); k>(-1); k--){
									newWay.add(ways[j].get(k));
								}
								waysAdded = true;
							}else if(endID.equalsIgnoreCase(startID2)){
								for (int k = 0; k < ways[i].size(); k++){
									newWay.add(ways[i].get(k));
								}
								for (int k = 1; k < ways[j].size(); k++){
									newWay.add(ways[j].get(k));
								}
								waysAdded = true;
							}
							if (waysAdded){
								ways[i] = newWay;
								ways[j] = null;
								if (i != 0){
									i--;
								}
								break;
							}
						}
					}
				}
			}
		}
		
		br = new BufferedReader(new FileReader(file));
		writer.write(br.readLine());
		writer.write(System.getProperty("line.separator"));
		writer.write(br.readLine());
		writer.write(System.getProperty("line.separator"));
		writer.flush();
		
		while (((nextLine = br.readLine()) != null)) {
			if(nextLine.trim().startsWith("<node")){
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();
			}else if(nextLine.trim().startsWith("</osm")){
				int neuAnzahl = 0;
				for (int i=0; i<ways.length; i++){
					if (!(ways[i] == null)){
						writer.write("<way>");
						writer.write(System.getProperty("line.separator"));
						for (int j=0; j<ways[i].size(); j++){
							writer.write("<nd ref=\"" + ways[i].get(j) +"\"/>");
							writer.write(System.getProperty("line.separator"));
						}
						writer.write("</way>");
						writer.write(System.getProperty("line.separator"));
						writer.flush();
						neuAnzahl++;
					}
				}
				System.out.println(neuAnzahl);
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();
			}
		}
		
	}

}
