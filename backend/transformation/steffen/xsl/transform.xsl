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
			<xsl:element name="g">
				<xsl:attribute name="fill">none</xsl:attribute>
				<xsl:attribute name="stroke">black</xsl:attribute>
				<xsl:attribute name="stroke-width">2</xsl:attribute>
				<xsl:apply-templates select="way" />
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="way">
		<xsl:element name="g">
			<xsl:element name="polyline">
				<xsl:attribute name="points">
					<xsl:for-each select="nd">
						<xsl:variable name="ref" select="@ref" />
						<xsl:apply-templates select="../../node[@id=$ref]" />
						<xsl:text> </xsl:text>
					</xsl:for-each>
				</xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="tag" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="node">
		<xsl:value-of select="@lon * 150 - 1050" />
		<xsl:text>,</xsl:text>
		<xsl:value-of select="@lat * -190 + 9500" />
	</xsl:template>

	<xsl:template match="tag">
		<xsl:element name="tag">
			<xsl:attribute name="k"> <xsl:value-of select="@k" /> </xsl:attribute>
			<xsl:attribute name="v"> <xsl:value-of select="@v" /> </xsl:attribute>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>