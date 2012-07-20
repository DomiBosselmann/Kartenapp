<?php

// Deprecated/Obsolete

header("Content-Type: image/svg+xml");

$xmlfile = "http://dhbwweb.draco.uberspace.de/backend/transformation/steffen/php/pretransform.php?";
$xslfile = "http://dhbwweb.draco.uberspace.de/backend/transformation/steffen/php/dynamic_xsl.php?";
if ($_GET['l']) {
	$layer = substr($_GET['l'],0,1);
	switch ($layer) {
		case "c":
			{
				$xmlfile = "http://dhbwweb.draco.uberspace.de/backend/transformation/steffen/php/cities_pretransform.php?";
				$xslfile .= "l=c&";
				switch ($_GET['l']) {
					case "c":
						{
							$xslfile .= "id=cities&";
							break;
						}
					case "c1":
						{
							$xslfile .= "id=towns&";
							break;
						}
					case 'c2':
						{
							$xslfile .= "id=villages&";
							break;
						}
					case 'c3':
						{
							$xslfile .= "id=hamlets&";
							break;
						}
					case 'c4':
						{
							$xslfile .= "id=suburbs&";
							break;
						}
					default:
						{
							$xslfile .= "id=places&";
							break;
						}
				}
				break;
			}
		case "b":
			{
				$xslfile .= "l=b&";
				switch ($_GET['l']) {
					case "b":
						{
							$xslfile .= "id=federal&";
							break;
						}
					case "b1":
						{
							$xslfile .= "id=counties&";
							break;
						}
					default:
						{
							$xslfile .= "id=bounds&";
							break;
						}
				}
				break;
			}
		case "w":
			{
				$xslfile .= "l=w&";
				switch ($_GET['l']) {
					case "w":
						{
							$xslfile .= "id=rivers&";
							break;
						}
					case "w1":
						{
							$xslfile .= "id=canals&";
							break;
						}
					case "w2":
						{
							$xslfile .= "id=namedLakes&";
							break;
						}
					case "w3":
						{
							$xslfile .= "id=allLakes&";
							break;
						}
					default:
						{
							$xslfile .= "id=waters&";
							break;
						}
				}
				break;
			}
		case "s":
			{
				$xslfile .= "l=s&";
				switch ($_GET['l']) {
					case "s":
						{
							$xslfile .= "id=motorways&";
							break;
						}
					case "s1":
						{
							$xslfile .= "id=trunks&";
							break;
						}
					case "s2":
						{
							$xslfile .= "id=primaries&";
							break;
						}
					case "s3":
						{
							$xslfile .= "id=secondaries&";
							break;
						}
					case "s4":
						{
							$xslfile .= "id=tertiaries&";
							break;
						}
					default:
						{
							$xslfile .= "id=streets&";
							break;
						}
				}
				break;
			}
	}
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

// Load the XML
$xml = new DOMDocument;
$xml->load($xmlfile);

// Load the XSL
$xsl = new DOMDocument;
$xsl->load($xslfile);

// Configure the transformer
$proc = new XSLTProcessor;
$proc->importStyleSheet($xsl);

echo $proc->transformToXML($xml);

?>