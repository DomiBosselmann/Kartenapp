
package steffen.partialTransformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
import steffen.layer.Layer;

public class TransformPartialXMLs {
	
	private static boolean	fileLog	= false;
	
	public static void main(String[] args) throws Exception {
		Layer layer = Layer.Federal;
		String xmlFileSource = "bawu boundary p0.05 splitted ";
		
		TransformPartialXMLs.transformTheseXMLs(xmlFileSource, layer);
	}
	
	public static void transformTheseXMLs(final String xmlFileSource, Layer layer) throws IOException, TransformerException {
		TransformPartialXMLs.transformTheseXMLs(xmlFileSource, layer, true);
	}
	
	public static void transformTheseXMLs(final String xmlFileSource, Layer layer, boolean deleteFiles) throws IOException,
			TransformerException {
		System.out.println("Begin transforming partial files of " + layer.name + "...");
		
		String xsltFileSource = null;
		if (layer.nodeLayer) {
			xsltFileSource = "places_part_transform.xsl2";
		} else {
			xsltFileSource = "ways_part_transform.xsl2";
		}
		String fileTargetName = layer.name;
		
		int splittedFilesCount = new File(Constants.pathToExternXMLs).list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.indexOf(xmlFileSource) >= 0;
			}
		}).length;
		Source xsltSource = new StreamSource(new File(Constants.pathToInternXSLs + "partial/" + xsltFileSource));
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Templates xslt = transformerFactory.newTemplates(xsltSource);
		Transformer transformer = xslt.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://www.w3.org/1999/XSL/Transform}xmlns", "http://www.w3.org/2000/svg");
		transformer.setOutputProperty("{http://www.w3.org/1999/XSL/Transform}xmlns:xlink", "http://www.w3.org/1999/xlink");
		
		for (int i = 1; i <= splittedFilesCount; i++) {
			File sourceFile = new File(Constants.pathToExternXMLs + xmlFileSource + i + ".xml");
			Source xmlSource = new StreamSource(sourceFile);
			File targetFile = new File(Constants.pathToExternXMLs + fileTargetName + i + ".svg");
			FileOutputStream outputStream = new FileOutputStream(targetFile);
			transformer.transform(xmlSource, new StreamResult(outputStream));
			outputStream.close();
			if (deleteFiles) {
				sourceFile.delete();
				targetFile.deleteOnExit();
			}
			if (fileLog) {
				System.out.println("Step 1: File " + i + "/" + splittedFilesCount);
			}
		}
		
		System.out.println("Step 1");
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Constants.pathToExternXMLs + fileTargetName
				+ ".svg")));
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Constants.lineSeparator);
		for (int i = 1; i <= splittedFilesCount; i++) {
			File partSVGFile = new File(Constants.pathToExternXMLs + fileTargetName + i + ".svg");
			BufferedReader reader = new BufferedReader(new FileReader(partSVGFile));
			String line = null;
			while (reader.ready()) {
				line = reader.readLine();
				if (line.indexOf("<svg") >= 0) {
					if (i == 1) {
						writer.write(line + Constants.lineSeparator);
					}
				} else {
					if (line.indexOf("<defs") >= 0) {
						if (i == 1) {
							writer.write(line + Constants.lineSeparator);
						}
					} else {
						if (line.indexOf("<g") >= 0) {
							if (i == 1) {
								writer.write(line + Constants.lineSeparator);
							}
						} else {
							if (line.indexOf("</g") >= 0) {
								if (i == splittedFilesCount) {
									writer.write(line + Constants.lineSeparator);
								}
							} else {
								if (line.indexOf("</svg") >= 0) {
									if (i == splittedFilesCount) {
										writer.write(line + Constants.lineSeparator);
									}
								} else {
									line = line.replaceAll("xmlns=\"\" ", "");
									writer.write(line + Constants.lineSeparator);
								}
							}
						}
					}
				}
			}
			reader.close();
			if (deleteFiles) {
				partSVGFile.delete();
			}
			
			if (fileLog) {
				System.out.println("Step 2: File " + i + "/" + splittedFilesCount);
			}
		}
		writer.close();
		
		System.out.println("Done");
	}
}
