<?php

if ($_GET) {
	header("Content-Type: image/svg+xml");

	// coordinate detection
	$region = false;
	$koords = "<coords ";
	if (isset($_GET['lon1'])) {
		$lon1 = getPixelForLon(doubleval($_GET['lon1']));
		$koords .= 'lon1="' . $_GET['lon1'] . '" ';
		$region = true;
	} else {
		$lon1 = getPixelForLon(doubleval(7.483968));
		$koords .= 'lon1="7.483968" ';
	}
	if (isset($_GET['lon2'])) {
		$lon2 = getPixelForLon(doubleval($_GET['lon2']));
		$koords .= 'lon2="' . $_GET['lon2'] . '" ';
		$region = true;
	} else {
		$lon2 = getPixelForLon(doubleval(10.557579));
		$koords .= 'lon2="10.557579" ';
	}
	if (isset($_GET['lat1'])) {
		$lat1 = getPixelForLat(doubleval($_GET['lat1']));
		$koords .= 'lat1="' . $_GET['lat1'] . '" ';
		$region = true;
	} else {
		$lat1 = getPixelForLat(doubleval(49.836835));
		$koords .= 'lat1="49.836835" ';
	}
	if (isset($_GET['lat2'])) {
		$lat2 = getPixelForLat(doubleval($_GET['lat2']));
		$koords .= 'lat2="' . $_GET['lat2'] . '" ';
		$region = true;
	} else {
		$lat2 = getPixelForLat(doubleval(47.478901));
		$koords .= 'lat2="47.478901" ';
	}
	$koords .= "/>";

	// width + height detection
	$originWidth = 490.0;
	$originHeight = 510.0;
	if (isset($_GET['width'])) {
		$sizing = true;
		$width = $_GET['width'];
	} else {
		$width = $originWidth;
	}
	if (isset($_GET['height'])) {
		$sizing = true;
		$height = $_GET['height'];
	} else {
		$height = $originHeight;
	}
	if ($_GET['custom']) {
		$widthFactor = $width / $originWidth;
		$heightFactor = $height / $originHeight;
	} else {
		// TODO verh�ltnism��ige streckung
	}

	if ($region) {
		$delta_x = $lon2 - $lon1;
		$widthFactor = $width / $delta_x;
		$delta_y = $lat2 - $lat1;
		$heightFactor = $height / $delta_y;
		// 		echo $delta_x . PHP_EOL;
		// 		echo $delta_y . PHP_EOL;
		// 		echo $widthFactor . PHP_EOL;
		// 		echo $heightFactor . PHP_EOL;
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
	$file = $begin . PHP_EOL . $koords . PHP_EOL;
	if ($defsPrint === true) {
		$file .= $defs . PHP_EOL;
	}
	$file .= $nfile . $end;


	// region and width+height customizing of the svg
	// TODO width+height unterst�tzen

	$newfile = $begin . PHP_EOL . $koords . PHP_EOL;
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
					$translateEnd = stripos($use, ")", $translateBegin);
					$translate = substr($use, $translateBegin, $translateEnd - $translateBegin);
					$space = stripos($translate, " ");
					if ($space !== false) {
						$lonCoord = doubleval(substr($translate, 0, $space));
						$latCoord = doubleval(substr($translate, $space + 1));
						if ($region === true) {
							if (($lonCoord >= $lon1) && ($lonCoord <= $lon2)) {
								if (($latCoord >= $lat1) && ($latCoord <= $lat2)) {
									$print = true;
								}
							}
						}
						if ($region === true) {
							$newLonCoord = $lonCoord - $lon1;
							$newLatCoord = $latCoord - $lat1;
							$newLonCoord *= $widthFactor;
							$newLatCoord *= $heightFactor;
							$translate = str_replace($lonCoord, number_format($newLonCoord, 3, '.', ''), $translate);
							$translate = str_replace($latCoord, number_format($newLatCoord, 3, '.', ''), $translate);
						} else {
							if ($sizing === true) {
								$newLonCoord = $lonCoord * $widthFactor;
								$newLatCoord = $latCoord * $heightFactor;
								$translate = str_replace($lonCoord, number_format($newLonCoord, 3, '.', ''), $translate);
								$translate = str_replace($latCoord, number_format($newLatCoord, 3, '.', ''), $translate);
							}
						}
					}
				}
				if (($region === false) || ($print === true)) {
					$newfile .= '<use transform="translate(' . $translate . substr($use, $translateEnd) . PHP_EOL;
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
					$space = stripos($points, " ", $coordBegin);
					$lonCoord = doubleval(substr($points, $coordBegin, $comma - $coordBegin));
					$latCoord = doubleval(substr($points, $comma + 1, $space - $comma));
					if ($region === true) {
						if (($lonCoord >= $lon1) && ($lonCoord <= $lon2)) {
							if (($latCoord >= $lat1) && ($latCoord <= $lat2)) {
								$print = true;
								$newLonCoord = $lonCoord - $lon1;
								$newLatCoord = $latCoord - $lat1;
								$newLonCoord *= $widthFactor;
								$newLatCoord *= $heightFactor;
								$newline .= number_format($newLonCoord, 3, '.', '') . "," . number_format($newLatCoord, 3, '.', '') . " ";
							}
						}
					} else {
						if ($sizing === true) {
							$newline .= number_format(($lonCoord * $widthFactor), 3, '.', '') . "," . number_format(($latCoord * $heightFactor), 3, '.', '') . " ";
						} else {
							$newline .= $lonCoord . "," . $latCoord . " ";
						}
					}
					$coordBegin = stripos($points, " ", $coordBegin) + 1;
				}
				if (($region === false ) || ($print === true)) {
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
}

function getPixelForLon($lon) {
	// f�r 500px
	return ($lon - 7.483968) * 162.675;
}

function getPixelForLat($lat) {
	// f�r 500px
	return (-$lat + 49.83) * 218.716;
}

?>