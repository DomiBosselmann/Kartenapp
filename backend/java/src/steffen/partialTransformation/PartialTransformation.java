
package steffen.partialTransformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import steffen.Constants;

public class PartialTransformation {
	
	private static String	fileSource		= "bawu rivers2 p0.02.xml";
	private static String	groupID			= "canals";
	private static String	xsltFileSource	= "part_transform.xsl";
	private static int		width				= 500;
	private static int		height			= 550;
	private static double	lon1				= 7.5;
	private static double	lon2				= 10.535;
	private static double	lat1				= 49.83;
	private static double	lat2				= 47.494;
	
	// private static double lon1 = 7.5117461;
	// private static double lon2 = 10.5298008;
	// private static double lat1 = 49.8188685;
	// private static double lat2 = 47.4968682;
	
	public static void main(String[] args) throws Exception {
//		DecimalFormat format = new DecimalFormat("#.000");
//		String lon_factor = String.valueOf(format.format(width / (lon2 - lon1)));
//		String lat_factor = String.valueOf(format.format(height / (lat1 - lat2)));
//		System.out.println(lon_factor + " " + lat_factor);
//		System.exit(0);
		System.out.println("Begin Partial Transformation..");
		System.out.println("Begin Splitting Files...");
		SplitIntoSimplierFiles.splitThisFile(PartialTransformation.fileSource, 10);
		System.out.println("Begin Building XSL..");
		PartialTransformation.dynamicNewPartTransformXSL();
		System.out.println("Begin Transforming Partial Files..");
		TransformPartialXMLs.transformTheseXMLs(PartialTransformation.fileSource.replaceFirst(".xml", ".splitted"),
				PartialTransformation.xsltFileSource + "2", PartialTransformation.groupID, true);
		System.out.println("Partial Transformation Finished!");
	}
	
	private static void dynamicNewPartTransformXSL() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToInternXSLs
				+ PartialTransformation.xsltFileSource)));
		File tempXSL = new File(Constants.pathToInternXSLs + PartialTransformation.xsltFileSource + "2");
		tempXSL.deleteOnExit();
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
			writer.write(line + Constants.lineSeperator);
		}
		reader.close();
		writer.close();
		System.out.println("Done");
	}
}
