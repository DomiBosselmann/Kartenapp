<?php

session_start();

if ($_SESSION['loggedin'] === true) {

	if (isset($_POST['a'])) {

		$action = $_POST['a'];

		$db_host = "127.0.0.1:3306";
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
				$username = $_SESSION['username'];
				switch ($action) {
					case "i":
						{
							// import routes as xml into the database and respond all new routes as json
							$route_ids = array();
							$data_xml = new simplexmlelement($_POST['data']);
							foreach ($data_xml->children() as $data_element => $data_value) {
								switch ($data_element) {
									case "routes":
										{
											foreach ($data_value->children() as $route_node => $route_node_value) {
												foreach ($route_node_value as $route_child_node => $route_child_node_value) {
													switch ($route_child_node) {
														case "user":
															{
																$route_user = $route_child_node_value;
																break;
															}
														case "name":
															{
																$route_name = $route_child_node_value;
																break;
															}
														case "length":
															{
																$route_length = $route_child_node_value;
																break;
															}
														case "coordinates":
															{
																$route_coordinates = array();
																foreach ($route_child_node_value as $node_element => $node_element_value) {
																	switch ($node_element) {
																		case "coord":
																			{
																				$node_coordinate = array();
																				foreach ($node_element_value->attributes() as $node_attribute => $node_attribute_value) {
																					if (($node_attribute == "lon") || ($node_attribute == "lat")) {
																						array_push($node_coordinate, $node_attribute_value);
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
											}
											$query = "insert into `TROUTES` ( `CNAME`, `CLENGTH`, `CUSER` ) values ( '$route_name', $route_length, '$route_user' )";
											$result = mysql_query($query);
											if (!$result) {
												echo_mysql_error("Route insertion error");
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
														echo_mysql_error("Waypoint insertion error");
													} else {
														$order++;
													}
												}
											}
											break;
										}
									case "places":
										{
											foreach ($data_value->children() as $place_node => $place_node_value) {
												foreach ($place_node_value as $place_child_node => $place_child_node_value) {
													switch ($place_child_node) {
														case "user":
															{
																$username = $place_child_node_value;
																break;
															}
														case "name":
															{
																$place_name = $place_child_node_value;
																break;
															}
														case "coord":
															{
																foreach ($place_child_node_value->attributes() as $child_attribute => $child_attribute_value) {
																	switch ($child_attribute) {
																		case "lon":
																			{
																				$longitude = $child_attribute_value;
																				break;
																			}
																		case "lat":
																			{
																				$latitude = $child_attribute_value;
																				break;
																			}
																	}
																}
																break;
															}
													}
													$query = "insert into `TLOCATIONS` ( `CNAME`, `CUSER`, `CXKOORD`, `CYKOORD` ) values ( '$place_name', '$username', $longitude, $latitude )";
													$result = mysql_query($query);
													if (!$result) {
														echo_mysql_error("Location insertion error");
													}
												}
											}
											break;
										}
								}
							}

							// json response
							$query = "select `CID`, `CNAME`, `CLENGTH` from `TROUTES` where ( `CUSER` = $username)";
							$result = mysql_query($query);
							if (!$result) {
								echo_mysql_error("Routes selection error");
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
											echo_mysql_error("Waypoint selection error");
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
							header("Content-Type: application/octet-stream");
							header("Content-Disposition: attachment; filename=export.xml");
							$query = "select `CID`, `CNAME`, `CLENGTH` from `TROUTES` where ( `CUSER` = $username)";
							$result = mysql_query($query);
							if (!$result) {
								echo_mysql_error("Routes selection error");
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
										echo_mysql_error("Waypoint selection error");
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
							// save routes
							if ($_POST['routes'] && $_POST['places']) {
								$json = array();
								if ($_POST['routes']) {
									$data = json_decode($_POST['data']);
									$route_name = $data['name'];
									$route_length = $data['distance'];
									$route_coordinates = $data['coordinates'];

									$query = "insert into `TROUTES` ( `CNAME`, `CLENGTH`, `CUSER` ) values ( '$route_name', $route_length, '$username' )";
									$result = mysql_query($query);
									if (!$result) {
										echo_mysql_error("Route insertion error");
									} else {
										$route_id = mysql_insert_id();
										$order = 0;
										foreach ($route_coordinates as $coordinate) {
											$longitude = $coordinate[0];
											$latitude = $coordinate[1];
											$query = "insert into `TWAYPOINTS` ( `CRELROUTEID`, `CORDER`, `CXKOORD`, `CYKOORD` ) values ($route_id, $order, $longitude, $latitude)";
											$result = mysql_query($query);
											if (!$result) {
												echo_mysql_error("Waypoint insertion error");
											} else {
												$order++;
											}
										}
										$routes_json = json_encode(array("success"=>true, "message"=>"Routes successfully added!"));
									}
								}
								// saving places
								if ($_POST['places']) {
									$data = json_decode($_POST['places']);
									$place_name = $data['name'];
									$place_visible = $data['visible'];
									$place_coordinates = $data['coordinates'];
									$longitude = $place_coordinates[0];
									$latitude = $place_coordinates[1];

									$query = "insert into `TLOCATIONS` ( `CNAME`, `CUSER`, `CVISIBLE`, `CXKOORD`, `CYKOORD` ) values ( '$place_name', '$username', $place_visible, $longitude, $latitude )";
									$result = mysql_query($query);
									if (!$result) {
										echo_mysql_error("Route insertion error");
									} else {
										$places_json = json_encode(array("success"=>true, "message"=>"Places successfully added!"));
									}
								}
								exit(json_encode("routes"=>$routes_json, "places"=>$places_json));
							} else {
								// neither routes nor places submitted
								exit(json_encode("success"=>false, "message"=>"No routes or places submitted!");
							}
							break;
						}
					default:
						{
							exit(json_encode("success"=>false, "message"=>"Wrong action submitted!");
							break;
						}
				}
				mysql_close($link);
			}
		}
	} else {
		exit(json_encode("success"=>false, "message"=>"No action submitted!");
	}
} else {
	exit(json_encode("success"=>false, "message"=>"Not logged in!");
}

function echo_mysql_error($error) {
	if ($link) {
		mysql_close($link);
	}
	exit(json_encode("success"=>false, "message"=>$error . ": " . mysql_error());
}

?>