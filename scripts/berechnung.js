var zoom=2; /* Zoomstufe am Beginn */

function setting(zoom){

	pxProM= Math.round((maxWidth*(1/Math.pow(2,zoom)))/800); 
	
	switch(zoom) {
		case"0": minLat=5;
				 maxLat=13;
				 minLong=54;
				 maxLong=46;
				 break;
		case"1": minLat=7;
				 maxLat=11;
				 minLong=52;
				 maxLong=48;
				 break;
		case"2": minLat=8;
				 maxLat=10;
				 minLong=51;
				 maxLong=49;
				 break;
		case"3": minLat=9;
				 maxLat=10;
				 minLong=51;
				 maxLong=50;
				 break;
		case"4": minLat=9.5;
				 maxLat=10;
				 minLong=51;
				 maxLong=50.5;
				 break;
	}
	
	
}