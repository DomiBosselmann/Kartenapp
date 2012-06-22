<?php

// Load the XML
$xmlfile = "http://www.schteffens.de/karte/pretransform.php?";
$xml = new DOMDocument;
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
$xml->load($xmlfile);

// Load the XSL
$xsl = new DOMDocument;
$xsl->load('transform.xsl');

// Configure the transformer
$proc = new XSLTProcessor;
$proc->importStyleSheet($xsl);

echo $proc->transformToXML($xml);

?>