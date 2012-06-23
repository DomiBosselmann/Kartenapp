<?php

header("Content-Type: image/svg+xml");

// Load the XML
$xmlfile = "http://www.schteffens.de/karte/php/";
$xslfile = "../xsl/";
$xml = new DOMDocument;
if ($_GET['l']) {
	$layer = substr($_GET['l'],0,1);
	switch ($layer) {
		case 'c':
			{
				$xmlfile .= "cities_pretransform.php?";
				$xslfile .= "cities_transform.xsl";
				break;
			}
		default:
			{
				$xmlfile .= "pretransform.php?";
				$xslfile .= "transform.xsl";
				break;
			}
	}
	$xmlfile .= "l=" . $_GET['l'] . "&";
} else {
	$xmlfile .= "pretransform.php?";
	$xslfile .= "transform.xsl";
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
$xml->load($xmlfile);

// Load the XSL
$xsl = new DOMDocument;
$xsl->load($xslfile);

// Configure the transformer
$proc = new XSLTProcessor;
$proc->importStyleSheet($xsl);

echo $proc->transformToXML($xml);

?>