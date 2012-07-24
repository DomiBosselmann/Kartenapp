<?php

session_start();

if ($_post) {

	$db_host = "127.0.0.1:3306";
	$db_username = "dhbwweb";
	$db_password = "";
	$db_database = "";

	if (!$_session['loggedin']) {
		$link = mysql_connect($db_host, $db_username, $db_password);

		if (!$link) {
			echo_mysql_error($link, "Database connection error");
		} else {
			$database = mysql_select_db($db_database, $link);

			if (!$database) {
				echo_mysql_error($link, "Database selection error");
			} else {

				$username = mysql_real_escape_string($_post['username']);
				$password = md5($_post['password']);

				if ($username && $password) {
					$query = "select `CNAME` from `TUSER` where ( `CNAME` = '$username' and `CPASSWORD` = '$password' )";
					$result = mysql_query($query);

					if(!$result){
						echo_mysql_error($link, "User selection error");
					} else {
						$resultarray = mysql_fetch_array($result);

						if (count($resultarray) >= 1) {
							// successfully logged in
							$_session['loggedin'] = true;
							$_session['username'] = $username;
							echo json_encode(array("success"=>true, "error"=>null));
						} else {
							// login failed
							echo json_encode(array("success"=>false, "error"=>"login failed"));
						}
					}
				}
			}
		}
	} else {

	}
}

function echo_mysql_error($link, $error) {
	if ($link) {
		mysql_close($link);
	}
	exit(json_encode("success"=>false, "error"=>$error . ": " . mysql_error()));
}

?>