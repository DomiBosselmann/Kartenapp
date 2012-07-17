
package steffen;

public class Constants {
	
	public static String	lineSeparator		= System.getProperty("line.separator", "\n");
	public static String	fileSeparator		= System.getProperty("file.separator", "/");
	
	public static String	pathToExternXMLs	= ".." + fileSeparator + ".." + fileSeparator + ".." + fileSeparator + ".."
																+ fileSeparator + "Projekt Karte" + fileSeparator + "xml" + fileSeparator;
	public static String	pathToInternXSLs	= ".." + fileSeparator + "transformation" + fileSeparator + "steffen"
																+ fileSeparator + "xsl" + fileSeparator;
	
	public static double	xRatio				= 72.0;
	public static double	yRatio				= 111.32;
}
