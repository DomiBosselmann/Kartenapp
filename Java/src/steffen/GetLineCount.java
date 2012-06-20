
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GetLineCount {
	private static String	sourceFilePath	= "LayerGrenzen.osm";
	
	public static void main(String[] args) throws IOException {
		// create
		File file1 = new File(GetLineCount.sourceFilePath);
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file1));
		
		// actions
		int i = 0;
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			// System.out.println(i);
			i++;
		}
		
		System.out.println(i);
		System.out.println(line);
		
		// destroy
		if (reader != null) {
			reader.close();
		}
	}
}
