<?php

session_start();

if ($_SESSION['loggedin'] === true) {

	if ($_POST['a']) {

		$action = $_POST['a'];

		$db_host = "127.0.0.1:3306";
		$db_username = "user";
		$db_password = "pw";
		$db_database = "dhbwweb";


		$link = mysql_connect($db_host, $db_username, $db_password);
		if (!$link) {
			echo_mysql_error($link, "Database connection error");
		} else {
			$database = mysql_select_db($db_database, $link);
			if (!$database) {
				echo_mysql_error($link, "Database selection error");
			} else {
				$username = $_SESSION['username'];
				switch ($action) {
					case "i":
						{
							// import routes as xml into the database and respond all available routes as json
							$route_ids = array();
							$data_xml = new simplexmlelement($_POST['data']);
							foreach ($data_xml->children() as $route_element => $route_value) {
								foreach ($route_value->children() as $route_node => $route_node_value) {
									switch ($route_node) {
										case "name":
											{
												$route_name = $route_node_value;
												break;
											}
										case "length":
											{
												$route_length = $route_node_value;
												break;
											}
										case "user":
											{
												$route_user = $route_node_value;
												break;
											}
										case "coordinates":
											{
												$route_coordinates = array();
												foreach ($route_node_value as $node_element => $node_element_value) {
													switch ($node_element) {
														case "node":
															{
																$node_coordinate = array();
																foreach ($node_element_value as $node_child => $node_child_value) {
																	if (($node_child == "longitude") || ($node_child == "latitude")) {
																		array_push($node_coordinate, $node_child_value);
																	}
																}
																array_push($route_coordinates, $node_coordinate);
																break;
															}
													}
												}
												break;
											}
									}
								}
								$query = "insert into `TROUTES` ( `CNAME`, `CLENGTH`, `CUSER` ) values ( '$route_name', $route_length, '$route_user' )";
								$result = mysql_query($query);
								if (!$result) {
									echo_mysql_error($link, "Route insertion error");
								} else {
									$route_id = mysql_insert_id();
									array_push($route_ids, $route_id);
									$order = 0;
									foreach ($route_coordinates as $coordinate) {
										$longitude = $coordinate[0];
										$latitude = $coordinate[1];
										$query = "insert into `TWAYPOINTS` ( `CRELROUTEID`, `CORDER`, `CXKOORD`, `CYKOORD` ) values ($route_id, $order, $longitude, $latitude)";
										$result = mysql_query($query);
										if (!$result) {
											echo_mysql_error($link, "Waypoint insertion error");
										} else {
											$order++;
										}
									}
								}
							}

							// json response
							$query = "select `CID`, `CNAME`, `CLENGTH` from `TROUTES` where ( `CUSER` = $username)";
							$result = mysql_query($query);
							if (!$result) {
								echo_mysql_error($link, "Routes selection error");
							} else {
								$routes = array();
								while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
									$route_id = $row['CID'];
									if (in_array($route_id, $route_ids)) {
										$route_name = $row['CNAME'];
										$route_length = $row['CLENGTH'];
										$query = "select `CXKOORD`, `CYKOORD`, `CORDER` from `TWAYPOINTS` where ( `CRELROUTEID` = $route_id) order by `CORDER`";
										$result = mysql_query($query);
										if (!$result) {
											echo_mysql_error($link, "Waypoint selection error");
										} else {
											$coordinates = array();
											while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
												$longitude = $row['CXKOORD'];
												$latitude = $row['CYKOORD'];
												array_push($coordinates, array($longitude, $latitude));
											}
											array_push($routes, array("name"=>$route_name, "length"=>$route_length), "coordinates"=>$coordinates);
										}
									}
								}
								echo json_encode($routes);
							}
							break;
						}
					case "e":
						{
							// export routes as xml
							$query = "select `CID`, `CNAME`, `CLENGTH` from `TROUTES` where ( `CUSER` = $username)";
							$result = mysql_query($query);
							if (!$result) {
								echo_mysql_error($link, "Routes selection error");
							} else {
								$routes_xml = new simplexmlelement('<?xml version="1.0" encoding="UTF-8"?><routes/>');
								while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
									$route_element = $routes_xml->addChild("route");
									$route_id = $row['CID'];
									$route_element->addChild("name", $row['CNAME']);
									$route_element->addChild("length", $row['CLENGTH']);
									$query = "select `CXKOORD`, `CYKOORD`, `CORDER` from `TWAYPOINTS` where ( `CRELROUTEID` = $route_id) order by `CORDER`";
									$result = mysql_query($query);
									if (!$result) {
										echo_mysql_error($link, "Waypoint selection error");
									} else {
										while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
											$node_element = $route_element->addChild("node");
											$node_element->addChild("longitude", $row['CXKOORD']);
											$node_element->addChild("latitude", $row['CYKOORD']);
										}
									}
								}
								echo json_encode("success"=>true, "data"=>$routes_xml->asXML());
							}
							break;
						}
					case "s":
						{
							// save routes into the database
							if ($_POST['data']) {
								$data = json_decode($_POST['data']);
								$route_name = $data['name'];
								$route_length = $data['distance'];
								$route_coordinates = $data['coordinates'];

								$query = "insert into `TROUTES` ( `CNAME`, `CLENGTH`, `CUSER` ) values ( '$route_name', $route_length, '$username' )";
								$result = mysql_query($query);
								if (!$result) {
									echo_mysql_error($link, "Route insertion error");
								} else {
									$route_id = mysql_insert_id();
									$order = 0;
									foreach ($route_coordinates as $coordinate) {
										$longitude = $coordinate[0];
										$latitude = $coordinate[1];
										$query = "insert into `TWAYPOINTS` ( `CRELROUTEID`, `CORDER`, `CXKOORD`, `CYKOORD` ) values ($route_id, $order, $longitude, $latitude)";
										$result = mysql_query($query);
										if (!$result) {
											echo_mysql_error($link, "Waypoint insertion error");
										} else {
											$order++;
										}
									}
									$arr = array("success"=>true, "error"=>null);
									echo(json_encode($arr));
								}
							} else {
								exit(json_encode("success"=>false, "error"=>"No data submitted!");
							}
							break;
						}
					default:
						{
							exit(json_encode("success"=>false, "error"=>"Wrong action submitted!");
							break;
						}
				}
				mysql_close($link);
			}
		}
	} else {
		exit(json_encode("success"=>false, "error"=>"No action submitted!");
	}
} else {
	exit(json_encode("success"=>false, "error"=>"Not logged in!");
}

function echo_mysql_error($link, $error) {
	if ($link) {
		mysql_close($link);
	}
	exit(json_encode("success"=>false, "error"=>$error . ": " . mysql_error());
}

?>
