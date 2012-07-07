<?php

$layer = $_GET['l'];

if ($_GET) {
	$begin = '<svg xmlns="http://www.w3.org/2000/svg" style="position:absolute;">';
	$end = '</svg>';
	header("Content-Type: image/svg+xml");

	if (isset($_GET['lon1'])) {
		$lon1 = $_GET['lon1'];
		@lon * 150 - 1050
	} else {
		$lon1 = -99999;
	}
	if (isset($_GET['lon2'])) {
		$lon2 = $_GET['lon2'];
	} else {
		$lon2 = 99999;
	}
	if (isset($_GET['lat1'])) {
		$lat1 = $_GET['lat1'];
		@lat * -200 + 10000
	} else {
		$lat1 = -99999;
	}
	if (isset($_GET['lat2'])) {
		$lat2 = $_GET['lat2'];
	} else {
		$lat2 = 99999; 
	}

	$file = $begin;
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
								$filename .= "bounds/bawubounds.svg";
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
						case "m":
							{
								$filename .= "streets/motorways.svg";
								break;
							}
						case "r":
							{
								$filename .= "waters/rivers.svg";
								break;
							}
						default:
							{
								$nope = true;
								break;
							}
					}
					if (!$nope){
						$filecontent = file_get_contents($filename);
						$file .= substr($filecontent,strlen($begin),strlen($filecontent)-strlen($begin)-strlen($end));
					}
					break;
				}
		}
	}
	echo $file.$end;
}

?>