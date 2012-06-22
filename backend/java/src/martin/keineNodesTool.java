package martin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class keineNodesTool {


	public static void main(String[] args) throws IOException {
		String nextLine = "";
		
		File myDir = new File("C:\\Users\\Dolle\\Desktop\\projektKarte");
		myDir.mkdir();
		File data = new File(myDir, "aaaaaaa.berlin.way.osm");
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dolle\\Desktop\\projektKarte\\berlin.osm"));
		FileWriter writer = new FileWriter(data);
				
		while (((nextLine = br.readLine()) != null)) {
			if (!(nextLine.trim().startsWith("<nd") || nextLine.trim().startsWith("</node") || nextLine.trim().startsWith("<tag k=\"created_by\"")||
					nextLine.trim().startsWith("<tag k=\"converted_by\"") || nextLine.trim().startsWith("<tag") || 
					nextLine.trim().startsWith("<member") || nextLine.trim().startsWith("</way") || nextLine.trim().startsWith("<way") || 
					nextLine.trim().startsWith("<relation") ||
					nextLine.trim().startsWith("</relation"))){
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
