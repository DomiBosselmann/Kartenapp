<?php

$_db_host = "127.0.0.1:3306";
$_db_username = "dhbwweb";
$_db_passwort = "VeadojcobcinbebWadod";
$_db_datenbank = "????";

SESSION_START();

# Datenbankverbindung herstellen
$link = mysql_connect($_db_host, $_db_username, $_db_passwort);

# Hat die Verbindung geklappt ?
if (!$link) {
	die("Keine Datenbankverbindung moeglich: " . mysql_error());
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

	# Befehl fuer die MySQL Datenbank
}

if ($_get == true) {
	getRoutes($cName, $cuser);
} else {
	saveRoute($cName, $cxkoord, $cykoord, $cuserName, $clength);
}

if (!$_query) {
	echo mysql_error($link);
}

# Datenbank wieder schliessen
mysql_close($link);

function getRoutes($_cname, $_cuser) {
	$_query = "SELECT * FROM TROUTES WHERE CNAME='$_cname' OR CUSER='$_cuser'";
	$_result = mysql_query($_query) or die ("Fehler: " . mysql_error());

	while($row = mysql_fetch_array($result)) {
		echo($row['CNAME']);
		echo($row['CLENGTH']);
		echo($row['CUSER']);
		echo($row['CID']);
	}
}

function saveRoute($_cName, $_cxkoord, $_cykoord, $_cuserName, $_clength) {
	$_insert = "INSERT INTO TROUTES (CNAME, CLENGTH, CUSER) VALUES ('$_cName', '$_clength', '$_cuserName')";
	$_query = mysql_query($insert);

	if (!$_query) {
		echo mysql_error($link);
	}

	$_insert = "INSERT INTO TWAYPOINTS (CXKOORD, CYKOORD) VALUES ('$_cxkoord', '$_cykoord')";
	mysql_query($insert);
}

?>
