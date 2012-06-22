package martin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

public class berlinEntschlacken {

	public static void main(String[] args) throws IOException {

		String nextLine = "";
		boolean tagSchreiben;
		boolean letzterKnotenNode = false;
		boolean relationSchreiben = false;
		int numOfTags = 1;
		String[] knoten = new String[50000];
		int endIndexNodeInformation;
		int endIndexRelationInformation;
		int startIndexNodeInformation;
		int startIndexID;
		int endIndexID;
		int startIndexMember;
		int endIndexMember;
		String nodeID;
		String memberID;
		String endStringNode;
		Hashtable<String, String> nodeIDs = new Hashtable<String, String>();
		Hashtable<String, String> wayIDs = new Hashtable<String, String>();
		
		File myDir = new File("C:\\Users\\Dolle\\Desktop\\projektKarte");
		myDir.mkdir();
		File data = new File(myDir, "berlin.Entschlackt.Help.osm");
		File helpdata = new File(myDir, "berlin.ndRef.osm");
		FileWriter helpwriter = new FileWriter(helpdata);
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dolle\\Desktop\\projektKarte\\berlin.osm"));
		FileWriter writer = new FileWriter(data);
		
		tagSchreiben = false;
		while (((nextLine = br.readLine()) != null)) {
			if (nextLine.trim().startsWith("<node")){
				/*if(letzterKnotenNode){
					writer.write(knoten[0]);
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
				startIndexNodeInformation  = nextLine.indexOf(" lat="); 
				endStringNode = nextLine.substring(startIndexNodeInformation, nextLine.length());
				
				endIndexNodeInformation = nextLine.indexOf(" timestamp=");
				if (endIndexNodeInformation > 0){
					nextLine = nextLine.substring(0, endIndexNodeInformation);
				}
				nextLine = nextLine + endStringNode;
						*/
				knoten[0] = nextLine;
				numOfTags = 1;
				letzterKnotenNode = true;
			}else if(nextLine.trim().startsWith("</node")) {
				/*writer.write(knoten[0]);
				writer.write(System.getProperty("line.separator"));
				if (tagSchreiben){
					for (int i = 1; i < numOfTags; i++){
						writer.write(knoten[i]);
						writer.write(System.getProperty("line.separator"));
						writer.flush();
					}
					startIndexID = knoten[0].indexOf(" id=\"") + 6;
					endIndexID = knoten[0].indexOf(" lat=") - 2;
					nodeID = knoten[0].substring(startIndexID, endIndexID);
					nodeIDs.put(nodeID, nodeID);
				}
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();*/
				tagSchreiben = false;
				numOfTags = 1;
				letzterKnotenNode = false;
			}else if(nextLine.trim().startsWith("<way")){
				if(letzterKnotenNode){
					writer.write(knoten[0]);
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
				endIndexNodeInformation  = nextLine.indexOf(" timestamp="); 
				
				if (endIndexNodeInformation > 0){
					nextLine = nextLine.substring(0, endIndexNodeInformation);
					nextLine = nextLine + ">";
				}
				
				knoten[0] = nextLine;
				numOfTags = 1;
			}else if(nextLine.trim().startsWith("</way")) {
				if (tagSchreiben){
					writer.write(knoten[0]);
					writer.write(System.getProperty("line.separator"));
					for (int i = 1; i < numOfTags; i++){
						if(knoten[i].trim().startsWith("<nd")){
							startIndexID = knoten[i].indexOf(" ref=") + 6;
							nodeID = knoten[i].substring(startIndexID, knoten[i].length() - (int)(3));
							helpwriter.write(nodeID);
							helpwriter.write(System.getProperty("line.separator"));
							helpwriter.flush();
							nodeIDs.put(nodeID, nodeID);
						}
						
						writer.write(knoten[i]);
						writer.write(System.getProperty("line.separator"));
						writer.flush();
					}
					startIndexID = knoten[0].indexOf(" id=") + 5;
					endIndexID = knoten[0].indexOf(" version=") - 2;
					nodeID = knoten[0].substring(startIndexID, endIndexID);
					wayIDs.put(nodeID, nodeID);
					writer.write(nextLine);
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
				tagSchreiben = false;
				numOfTags = 1;
				letzterKnotenNode = false;
			}else if(nextLine.trim().startsWith("<tag")){
				if (nextLine.contains("administrative")){
					tagSchreiben = true;
				}
				knoten[numOfTags] = nextLine;
				numOfTags++;
				letzterKnotenNode = false;
			}else if(nextLine.trim().startsWith("<nd")){
				knoten[numOfTags] = nextLine;
				numOfTags++;
				letzterKnotenNode = false;
			}else if(nextLine.trim().startsWith("<relation")){
				endIndexRelationInformation = nextLine.indexOf(" changeset=");
				if (endIndexRelationInformation > 0){
					nextLine = nextLine.substring(0, endIndexRelationInformation);
					nextLine = nextLine + ">";
				}
				knoten[0] = nextLine;
				relationSchreiben = false;
				letzterKnotenNode = false;
				numOfTags = 1;
			}else if(nextLine.trim().startsWith("<member")){
				knoten[numOfTags] = nextLine;		
				numOfTags++;
				letzterKnotenNode = false;
				if ((knoten[0].indexOf(" ref=") > 0) && (knoten[0].indexOf(" role=") > 0)){
					startIndexMember = knoten[0].indexOf(" ref=") + 6;
					endIndexMember = knoten[0].indexOf(" role=") - 1;
					memberID = knoten[0].substring(startIndexMember, endIndexMember);
					if(wayIDs.containsKey(memberID)){
						System.out.println("klappt");
						relationSchreiben = true;
					}
				}
			}else if(nextLine.trim().startsWith("</relation")){
				if (relationSchreiben){
					for (int i = 0 ; i < numOfTags; i++){
						writer.write(knoten[i]);
						writer.write(System.getProperty("line.separator"));
					}
					writer.write(nextLine);
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
				numOfTags = 1;
				relationSchreiben = false;
				letzterKnotenNode = false;
			}else{
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();	
				numOfTags = 1;
				relationSchreiben = false;
				letzterKnotenNode = false;
			}
		}
		System.out.println("fertig Teil 1");
		br.close();
		writer.close();
		helpwriter.close();
		
		data = new File(myDir, "berlin.Entschlackt.osm");
		br = new BufferedReader(new FileReader("C:\\Users\\Dolle\\Desktop\\projektKarte\\berlin.osm"));
		BufferedReader br2 = new BufferedReader(new FileReader("C:\\Users\\Dolle\\Desktop\\projektKarte\\berlin.Entschlackt.Help.osm"));
		writer = new FileWriter(data);
		helpdata = new File(myDir, "berlin.nodeID.osm");
		helpwriter = new FileWriter(helpdata);
		
		writer.write(br2.readLine());
		writer.write(System.getProperty("line.separator"));
		writer.write(br2.readLine());
		writer.write(System.getProperty("line.separator"));
		writer.flush();
		while (((nextLine = br.readLine()) != null)) {
			if (nextLine.trim().startsWith("<node")){
				startIndexID = nextLine.indexOf(" id=") + 5;
				endIndexID = nextLine.indexOf(" version=")-(int)(1);
				nodeID = nextLine.substring(startIndexID, endIndexID);
				helpwriter.write(nodeID);
				helpwriter.write(System.getProperty("line.separator"));
				helpwriter.flush();
				if (nodeIDs.containsKey(nodeID)){
					if (!(nextLine.endsWith("/>"))){
						nextLine = nextLine.substring(0, nextLine.length()-1) + "/>";
					}
					startIndexNodeInformation  = nextLine.indexOf(" lat="); 
					endStringNode = nextLine.substring(startIndexNodeInformation, nextLine.length());
					
					endIndexNodeInformation = nextLine.indexOf(" timestamp=");
					if (endIndexNodeInformation > 0){
						nextLine = nextLine.substring(0, endIndexNodeInformation);
					}
					nextLine = nextLine + endStringNode;
					writer.write(nextLine);
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
				
			}
		}
		while (((nextLine = br2.readLine()) != null)) {
			writer.write(nextLine);
			writer.write(System.getProperty("line.separator"));
			writer.flush();
		}
		System.out.println("fertig Teil 2");
		br.close();
		br2.close();
		writer.close();
		helpwriter.close();
		
		
	}

}

