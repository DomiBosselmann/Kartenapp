
package steffen.layer;

public enum Layer {
	
	Federal("federal", false), Counties("counties", false), Rivers("rivers", false), Canals("canals", false), NamedLakes(
			"namedLakes", false), UnnamedLakes("unnamedLakes", false), Motorways("motorways", false), Primaries("primaries",
			false), Secondaries("secondaries", false), Tertiaries("tertiaries", false), Cities("cities", true), Towns("towns",
			true), Villages("villages", true), Hamlets("hamlets", true), Suburbs("suburbs", true);
	
	public String	name;
	public boolean	nodeLayer;
	
	Layer(String name, boolean nodeLayer) {
		this.name = name;
		this.nodeLayer = nodeLayer;
	}
}
