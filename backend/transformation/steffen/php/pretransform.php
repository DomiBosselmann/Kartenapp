<?php 

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

$xmlfile = '../xml/';
if ($_GET['l']){
	$layer = substr($_GET['l'],0,1);
	switch ($layer) {
		case 'm':
			{
				$xmlfile .= 'highways/motorway.xml';
				break;
			}
		case 'b':
			{
				switch ($_GET['l']) {
					case 'b1':
						{
							$xmlfile .= 'bounds/bawubounds.xml';
							break;
						}
					default:
						{
							$xmlfile .= 'bounds/gerbounds.xml';
							break;
						}
				}
				break;
			}
		case 'r':
			{
				$xmlfile .= 'waters/rivers.xml';
				break;
			}
		default:
			{
				$xmlfile .= 'bounds/gerbounds.xml';
				break;
			}
	}
} else {
	$xmlfile .= 'bounds/gerbounds.xml';
}

$xml = simplexml_load_file($xmlfile);

echo "<osm>\n";
$nodes = array();

foreach ($xml as $node0 => $value0) {
	switch ($node0) {
		case "node":
			{
				$print1 = false;
				$print2 = false;
				$line = '<' . $node0 . ' ';
				foreach ($value0->attributes() as $attributeskey0 => $attributesvalue0) {
					switch ($attributeskey0) {
						case 'id':
							{
								$id = $attributesvalue0;
							}
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
					$nodes[''.$id] = true;
					echo $line . "/>\n";
				}
				break;
			}
		case "way":
			{
				$line = '<' . $node0 . ' ';
				foreach ($value0->attributes() as $attributeskey0 => $attributesvalue0) {
					switch ($attributeskey0) {
						case 'id':
							{
								$id = $attributesvalue0;
							}
						default:
							{
								$line .= $attributeskey0 . '="' . $attributesvalue0 . '" ';
								break;
							}
					}
				}
				$line .= '>';
				echo $line . "\n";
				foreach ($value0->children() as $childkey0 => $childvalue0) {
					$line = "<" . $childkey0 . " ";
					switch ($childkey0) {
						case 'nd':
							{
								foreach ($childvalue0->attributes() as $attributeskey1 => $attributesvalue1) {
									switch ($attributeskey1) {
										case 'ref':
											{
												$ref = $attributesvalue1;
											}
										default:
											{
												$line .= $attributeskey1 . '="' . $attributesvalue1 . '" ';
												break;
											}
									}
								}
								$line .= "/>";
								if ($nodes[''.$ref]) {
									echo $line . "/>\n";
								}
								break;
							}
						default:
							{
								foreach ($childvalue0->attributes() as $attributeskey1 => $attributesvalue1) {
									$line .= $attributeskey1 . '="' . $attributesvalue1 . '" ';
								}
								$line .= "/>";
								echo $line . "\n";
								break;
							}
					}
				}
				echo "</$node0>\n";
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