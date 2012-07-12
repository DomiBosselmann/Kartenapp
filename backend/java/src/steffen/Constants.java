
package steffen;

public class Constants {
	public static String	lineSeperator		= System.getProperty("line.separator");
	public static String	fileSeperator		= System.getProperty("file.separator");
	
	public static String	pathToExternXMLs	= ".." + fileSeperator + ".." + fileSeperator + ".." + fileSeperator + ".." + fileSeperator
																+ "Projekt Karte" + fileSeperator + "xml" + fileSeperator;
	public static String	pathToInternXSLs	= ".." + fileSeperator + "transformation" + fileSeperator + "steffen" + fileSeperator
																+ "xsl" + fileSeperator;
	
	public static double	xRatio				= 72.0;
	public static double	yRatio				= 111.32;
}
