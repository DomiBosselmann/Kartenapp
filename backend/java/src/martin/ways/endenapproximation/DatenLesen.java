package martin.ways.endenapproximation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class DatenLesen {
	
	public static void main(String[] args) throws IOException{
		
		String file = "C:\\Users\\Dolle\\Desktop\\projektKarte\\bawu_bounds_zusammen2.xml";
		File myDir = new File("C:\\Users\\Dolle\\Desktop\\projektKarte");
		String outputFile = "bawu_bounds_endgültig2.xml";
		
		myDir.mkdir();
		File data = new File(myDir, outputFile);
		BufferedReader br = new BufferedReader(new FileReader(file));
		FileWriter writer = new FileWriter(data);
		
		String nextLine;
		Hashtable<String, String> nodeIDs = new Hashtable<String, String>();
		int startIndex;
		double lat;
		double lon;
		String id;
		int anzahlWays=0;
		List<String>[] ways;
		String ndID;
		List<String> newWay = new ArrayList<String>(); 
		
		while (((nextLine = br.readLine()) != null)) {
			if(nextLine.trim().startsWith("<node")){
				startIndex = nextLine.indexOf("id=")+4;
				id = nextLine.substring(startIndex, nextLine.indexOf("\"", startIndex));
				startIndex = nextLine.indexOf("lat=")+5;
				lat = Double.parseDouble(nextLine.substring(startIndex, nextLine.indexOf("\"", startIndex))); 
				startIndex = nextLine.indexOf("lon=")+5;
				lon = Double.parseDouble(nextLine.substring(startIndex, nextLine.indexOf("\"", startIndex))); 

				lon = lon * 111120 * Math.cos(lat);
				lat = lat * 111120; 
				
				nodeIDs.put(id, lat + ";" + lon);
			}else if(nextLine.trim().startsWith("<way")){
				anzahlWays++;
			}
		}
		
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
			double minAbstand = 0;
			boolean isStartI = true;
			int wayJ = 0;
			boolean isStartJ = true;
			if (!(ways[i] == null)){
				for (int j=0; j < ways.length; j++){
					if (!(ways[j] == null)){
						if (i != j){
							double minAbstandLocal;
							boolean iStart;
							boolean jStart;
							
							String[] help = nodeIDs.get(ways[i].get(0)).split(";"); 
							double[] wayIStart = new double[2];
							wayIStart[0] = Double.parseDouble(help[0]);
							wayIStart[1] = Double.parseDouble(help[1]);
							
							double[] wayIEnd = new double[2];
							help = nodeIDs.get(ways[i].get(ways[i].size()-1)).split(";"); 
							wayIEnd[0] = Double.parseDouble(help[0]);
							wayIEnd[1] = Double.parseDouble(help[1]);
							
							double[] wayJStart = new double[2];
							help = nodeIDs.get(ways[j].get(0)).split(";"); 
							wayJStart[0] = Double.parseDouble(help[0]);
							wayJStart[1] = Double.parseDouble(help[1]);
							
							double[] wayJEnd = new double[2];
							help = nodeIDs.get(ways[j].get(ways[j].size()-1)).split(";"); 
							wayJEnd[0] = Double.parseDouble(help[0]);
							wayJEnd[1] = Double.parseDouble(help[1]);
							
							minAbstandLocal = abstand(wayIStart, wayJStart);
							iStart = true;
							jStart = true;
							
							if (abstand(wayIStart, wayJEnd) < minAbstandLocal){
								iStart = true;
								jStart = false;
								minAbstandLocal = abstand(wayIStart, wayJEnd);
							}
							if (abstand(wayIEnd, wayJStart) < minAbstandLocal){
								iStart = false;
								jStart = true;
								minAbstandLocal = abstand(wayIEnd, wayJStart);
							}
							if (abstand(wayIEnd, wayJEnd) < minAbstandLocal){
								iStart = false;
								jStart = false;
								minAbstandLocal = abstand(wayIEnd, wayJEnd);
							}
							
							if ((minAbstand == 0) || (minAbstandLocal < minAbstand)){
								minAbstand = minAbstandLocal;
								if(iStart){
									isStartI = true;
								}else{
									isStartI = false;
								}
								if(jStart){
									isStartJ = true;
								}else{
									isStartJ = false;
								}
								wayJ = j;
								System.out.println(j);
							}
						}	
					}
				}
				
				boolean waysAdded = false;
				newWay = new ArrayList<String>();
				System.out.println("------------");
				System.out.println(wayJ);
				System.out.println(minAbstand);
				System.out.println("------------");
				System.out.println("------------");
				if (ways[wayJ] != null){
					if(isStartI && isStartJ){
						for (int k = ways[wayJ].size(); k > 0; k--){
							newWay.add(ways[wayJ].get(k + (int)(-1)));
						}
						for (int k = 0; k < ways[i].size(); k++){
							newWay.add(ways[i].get(k));
						}
						waysAdded = true;
					}else if(isStartI && !(isStartJ)){
						for (int k = 0; k < ways[wayJ].size(); k++){
							newWay.add(ways[wayJ].get(k));
						}
						for (int k = 0; k < ways[i].size(); k++){
							newWay.add(ways[i].get(k));
						}
						waysAdded = true;
					}else if(!(isStartI) && !(isStartJ)){
						for (int k = 0; k < ways[i].size(); k++){
							newWay.add(ways[i].get(k));
						}
						for (int k = (ways[wayJ].size()); k > 0; k--){
							newWay.add(ways[wayJ].get(k + (int)(-1)));
						}
						waysAdded = true;
					}else if(!(isStartI) && isStartJ){
						for (int k = 0; k < ways[i].size(); k++){
							newWay.add(ways[i].get(k));
						}
						for (int k = 0; k < ways[wayJ].size(); k++){
							newWay.add(ways[wayJ].get(k));
						}
						waysAdded = true;
					}
					if (waysAdded){
						ways[i] = newWay;
						ways[wayJ] = null;
						while ((i != 0) && (!(ways[i] == null))){						
							i--;
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
	
	public static double abstand(double[] x, double[] y){
		return Math.sqrt(Math.pow((x[0] - y[0]), 2) + Math.pow((x[1] - y[1]), 2));
	}
}
