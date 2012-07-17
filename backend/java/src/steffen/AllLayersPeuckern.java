
package steffen;

import java.io.IOException;
import steffen.layer.Layer;
import steffen.peucker.Peuckern;

public class AllLayersPeuckern {
	
	private static String	originFileName		= "bawu";
	private static boolean	deleteOldFiles		= true;
	private static double	peuckerDistance	= 0.4;
	
	public static void main(String[] args) throws IOException {
		System.out.println("Begin all layers peuckern...");
		for (Layer layer : Layer.values()) {
			if (!layer.nodeLayer) {
				System.out.println("Layer " + (layer.ordinal() + 1) + "/" + Layer.values().length);
				Peuckern.peuckern(originFileName + " " + layer.name + ".xml", peuckerDistance, deleteOldFiles);
			}
		}
		System.out.println("Finished all layers peuckern!");
	}
}
