<?php 

// Deprecated/Obsolete

if (isset($_GET['lat1'])) {
	$lat1 = $_GET['lat1'];
} else {
	$lat1 = -99999;
}

if (isset($_GET['lat2'])) {
	$lat2 = $_GET['lat2'];
} else {
	$lat2 = 99999;
}

if (isset($_GET['lon1'])) {
	$lon1 = $_GET['lon1'];
} else {
	$lon1 = -99999;
}

if (isset($_GET['lon2'])) {
	$lon2 = $_GET['lon2'];
} else {
	$lon2 = 99999;
}

$xmlfile = "../../../xmls/cities/";
switch ($_GET[l]) {
	case "c":
		{
			$xmlfile .= "cities.xml";
			break;
		}
	case "c1":
		{
			$xmlfile .= "towns.xml";
			break;
		}
	case "c2":
		{
			$xmlfile .= "villages.xml";
			break;
		}
	case "c3":
		{
			$xmlfile .= "hamlets.xml";
			break;
		}
	case "c4":
		{
			$xmlfile .= "suburbs.xml";
			break;
		}
	default:
		{
			$xmlfile .= "cities.xml";
			break;
		}
}

$xml = simplexml_load_file($xmlfile);

echo "<osm>\n";

foreach ($xml as $node0 => $value0) {
	switch ($node0) {
		case "node":
			{
				$print1 = false;
				$print2 = false;
				$line = "<" . $node0 . " ";
				foreach ($value0->attributes() as $attributeskey0 => $attributesvalue0) {
					switch ($attributeskey0) {
						case 'lat':
							{
								if (($attributesvalue0 >= $lat1) && ($attributesvalue0 <= $lat2)) {
									$print1 = true;
								}
							}
						case 'lon':
							{
								if (($attributesvalue0 >= $lon1) && ($attributesvalue0 <= $lon2)) {
									$print2 = true;
								}
							}
						default:
							{
								$line .= $attributeskey0 . '="' . $attributesvalue0 . '" ';
								break;
							}
					}
				}
				if ($print1 && $print2) {
					$line .= ">\n";
					foreach ($value0->children() as $node1 => $value1) {
						$line .= "<" . $node1 . " ";
						foreach ($value1->attributes() as $attributeskey1 => $attributesvalue1) {
							$line .= $attributeskey1 . '="' . $attributesvalue1 . '" ';
						}
						$line .= "/>\n";
					}
					echo $line . "</" . $node0 . ">\n";
				}
				break;
			}
		default:
			{
				break;
			}
	}
}

echo "</osm>";

?>