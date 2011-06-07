<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

 <xsl:output method="html" indent="yes" encoding="UTF-8" />
 <xsl:strip-space elements="*" />
 <xsl:param name="imageLocation" />
 <xsl:param name="cssLocation" />
 <xsl:param name="imageFormat" />

 <xsl:template match="compartments">
  <xsl:element name="html">
   <xsl:element name="head">
    <xsl:element name="title">
     <xsl:text>Report für Softwarekomponenten</xsl:text>
    </xsl:element>
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="description" content="Report für Softwarekomponenten" />
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
    <xsl:element name="h1">
     <xsl:text>Softwarekomponenten </xsl:text>
    </xsl:element>
    <xsl:element name="table">
     <xsl:element name="caption">
      Softwarekomponenten
     </xsl:element>
     <xsl:element name="thead">
      <xsl:element name="tr">
       <xsl:element name="th">
        Komponente
       </xsl:element>
       <xsl:element name="th">
        Hersteller
       </xsl:element>
      </xsl:element>
     </xsl:element>
     <xsl:element name="tbody">
      <xsl:apply-templates select="compartment" />
     </xsl:element>
    </xsl:element>
    <p>
     <xsl:call-template name="image">
      <xsl:with-param name="name">
       <xsl:value-of select="@development-configuration" />
      </xsl:with-param>
     </xsl:call-template>
    </p>

   </xsl:element>
  </xsl:element>
 </xsl:template>

 <xsl:template match="compartment[@archive-state='no']">
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
     <xsl:attribute name="href"><xsl:value-of select="concat(@sc-name, '/index.html')" />
								</xsl:attribute>
     <xsl:value-of select="@sc-name" />
    </xsl:element>
   </xsl:element>
   <xsl:element name="td">
    <xsl:value-of select="@vendor" />
   </xsl:element>
  </xsl:element>
 </xsl:template>

 <xsl:template name="image">
  <xsl:param name="name" />
  <xsl:choose>
   <xsl:when test="$imageFormat = 'svg'">
    <xsl:element name="object">
     <xsl:attribute name="data"><xsl:value-of select="$imageLocation" /><xsl:text>/</xsl:text><xsl:value-of
      select="$name" /><xsl:text>.</xsl:text><xsl:value-of select="$imageFormat" /></xsl:attribute>
     <xsl:attribute name="type">image/svg+xml</xsl:attribute>
    </xsl:element>
   </xsl:when>
   <xsl:otherwise>
    <xsl:element name="img">
     <xsl:attribute name="src"><xsl:value-of select="$imageLocation" /><xsl:text>/</xsl:text><xsl:value-of
      select="$name" /><xsl:text>.</xsl:text><xsl:value-of select="$imageFormat" />
				</xsl:attribute>
    </xsl:element>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template match="development-component" />
</xsl:stylesheet>