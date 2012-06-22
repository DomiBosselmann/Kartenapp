package DouglasPeuker;

public class Punkt {

	private double x, y;
	private String id;
	
	public Punkt(double xWert, double yWert, String id){
		setX(xWert);
		setY(yWert);
		setID(id);
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public void setX(double wert){
		x = wert;
	}
	
	public void setY(double wert){
		y = wert;
	}
	
	public void setID(String wert){
		id = wert;
	}
	
	public String getID(){
		return id;
	}
	
}
