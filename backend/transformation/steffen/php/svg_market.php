<?php

if ($_GET) {
	header("Content-Type: image/svg+xml");

	$koords = "<coords ";

	if (isset($_GET['lon1'])) {
		$lon1 = $_GET['lon1'] * 150 - 1050;
		$koords .= "lon1=\"" . $_GET['lon1'] . "\" ";
	} else {
		$lon1 = -99999;
		$koords .= "lon1=\"7.5117461\" ";
	}
	if (isset($_GET['lon2'])) {
		$lon2 = $_GET['lon2'] * 150 - 1050;
		$koords .= "lon2=\"" . $_GET['lon2'] . "\" ";
	} else {
		$lon2 = 99999;
		$koords .= "lon2=\"10.4954066\" ";
	}
	if (isset($_GET['lat1'])) {
		$lat1 = $_GET['lat1'] * -200 + 10000;
		$koords .= "lat1=\"" . $_GET['lat1'] . "\" ";
	} else {
		$lat1 = -99999;
		$koords .= "lat1=\"49.7913369\" ";
	}
	if (isset($_GET['lat2'])) {
		$lat2 = $_GET['lat2'] * -200 + 10000;
		$koords .= "lat2=\"" . $_GET['lat2'] . "\" ";
	} else {
		$lat2 = 99999;
		$koords .= "lat2=\"47.5312706\" ";
	}
	$koords .= "/>";

	$begin = '<svg xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" style="position:absolute;">';
	$end = '</svg>';

	$layer = $_GET['l'];
	$file = $begin . $koords;
	foreach ($_GET as $para => $layer) {
		switch ($para) {
			case "lon1":
				{
					break;
				}
			case "lon2":
				{
					break;
				}
			case "lat1":
				{
					break;
				}
			case "lat2":
				{
					break;
				}
			default:
				{
					$filename = "../../../svgs/";
					switch ($layer) {
						case "b":
							{
								$filename .= "bounds/federal.svg";
								break;
							}
						case "b1":
							{
								$filename .= "bounds/counties.svg";
								break;
							}
						case "c":
							{
								$filename .= "cities/cities.svg";
								break;
							}
						case "c1":
							{
								$filename .= "cities/towns.svg";
								break;
							}
						case "c2":
							{
								$filename .= "cities/villages.svg";
								break;
							}
						case "c3":
							{
								$filename .= "cities/hamlets.svg";
								break;
							}
						case "c4":
							{
								$filename .= "cities/suburbs.svg";
								break;
							}
						case "s":
							{
								$filename .= "streets/motorways.svg";
								break;
							}
						case "w":
							{
								$filename .= "waters/rivers.svg";
								break;
							}
						case "w1":
							{
								$filename .= "waters/namedLakes.svg";
								break;
							}
						case "w2":
							{
								$filename .= "waters/allLakes.svg";
								break;
							}
						default:
							{
								$nope = true;
								break;
							}
					}
					if (!$nope) {
						$filecontent = file_get_contents($filename);
						$file .= substr($filecontent,strlen($begin),strlen($filecontent)-strlen($begin)-strlen($end));
					}
					break;
				}
		}
	}

	echo $file . $end;
}

?>