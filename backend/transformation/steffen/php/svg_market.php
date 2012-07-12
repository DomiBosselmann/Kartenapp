<?php

if ($_GET) {
	header("Content-Type: image/svg+xml");

	// build the svg

	$pixelsPrint = false;

	$koords = "<coords ";
	$pixels = "<pixels ";

	if (isset($_GET['lon1'])) {
		$lon1 = getPixelForLon(doubleval($_GET['lon1']));
		$koords .= 'lon1="' . $_GET['lon1'] . '" ';
		$pixels .= 'lon1="' . $lon1 . '" ';
		$region = true;
	} else {
		$lon1 = doubleval(-99999.0);
		$koords .= 'lon1="7.5117461" ';
	}
	if (isset($_GET['lon2'])) {
		$lon2 = getPixelForLon(doubleval($_GET['lon2']));
		$koords .= 'lon2="' . $_GET['lon2'] . '" ';
		$pixels .= 'lon2="' . $lon2 . '" ';
		$region = true;
	} else {
		$lon2 = doubleval(99999.0);
		$koords .= 'lon2="10.4954066" ';
	}
	if (isset($_GET['lat1'])) {
		$lat1 = getPixelForLat(doubleval($_GET['lat1']));
		$koords .= 'lat1="' . $_GET['lat1'] . '" ';
		$pixels .= 'lat1="' . $lat1 . '" ';
		$region = true;
	} else {
		$lat1 = doubleval(-99999.0);
		$koords .= 'lat1="49.7913369" ';
	}
	if (isset($_GET['lat2'])) {
		$lat2 = getPixelForLat(doubleval($_GET['lat2']));
		$koords .= 'lat2="' . $_GET['lat2'] . '" ';
		$pixels .= 'lat2="' . $lat2 . '" ';
		$region = true;
	} else {
		$lat2 = doubleval(99999.0);
		$koords .= 'lat2="47.5312706" ';
	}
	$koords .= "/>";
	$pixels .= "/>";

	$begin = '<svg xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" style="position:absolute;">';
	$end = '</svg>';

	$file = "";
	$defsBeginSearchString = '<defs>';
	$defsEndSearchString = '</defs>';
	$defs = $defsBeginSearchString;
	$layer = $_GET['l'];
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
					$nope = false;
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
						case "s1":
							{
								$filename .= "streets/trunks.svg";
								break;
							}
						case "s2":
							{
								$filename .= "streets/primaries.svg";
								break;
							}
						case "s3":
							{
								$filename .= "streets/secondaries.svg";
								break;
							}
						case "s4":
							{
								$filename .= "streets/tertiaries.svg";
								break;
							}
						case "w":
							{
								$filename .= "waters/rivers.svg";
								break;
							}
						case "w1":
							{
								$filename .= "waters/canals.svg";
								break;
							}
						case "w2":
							{
								$filename .= "waters/namedLakes.svg";
								break;
							}
						case "w3":
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
					if ($nope === false) {
						$filecontent = file_get_contents($filename);
						$defsBegin = stripos($filecontent, $defsBeginSearchString);
						if ($defsBegin !== false) {
							$defsPrint = true;
							$defsEnd = stripos($filecontent, $defsEndSearchString, $defsBegin);
							$defs .= substr($filecontent, $defsBegin + strlen($defsBeginSearchString), $defsEnd - $defsBegin - strlen($defsBeginSearchString));
							$filecontent = substr($filecontent, 0, $defsBegin) . substr($filecontent, $defsEnd + strlen($defsEndSearchString));
						}
						$file .= substr($filecontent, strlen($begin), strlen($filecontent) - strlen($begin) - strlen($end));
					}
					break;
				}
		}
	}
	$defs .= $defsEndSearchString;
	if ($defsPrint === true) {
		$file = $begin . $koords . $pixels . "\n" . $defs . "\n" . $file . $end;
	} else {
		$file = $begin . $koords . $pixels . "\n" . $file . $end;
	}

	// filter region of the svg
	if ($region) {
		if ($pixelsPrint === true) {
			$newfile = $begin . $koords . $pixels . "\n";
		} else {
			$newfile = $begin . $koords . "\n";
		}

		// defs
		if ($defsPrint === true) {
			$newfile .= $defs . "\n";
		}

		$groupSearchString = '<g';
		$useSearchString = '<use';
		$polylineSearchString = '<polyline';
		$translateSearchString = 'translate(';
		$pointsSearchString = 'points="';

		// groups
		$groupBegin = stripos($file, $groupSearchString);
		while ($groupBegin !== false) {
			$groupEnd = stripos($file, ">", $groupBegin) + 1;
			$newfile .= substr($file, $groupBegin, $groupEnd - $groupBegin);

			// places with uses
			$useBegin = stripos($file, $useSearchString, $groupEnd);
			if ($useBegin !== false) {
				while ($useBegin !== false) {
					$print = false;
					$translateBegin = stripos($file, $translateSearchString, $useBegin) + strlen($translateSearchString);
					if ($translateBegin !== false) {
						$translateEnd = stripos($file, ")", $translateBegin);
						$translate = substr($file, $translateBegin, $translateEnd - $translateBegin);
						$space = stripos($translate, " ");
						if ($space !== false) {
							$lonCoord = doubleval(substr($translate, 0, $space));
							if (($lonCoord >= $lon1) && ($lonCoord <= $lon2)) {
								$latCoord = doubleval(substr($translate, $space + 1));
								if (($latCoord >= $lat1) && ($latCoord <= $lat2)) {
									$print = true;
								}
							}
						}
					}
					$useEnd = stripos($file, "/>", $useBegin) + 2;
					if ($print === true) {
						$newfile .= substr($file, $useBegin, $useEnd - $useBegin);
					}
					$useBegin = stripos($file, $useSearchString, $useEnd);
				}
			} else {
				// other with polylines
				$polylineBegin = stripos($file, $polylineSearchString, $groupEnd);
				while ($polylineBegin !== false) {
					$print = false;
					$polylineEnd = stripos($file, '/>', $polylineBegin) + 2;
					$pointsBegin = stripos($file, $pointsSearchString, $polylineBegin) + strlen($pointsSearchString);
					$newline = substr($file, $polylineBegin, $pointsBegin - $polylineBegin);
					$pointsEnd = stripos($file, '"', $pointsBegin);
					$points = substr($file, $pointsBegin, $pointsEnd - $pointsBegin);
					$coordBegin = 0;
					while ($coordBegin < strlen($points) - 1) {
						$comma = stripos($points, ",", $coordBegin);
						$lonCoord = doubleval(substr($points, $coordBegin, $comma - $coordBegin));
						if (($lonCoord >= $lon1) && ($lonCoord <= $lon2)) {
							$space = stripos($points, " ", $coordBegin);
							$latCoord = doubleval(substr($points, $comma + 1, $space - $comma));
							if (($latCoord >= $lat1) && ($latCoord <= $lat2)) {
								$newline .= $lonCoord . "," . $latCoord . " ";
								$print = true;
							}
						}
						$coordBegin = stripos($points, " ", $coordBegin) + 1;
					}
					if ($print === true) {
						$newline .= substr($file, $pointsEnd, $polylineEnd - $pointsEnd);
						$newfile .= $newline;
					}
					$polylineBegin = stripos($file, $polylineSearchString, $polylineEnd);
				}
			}
			$newfile .= "</g>\n";
			$groupBegin = stripos($file, $groupSearchString, $groupEnd);
		}
		echo $newfile . $end;
	} else {
		echo $file;
	}
}

function getPixelForLon($lon) {
	return $lon * 150 - 1050;
}

function getPixelForLat($lat) {
	return $lat * -200 + 10000;
}

?>