<?xml version='1.0' encoding='UTF-8' ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" />

	<xsl:template match="/">
		<xsl:apply-templates select="osm" />
	</xsl:template>

	<xsl:template match="osm">
		<svg xmlns:xlink="http://www.w3.org/1999/xlink">
			<xsl:attribute name="xmlns">http://www.w3.org/2000/svg</xsl:attribute>
			<xsl:attribute name="style">position:absolute;</xsl:attribute>
			<xsl:element name="g">
				<xsl:attribute name="id">~~id~~</xsl:attribute>
				<xsl:attribute name="fill">none</xsl:attribute>
				<xsl:attribute name="stroke">black</xsl:attribute>
				<xsl:attribute name="stroke-width">1</xsl:attribute>
				<xsl:apply-templates select="way" />
			</xsl:element>
		</svg>
	</xsl:template>

	<xsl:template match="way">
		<xsl:element name="polyline">
			<xsl:attribute name="points">
				<xsl:for-each select="nd">
					<xsl:variable name="ref" select="@ref" />
					<xsl:apply-templates select="../../node[@id=$ref]" />
					<xsl:text> </xsl:text>
				</xsl:for-each>
			</xsl:attribute>
			<xsl:apply-templates select="tag" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="node">
		<xsl:value-of select='format-number(@lon * 150 - 1050, "#.000")' />
		<xsl:text>,</xsl:text>
		<xsl:value-of select='format-number(@lat * -200 + 10000, "#.000")' />
	</xsl:template>

	<xsl:template match="tag">
		<xsl:choose>
			<xsl:when test="@k = &#34;admin_level&#34;">
				<xsl:attribute name="admin_lvl"><xsl:value-of select="@v"></xsl:value-of></xsl:attribute>
			</xsl:when>
			<xsl:when test="@k = &#34;ref&#34;">
				<xsl:attribute name="mw_ref"><xsl:value-of select="@v"></xsl:value-of></xsl:attribute>
			</xsl:when>
			<xsl:when test="@k = &#34;water&#34;">
				<xsl:attribute name="water"><xsl:value-of select="@v"></xsl:value-of></xsl:attribute>
			</xsl:when>
			<xsl:when test="@k = &#34;name&#34;">
				<xsl:attribute name="name"><xsl:value-of select="@v"></xsl:value-of></xsl:attribute>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
