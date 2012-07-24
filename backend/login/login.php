<?php

session_start();

if ($_POST) {

	$db_host = "127.0.0.1:3306";
	$db_username = "dhbwweb";
	$db_password = "password";
	$db_database = "database";

	if ($_session['loggedin'] === true) {
		$link = mysql_connect($db_host, $db_username, $db_password);

		if (!$link) {
			echo_mysql_error($link, "Database connection error");
		} else {
			$database = mysql_select_db($db_database, $link);

			if (!$database) {
				echo_mysql_error($link, "Database selection error");
			} else {

				$username = mysql_real_escape_string($_post['username']);
				$password = mysql_real_escape_string(md5($_post['password']));

				if ($username && $password) {
					$query = "SELECT CNAME FROM TUSER WHERE CNAME='$username' and CPASSWORD='$password'";
					$result = mysql_query($query);

					if(!$result){
						echo_mysql_error($link, "Query error");
					} else {
						$resultarray = mysql_fetch_array($result);

						if (count($resultarray) >= 1) {
							// successfully logged in
							$_session['loggedin'] = true;
							$_session['username'] = $username;
							$response = array("success"=>true, "error"=>null);
							echo json_encode($response);
						} else {
							// login failed
							$response = array("success"=>false, "error"=>"login failed");
							echo json_encode($response);
						}
					}
				}
			}
		}
	}
}

function echo_mysql_error($link, $error) {
	mysql_close($link);
	exit($error . ": " . mysql_error());
}

?>