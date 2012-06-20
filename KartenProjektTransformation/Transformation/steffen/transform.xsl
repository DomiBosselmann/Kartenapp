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
				<xsl:element name="g">
					<xsl:attribute name="style">fill:none;stroke:deeppink;stroke-width:1</xsl:attribute>
					<xsl:attribute name="transform">scale(100) translate(-47 -7)</xsl:attribute>
					<xsl:apply-templates select="way" />
				</xsl:element>
				<xsl:element name="g">
					<xsl:attribute name="style">fill:none;stroke:deeppink;stroke-width:1</xsl:attribute>
					<xsl:attribute name="transform">scale(20) translate(-7 -7)</xsl:attribute>
					<polyline
						points="29.212345,17.712345 37.212345,28.712345 48.212345,39.712345 28.412345,78.312345 68.012345,57.912345 78.912345,67.312345 89.112345,77.112345 18.812345,28.512345" />
					<polyline points="20,20 40,25 60,40 80,120 120,140 200,180" />
				</xsl:element>
			</xsl:element>
			<!-- <xsl:element name="g"> <xsl:element name="g"> <xsl:attribute name="fill">none</xsl:attribute> 
				<xsl:attribute name="stroke-width">1</xsl:attribute> <xsl:attribute name="transform">scale(100) translate(-47 
				-7)</xsl:attribute> <xsl:for-each select="way"> <xsl:element name="g"> <xsl:element name="use"> <xsl:attribute 
				name="xlink:href">#<xsl:value-of select="@id" /></xsl:attribute> </xsl:element> </xsl:element> </xsl:for-each> 
				</xsl:element> </xsl:element> -->
		</xsl:element>
	</xsl:template>

	<xsl:template match="way">
		<xsl:element name="polyline">
			<!-- <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute> <xsl:attribute name="stroke"> 
				<xsl:variable name="posi" select="position() mod 16" /> <xsl:choose> <xsl:when test="$posi = 0">sandybrown</xsl:when> 
				<xsl:when test="$posi = 1">blue</xsl:when> <xsl:when test="$posi = 2">green</xsl:when> <xsl:when test="$posi 
				= 3">yellow</xsl:when> <xsl:when test="$posi = 4">orange</xsl:when> <xsl:when test="$posi = 5">grey</xsl:when> 
				<xsl:when test="$posi = 6">violet</xsl:when> <xsl:when test="$posi = 7">aquamarine</xsl:when> <xsl:when 
				test="$posi = 8">brown</xsl:when> <xsl:when test="$posi = 9">cyan</xsl:when> <xsl:when test="$posi = 
				10">lawngreen</xsl:when> <xsl:when test="$posi = 11">hotpink</xsl:when> <xsl:when test="$posi = 12">orangered</xsl:when> 
				<xsl:when test="$posi = 13">salmon</xsl:when> <xsl:when test="$posi = 14">black</xsl:when> <xsl:when 
				test="$posi = 15">olivedrab</xsl:when> </xsl:choose> </xsl:attribute> -->
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
		<xsl:value-of select="@lat" />
		<xsl:text>,</xsl:text>
		<xsl:value-of select="@lon" />
	</xsl:template>

	<xsl:template match="tag">
		<xsl:element name="tag">
			<xsl:attribute name="k"> <xsl:value-of select="@k" /> </xsl:attribute>
			<xsl:attribute name="v"> <xsl:value-of select="@v" /> </xsl:attribute>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>