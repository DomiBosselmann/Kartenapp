<?php

if ($_POST['a']) {

	session_start();

	$action = $_POST['a'];

	$db_host = "127.0.0.1:3306";
	$db_username = "dhbwweb";
	$db_password = "password";
	$db_database = "database";

	if ($_SESSION['loggedin'] === true) {

		$link = mysql_connect($db_host, $db_username, $db_password);
		if (!$link) {
			echo_mysql_error($link, "Database connection error");
		} else {

			$database = mysql_select_db($db_database, $link);
			if (!$database) {
				echo_mysql_error($link, "Database selection error");
			} else {

				$username = $_SESSION['user'];

				switch ($action) {
					case "i":
						{
							// import routes as xml into the database and respond all available routes as json
							$data = json_decode($_POST['data']);
							// data parsing
							$route_name = "";

							$query = "INSERT INTO TROUTES (CNAME, CUSER) VALUES ('$route_name', '$username')";
							$response = mysql_query($query);
							if (!$response) {
								echo_mysql_error($link, "Insertion error");
							} else {

								$query = "INSERT INTO TWAYPOINTS (CXKOORD, CYKOORD) VALUES ('$_cxkoord', '$_cykoord')";
								$response = mysql_query($query);
								if (!$response) {
									echo_mysql_error($link, "Insertion error");
								}
							}
							break;
						}
					case "e":
						{
							// export routes as xml

							$query = "SELECT * FROM TROUTES WHERE CUSER='$username'";
							$response = mysql_query($query);
							if (!$response) {
								echo_mysql_error($link, "Insertion error");
							} else {

								while($row = mysql_fetch_array($result)) {
									$arr = array("name"=>$row['CNAME'], "length"=>$row['CLENGTH'], "user"=>$row['CUSER'], "id"=>$row['CID']);
									$json = json_encode($arr);
									echo($json);
								}
							}
							break;
						}
					case "s":
						{
							// save routes into the database
							$data = $_POST['data'];
							// data parsing as json

							// save parsed routes and nodes into the database

							// echo success/fail
							break;
						}
				}

				mysql_close($link);
			}
		}
	}
}

function echo_mysql_error($link, $error) {
	if ($link) {
		mysql_close($link);
	}
	exit($error . ": " . mysql_error());
}

?>
