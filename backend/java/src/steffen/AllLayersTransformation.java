
package steffen;

import java.io.IOException;
import javax.xml.transform.TransformerException;
import steffen.layer.Layer;
import steffen.partialTransformation.PartialTransformation;

public class AllLayersTransformation {
	
	private static boolean	bawu	= true;
	
	public static void main(String[] args) throws IOException, TransformerException {
		System.out.println("Begin all layers transformation...");
		
		for (Layer layer : Layer.values()) {
			System.out.println("Layer " + (layer.ordinal() + 1) + "/" + Layer.values().length);
			PartialTransformation.transformThisXML(AllLayersTransformation.bawu, layer);
		}
		
		System.out.println("Finished all layers transformation!");
	}
}
