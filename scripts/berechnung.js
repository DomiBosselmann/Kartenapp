var pxProM =0;
var pxWidth=800; /* Breite Anzeige*/
var pxHeight=600;/*Höhe Anzeige*/

var maxWidth = 890556;  /* Maximale Breite und Höhe in Metern*/
var maxHeight = 890556;

var zoom=2; /* Zoomstufe am Beginn */

var minLat=5; /*Kleinster Breitengrad*/
var maxLat=13;/*Größter Breitengrad*/
var minLong=54;/*Kleinseter Längengrad*/
var maxLong=46;/*Größter Breitengrad*/

/*Aus Dezimalkoordinaten berechnete Pixelwerte*/
var xPixel; 
var yPixel;

/*Eingabewerte in Dezimalkoordinatenform*/ 
var inpX=11.15;
var inpY=49.1235;

function setting(zoom){

	pxProM= Math.round((maxWidth*(1/Math.pow(2,zoom)))/800); 
	
	switch(zoom)
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

//Rechnet Dezimalkoordinaten in Pixelwerte um.
function calculate(inpX,inpY){
	xPixel= (inpX-minLat)/(maxLat-minLat)*800;
	yPixel= (inpY-minLong)/(maxLong-minLong)*600;
	
	alert(xPixel+","+yPixel+" pxProM:" +pxProM);
}