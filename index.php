<?php
	$loggedin = "false";
	
	if (!!!session_start()) {
		session_start();
	}
	
	if ($_SESSION["loggedin"] === true) {
		$loggedin = "true";
	}
?>
<!Doctype html>
<html>
	<head>
		<title>Die Karte</title>
		<link rel="stylesheet" href="styles/main.css" />
		<link rel="stylesheet" href="styles/map.css" />
		<script type="application/javascript" src="scripts/map.js"></script>
		<script type="application/javascript" src="scripts/filter.js"></script>
		<meta charset="utf-8" />
	</head>
	<body data-loggedin="<?php echo $loggedin; ?>">
		<aside>
			<section id="flaglist">
				<nav id="toolbar">
					<button title="Route oder Punkt hinzufügen"><img src="images/add.svg" alt="Route oder Punkt hinzufügen" height="15" /></button>
					<section class="group">
						<button title="Sichern" class="rightside"><img src="images/save.svg" alt="Sichern" height="15" /></button>
						<button title="Exportieren" class="rightside"><img src="images/export.svg" alt="Exportieren" height="15" /></button>
						<button title="Importieren" class="rightside"><img src="images/import.svg" alt="Importieren" height="15" /></button>
						<button title="Suchen" class="rightside" id="searchbutton"><img src="images/search.svg" alt="Suchen" height="15" /></button>
					</section>
					<input type="search" name="searchField" id="searchField" placeholder="Suchen" />
					<p id="newFlagTooltip">Auf die Karte klicken, um Punkt hinzuzufügen</p>
				</nav>
				<div class="container">
					<h1>Strecken</h1>
					<ul id="routes"></ul>
					<h1>Orte</h1>
					<ul id="places"></ul>
				</div>
			</section>
			<form id="viewpreferences" name="viewpreferences">
				<section id="visibilities">
				</section>
				<section id="mapscaler">
					<svg width="102" height="16" id="mapscale">
						<title>Maßstab</title>
						<rect x="0" y="7" width="102" height="2"></rect>
						<rect x="0" y="0" width="2" height="16"></rect>
						<rect x="26" y="4" width="2" height="8" class="scalable"></rect>
						<rect x="51" y="4" width="2" height="8" class="scalable"></rect>
						<rect x="76" y="4" width="2" height="8" class="scalable"></rect>
						<rect x="100" y="0" width="2" height="16" id="scaler" class="scalable"></rect>
					</svg>
					<span id="scalevalue"></span>
					<!--<svg width="50" height="16" id="compass">
						<rect fill="pink" x="0" y="0" width="50" height="16"></rect>
					</svg>-->
					<!--<input type="image" name="da-muss-noch-ein-guter-name-rein" src="http://placekitten.com/20/20" />-->
					<output for="zoomlevel" form="viewpreferences"></output>
				</section>
			</form>
		</aside>
		<section id="mapcontainer">
			<svg width="1000" height="1000" id="map">
				<defs>
					<filter id="dropshadow">
				    	<feGaussianBlur result="blurOut" in="SourceAlpha" stdDeviation="2" />
				    	<feBlend in="SourceGraphic" in2="blurOut" mode="normal" />
				    </filter>
				    <filter id="streetShadow">
				    	<feGaussianBlur result="blurOut" in="SourceAlpha" stdDeviation="0.2" />
				    	<feBlend in="SourceGraphic" in2="blurOut" mode="normal" />
				    </filter>
				    <clippath id="borderClip">
				    </clippath>
					<g id="pin">
						<path d="M 0 0 q -15 -155 -100 -175 l 200 000 q -85 20 -100 175" stroke="black" stroke-width="5" fill="silver" />
						<ellipse cx="0" cy="-200" rx="120" ry="45" style="fill:grey;stroke:black;stroke-width:5" />
						<ellipse cx="0" cy="-225" rx="90" ry="30" style="fill:grey;stroke:black;stroke-width:5" />
						<ellipse cx="0" cy="-250" rx="60" ry="20" style="fill:grey;stroke:black;stroke-width:5" />
						<ellipse cx="0" cy="-270" rx="30" ry="10" style="fill:grey;stroke:black;stroke-width:5" />			
					</g>
					<g id="heli">
						<path d="M10.892,17.19"/>
						<path d="M10.892,6.294C4.878,6.303,0.015,11.169,0,17.19c0.015,5.98,4.878,10.842,10.892,10.841 c5.989,0.001,10.853-4.861,10.841-10.841C21.745,11.169,16.88,6.303,10.892,6.294z M10.892,24.084 c-3.832-0.001-6.926-3.098-6.946-6.894c0.02-3.842,3.114-6.936,6.946-6.947c3.807,0.011,6.902,3.105,6.896,6.947 C17.793,20.986,14.699,24.083,10.892,24.084z" />
						<path d="M10.892,17.19"/>
						<path d="M45.816,52.016c-8.298-0.029-8.298,11.147,0,11.141h48.068c8.075,0.007,8.075-11.17,0-11.141H45.816L45.816,52.016z"/>
						<polygon points="60.056,13.541 60.056,3.398 55.76,3.398 55.76,13.541 60.056,13.541 "/>
						<path d="M57.907,8.495"/>
						<polygon points="66.603,3.697 66.603,1.498 49.264,1.498 49.264,3.697 66.603,3.697 "/>
						<path d="M57.958,2.599"/>
						<path d="M68.101,0H95.83c3.69-0.018,3.69,5.269,0,5.246H68.101C64.527,5.269,64.527-0.018,68.101,0L68.101,0z"/>
						<path d="M20.084,0h27.38c3.69-0.018,3.69,5.269,0,5.246h-27.38C16.163,5.269,16.11-0.018,20.084,0L20.084,0z"/>
						<path d="M63.806,23.534"/>
						<path d="M75.598,23.534"/>
						<path d="M99.431,36.175l-5.547-13.392c-1.967-4.91-6.234-9.505-13.89-9.491H11.24c-4.366-0.015-5.305,5.673-1.449,7.146 l31.627,12.589l2.798,6.994c0.574,2.19,2.353,4.784,5.897,4.798h43.019C98.045,44.782,101.391,40.774,99.431,36.175z M67.501,29.479 h-7.394V17.587h7.394V29.479z M79.244,29.479h-7.346V17.587h7.346V29.479z M83.74,29.531V18.089 c2.297,0.547,4.588,2.594,6.097,5.996l2.346,5.446H83.74z"/>
					</g>
				</defs>
				<g id="flags">
				</g>
			</svg>
		</section>
		<form id="login">
			<label for="username">Name</label>
			<input type="text" name="username" />
			<label for="password">Passwort</label>
			<input type="password" name="password" />
			<input type="submit" name="login" value="Anmelden" />
		</form>
	</body>
</html>