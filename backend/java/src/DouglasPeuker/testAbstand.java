package DouglasPeuker;

public class testAbstand {
	
	public static void main(String[] args){
		
		Punkt p = new Punkt(2, 0, "0");
		
		Gerade g = new Gerade(1, 0);
		
		DouglasPeuker help = new DouglasPeuker(200);
		
		System.out.println(help.abstandGeradePunkt(p,g));
	}
	
}
