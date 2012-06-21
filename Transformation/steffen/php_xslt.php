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

echo "<vsg>\n<g style=\"fill:none;stroke-width:1\" transform=\"scale(1) translate(-4400 -700)\" >\n";

foreach ($xml as $node0 => $value0) {
	switch ($node0) {
		/*	case "node":
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
		$line .= "/>";
		if ($print1 && $print2) {
		$nodes[''.$id] = true;
		echo $line . "\n";
		}
		break;
		}  */
		case "way":
			{
				$line = "<path d=\""


				/*	foreach ($value0->attributes() as $attributeskey0 => $attributesvalue0) {
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
				echo $line . "\n";
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
				*/

				echo line . "/>";
				break;
			}
		default:
			{
				break;
			}
	}
}

echo "</g>\n</vsg>\n";

?>