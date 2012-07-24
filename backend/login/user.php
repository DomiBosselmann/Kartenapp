<?php

if ($_GET) {

	$db_host = "127.0.0.1:3306";
	$db_username = "user";
	$db_password = "pw";
	$db_database = "dhbwweb";

	if ($_GET['new']) {
		$username = $_GET['name'];
		$password = md5($_GET['pw']);
		$query = "insert into `TUSER` ( `CNAME`, `CPASSWORD` ) values ( '$username', '$password' )";
		$result = mysql_query($query);
		echo $result;
	} else {
		if ($_GET['delete']) {
			$username = $_GET['name'];
			$password = md5($_GET['pw']);
			$query = "delete from `TUSER` where ( `CNAME` = '$username' and `CPASSWORD` = '$password' )";
			$result = mysql_query($query);
			echo $result;
		}
	}
}

?>