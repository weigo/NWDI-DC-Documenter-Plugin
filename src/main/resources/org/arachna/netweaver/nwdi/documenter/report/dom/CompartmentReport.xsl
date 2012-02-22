<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="html" indent="yes" encoding="UTF-8" />
  <xsl:strip-space elements="*" />
  <xsl:param name="imageLocation" />
  <xsl:param name="cssLocation" />
  <xsl:param name="imageFormat" />
  <xsl:template match="compartment">
    <xsl:element name="html">
      <xsl:element name="head">
        <xsl:element name="title">
          <xsl:text>Report für Development Configurations</xsl:text>
        </xsl:element>
        <meta http-equiv="pragma" content="no-cache" />
        <meta http-equiv="cache-control" content="no-cache" />
        <meta http-equiv="expires" content="0" />
        <meta http-equiv="description" content="Report für Development Configurations" />
        <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
        <xsl:element name="link">
          <xsl:attribute name="rel">stylesheet</xsl:attribute>
          <xsl:attribute name="type">text/css</xsl:attribute>
          <xsl:attribute name="href">
            <xsl:value-of select="concat($cssLocation, '/report.css')" />
		  </xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="body">
        <xsl:attribute name="name">
          <xsl:value-of select="@sc-name" />
		</xsl:attribute>
        <xsl:element name="h3">
          <xsl:text>Softwarekomponente </xsl:text>
          <xsl:value-of select="@sc-name" />
        </xsl:element>
        <xsl:element name="table">
          <xsl:element name="caption">
            Entwicklungskomponenten
          </xsl:element>
          <xsl:element name="thead">
            <xsl:element name="tr">
              <xsl:element name="th">
                Komponente
              </xsl:element>
              <xsl:element name="th">
                Typ
              </xsl:element>
              <xsl:element name="th">
                Hersteller
              </xsl:element>
              <xsl:element name="th">
                Beschreibung
              </xsl:element>
            </xsl:element>
          </xsl:element>
          <xsl:element name="tbody">
            <xsl:for-each select="development-components/development-component">
              <xsl:sort select="@name" order="descending" data-type="text" />
              <xsl:element name="tr">
                <xsl:choose>
                  <xsl:when test="position() mod 2 != 1">
                    <xsl:attribute name="class">even</xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:attribute name="class">odd</xsl:attribute>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:element name="td">
                  <xsl:element name="a">
                    <xsl:attribute name="href">
                    <xsl:value-of select="concat(@vendor, '~', translate(@name,'/','~'), '.html')" />
				  </xsl:attribute>
                    <xsl:value-of select="@name" />
                  </xsl:element>
                </xsl:element>
                <xsl:element name="td">
                  <xsl:value-of select="@type" />
                </xsl:element>
                <xsl:element name="td">
                  <xsl:value-of select="@vendor" />
                </xsl:element>
                <xsl:element name="td">
                  <xsl:value-of select="description/text()" />
                </xsl:element>
              </xsl:element>
            </xsl:for-each>
          </xsl:element>
        </xsl:element>
        <h4>Entwicklungskomponenten, welche in dieser Softwarekomponente benutzt werden</h4>
        <p>
          <xsl:element name="img">
            <xsl:attribute name="src">
              <xsl:value-of select="concat($imageLocation, '/', @name, '-usedDCs.', $imageFormat)" />
            </xsl:attribute>
          </xsl:element>
        </p>
        <h4>Entwicklungskomponenten, welche diese Softwarekomponente benutzen</h4>
        <xsl:element name="img">
          <xsl:attribute name="src">
        <xsl:value-of select="concat($imageLocation, '/', @name, '-usingDCs.', $imageFormat)" />
      </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:template>
  <xsl:template name="image">
    <xsl:param name="name" />
    <xsl:choose>
      <xsl:when test="$imageFormat = 'svg'">
        <xsl:element name="object">
          <xsl:attribute name="data"><xsl:value-of select="concat($imageLocation, '/', $name, '.', $imageFormat)" /></xsl:attribute>
          <xsl:attribute name="type">image/svg+xml</xsl:attribute>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="img">
          <xsl:attribute name="src"><xsl:value-of select="concat($imageLocation, '/', $name, '.', $imageFormat)" />
        </xsl:attribute>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>