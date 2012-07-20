<?php

$transform = 'http://dhbwweb.draco.uberspace.de/backend/transformation/steffen/php/transform.php?';

foreach ($_GET as $key => $value) {
	echo file_get_contents($transform . 'l=' . $value);
}

?>