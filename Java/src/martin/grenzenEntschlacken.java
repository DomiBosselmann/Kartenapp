package martin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class grenzenEntschlacken {

	
	public static void main(String[] args) throws IOException{
		
		String nextLine;
		int endIndexNodeInformation;
		
		
		File myDir = new File("C:\\Users\\Dolle\\Desktop\\projektKarte");
		myDir.mkdir();
		File data = new File(myDir, "grenzen.entschlackt.osm");
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dolle\\Desktop\\projektKarte\\LayerGrenzen.osm"));
		FileWriter writer = new FileWriter(data);
		
		while (((nextLine = br.readLine()) != null)) {
			if (nextLine.trim().startsWith("<node")){
				endIndexNodeInformation = nextLine.indexOf(" version=");
				if (endIndexNodeInformation > 0){
					nextLine = nextLine.substring(0, endIndexNodeInformation) + "/>";
				}		
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();
			}else if(nextLine.trim().startsWith("<Tag")){
				if (!(nextLine.contains("source"))){
					writer.write(nextLine.trim());
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
			}else if (nextLine.trim().startsWith("<node")){
				endIndexNodeInformation = nextLine.indexOf(" version=");
				if (endIndexNodeInformation > 0){
					nextLine = nextLine.substring(0, endIndexNodeInformation) + ">";
				}		
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();
			}else{				
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();
			}
			
		}
		System.out.println("fertig");
		br.close();
		writer.close();
		
	}
}
