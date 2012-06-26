<?php 
$transform = 'http://www.schteffens.de/karte/php/transform.php?';

foreach ($_GET as $key => $value) {
	echo file_get_contents($transform . 'l=' . $value);
}

?>