
package steffen.partialTransformation;

public class PartialTransformation {
	
	private static String	fileSource		= "bawu boundary.xml";
	private static String	fileTargetName	= "boundary";
	private static String	xsltFileSource	= "part_transform.xsl";
	
	public static void main(String[] args) throws Exception {
		System.out.println("Begin Partial Transformation..");
		System.out.println("Begin Splitting Files...");
		SplitIntoSimplierFiles.splitThisFile(PartialTransformation.fileSource, 10);
		System.out.println("Begin Transforming Partial Files..");
		TransformPartialXMLs.transformTheseXMLs(PartialTransformation.fileSource.replaceFirst(".xml", " splitted "),
				PartialTransformation.xsltFileSource, PartialTransformation.fileTargetName, true);
		System.out.println("Partial Transformation Finished!");
	}
}
