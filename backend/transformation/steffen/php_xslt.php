<?php

$xmlfile = "http://www.schteffens.de/karte/pretransform.php?";
if ($_GET['l']) {
	$xmlfile .= "l=" . $_GET['l'] . "&";
}
if ($_GET['lat1']) {
	$xmlfile .= "lat1=" . $_GET['lat1'] . "&";
}
if ($_GET['lat2']) {
	$xmlfile .= "lat2=" . $_GET['lat2'] . "&";
}
if ($_GET['lon1']) {
	$xmlfile .= "lon1=" . $_GET['lon1'] . "&";
}
if ($_GET['lon2']) {
	$xmlfile .= "lon2=" . $_GET['lon2'] . "&";
}

$xml = simplexml_load_file($xmlfile);
$xml2 = simplexml_load_file($xmlfile);

echo "<svg xmlns=\"http://www.w3.org/2000/svg\">\n<g style=\"fill:none;stroke-width:1;\" transform=\"scale(1) translate(-4400 -700)\" >\n";

foreach ($xml as $node0 => $value0) {
	switch ($node0) {
		case "way":
			{
				$line = "<polyline stroke=\"black\" points=\"";

				foreach ($value0->children() as $childkey0 => $childvalue0) {
					switch ($childkey0) {
						case 'nd':
							{
								foreach ($childvalue0->attributes() as $attributeskey0 => $attributesvalue0) {
									switch ($attributeskey0) {
										case 'ref':
											{
												foreach ($xml2 as $node1 => $value1) {
													if ($node1 == 'node') {
														foreach ($value1->attributes() as $attributeskey1 => $attributesvalue1) {
															if ($attributeskey1 == 'id') {
																if (''.$attributesvalue1 == ''.$attributesvalue0){
																	foreach ($value1->attributes() as $attributeskey2 => $attributesvalue2) {
																		if ($attributeskey2 == 'lat') {
																			$lat = (double) $attributesvalue2;
																			$line .= "" . $lat * 100.0 . ",";
																		} else {
																			if ($attributeskey2 == 'lon') {
																				$lon = (double) $attributesvalue2;
																				$line .= "" . $lon * 100.0 . " ";
																			}
																		}
																	}
																}
															}
														}
													}
												}
												break;
											}
									}
								}
								break;
							}
					}
				}
				$line .= "\" />";
				echo $line . "\n";
				break;
			}
		default:
			{
				break;
			}
	}
}

echo "</g>\n</svg>\n";

?>