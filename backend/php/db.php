<?php

session_start();

if ($_SESSION['loggedin'] === true) {

	if (isset($_GET['a'])) {

		$action = $_GET['a'];

		include "database_access.php";

		$json_communication_log_enabled = false;
		$json_communication_log_email = "webmaster@schteffens.de";

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
							if (isset($_FILES['importFile'])) {
								foreach ($_FILES['importFile'] as $file) {
									if ($content = file_get_contents($_FILES['importFile']['tmp_name'])) {
										$json = array();
										$data_xml = new simplexmlelement($content);
										$route_ids = array();
										$place_ids = array();
										foreach ($data_xml->children() as $data_element => $data_value) {
											switch ($data_element) {
												case "route":
													{
														foreach ($data_value->children() as $route_child => $route_child_value) {
															switch ($route_child) {
																case "name":
																	{
																		$route_name = $route_child_value;
																		break;
																	}
																case "length":
																	{
																		$route_length = $route_child_value;
																		break;
																	}
																case "coordinates":
																	{
																		$route_coordinates = array();
																		foreach ($route_child_value as $node_element => $node_element_value) {
																			switch ($node_element) {
																				case "coord":
																					{
																						foreach ($node_element_value->attributes() as $node_attribute => $node_attribute_value) {
																							if ($node_attribute == "lon") {
																								$lon = $node_attribute_value;
																							} else {
																								if ($node_attribute == "lat") {
																									$lat = $node_attribute_value;
																								}
																							}
																						}
																						array_push($route_coordinates, array($lat, $lon));
																						break;
																					}
																			}
																		}
																		break;
																	}
															}
														}
														$query = "insert into `TROUTES` ( `CNAME`, `CLENGTH`, `CUSER` ) values ( '$route_name', $route_length, '$username' )";
														$result = mysql_query($query);
														if (!$result) {
															array_push($json, array("error"=>"Route insertion error on route ". $route_name));
														} else {
															$route_id = mysql_insert_id();
															array_push($route_ids, $route_id);
															$order = 0;
															foreach ($route_coordinates as $coordinate) {
																$longitude = $coordinate[1];
																$latitude = $coordinate[0];
																$query = "insert into `TWAYPOINTS` ( `CRELROUTEID`, `CORDER`, `CXKOORD`, `CYKOORD` ) values ($route_id, $order, $longitude, $latitude)";
																$result = mysql_query($query);
																if (!$result) {
																	array_push($json, array("error"=>"Waypoint insertion error on waypoint ". $order . " of route " . $route_name));
																} else {
																	$order++;
																}
															}
														}
														break;
													}
												case "place":
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
																	array_push($json, array("error"=>"Location insertion error on location ". $place_name));
																} else {
																	array_push($place_ids, mysql_insert_id());
																}
															}
														}
														break;
													}
											}
										}

										// json response
										// get saved routes
										$query = "select `CID`, `CNAME`, `CLENGTH`, `CVISIBLE` from `TROUTES` where ( `CUSER` = '$username' )";
										$result = mysql_query($query);
										if (!$result) {
											array_push($json, array("error"=>"Routes selection error on routes of user ". $username));
										} else {
											$routes = array();
											while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
												$route_id = $row['CID'];
												if (in_array($route_id, $route_ids)) {
													$route_name = $row['CNAME'];
													$route_length = $row['CLENGTH'];
													if ($row['CVISIBLE'] == 0) {
														$route_visible = false;
													} else {
														if ($row['CVISIBLE'] == 1) {
															$route_visible = true;
														} else {
															$route_visible = null;
														}
													}
													$query = "select `CXKOORD`, `CYKOORD`, `CORDER` from `TWAYPOINTS` where ( `CRELROUTEID` = $route_id) order by `CORDER`";
													$result = mysql_query($query);
													if (!$result) {
														array_push($json, array("error"=>"Waypoint selection error on waypoints of route with id ". $route_id));
													} else {
														$coordinates = array();
														while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
															array_push($coordinates, array($row['CYKOORD'], $row['CXKOORD']));
														}
														array_push($routes, array("name"=>$route_name, "distance"=>$route_length, "visible"=>$route_visible, "coordinates"=>$coordinates));
													}
												}
											}
											array_push($json, $routes);
										}
										// get places
										$query = "select `CID`, `CNAME`, `CXKOORD`, `CYKOORD`, `CVISIBLE` from `TLOCATIONS` where ( `CUSER` = '$username')";
										$result = mysql_query($query);
										if (!$result) {
											array_push($json, array("error"=>"Locations selection error on locations of user ". $username));
										} else {
											$places = array();
											while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
												$place_id = $row['CID'];
												if (in_array($place_id, $place_ids)) {
													if ($row['CVISIBLE'] == 0) {
														$place_visible = false;
													} else {
														if ($row['CVISIBLE'] == 1) {
															$place_visible = true;
														} else {
															$place_visible = null;
														}
													}
													array_push($places, array("name"=>$row['CNAME'], "visible"=>$place_visible, "coordinates"=>array($row['CYKOORD'], $row['CXKOORD'])));
												}
											}
											array_push($json, $places);
										}
										$json_string = json_encode($json);
										if ($json_communication_log_enabled) {
											mail($json_communication_log_email, $username . " returned import json", $json_string);
										}
										exit($json_string);
									}
								}
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
								//	echo_mysql_error("Routes selection error");
							} else {
								while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
									$route_element = $locations_xml->addChild("route");
									$route_id = $row['CID'];
									$route_element->addChild("user", $username);
									$route_element->addChild("name", $row['CNAME']);
									$route_element->addChild("length", $row['CLENGTH']);
									$query = "select `CXKOORD`, `CYKOORD`, `CORDER` from `TWAYPOINTS` where ( `CRELROUTEID` = $route_id ) order by `CORDER`";
									$result = mysql_query($query);
									if (!$result) {
										//	echo_mysql_error("Waypoint selection error");
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
								//	echo_mysql_error("Routes selection error");
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
									if ($json_communication_log_enabled) {
										mail($json_communication_log_enabled, $username . " posted routes json", $_POST['routes']);
									}

									// delete old routes and it's waypoints
									// retrieve old routes to delete it's waypoints
									$query = "select `CID` from `TROUTES` where ( `CUSER` = '$username' )";
									$result = mysql_query($query);
									if (!$result) {
										array_push($json, array("error"=>"Routes selection error"));
									} else {
										// delete old waypoints of the routes
										while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
											$route_id = $row['CID'];
											$query = "delete from `TLOCATIONS` where ( `CRELROUTEID` = $route_id )";
											$result = mysql_query($query);
											if (!$result) {
												array_push($json, array("error"=>"Waypoints deletion error"));
											}
										}
									}
									// delete old routes
									$query = "delete from `TROUTES` where ( `CUSER` = '$username' )";
									$result = mysql_query($query);
									if (!$result) {
										array_push($json, array("error"=>"Routes deletion error"));
									}

									//	save new data
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
											array_push($json, array("error"=>"Routes insertion error on route ". $route_name));
										} else {
											$route_id = mysql_insert_id();
											$order = 0;
											foreach ($route_coordinates as $coordinate) {
												$latitude = $coordinate[0];
												$longitude = $coordinate[1];
												$query = "insert into `TWAYPOINTS` ( `CRELROUTEID`, `CORDER`, `CXKOORD`, `CYKOORD` ) values ($route_id, $order, $longitude, $latitude )";
												$result = mysql_query($query);
												if (!$result) {
													array_push($json, array("error"=>"Waypoint insertion error on waypoint $order of route ". $route_name));
												} else {
													$order++;
												}
											}
										}
										array_push($json, array("success"=>true, "message"=>"Routes successfully added!"));
									}
								}
								// saving places
								if ($_POST['places']) {
									if ($json_communication_log_enabled) {
										mail($json_communication_log_email, $username . " posted places json", $_POST['places']);
									}

									// delete old places
									$query = "delete from `TLOCATIONS` where ( `CUSER` = '$username' )";
									$result = mysql_query($query);
									if (!$result) {
										array_push($json, array("error"=>"Places deletion error"));
									}

									//	save new data
									$data = json_decode($_POST['places'], true);
									foreach ($data as $data_element) {
										$place_name = $data_element['name'];
										if ($data_element['visible'] == true) {
											$place_visible = 1;
										} else {
											$place_visible = 0;
										}
										$place_coordinates = $data_element['coordinates'];
										$latitude = $place_coordinates[0];
										$longitude = $place_coordinates[1];

										$query = "insert into `TLOCATIONS` ( `CNAME`, `CUSER`, `CVISIBLE`, `CXKOORD`, `CYKOORD` ) values ( '$place_name', '$username', $place_visible, $longitude, $latitude )";
										$result = mysql_query($query);
										if (!$result) {
											array_push($json, array("error"=>"Location insertion error on location ". $place_name));
										}
									}
									array_push($json, array("success"=>true, "message"=>"Places successfully added!"));
								}
								$json_string = json_encode($json);
								if ($json_communication_log_enabled) {
									mail($json_communication_log_email, $username . " returned save json", $json_string);
								}
								exit($json_string);
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
								array_push($json, array("error"=>"Routes selection error on routes of user ". $username));
							} else {
								$routes = array();
								while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
									$route_id = $row['CID'];
									$route_name = $row['CNAME'];
									$route_length = $row['CLENGTH'];
									if ($row['CVISIBLE'] == 0) {
										$route_visible = false;
									} else {
										if ($row['CVISIBLE'] == 1) {
											$route_visible = true;
										} else {
											$route_visible = null;
										}
									}
									$query = "select `CXKOORD`, `CYKOORD`, `CORDER` from `TWAYPOINTS` where ( `CRELROUTEID` = $route_id) order by `CORDER`";
									$result = mysql_query($query);
									if (!$result) {
										array_push($json, array("error"=>"Waypoints selection error on waypoints of route with id ". $route_id));
									} else {
										$coordinates = array();
										while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
											array_push($coordinates, array($row['CYKOORD'], $row['CXKOORD']));
										}
										array_push($routes, array("name"=>$route_name, "distance"=>$route_length, "visible"=>$route_visible, "coordinates"=>$coordinates));
									}
								}
								array_push($json, $routes);
							}
							// get places
							$query = "select `CNAME`, `CXKOORD`, `CYKOORD`, `CVISIBLE` from `TLOCATIONS` where ( `CUSER` = '$username')";
							$result = mysql_query($query);
							if (!$result) {
								array_push($json, array("error"=>"Routes selection error on routes of user ". $username));
							} else {
								$places = array();
								while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
									if ($row['CVISIBLE'] == 0) {
										$place_visible = false;
									} else {
										if ($row['CVISIBLE'] == 1) {
											$place_visible = true;
										} else {
											$place_visible = null;
										}
									}
									array_push($places, array("name"=>$row['CNAME'], "visible"=>$place_visible, "coordinates"=>array($row['CYKOORD'], $row['CXKOORD'])));
								}
								array_push($json, $places);
							}

							// respond the json
							$json_string = json_encode($json);
							if ($json_communication_log_enabled) {
								mail($json_communication_log_email, $username . " returned get json", $json_string);
							}
							exit($json_string);
							break;
						}
					default:
						{
							exit(json_encode(array("success"=>false, "message"=>"Wrong action submitted!")));
							break;
						}
				}
				if ($link) {
					mysql_close($link);
				}
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
	$json_string = json_encode(array("success"=>false, "message"=>$error . ": " . mysql_error()));
	if ($json_communication_log_enabled) {
		mail($json_communication_log_email, $username . " mysql error", $json_string);
	}
	exit($json_string);
}

?>
