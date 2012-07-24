<?php

if ($_GET) {

	$db_host = "127.0.0.1:3306";
	$db_username = "user";
	$db_password = "pw";
	$db_database = "dhbwweb";

	$link = mysql_connect($db_host, $db_username, $db_password);

	if (!$link) {
		echo_mysql_error($link, "Database connection error");
	} else {
		$database = mysql_select_db($db_database, $link);

		if (!$database) {
			echo_mysql_error($link, "Database selection error");
		} else {

			if (isset($_GET['new'])) {
				$username = $_GET['name'];
				$password = md5($_GET['pw']);
				$query = "insert into `TUSER` ( `CNAME`, `CPASSWORD` ) values ( '$username', '$password' )";
				$result = mysql_query($query);
				echo $result;
			} else {
				if (isset($_GET['delete'])) {
					$username = $_GET['name'];
					$password = md5($_GET['pw']);
					$query = "delete from `TUSER` where ( `CNAME` = '$username' and `CPASSWORD` = '$password' )";
					$result = mysql_query($query);
					echo $result;
				} else {
					echo "nope";
				}
			}
		}
	}
}

?>