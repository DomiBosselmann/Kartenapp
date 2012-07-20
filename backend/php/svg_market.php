<?php

// Obsolete

if ($_GET) {
	header("Content-Type: image/svg+xml");

	// coordinate detection
	$pixelsPrint = false;

	$koords = "<coords ";
	if ($pixelsPrint === true) {
		$pixels = "<pixels ";
	}

	if (isset($_GET['lon1'])) {
		$lon1 = getPixelForLon(doubleval($_GET['lon1']));
		$koords .= 'lon1="' . $_GET['lon1'] . '" ';
		if ($pixelsPrint === true) {
			$pixels .= 'lon1="' . $lon1 . '" ';
		}
		$region = true;
	} else {
		$lon1 = doubleval(-99999.0);
		$koords .= 'lon1="7.5117461" ';
	}
	if (isset($_GET['lon2'])) {
		$lon2 = getPixelForLon(doubleval($_GET['lon2']));
		$koords .= 'lon2="' . $_GET['lon2'] . '" ';
		if ($pixelsPrint === true) {
			$pixels .= 'lon2="' . $lon2 . '" ';
		}
		$region = true;
	} else {
		$lon2 = doubleval(99999.0);
		$koords .= 'lon2="10.4954066" ';
	}
	if (isset($_GET['lat1'])) {
		$lat1 = getPixelForLat(doubleval($_GET['lat1']));
		$koords .= 'lat1="' . $_GET['lat1'] . '" ';
		if ($pixelsPrint === true) {
			$pixels .= 'lat1="' . $lat1 . '" ';
		}
		$region = true;
	} else {
		$lat1 = doubleval(-99999.0);
		$koords .= 'lat1="49.7913369" ';
	}
	if (isset($_GET['lat2'])) {
		$lat2 = getPixelForLat(doubleval($_GET['lat2']));
		$koords .= 'lat2="' . $_GET['lat2'] . '" ';
		if ($pixelsPrint === true) {
			$pixels .= 'lat2="' . $lat2 . '" ';
		}
		$region = true;
	} else {
		$lat2 = doubleval(99999.0);
		$koords .= 'lat2="47.5312706" ';
	}
	$koords .= "/>";
	if ($pixelsPrint === true) {
		$pixels .= "/>";
	}

	// width + height detection

	if (isset($_GET['width'])) {
		$sizing = true;
		$width = $_GET['width'];
	} else {
		$width = 500.0;
	}
	if (isset($_GET['height'])) {
		$sizing = true;
		$height = $_GET['height'];
	} else {
		$height = 550.0;
	}

	// build the svg

	$begin = '<svg style="position:absolute;" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg">';
	$end = '</svg>';

	$file = "";
	$groupBeginSearchString = '<g';
	$groupEndSearchString = '</g>';
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
			case "width":
				{
					break;
				}
			case "height":
				{
					break;
				}
			case "custom":
				{
					break;
				}
			case "border":
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
								$filename .= "streets/primaries.svg";
								break;
							}
						case "s2":
							{
								$filename .= "streets/secondaries.svg";
								break;
							}
						case "s3":
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
						$groupBeginn = stripos($filecontent, $groupBeginSearchString);
						$groupEndd = stripos($filecontent, $groupEndSearchString, $groupBeginn) + strlen($groupEndSearchString);
						$file .= substr($filecontent, $groupBeginn, $groupEndd - $groupBeginn) . PHP_EOL;
					}
					break;
				}
		}
	}
	$defs .= $defsEndSearchString;

	$nfile = $file;
	$file = $begin . PHP_EOL . $koords;
	if ($pixelsPrint === true) {
		$file .= PHP_EOL . $pixels;
	}
	$file .= PHP_EOL;
	if ($defsPrint === true) {
		$file .= $defs . PHP_EOL;
	}
	$file .= $nfile . $end;


	// filter region of the svg
	// TODO width+height unterstützen
	if ($region) {
		$newfile = $begin . PHP_EOL . $koords;
		if ($pixelsPrint === true) {
			$newfile .= PHP_EOL . $pixels;
		}
		$newfile .= PHP_EOL;
		if ($defsPrint === true) {
			$newfile .= $defs . PHP_EOL;
		}

		$useSearchString = '<use';
		$polylineSearchString = '<polyline';
		$translateSearchString = 'translate(';
		$pointsSearchString = 'points="';

		// groups
		$groupBegin = stripos($file, $groupBeginSearchString);
		while ($groupBegin !== false) {
			$groupEnd = stripos($file, ">", $groupBegin) + 1;
			$newfile .= substr($file, $groupBegin, $groupEnd - $groupBegin) . PHP_EOL;

			// places with uses
			$useBegin = stripos($file, $useSearchString, $groupEnd);
			$groupEnding = stripos($file, $groupEndSearchString, $groupEnd);
			if (($useBegin !== false) && ($useBegin < $groupEnding)) {
				while (($useBegin !== false) && ($useBegin < $groupEnding)) {
					$useEnd = stripos($file, "/>", $useBegin) + 2;
					$use = substr($file, $useBegin, $useEnd - $useBegin);
					$print = false;
					$translateBegin = stripos($use, $translateSearchString) + strlen($translateSearchString);
					if ($translateBegin !== false) {
						$translateEnd = stripos($use, ")", $translateBegin) + 1;
						$translate = substr($use, $translateBegin, $translateEnd - $translateBegin);
						$space = stripos($translate, " ");
						if ($space !== false) {
							$lonCoord = doubleval(substr($translate, 0, $space));
							if (($lonCoord >= $lon1) && ($lonCoord <= $lon2)) {
								$latCoord = doubleval(substr($translate, $space + 1));
								if (($latCoord >= $lat1) && ($latCoord <= $lat2)) {
									$print = true;
									if ($sizing === true) {
										// TODO $translate bearbeiten
									}
								}
							}
						}
					}
					if ($print === true) {
						$newfile .= "<use " . $translate . substr($use, $translateEnd);
						//	$newfile .= substr($file, $useBegin, $useEnd - $useBegin) . PHP_EOL;
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
								$print = true;
								if ($sizing === true) {
									$newline .= ($lonCoord * (500 / $width)) . "," . ($latCoord * (550 / $height)) . " ";
								} else {
									$newline .= $lonCoord . "," . $latCoord . " ";
								}
							}
						}
						$coordBegin = stripos($points, " ", $coordBegin) + 1;
					}
					if ($print === true) {
						$newline .= substr($file, $pointsEnd, $polylineEnd - $pointsEnd);
						$newfile .= $newline . PHP_EOL;
					}
					$polylineBegin = stripos($file, $polylineSearchString, $polylineEnd);
				}
			}
			$newfile .= $groupEndSearchString . PHP_EOL;
			$groupBegin = stripos($file, $groupBeginSearchString, $groupEnd);
		}
		echo $newfile . $end;
	} else {
		echo $file;
	}
}

function getPixelForLon($lon) {
	return ($lon - 7.5) * 164.745;
}

function getPixelForLat($lat) {
	return (-$lat + 49.83) * 235.445;
}

?>