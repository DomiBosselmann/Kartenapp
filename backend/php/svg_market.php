<?php

if ($_GET) {
	header("Content-Type: image/svg+xml");

	$xmlHeader = '<?xml version="1.0" encoding="UTF-8" ?>';
	$xmlnsNamespace = "http://www.w3.org/2000/svg";
	$xlinkNamespace = "http://www.w3.org/1999/xlink";
	$newSVG = new simplexmlelement($xmlHeader . '<svg style="position:absolute;" xmlns="' . $xmlnsNamespace . '" xmlns:xlink="' . $xlinkNamespace . '" />');

	//	$newSVG->addAttribute("style", "position:absolute;");
	// $newSVG->addAttribute("xmlns", $xmlnsNamespace);
	// $newSVG->addAttribute("xlink:href", null, $xlinkNamespace);

	// coordinate detection
	$region = false;
	$coords = $newSVG->addChild("coords");
	$originLon1 = 7.483968;
	$originLon2 = 10.557579;
	$originLat1 = 49.836835;
	$originLat2 = 47.478901;
	if (isset($_GET['lon1'])) {
		if ($_GET['lon1'] < $originLon1) {
			$lon1 = getPixelForLon($originLon1);
			$coords->addAttribute("lon1", $originLon1);
		} else {
			$lon1 = getPixelForLon($_GET['lon1']);
			$coords->addAttribute("lon1", $_GET['lon1']);
			$region = true;
		}
	} else {
		$lon1 = getPixelForLon($originLon1);
		$coords->addAttribute("lon1", $originLon1);
	}
	if (isset($_GET['lon2'])) {
		if ($_GET['lon2'] > $originLon2) {
			$lon2 = getPixelForLon($originLon2);
			$coords->addAttribute("lon2", $originLon2);
		} else {
			$lon2 = getPixelForLon($_GET['lon2']);
			$coords->addAttribute("lon2", $_GET['lon2']);
			$region = true;
		}
	} else {
		$lon2 = getPixelForLon($originLon2);
		$coords->addAttribute("lon2", $originLon2);
	}
	if (isset($_GET['lat1'])) {
		if ($_GET['lat1'] > $originLat1) {
			$lat1 = getPixelForLat($originLat1);
			$coords->addAttribute("lat1", $originLat1);
		} else {
			$lat1 = getPixelForLat($_GET['lat1']);
			$coords->addAttribute("lat1", $_GET['lat1']);
			$region = true;
		}
	} else {
		$lat1 = getPixelForLat($originLat1);
		$coords->addAttribute("lat1", $originLat1);
	}
	if (isset($_GET['lat2'])) {
		if ($_GET['lat2'] < $originLat2) {
			$lat2 = getPixelForLat($originLat2);
			$coords->addAttribute("lat2", $originLat2);
		} else {
			$lat2 = getPixelForLat($_GET['lat2']);
			$coords->addAttribute("lat2", $_GET['lat2']);
			$region = true;
		}
	} else {
		$lat2 = getPixelForLat($originLat2);
		$coords->addAttribute("lat2", $originLat2);
	}

	// width + height detection
	$originWidth = 490.0;
	$originHeight = 510.0;
	$extraWidth = 0;
	$extraHeight = 0;
	if (isset($_GET['width'])) {
		$sizing = true;
		$width = $_GET['width'];
	} else {
		$width = $originWidth;
	}
	if (isset($_GET['height'])) {
		$sizing = true;
		$height = $_GET['height'];
	} else {
		$height = $originHeight;
	}
	if (isset($_GET['custom'])) {
		$widthFactor = $width / $originWidth;
		$heightFactor = $height / $originHeight;
	} else {
		$lonKm = 100.0;
		$latKm = 111.32;

		$tryHeight = $width * ($latKm / $lonKm);
		if ($tryHeight <= $height) {
			$height = $tryHeight;
			$extraHeight = ($_GET['height'] - $height) / 2;
		} else {
			$width = $height * ($lonKm / $latKm);
			$extraWidth = ($_GET['width'] - $width) / 2;
		}

		if ($region === true) {
			$delta_x = $lon2 - $lon1;
			$widthFactor = $width / $delta_x;
			$delta_y = $lat2 - $lat1;
			$heightFactor = $height / $delta_y;
		} else {
			$widthFactor = $width / $originWidth;
			$heightFactor = $height / $originHeight;
		}
	}

	// svg building
	foreach ($_GET as $para => $layer) {
		switch ($para) {
			case "lon1":
				{
					break;
				}
			case "lon2":
				{
					break;
				}
			case "lat1":
				{
					break;
				}
			case "lat2":
				{
					break;
				}
			case "width":
				{
					break;
				}
			case "height":
				{
					break;
				}
			case "custom":
				{
					break;
				}
			case "border":
				{
					break;
				}
			default:
				{
					$nope = false;
					$filename = "../svgs/";
					switch ($layer) {
						case "b":
							{
								$filename .= "bounds/federal_polygon.svg";
								break;
							}
						case "b1":
							{
								$filename .= "bounds/counties.svg";
								break;
							}
						case "c":
							{
								$filename .= "cities/cities.svg";
								break;
							}
						case "c1":
							{
								$filename .= "cities/towns.svg";
								break;
							}
						case "c2":
							{
								$filename .= "cities/villages.svg";
								break;
							}
						case "c3":
							{
								$filename .= "cities/hamlets.svg";
								break;
							}
						case "c4":
							{
								$filename .= "cities/suburbs.svg";
								break;
							}
						case "s":
							{
								$filename .= "streets/motorways.svg";
								break;
							}
						case "s1":
							{
								$filename .= "streets/primaries.svg";
								break;
							}
						case "s2":
							{
								$filename .= "streets/secondaries.svg";
								break;
							}
						case "s3":
							{
								$filename .= "streets/tertiaries.svg";
								break;
							}
						case "w":
							{
								$filename .= "waters/rivers.svg";
								break;
							}
						case "w1":
							{
								$filename .= "waters/canals.svg";
								break;
							}
						case "w2":
							{
								$filename .= "waters/namedLakes.svg";
								break;
							}
						case "w3":
							{
								$filename .= "waters/allLakes.svg";
								break;
							}
						default:
							{
								$nope = true;
								break;
							}
					}
					if ($nope === false) {
						$oldSVG = simplexml_load_file($filename);
						foreach ($oldSVG->children() as $node0 => $value0) {
							switch ($node0) {
								case "defs":
									{
										if (!$defs) {
											$defs = $newSVG->addChild($node0, $value0);
										}
										copyAllAttributes($value0, $defs);
										copyAllChildren($value0, $defs);
										break;
									}
								case "g":
									{
										$group = $newSVG->addChild($node0);
										copyAllAttributes($value0, $group);
										foreach ($value0->children() as $node1 => $value1) {
											switch ($node1) {
												case "use":
													{
														$keep = false;
														foreach ($value1->attributes() as $attr0 => $attrValue0) {
															switch ($attr0) {
																case "transform":
																	{
																		$translate = $attrValue0;
																		$keep = false;
																		$begin = stripos($translate, "(") + 1;
																		$space = stripos($translate, " ", $begin);
																		$end = stripos($translate, ")", $space);
																		$lon = doubleval(substr($translate, $begin, $space - $begin));
																		$lat = doubleval(substr($translate, $space + 1, $end - $space - 1));
																		unset($value1[transform]);
																		if (($lon >= $lon1) && ($lon <= $lon2)) {
																			if (($lat >= $lat1) && ($lat <= $lat2)) {
																				$keep = true;
																				$lon = (($lon - $lon1) * $widthFactor) + $extraWidth;
																				$lat = (($lat - $lat1) * $heightFactor) + $extraHeight;
																				$newTransform = "translate(" . $lon . " " . $lat . ")";
																			}
																		}
																		break;
																	}
															}
														}
														if ($keep === true) {
															$use = $group->addChild($node1, $value1);
															copyAllAttributes($value1, $use);
															$use->addAttribute("transform", $newTransform);
															copyAllChildren($value1, $use);
														}
														break;
													}
												case "polyline":
													{
														$keep = false;
														foreach ($value1->attributes() as $attr0 => $attrValue0) {
															switch ($attr0) {
																case "points":
																	{
																		$points = explode(" ", $attrValue0);
																		unset($value1[points]);
																		$newPoints = "";
																		foreach ($points as $point) {
																			if ($point != "") {
																				$comma = strpos($point, ",");
																				$lon = doubleval(substr($point, 0, $comma));
																				if (($lon >= $lon1) && ($lon <= $lon2)) {
																					$lat = doubleval(substr($point, $comma + 1));
																					if (($lat >= $lat1) && ($lat <= $lat2)) {
																						$keep = true;
																						$lon = (($lon - $lon1) * $widthFactor) + $extraWidth;
																						$lat = (($lat - $lat1) * $heightFactor) + $extraHeight;
																						$newPoints .= $lon . "," . $lat . " ";
																					}
																				}
																			}
																		}
																		break;
																	}
															}
														}
														if ($keep === true) {
															$polyline = $group->addChild($node1, $value1);
															copyAllAttributes($value1, $group);
															$polyline->addAttribute("points", $newPoints);
															copyAllChildren($value1, $group);
														}
														break;
													}
												case "polygon":
													{
														foreach ($value1->attributes() as $attr0 => $attrValue0) {
															switch ($attr0) {
																case "points":
																	{
																		$points = explode(" ", $attrValue0);
																		unset($value1[points]);
																		$newPoints = "";
																		foreach ($points as $point) {
																			if ($point != "") {
																				$comma = strpos($point, ",");
																				$lon = doubleval(substr($point, 0, $comma));
																				$lat = doubleval(substr($point, $comma + 1));
																				if ($lon < $lon1) {
																					$lon = $lon1;
																				}
																				if ($lon > $lon2) {
																					$lon = $lon2;
																				}
																				if ($lat < $lat1) {
																					$lat = $lat1;
																				}
																				if ($lat > $lat2) {
																					$lat = $lat2;
																				}
																				if ((!($lon == $oldLon)) && (!($lat == $oldLat))) {
																					$oldLon = $lon;
																					$oldLat = $lat;
																					$lon = (($lon - $lon1) * $widthFactor) + $extraWidth;
																					$lat = (($lat - $lat1) * $heightFactor) + $extraHeight;
																					$newPoints .= $lon . "," . $lat . " ";
																				}
																			}
																		}
																		break;
																	}
															}
														}
														$polygon = $group->addChild($node1, $value1);
														copyAllAttributes($value1, $group);
														$polygon->addAttribute("points", $newPoints);
														copyAllChildren($value1, $group);
														break;
													}
											}
										}
										break;
									}
							}
						}
					}
					break;
				}
		}
	}

	// border adding
	if (isset($_GET['border'])) {
		$rect = $newSVG->addChild("rect");
		$rect->addAttribute("id", "outerBorder");
		$rect->addAttribute("fill", "none");
		$rect->addAttribute("stroke", "black");
		if (isset($_GET['width'])) {
			$rect->addAttribute("width", $_GET['width']);
		} else {
			$rect->addAttribute("width", $originWidth);
		}
		if (isset($_GET['height'])) {
			$rect->addAttribute("height", $_GET['height']);
		} else {
			$rect->addAttribute("height", $originHeight);
		}

		if (!isset($_GET['custom'])) {
			$rect = $newSVG->addChild("rect");
			$rect->addAttribute("id", "innerBorder");
			$rect->addAttribute("fill", "none");
			$rect->addAttribute("stroke", "black");
			$rect->addAttribute("x", $extraWidth);
			$rect->addAttribute("y", $extraHeight);
			$rect->addAttribute("width", $width);
			$rect->addAttribute("height", $height);
		}
	}

	// echoing
	echo $newSVG->asXML();
}

