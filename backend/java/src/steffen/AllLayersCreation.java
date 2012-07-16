
package steffen;

import java.io.IOException;
import steffen.layer.FilterNodesLayer;
import steffen.layer.FilterWaysLayer;
import steffen.layer.Layer;

public class AllLayersCreation {
	
	private static String	originFileName	= "bawu";
	private static boolean	deleteOldFiles	= true;
	
	public static void main(String[] args) throws IOException {
		System.out.println("Begin all layers creation...");
		for (Layer layer : Layer.values()) {
			System.out.println("Layer " + (layer.ordinal() + 1) + "/" + Layer.values().length);
			if (layer.nodeLayer) {
				FilterNodesLayer.filterNodesLayer(originFileName + ".xml", layer, deleteOldFiles);
			} else {
				FilterWaysLayer.filterWaysLayer(originFileName + ".xml", layer, deleteOldFiles);
			}
		}
		System.out.println("Finished all layers creation!");
	}
}
