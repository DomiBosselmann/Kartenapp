<?php

// Load the XML
$xml = new DOMDocument;
$layer = $_GET[l];
switch ($layer) {
	case 'b':
		{
			$xmlfile = 'boundary.xml';
			break;
		}
	case 'b2':
		{
			$xmlfile = 'boundary2.xml';
			break;
		}
	case 'h':
		{
			$xmlfile = 'highway.xml';
			break;
		}
	default:
		{
			$xmlfile = 'highway.xml';
			break;
		}
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