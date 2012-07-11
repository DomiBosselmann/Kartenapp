
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class ConcatePartialSVGs {
	private static String	xmlFileSource	= "bawu boundary p0.05 splitted ";
	private static String	xsltFileSource	= "part_transform.xsl";
	private static String	fileTarget		= "boundary";
	
	public static void main(String[] args) throws TransformerException, IOException {
		int splittedFilesCount = new File(FilePath.path).list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.indexOf(ConcatePartialSVGs.xmlFileSource) >= 0;
			}
		}).length;
		Source xsltSource = new StreamSource(new File("../transformation/steffen/xsl/" + ConcatePartialSVGs.xsltFileSource));
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Templates xslt = transformerFactory.newTemplates(xsltSource);
		Transformer transformer = xslt.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty("{http://www.w3.org/1999/XSL/Transform}xmlns", "http://www.w3.org/2000/svg");
		transformer.setOutputProperty("{http://www.w3.org/1999/XSL/Transform}xmlns:xlink", "http://www.w3.org/1999/xlink");
		
		for (int i = 1; i <= splittedFilesCount; i++) {
			Source xmlSource = new StreamSource(new File(FilePath.path + ConcatePartialSVGs.xmlFileSource + i + ".xml"));
			
			FileWriter writer = new FileWriter(new File(FilePath.path + ConcatePartialSVGs.fileTarget + i + ".svg"));
			transformer.transform(xmlSource, new StreamResult(writer));
			writer.close();
			System.out.println("Step 1: File " + i + "/" + splittedFilesCount);
		}
		
		System.out.println("Step 1");
		
		FileWriter writer = new FileWriter(new File(FilePath.path + ConcatePartialSVGs.fileTarget + ".svg"));
		for (int i = 1; i <= splittedFilesCount; i++) {
			BufferedReader reader = new BufferedReader(new FileReader(
					new File(FilePath.path + ConcatePartialSVGs.fileTarget + i + ".svg")));
			String line = null;
			if (reader.ready()) {
				line = reader.readLine();
				if (i == 1) {
					int begin = line.indexOf("<svg");
					if (begin >= 0) {
						writer.write(line);
					}
				}
			}
			while (reader.ready()) {
				line = reader.readLine();
				int end = line.indexOf("</g");
				if (end >= 0) {
					if (i == splittedFilesCount) {
						writer.write(line);
					}
				} else {
					line = line.replaceAll("xmlns=\"\" ", "");
					writer.write(line);
				}
			}
			reader.close();
			
			System.out.println("Step 2: File " + i + "/" + splittedFilesCount);
		}
		writer.close();
		
		System.out.println("Done");
	}
}
