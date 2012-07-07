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
			<xsl:attribute name="style">position:absolute;</xsl:attribute>
			<xsl:element name="defs">
				<xsl:element name="rect">
					<xsl:attribute name="id">~~rect_id~~</xsl:attribute>
					<xsl:attribute name="fill">~~rect_fill~~</xsl:attribute>
					<xsl:attribute name="x">~~rect_coord~~</xsl:attribute>
					<xsl:attribute name="y">~~rect_coord~~</xsl:attribute>
					<xsl:attribute name="width">~~rect_size~~</xsl:attribute>
					<xsl:attribute name="height">~~rect_size~~</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:element name="g">
				<xsl:attribute name="id">~~id~~</xsl:attribute>
				<xsl:apply-templates select="node" />
			</xsl:element>
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
				<xsl:attribute name="name"><xsl:value-of select="@v" /></xsl:attribute>
				<!-- <xsl:element name="text"> <xsl:attribute name="name"><xsl:value-of select="@v" /></xsl:attribute> 
					<xsl:attribute name="x">12</xsl:attribute> <xsl:attribute name="y">5</xsl:attribute> <xsl:attribute name="pointer-events">none</xsl:attribute> 
					<xsl:value-of select="@v" /> </xsl:element> -->
			</xsl:when>
			<xsl:when test="@k = &#34;place&#34;">
				<xsl:element name="use">
					<xsl:attribute name="xlink:href">#<xsl:value-of select="@v" /></xsl:attribute>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>