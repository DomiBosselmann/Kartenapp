package DouglasPeuker;

import java.io.IOException;
import java.util.ArrayList;

public class testBerechneKoords extends berechneKoord {
	
	Punkt[] point = new Punkt[3];
	static berechneKoord koord = new berechneKoord();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList();
		list.add("289645");
		list.add("13354924");
		list.add("13355228");

		
		try {
			koord.berechneLaenge(list);
			System.out.println("erfolgreich");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
