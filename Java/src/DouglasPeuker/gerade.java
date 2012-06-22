package DouglasPeuker;

public class gerade {
	
	private double m, c;

	gerade(punkt p, punkt q){
		setM((q.getY() - p.getY())/(q.getX() - p.getX()));
		setC(p.getY() - (this.getM()*p.getX()));
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
