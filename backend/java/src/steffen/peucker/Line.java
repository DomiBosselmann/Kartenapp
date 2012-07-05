
package steffen.peucker;

public class Line {
	private static Double	xratio	= 72.0;
	private static Double	yratio	= 111.0;
	
	public static void main(String[] args) {
		// Tests
		Line line = new Line(0.0, 0.0, 0.0, 1.0);
		System.out.println(Line.xratio + "?: " + line.getDistanceOfPoint(1.0, 0.5));
		line = new Line(0.0, 0.0, 1.0, 0.0);
		System.out.println(Line.yratio + "?: " + line.getDistanceOfPoint(0.5, 1.0));
	}
	
	private Double	lon1;
	private Double	lat1;
	private Double	lon2;
	private Double	lat2;
	
	private Double	c;
	
	public Line(Double lon1, Double lat1, Double lon2, Double lat2) {
		this.lon1 = lon1;
		this.lat1 = lat1;
		this.lon2 = lon2;
		this.lat2 = lat2;
		this.c = Math.sqrt(Math.pow((lon1 - lon2) * Line.xratio, 2) + Math.pow((lat1 - lat2) * Line.yratio, 2));
	}
	
	public Double getDistanceOfPoint(Double lon, Double lat) {
		Double a = Math.sqrt(Math.pow((lon - this.lon1) * Line.xratio, 2) + Math.pow((lat - this.lat1) * Line.yratio, 2));
		Double b = Math.sqrt(Math.pow((lon - this.lon2) * Line.xratio, 2) + Math.pow((lat - this.lat2) * Line.yratio, 2));
		return Math.sqrt(2
				* (Math.pow(a, 2) * Math.pow(b, 2) + Math.pow(b, 2) * Math.pow(this.c, 2) + Math.pow(this.c, 2) * Math.pow(a, 2))
				- (Math.pow(a, 4) + Math.pow(b, 4) + Math.pow(this.c, 4)))
				/ (2 * this.c);
	}
}
