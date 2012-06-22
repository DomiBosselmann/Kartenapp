package DouglasPeuker;

public class punkt {

	private double x, y;
	private String id;
	
	public punkt(double xWert, double yWert, int position){
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
