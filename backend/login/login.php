<?php
	$_db_host = "127.0.0.1:3306"; 
	$_db_username = "dhbwweb";
	$_db_passwort = "VeadojcobcinbebWadod";
	
	session_start();
	
	# Datenbankverbindung herstellen
	$link = mysql_connect($_db_host, $_db_username, $_db_passwort);
	
	# Hat die Verbindung geklappt ?
	if (!$link) {
		die("Keine Datenbankverbindung möglich: " . mysql_error());
	}
	
	# Verbindung zur richtigen Datenbank herstellen
	$datenbank = mysql_select_db($_db_datenbank, $link);
	
	if (!$datenbank) {
		echo "Kann die Datenbank nicht benutzen: " . mysql_error();
		mysql_close($link);        # Datenbank schliessen
		exit;                    # Programm beenden !
	}
	
	if (!empty($_POST["submit"])) {
		#UserDaten verbergen
		$_username = mysql_real_escape_string($_POST["username"]);
		$_passwort = mysql_real_escape_string($_POST["passwort"]);
	
		# Befehl für die MySQL Datenbank
	}
	
	function logIn($_cName, $_password) {
		$_query = "SELECT CNAME, CPASSWORD FROM TUSER WHERE CNAME='$_cName' and CPASSWORD='$_password'";
		$_result = mysql_query($_query) or die ("Fehler: " . mysql_error());
		
		if(!$_query)
		echo mysql_error($link);
		
		$_SESSION["loggedin"] = true;
		echo json_encode("{ success : true, error : [] }");
	}
	
	# Datenbank wieder schliessen
	mysql_close($link);
?>