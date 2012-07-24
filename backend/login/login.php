<?php

if ($_POST) {

	$db_host = "127.0.0.1:3306";
	$db_username = "dhbwweb";
	$db_password = "password";
	$db_database = "database";

	session_start();

	# Datenbankverbindung herstellen
	$link = mysql_connect($db_host, $db_username, $db_password);

	# Hat die Verbindung geklappt ?
	if (!$link) {
		die("Database connection error: " . mysql_error());
	}

	# Verbindung zur richtigen Datenbank herstellen
	$database = mysql_select_db($db_database, $link);

	if (!$database) {
		echo "Database selection error: " . mysql_error();
		mysql_close($link);        # Datenbank schliessen
		exit;                    # Programm beenden !
	}

	$username = mysql_real_escape_string($_POST['username']);
	$password = mysql_real_escape_string(md5($_POST['pw']));

	if ($username && $password) {

		$query = "SELECT CNAME FROM TUSER WHERE CNAME='$username' and CPASSWORD='$password'";
		$result = mysql_query($query) or die ("Query error: " . mysql_error());

		if(!$result){
			echo mysql_error($link);
			# Datenbank wieder schliessen
			mysql_close($link);
			exit();
		} else {
			$_SESSION["loggedin"] = true;
			$response = array("succes"=>true, "error"=>null);
			echo json_encode($response);
		}
	}

}

?>