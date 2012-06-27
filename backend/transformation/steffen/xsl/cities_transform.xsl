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
				<xsl:element name="polygon">
					<xsl:attribute name="id">city</xsl:attribute>
					<xsl:attribute name="fill">red</xsl:attribute>
					<xsl:attribute name="points">-5,-5 5,-5 5,5 -5,5</xsl:attribute>
				</xsl:element>
				<xsl:element name="polygon">
					<xsl:attribute name="id">town</xsl:attribute>
					<xsl:attribute name="fill">red</xsl:attribute>
					<xsl:attribute name="points">-4,-4 4,-4 4,4 -4,4</xsl:attribute>
				</xsl:element>
				<xsl:element name="polygon">
					<xsl:attribute name="id">village</xsl:attribute>
					<xsl:attribute name="fill">red</xsl:attribute>
					<xsl:attribute name="points">-3,-3 3,-3 3,3 -3,3</xsl:attribute>
				</xsl:element>
				<xsl:element name="polygon">
					<xsl:attribute name="id">hamlet</xsl:attribute>
					<xsl:attribute name="fill">red</xsl:attribute>
					<xsl:attribute name="points">-2,-2 2,-2 2,2 -2,2</xsl:attribute>
				</xsl:element>
				<xsl:element name="polygon">
					<xsl:attribute name="id">suburb</xsl:attribute>
					<xsl:attribute name="fill">orange</xsl:attribute>
					<xsl:attribute name="points">-1,-1 1,-1 1,1 -1,1</xsl:attribute>
				</xsl:element>
			</xsl:element>

			<xsl:apply-templates select="node" />
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
					<xsl:attribute name="x">15</xsl:attribute>
					<xsl:attribute name="y">0</xsl:attribute>
					<xsl:attribute name="pointer-events">none</xsl:attribute>
					<xsl:value-of select="@v" />
				</xsl:element>
			</xsl:when>
			<xsl:when test="@k = &#34;place&#34;">
				<xsl:element name="use">
					<xsl:attribute name="xlink:href">#<xsl:value-of select="@v"></xsl:value-of></xsl:attribute>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>