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
			<xsl:element name="defs">
				<xsl:element name="g">
					<xsl:attribute name="id">city</xsl:attribute>
					<xsl:attribute name="fill">red</xsl:attribute>
					<xsl:element name="polygon">
						<xsl:attribute name="points">0,0 10,0 10,10 0,10 0,0</xsl:attribute>
					</xsl:element>
				</xsl:element>
			</xsl:element>

			<xsl:element name="g">
				<xsl:apply-templates select="node" />
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="node">
		<xsl:element name="g">
			<xsl:attribute name="transform">translate(<xsl:value-of select="@lon * 150 - 1050" /><xsl:text> </xsl:text><xsl:value-of
				select="@lat * -150 + 7500" />)</xsl:attribute>
			<xsl:element name="use">
				<xsl:attribute name="xlink:href">#city</xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="tag" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="tag">
		<xsl:if test="@k = &#34;name&#34;">
			<xsl:element name="text">
				<xsl:attribute name="id">name</xsl:attribute>
				<xsl:attribute name="x">15</xsl:attribute>
				<xsl:attribute name="y">0</xsl:attribute>
				<xsl:attribute name="pointer-events">none</xsl:attribute>
				<xsl:value-of select="@v" />
			</xsl:element>
		</xsl:if>
		<xsl:element name="tag">
			<xsl:attribute name="k"> <xsl:value-of select="@k" /> </xsl:attribute>
			<xsl:attribute name="v"> <xsl:value-of select="@v" /> </xsl:attribute>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>