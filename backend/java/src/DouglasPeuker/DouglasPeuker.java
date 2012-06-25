package DouglasPeuker;

public class DouglasPeuker {
	
	private double epsilon;
	
	DouglasPeuker(double e){
		setEpsilon(e);
	}
	
	public void setEpsilon(double wert){
		epsilon = wert;
	}
	
	public double getEpsilon(){
		return epsilon;
	}
	
	public Punkt[] linienGlaetten(Punkt[] nodes){
		return linienGlaetten(nodes, 0, nodes.length-1);
	}
	
	public Punkt[] linienGlaetten(Punkt[] nodes, int minIndex, int maxIndex){
		
		Punkt startPunkt = nodes[minIndex];
		Punkt endPunkt = nodes[maxIndex];
		
		Gerade verbindung = new Gerade(startPunkt, endPunkt);
		
		if (maxIndex-minIndex > 1){
				int indexEntferntesterPunkt = getEntferntestenPunkt(verbindung, nodes, minIndex, maxIndex);
				if (abstandGeradePunkt(nodes[indexEntferntesterPunkt], verbindung) > epsilon){
					nodes = linienGlaetten(nodes, minIndex, indexEntferntesterPunkt);
					nodes = linienGlaetten(nodes, indexEntferntesterPunkt, maxIndex);
				}else{
					for (int i = minIndex+1; i < maxIndex; i++){
						nodes[i] = null;
					}
				}
		}
		
		return nodes;
	}

	public double abstandGeradePunkt(Punkt p, Gerade g){
		
		return Math.sqrt(Math.pow(p.getX()-((g.getM()*p.getY()+p.getX()-g.getM()*g.getC())/(Math.pow(g.getM(),2)+1)), 2) +
				Math.pow(p.getY()-(g.getM()*(g.getM()*p.getY()+p.getX()-g.getM()*g.getC())/(Math.pow(g.getM(),2)+1)) + g.getC(), 2));
	}
	
	public int getEntferntestenPunkt(Gerade verbindung, Punkt[] nodes, int minIndex, int maxIndex){
		double[] abstand = new double[nodes.length];
		double groessterAbstand = 0;
		int indexEntferntesterPunkt = 0;
		for (int i = minIndex+1; i<maxIndex; i++){
			abstand[i] = abstandGeradePunkt(nodes[i], verbindung);
			if (groessterAbstand < abstand[i]){
				groessterAbstand = abstand[i];
				indexEntferntesterPunkt = i;
			}
		}
		return indexEntferntesterPunkt;
	}
}
