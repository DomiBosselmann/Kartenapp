<?php

session_start();

if ($_POST) {

	if (!$_SESSION['loggedin']) {

		include "database_access.php";

		$link = mysql_connect($db_host, $db_username, $db_password);

		if (!$link) {
			echo_mysql_error("Database connection error");
		} else {
			$database = mysql_select_db($db_database, $link);

			if (!$database) {
				echo_mysql_error("Database selection error");
			} else {

				$username = mysql_real_escape_string($_POST['username']);
				$password = md5($_POST['password']);

				if ($username && $password) {
					$query = "select `CPASSWORD` from `TUSER` where ( `CNAME` = '$username' )";
					$result = mysql_query($query);

					if (!$result) {
						echo_mysql_error("User selection error");
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

function echo_mysql_error($error) {
	if ($link) {
		mysql_close($link);
	}
	exit(json_encode(array("success"=>false, "message"=>$error . ": " . mysql_error())));
}

?>