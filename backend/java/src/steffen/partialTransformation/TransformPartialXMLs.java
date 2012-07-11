
package steffen.partialTransformation;

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
import steffen.Constants;

public class TransformPartialXMLs {
	
	public static void main(String[] args) throws Exception {
		String xmlFileSource = "bawu boundary p0.05 splitted ";
		String xsltFileSource = "part_transform.xsl";
		String fileTargetName = "boundary";
		
		TransformPartialXMLs.transformTheseXMLs(xmlFileSource, xsltFileSource, fileTargetName);
	}
	
	public static void transformTheseXMLs(final String xmlFileSource, String xsltFileSource, String fileTargetName) throws IOException,
			TransformerException {
		TransformPartialXMLs.transformTheseXMLs(xmlFileSource, xsltFileSource, fileTargetName, true);
	}
	
	public static void transformTheseXMLs(final String xmlFileSource, String xsltFileSource, String fileTargetName, boolean deleteFiles)
			throws IOException, TransformerException {
		int splittedFilesCount = new File(Constants.pathToExternXMLs).list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.indexOf(xmlFileSource) >= 0;
			}
		}).length;
		Source xsltSource = new StreamSource(new File(Constants.pathToInternXSLs + xsltFileSource));
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Templates xslt = transformerFactory.newTemplates(xsltSource);
		Transformer transformer = xslt.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty("{http://www.w3.org/1999/XSL/Transform}xmlns", "http://www.w3.org/2000/svg");
		transformer.setOutputProperty("{http://www.w3.org/1999/XSL/Transform}xmlns:xlink", "http://www.w3.org/1999/xlink");
		
		for (int i = 1; i <= splittedFilesCount; i++) {
			File sourceFile = new File(Constants.pathToExternXMLs + xmlFileSource + i + ".xml");
			if (deleteFiles) {
				sourceFile.deleteOnExit();
			}
			Source xmlSource = new StreamSource(sourceFile);
			File targetFile = new File(Constants.pathToExternXMLs + fileTargetName + i + ".svg");
			targetFile.deleteOnExit();
			FileWriter writer = new FileWriter(targetFile);
			transformer.transform(xmlSource, new StreamResult(writer));
			writer.close();
			System.out.println("Step 1: File " + i + "/" + splittedFilesCount);
		}
		
		System.out.println("Step 1");
		
		FileWriter writer = new FileWriter(new File(Constants.pathToExternXMLs + fileTargetName + ".svg"));
		for (int i = 1; i <= splittedFilesCount; i++) {
			BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToExternXMLs + fileTargetName + i + ".svg")));
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
