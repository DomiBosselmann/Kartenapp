<?php 

switch (substr($_GET['l'],0,1)) {
	case "c":
		{
			$xslname = "../xsl/cities_transform.xsl";
			break;
		}
	default:
		{
			$xslname = "../xsl/transform.xsl";
			break;
		}
}

$xsl = file_get_contents($xslname);

$id = $_GET["id"];

switch (substr($_GET['l'],0,1)) {
	case "c":
		{
			switch ($id) {
				case "cities":
					{
						$rect_id_replace = "city";
						$rect_fill_replace = "red";
						$rect_coord_replace = "-5";
						$rect_size_replace = "10";
						break;
					}
				case "towns":
					{
						$rect_id_replace = "town";
						$rect_fill_replace = "red";
						$rect_coord_replace = "-4";
						$rect_size_replace = "8";
						break;
					}
				case "villages":
					{
						$rect_id_replace = "village";
						$rect_fill_replace = "red";
						$rect_coord_replace = "-3";
						$rect_size_replace = "6";
						break;
					}
				case "hamlets":
					{
						$rect_id_replace = "hamlet";
						$rect_fill_replace = "red";
						$rect_coord_replace = "-2";
						$rect_size_replace = "4";
						break;
					}
				case "suburbs":
					{
						$rect_id_replace = "suburb";
						$rect_fill_replace = "orange";
						$rect_coord_replace = "-1";
						$rect_size_replace = "2";
						break;
					}
			}

			$xsl = str_replace("~~id~~", $id, $xsl);
			$xsl = str_replace("~~rect_id~~", $rect_id_replace, $xsl);
			$xsl = str_replace("~~rect_fill~~", $rect_fill_replace, $xsl);
			$xsl = str_replace("~~rect_coord~~", $rect_coord_replace, $xsl);
			$xsl = str_replace("~~rect_size~~", $rect_size_replace, $xsl);
			break;
		}
	case "b":
		{
			$xsl = str_replace("~~id~~", $id, $xsl);
			break;
		}
	case "s":
		{
			$xsl = str_replace("~~id~~", $id, $xsl);
			break;
		}
	case "w":
		{
			$xsl = str_replace("~~id~~", $id, $xsl);
			break;
		}
}

echo $xsl;

?>