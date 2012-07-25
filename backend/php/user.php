<?php

if ($_GET) {

	$db_host = "localhost:3306";
	$db_username = "dhbwweb";
	$db_password = "VeadojcobcinbebWadod";
	$db_database = "dhbwweb";

	$link = mysql_connect($db_host, $db_username, $db_password);
	if (!$link) {
		echo_mysql_error("Database connection error");
	} else {
		$database = mysql_select_db($db_database, $link);
		if (!$database) {
			echo_mysql_error("Database selection error");
		} else {
			if (isset($_GET['new'])) {
				$username = $_GET['username'];
				$password = md5($_GET['password']);
				$query = "insert into `TUSER` ( `CNAME`, `CPASSWORD` ) values ( '$username', '$password' )";
				$result = mysql_query($query);
				if ($result == 0) {
					exit(json_encode(array("success"=>false, "message"=>"Username already exists!")));
				} else {
					exit(json_encode(array("success"=>true, "message"=>"User created with ID " . mysql_insert_id())));
				}
				echo $result;
			} else {
				if (isset($_GET['delete'])) {
					$username = $_GET['username'];
					$password = md5($_GET['password']);
					$query = "delete from `TUSER` where ( `CNAME` = '$username' and `CPASSWORD` = '$password' )";
					$result = mysql_query($query);
					if (!$result) {
						echo_mysql_error("Deletion failed");
					} else {
						if (mysql_affected_rows() == 1) {
							exit(json_encode(array("success"=>true, "message"=>"User successfully deleted!")));
						} else {
							exit(json_encode(array("success"=>false, "message"=>"Deletion failed!")));
						}
					}
				} else {
					exit(json_encode(array("success"=>false, "message"=>"No action submitted!")));
				}
			}
		}
	}
}

function echo_mysql_error($error) {
	if ($link) {
		mysql_close($link);
	}
	exit(json_encode(array("success"=>false, "message"=>$error . ": " . mysql_error())));
}

?>