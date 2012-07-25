
package steffen.partialTransformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.xml.transform.TransformerException;
import steffen.Constants;
import steffen.layer.Layer;

public class PartialTransformation {
	
	private static Layer		myLayer				= Layer.Tertiaries;
	private static boolean	myBawu				= true;
	
	private static int		width					= 500;
	private static int		height				= 500;
	private static boolean	deleteTempFiles	= true;
	
	private static double	lon1;
	private static double	lon2;
	private static double	lat1;
	private static double	lat2;
	
	public static void main(String[] args) throws Exception {
		PartialTransformation.transformThisXML(PartialTransformation.myBawu, PartialTransformation.myLayer);
	}
	
	public static void transformThisXML(boolean bawu, Layer layer) throws IOException, TransformerException {
		System.out.println("Begin partial transformation for " + layer.name + "...");
		
		if (bawu) {
			PartialTransformation.lon1 = 7.5117461;
			PartialTransformation.lon2 = 10.5298008;
			PartialTransformation.lat1 = 49.8188685;
			PartialTransformation.lat2 = 47.4968682;
		} else {
			PartialTransformation.lon1 = 5.8663101;
			PartialTransformation.lon2 = 16.6467723;
			PartialTransformation.lat1 = 55.3372787;
			PartialTransformation.lat2 = 47.236307;
		}
		
		String fileSource = "";
		if (bawu) {
			fileSource += "bawu ";
		} else {
			fileSource += "ger ";
		}
		fileSource += layer.name + ".xml";
		
		SplitIntoSimplierFiles.splitThisFile(fileSource, layer);
		
		PartialTransformation.dynamicNewPartTransformXSL(layer, PartialTransformation.deleteTempFiles);
		
		TransformPartialXMLs.transformTheseXMLs(fileSource.replaceFirst(".xml", " splitted"), layer,
				PartialTransformation.deleteTempFiles);
		
		System.out.println("Partial transformation finished!");
	}
	
	private static void dynamicNewPartTransformXSL(Layer layer, boolean delete) throws IOException {
		System.out.println("Begin building xsl for " + layer.name + "...");
		
		String xslFileSource = null;
		String rect_id = null;
		String rect_fill = null;
		String rect_coord = null;
		String rect_size = null;
		if (layer.nodeLayer) {
			xslFileSource = "places_part_transform.xsl";
			switch (layer) {
				case Cities: {
					rect_id = "city";
					rect_fill = "red";
					rect_coord = "-5";
					rect_size = "10";
					break;
				}
				case Towns: {
					rect_id = "town";
					rect_fill = "red";
					rect_coord = "-4";
					rect_size = "8";
					break;
				}
				case Villages: {
					rect_id = "village";
					rect_fill = "red";
					rect_coord = "-3";
					rect_size = "6";
					break;
				}
				case Hamlets: {
					rect_id = "hamlet";
					rect_fill = "red";
					rect_coord = "-2";
					rect_size = "4";
					break;
				}
				case Suburbs: {
					rect_id = "suburb";
					rect_fill = "orange";
					rect_coord = "-1";
					rect_size = "2";
					break;
				}
				default: {
					System.exit(1);
					break;
				}
			}
		} else {
			xslFileSource = "ways_part_transform.xsl";
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToInternXSLs + "partial/"
				+ xslFileSource)));
		String newXSLFileName = xslFileSource + "2";
		File tempXSL = new File(Constants.pathToInternXSLs + "partial/" + newXSLFileName);
		if (delete) {
			tempXSL.deleteOnExit();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempXSL));
		
		DecimalFormat format = new DecimalFormat("#.000");
		double lon_factor = PartialTransformation.width / (PartialTransformation.lon2 - PartialTransformation.lon1);
		double lat_factor = PartialTransformation.height / (PartialTransformation.lat1 - PartialTransformation.lat2);
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			line = line.replaceFirst("~~group_id~~", layer.name);
			line = line.replaceFirst("~~lon_factor~~", format.format(lon_factor));
			line = line.replaceFirst("~~lat_factor~~", format.format(lat_factor));
			line = line.replaceFirst("~~lon1~~", format.format(PartialTransformation.lon1));
			line = line.replaceFirst("~~lat1~~", format.format(PartialTransformation.lat1));
			if (layer.nodeLayer) {
				line = line.replaceFirst("~~rect_id~~", rect_id);
				line = line.replaceFirst("~~rect_fill~~", rect_fill);
				line = line.replaceFirst("~~rect_coord~~", rect_coord);
				line = line.replaceFirst("~~rect_size~~", rect_size);
			}
			writer.write(line + Constants.lineSeparator);
		}
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
}
