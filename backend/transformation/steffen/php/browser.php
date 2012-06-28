<?php 


if (strrpos($_SERVER['HTTP_USER_AGENT'], "MSIE") > 0) {
	echo '<image src="http://www.schteffens.de/ie.png" />';
} else {
	echo 'good boy!';
}

?>