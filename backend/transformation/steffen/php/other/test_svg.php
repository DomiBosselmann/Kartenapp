<?php 
header('Content-type="text/svg"');

$svg = <<<"SVG"
<svg xmlns="http://www.w3.org/2000/svg" >
	<defs>
		<g id="boundaries" style="fill:none;stroke:black;">
			<polyline points="150,150 200,125 360,175 325,250 350,350 360,400 310,460 250,500 205,470 175,380 90,333 143,303 120,255 80,200 105,170 150,150" />
		</g>
		<g id="city" style="fill:red;">
			<polygon points="0,0 10,0 10,10 0,10 0,0" />
		</g>
		<g id="cities">
			<g id="mannheim" transform="translate(190 270)">
				<use xlmns:xlink="http://www.w3.org/1999/xlink" xlink:href="#city" />
				<text x="15" y="0">Mannheim</text>
			</g>
			<g id="karlsruhe" transform="translate(200 340)">
				<use xlmns:xlink="http://www.w3.org/1999/xlink" xlink:href="#city" />
				<text x="15" y="0">Karlsruhe</text>
			</g>
		</g>
	</defs>
	<g transform="translate(10 -10)">
		<g style="stroke-width:2;">
			<use xlmns:xlink="http://www.w3.org/1999/xlink" xlink:href="#boundaries" />
		</g>
		<g transform="scale(1.5) translate(-80 -110)" style="stroke-width:6;">
			<use xlmns:xlink="http://www.w3.org/1999/xlink" xlink:href="#boundaries" />
		</g>
		<g transform="scale(0.5) translate(220 330)" style="stroke-width:3;">
			<use xlmns:xlink="http://www.w3.org/1999/xlink" xlink:href="#boundaries" />
		</g>
		<g>
			<use xlmns:xlink="http://www.w3.org/1999/xlink" xlink:href="#cities" />
		</g>
	</g>
</svg>
SVG;

echo $svg;

?>