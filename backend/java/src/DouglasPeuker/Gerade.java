package DouglasPeuker;

public class Gerade {
	
	private double m, c;

	Gerade(Punkt p, Punkt q){
		setM((q.getY() - p.getY())/(q.getX() - p.getX()));
		setC(p.getY() - (this.getM()*p.getX()));
	}
	
	Gerade(double m, double c){
		setM(m);
		setC(c);
	}
	
	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}

	public double getM() {
		return m;
	}

	public void setM(double m) {
		this.m = m;
	}
	
	
}
