<?php

if (isset($_GET['pw'])) {
	$pw = $_GET['pw'];
	echo "You entered '$pw' ...\n";
	echo "And the MD5-Checksum is '" . md5($_GET['pw']) . "'";
} else {
	echo "nope";
}

?>