<?xml version='1.0' encoding='UTF-8' ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" />

	<xsl:template match="/">
		<xsl:apply-templates select="osm" />
	</xsl:template>

	<xsl:template match="osm">
		<xsl:element name="svg">
			<xsl:attribute name="xmlns">http://www.w3.org/2000/svg</xsl:attribute>
			<!-- <svg xmlns="http://www.w3.org/2000/svg"> -->
			<!-- <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"> -->
			<xsl:attribute name="style">position:absolute;</xsl:attribute>
			<xsl:element name="defs">
				<xsl:element name="rect">
					<xsl:attribute name="id">city</xsl:attribute>
					<xsl:attribute name="fill">red</xsl:attribute>
					<xsl:attribute name="x">-5</xsl:attribute>
					<xsl:attribute name="y">-5</xsl:attribute>
					<xsl:attribute name="width">10</xsl:attribute>
					<xsl:attribute name="height">10</xsl:attribute>
				</xsl:element>
				<xsl:element name="rect">
					<xsl:attribute name="id">town</xsl:attribute>
					<xsl:attribute name="fill">red</xsl:attribute>
					<xsl:attribute name="x">-4</xsl:attribute>
					<xsl:attribute name="y">-4</xsl:attribute>
					<xsl:attribute name="width">8</xsl:attribute>
					<xsl:attribute name="height">8</xsl:attribute>
				</xsl:element>
				<xsl:element name="rect">
					<xsl:attribute name="id">village</xsl:attribute>
					<xsl:attribute name="fill">red</xsl:attribute>
					<xsl:attribute name="x">-3</xsl:attribute>
					<xsl:attribute name="y">-3</xsl:attribute>
					<xsl:attribute name="width">6</xsl:attribute>
					<xsl:attribute name="height">6</xsl:attribute>
				</xsl:element>
				<xsl:element name="rect">
					<xsl:attribute name="id">hamlet</xsl:attribute>
					<xsl:attribute name="fill">red</xsl:attribute>
					<xsl:attribute name="x">-2</xsl:attribute>
					<xsl:attribute name="y">-2</xsl:attribute>
					<xsl:attribute name="width">4</xsl:attribute>
					<xsl:attribute name="height">4</xsl:attribute>
				</xsl:element>
				<xsl:element name="rect">
					<xsl:attribute name="id">suburb</xsl:attribute>
					<xsl:attribute name="fill">orange</xsl:attribute>
					<xsl:attribute name="x">-1</xsl:attribute>
					<xsl:attribute name="y">-1</xsl:attribute>
					<xsl:attribute name="width">2</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
				</xsl:element>
			</xsl:element>

			<xsl:apply-templates select="node" />
			<!-- </svg> -->
		</xsl:element>
	</xsl:template>

	<xsl:template match="node">
		<xsl:element name="g">
			<xsl:attribute name="transform">translate(<xsl:value-of
				select='format-number(@lon * 150 - 1050, "#.000")' /><xsl:text> </xsl:text><xsl:value-of
				select='format-number(@lat * -200 + 10000, "#.000")' />)</xsl:attribute>
			<xsl:apply-templates select="tag" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="tag">
		<xsl:choose>
			<xsl:when test="@k = &#34;name&#34;">
				<xsl:element name="text">
					<xsl:attribute name="id">name</xsl:attribute>
					<xsl:attribute name="x">12</xsl:attribute>
					<xsl:attribute name="y">5</xsl:attribute>
					<xsl:attribute name="pointer-events">none</xsl:attribute>
					<xsl:value-of select="@v" />
				</xsl:element>
			</xsl:when>
			<xsl:when test="@k = &#34;place&#34;">
				<xsl:element name="use">
					<xsl:attribute name="xlink:href">#<xsl:value-of select="@v" /></xsl:attribute>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>