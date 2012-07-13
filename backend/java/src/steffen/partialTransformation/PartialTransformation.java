
package steffen.partialTransformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import steffen.Constants;

public class PartialTransformation {
	
	private static String	fileSource		= "ger boundary2 p0.2.xml";
	private static String	groupID			= "bounds";
	private static String	xsltFileSource	= "part_transform.xsl";
	private static int		width				= 500;
	private static int		height			= 550;
	
	// Ger
	private static double	lon1				= 5.84;
	private static double	lon2				= 16.66;
	private static double	lat1				= 55.5;
	private static double	lat2				= 47.1;
	
	// Links: 5.8663101
	// Rechts: 16.6467723
	// Oben: 55.3372787
	// Unten: 47.236307
	
	// Bawu
	// private static double lon1 = 7.5;
	// private static double lon2 = 10.535;
	// private static double lat1 = 49.83;
	// private static double lat2 = 47.494;
	// Links: 7.5117461
	// Rechts: 10.5298008
	// Oben: 49.8188685
	// Unte: 47.4968682
	
	public static void main(String[] args) throws Exception {
		boolean delete = true;
		System.out.println("Begin Partial Transformation..");
		SplitIntoSimplierFiles.splitThisFile(PartialTransformation.fileSource, 50);
		PartialTransformation.dynamicNewPartTransformXSL(delete);
		TransformPartialXMLs.transformTheseXMLs(PartialTransformation.fileSource.replaceFirst(".xml", ".splitted"),
				PartialTransformation.xsltFileSource + "2", PartialTransformation.groupID, delete);
		System.out.println("Partial Transformation Finished!");
	}
	
	private static void dynamicNewPartTransformXSL(boolean delete) throws IOException {
		System.out.println("Begin Building XSL..");
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToInternXSLs
				+ PartialTransformation.xsltFileSource)));
		File tempXSL = new File(Constants.pathToInternXSLs + PartialTransformation.xsltFileSource + "2");
		if (delete) {
			tempXSL.deleteOnExit();
		}
		FileWriter writer = new FileWriter(tempXSL);
		
		DecimalFormat format = new DecimalFormat("#.000");
		String lon_factor = String.valueOf(format.format(width / (lon2 - lon1)));
		String lat_factor = String.valueOf(format.format(height / (lat1 - lat2)));
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			line = line.replaceFirst("~~group_id~~", PartialTransformation.groupID);
			line = line.replaceFirst("~~lon_factor~~", lon_factor);
			line = line.replaceFirst("~~lat_factor~~", lat_factor);
			line = line.replaceFirst("~~lon1~~", String.valueOf(lon1));
			line = line.replaceFirst("~~lat1~~", String.valueOf(lat1));
			writer.write(line + Constants.lineSeparator);
		}
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
}
