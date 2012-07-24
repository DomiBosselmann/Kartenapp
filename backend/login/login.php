<?php

session_start();

if ($_GET) {

	$db_host = "127.0.0.1:3306";
	$db_username = "dhbwweb";
	$db_password = "VeadojcobcinbebWadod";
	$db_database = "dhbwweb";

	if (!$_SESSION['loggedin']) {
		$link = mysql_connect($db_host, $db_username, $db_password);

		if (!$link) {
			echo_mysql_error($link, "Database connection error");
		} else {
			$database = mysql_select_db($db_database, $link);

			if (!$database) {
				echo_mysql_error($link, "Database selection error");
			} else {

				$username = mysql_real_escape_string($_GET['username']);
				$password = md5($_GET['password']);

				if ($username && $password) {
					$query = "select `CPASSWORD` from `TUSER` where ( `CNAME` = '$username' )";
					$result = mysql_query($query);

					if (!$result) {
						echo_mysql_error($link, "User selection error");
					} else {
						while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
							if ($row['CPASSWORD'] == $password) {
								// successfully logged in
								$_SESSION['loggedin'] = true;
								$_SESSION['username'] = $username;
								exit(json_encode(array("success"=>true, "message"=>null)));
							}
						}
						// login failed
						exit(json_encode(array("success"=>false, "message"=>"Wrong username or password!")));
					}
				} else {
					exit(json_encode(array("success"=>false, "message"=>"Not logged in!")));
				}
			}
		}
	} else {
		exit(json_encode(array("success"=>true, "message"=>"Already logged in!")));
	}
}

function echo_mysql_error($link, $error) {
	if ($link) {
		mysql_close($link);
	}
	exit(json_encode(array("success"=>false, "message"=>$error . ": " . mysql_error())));
}

?>