function copyAllChildren($oldNodeValue, $newNode) {
	foreach ($oldNodeValue->children() as $node => $value) {
		$newNodeChild = $newNode->addChild($node, $value);
		copyAllAttributes($value, $newNodeChild);
		copyAllChildren($value, $newNodeChild);
	}
	foreach ($oldNodeValue->getNamespaces(true) as $namespace => $namespaceValue) {
		foreach ($oldNodeValue->children($namespaceValue) as $node => $value) {
			$newNode->addAttribute($namespace . ":" . $node, $value, $namespaceValue);
		}
	}
}

function copyAllAttributes($oldNodeValue, $newNode) {
	foreach ($oldNodeValue->attributes() as $attribute => $attributeValue) {
		$newNode->addAttribute($attribute, $attributeValue);
	}
	foreach ($oldNodeValue->getNamespaces(true) as $namespace => $namespaceValue) {
		foreach ($oldNodeValue->attributes($namespaceValue) as $attribute => $attributeValue) {
			$newNode->addAttribute($namespace . ":" . $attribute, $attributeValue, $namespaceValue);
		}
	}
}

function getPixelForLon($lon) {
	// for 500px
	return (doubleval($lon) - 7.483968) * 162.675;
}

function getPixelForLat($lat) {
	// for 500px
	return (-1 * doubleval($lat) + 49.83) * 218.716;
}

?>