<?php

if (strrpos($_SERVER['HTTP_USER_AGENT'], "MSIE") > 0) {
	echo '<image src="../../../../images/ie.png" />';
} else {
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
								$xslfile .= "id=cities&";
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
}

?>