package martin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

public class entschlackungsTool {

	public static void main(String[] args) throws IOException {
		
		String nextLine = "";
		boolean tagSchreiben = false;
		boolean tagSchreibenLevel = false;
		boolean letzterKnotenNode = false;
		boolean relationSchreiben = false;
		boolean addSlash = false;
		int numOfTags = 1;
		String[] knoten = new String[50000];
		int endIndexNodeInformation;
		int endIndexRelationInformation;
		int startIndexID;
		int endIndexID;
		int startIndexMember;
		int endIndexMember;
		int AdminLevelValue;
		int startIndexNodeInformation;
		String endStringNode;
		String nodeID;
		String memberID;
		Hashtable<String, String> nodeIDs = new Hashtable<String, String>();
		
		File myDir = new File("C:\\Users\\Dolle\\Desktop\\projektKarte");
		myDir.mkdir();
		File data = new File(myDir, "dataBW.help.osm");
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dolle\\Desktop\\projektKarte\\osmarender\\stylesheets\\data.Roh.osm"));
		FileWriter writer = new FileWriter(data);
		File helpdata = new File(myDir, "bw.ndRef.osm");
		FileWriter helpwriter = new FileWriter(helpdata);
		
		while (((nextLine = br.readLine()) != null)) {
			if (nextLine.trim().startsWith("<node")){
				/*if(letzterKnotenNode){
					writer.write(knoten[0]);
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
				if (nextLine.endsWith("/>")){
					addSlash = true;
				}else{
					addSlash = false;
				}
				endIndexNodeInformation = nextLine.indexOf(" version=");
				if (endIndexNodeInformation > 0){
					nextLine = nextLine.substring(0, endIndexNodeInformation);
					if (addSlash){
						nextLine = nextLine + "/>";
					}else{
						nextLine = nextLine + ">";
					}
				}*/
				
						
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
				tagSchreibenLevel = false;
				numOfTags = 1;
				letzterKnotenNode = false;
			}else if(nextLine.trim().startsWith("<tag")){
				if (nextLine.contains("k=\"boundary\" v=\"administrative\"")){
					tagSchreiben = true;
				}else if (nextLine.contains("k=\"admin_level")){
					AdminLevelValue = Integer.parseInt(nextLine.substring(nextLine.indexOf(" v=")+4, nextLine.length() - (int)(4)));
					if (AdminLevelValue < 7){
						tagSchreibenLevel = true;
					}
				}
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
					if(nodeIDs.containsKey(memberID)){
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
			}else if(nextLine.trim().startsWith("<way")){
				endIndexID = nextLine.indexOf(" changeset=");
				if (endIndexID > 0) {
					nextLine = nextLine.substring(0, endIndexID) + ">";
				}
				knoten[0] = nextLine;
				numOfTags = 1;
			}else if(nextLine.trim().startsWith("</way")){
					
				if (tagSchreiben && tagSchreibenLevel){
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
					writer.write(nextLine);
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
				tagSchreibenLevel = false;
				tagSchreiben = false;
				numOfTags = 1;
				letzterKnotenNode = false;
			}else if(nextLine.trim().startsWith("<nd")){
				knoten[numOfTags] = nextLine;
				numOfTags++;
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
		System.out.println("fertig");
		br.close();
		writer.close();
		helpwriter.close();
		
		
		data = new File(myDir, "dataBW.osm");
		br = new BufferedReader(new FileReader("C:\\Users\\Dolle\\Desktop\\projektKarte\\osmarender\\stylesheets\\data.Roh.osm"));
		BufferedReader br2 = new BufferedReader(new FileReader("C:\\Users\\Dolle\\Desktop\\projektKarte\\dataBW.help.osm"));
		writer = new FileWriter(data);
		helpdata = new File(myDir, "bw.nodeID.osm");
		helpwriter = new FileWriter(helpdata);
		
		writer.write(br2.readLine());
		writer.write(System.getProperty("line.separator"));
		writer.write(br2.readLine());
		writer.write(System.getProperty("line.separator"));
		writer.flush();
		while (((nextLine = br.readLine()) != null)) {
			if (nextLine.trim().startsWith("<node")){
				startIndexID = nextLine.indexOf(" id=") + 5;
				endIndexID = nextLine.indexOf("\"", nextLine.indexOf(" id=") + 5);
				nodeID = nextLine.substring(startIndexID, endIndexID);
				helpwriter.write(nodeID);
				helpwriter.write(System.getProperty("line.separator"));
				helpwriter.flush();
				if (nodeIDs.containsKey(nodeID)){
					
					nextLine = nextLine.substring(0, nextLine.indexOf(" changeset=")) + "/>";
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
