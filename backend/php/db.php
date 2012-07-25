<?php

session_start();

if ($_SESSION['loggedin'] === true) {

	if (isset($_GET['a'])) {

		$action = $_GET['a'];

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
				$username = $_SESSION['username'];
				switch ($action) {
					case "i":
						{
							// import routes as xml into the database and respond all new routes as json
							$route_ids = array();
							$data_xml = new simplexmlelement($_GET['data']);
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
													$longitude = $coordinate[1];
													$latitude = $coordinate[0];
													$query = "insert into `TWAYPOINTS` ( `CRELROUTEID`, `CORDER`, `CXKOORD`, `CYKOORD` ) values ($route_id, $order, $longitude, $latitude)";
													echo $query;
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
											array_push($routes, array("name"=>$route_name, "length"=>$route_length, "coordinates"=>$coordinates));
										}
									}
								}
								exit(json_encode($routes));
							}
							break;
						}
					case "e":
						{
							$locations_xml = new simplexmlelement('<?xml version="1.0" encoding="UTF-8"?><locations/>');
							// export routes
							$query = "select `CID`, `CNAME`, `CLENGTH` from `TROUTES` where ( `CUSER` = '$username')";
							$result = mysql_query($query);
							if (!$result) {
								echo_mysql_error("Routes selection error");
							} else {
								while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
									$route_element = $locations_xml->addChild("route");
									$route_id = $row['CID'];
									$route_element->addChild("user", $username);
									$route_element->addChild("name", $row['CNAME']);
									$route_element->addChild("length", $row['CLENGTH']);
									$query = "select `CXKOORD`, `CYKOORD`, `CORDER` from `TWAYPOINTS` where ( `CRELROUTEID` = $route_id) order by `CORDER`";
									$result = mysql_query($query);
									if (!$result) {
										echo_mysql_error("Waypoint selection error");
									} else {
										$coordinates_element = $route_element->addChild("coordinates");
										while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
											$coord_element = $coordinates_element->addChild("coord");
											$coord_element->addAttribute("lon", $row['CXKOORD']);
											$coord_element->addAttribute("lat", $row['CYKOORD']);
										}
									}
								}
							}
							// export places
							$query = "select `CNAME`, `CXKOORD`, `CYKOORD` from `TLOCATIONS` where ( `CUSER` = '$username')";
							$result = mysql_query($query);
							if (!$result) {
								echo_mysql_error("Routes selection error");
							} else {
								while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
									$place_element = $locations_xml->addChild("place");
									$place_element->addChild("user", $username);
									$place_element->addChild("name", $row['CNAME']);
									$coord_element = $place_element->addChild("coord");
									$coord_element->addAttribute("lon", $row['CXKOORD']);
									$coord_element->addAttribute("lat", $row['CYKOORD']);
								}
							}

							// respond the xml
							header("Content-Type: application/octet-stream");
							header("Content-Disposition: attachment; filename=export.xml");
							exit($locations_xml->asXML());
							break;
						}
					case "s":
						{
							if ($_POST['routes'] && $_POST['places']) {
								$json = array();
								// save routes
								if ($_POST['routes']) {
									$data = json_decode($_POST['routes'], true);
									foreach ($data as $data_element) {
										$route_name = $data_element['name'];
										$route_length = $data_element['distance'];
										if ($data_element['visible'] == true) {
											$route_visible = 1;
										} else {
											$route_visible = 0;
										}
										$route_coordinates = $data_element['coordinates'];

										$query = "insert into `TROUTES` ( `CNAME`, `CLENGTH`, `CUSER` ) values ( '$route_name', '$route_length', '$username' )";
										$result = mysql_query($query);
										if (!$result) {
											//	echo_mysql_error("Route insertion error");
										} else {
											$route_id = mysql_insert_id();
											$order = 0;
											foreach ($route_coordinates as $coordinate) {
												$longitude = $coordinate[1];
												$latitude = $coordinate[0];
												$query = "insert into `TWAYPOINTS` ( `CRELROUTEID`, `CORDER`, `CXKOORD`, `CYKOORD` ) values ($route_id, $order, $longitude, $latitude )";
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
								}
								// saving places
								if ($_POST['places']) {
									$data = json_decode($_POST['places'], true);
									foreach ($data as $data_element) {
										$place_name = $data_element['name'];
										if ($data_element['visible'] == true) {
											$place_visible = 1;
										} else {
											$place_visible = 0;
										}
										$place_coordinates = $data_element['coordinates'];
										$longitude = $place_coordinates[1];
										$latitude = $place_coordinates[0];

										$query = "insert into `TLOCATIONS` ( `CNAME`, `CUSER`, `CVISIBLE`, `CXKOORD`, `CYKOORD` ) values ( '$place_name', '$username', $place_visible, $longitude, $latitude )";
										$result = mysql_query($query);
										if (!$result) {
											echo_mysql_error("Route insertion error");
										} else {
											$places_json = json_encode(array("success"=>true, "message"=>"Places successfully added!"));
										}
									}
								}
								exit(json_encode(array("routes"=>$routes_json, "places"=>$places_json)));
							} else {
								// neither routes nor places submitted
								exit(json_encode(array("success"=>false, "message"=>"No routes or places submitted!")));
							}
							break;
						}
					case "g":
						{
							$json = array();
							// get routes
							$query = "select `CID`, `CNAME`, `CLENGTH`, `CVISIBLE` from `TROUTES` where ( `CUSER` = '$username')";
							$result = mysql_query($query);
							if (!$result) {
								echo_mysql_error("Routes selection error");
							} else {
								$routes = array();
								while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
									$route_id = $row['CID'];
									$route_name = $row['CNAME'];
									$route_length = $row['CLENGTH'];
									$query = "select `CXKOORD`, `CYKOORD`, `CORDER` from `TWAYPOINTS` where ( `CRELROUTEID` = $route_id) order by `CORDER`";
									$result = mysql_query($query);
									if (!$result) {
										echo_mysql_error("Waypoint selection error");
									} else {
										$coordinates = array();
										while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
											array_push($coordinates, array($row['CXKOORD'], $row['CYKOORD']));
										}
										array_push($routes, array("name"=>$route_name, "distance"=>$route_length, "visible"=>$row['CVISIBLE'], "coordinates"=>$coordinates));
									}
								}
								array_push($json, $routes);
							}
							// get places
							$query = "select `CNAME`, `CXKOORD`, `CYKOORD`, `CVISIBLE` from `TLOCATIONS` where ( `CUSER` = '$username')";
							$result = mysql_query($query);
							if (!$result) {
								echo_mysql_error("Routes selection error");
							} else {
								$places = array();
								while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
									array_push($places, array("user"=>$username, "name"=>$row['CNAME'], "visible"=>$row['CVISIBLE'], "coordinates"=>array($row['CXKOORD'], $row['CYKOORD'])));
								}
								array_push($json, $places);
							}

							// respond the json
							exit(json_encode($json));
							break;
						}
					default:
						{
							exit(json_encode(array("success"=>false, "message"=>"Wrong action submitted!")));
							break;
						}
				}
				mysql_close($link);
			}
		}
	} else {
		exit(json_encode(array("success"=>false, "message"=>"No action submitted!")));
	}
} else {
	exit(json_encode(array("success"=>false, "message"=>"Not logged in!")));
}

function echo_mysql_error($error) {
	if ($link) {
		mysql_close($link);
	}
	exit(json_encode(array("success"=>false, "message"=>$error . ": " . mysql_error())));
}

?>
