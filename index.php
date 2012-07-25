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