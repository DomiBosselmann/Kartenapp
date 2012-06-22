package martin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class grenzenSucheTool {

	public static void main(String[] args) throws IOException{
		
		String nextLine = "";
		int schreiben = 0;
		
		File myDir = new File("C:\\Users\\Dolle\\Desktop\\projektKarte");
		myDir.mkdir();
		File data = new File(myDir, "berlin.Grenzen..osm");
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dolle\\Desktop\\projektKarte\\berlin.osm"));
		FileWriter writer = new FileWriter(data ,true);
				
		while (((nextLine = br.readLine()) != null)) {
			if ((nextLine.contains("administrative"))){
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();
				schreiben = 10;
			}else if (schreiben > 0 ){
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
				writer.flush();
				schreiben--;
				if(schreiben == 0){
					writer.write(System.getProperty("line.separator"));
					writer.flush();
				}
			}
		}
		System.out.println("fertig");
		br.close();
		writer.close();
		
	}
}
