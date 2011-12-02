<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

 <xsl:output method="html" indent="yes" encoding="UTF-8" />
 <xsl:strip-space elements="*" />
 <xsl:param name="imageLocation" />
 <xsl:param name="cssLocation" />
 <xsl:param name="imageFormat" />

 <xsl:template match="/">
  <xsl:element name="html">
   <xsl:element name="head">
    <xsl:element name="title">
     <xsl:text>Report für Development Configurations</xsl:text>
    </xsl:element>
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="description" content="Report für Development Component" />
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
    <xsl:apply-templates />
   </xsl:element>
  </xsl:element>
 </xsl:template>

 <xsl:template match="development-component">
  <xsl:element name="a">
   <xsl:attribute name="name">
    <xsl:value-of select="@name" />
   </xsl:attribute>
  </xsl:element>
  <xsl:element name="h4">
   Entwicklungskomponente
   <xsl:value-of select="@name" />
  </xsl:element>
  <p>
   <xsl:value-of select="description/text()" />
  </p>
  <p>
   <xsl:call-template name="image">
    <xsl:with-param name="name">
     <xsl:value-of select="concat(@vendor, '~', translate(@name,'/','~'))" />
    </xsl:with-param>
   </xsl:call-template>
  </p>
  <p>
   <xsl:element name="table">
    <xsl:element name="caption">
     benutzte Entwicklungskomponenten
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
       Public Part
      </xsl:element>
      <xsl:element name="th">
       Buildtime
      </xsl:element>
      <xsl:element name="th">
       Runtime
      </xsl:element>
      <xsl:element name="th">
       Softwarecomponent
      </xsl:element>
     </xsl:element>
    </xsl:element>
    <xsl:element name="tbody">
     <xsl:for-each select="dependencies/dependency">
      <xsl:sort select="@vendor" order="ascending" data-type="text" />
      <xsl:sort select="@name" order="ascending" data-type="text" />
      <xsl:sort select="@type" order="ascending" data-type="text" />
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
        <xsl:value-of select="@name" />
       </xsl:element>
       <xsl:element name="td">
        <xsl:value-of select="@type" />
       </xsl:element>
       <xsl:element name="td">
        <xsl:value-of select="@vendor" />
       </xsl:element>
       <xsl:element name="td">
        <xsl:value-of select="@pp-ref" />
       </xsl:element>
       <xsl:element name="td">
        <xsl:choose>
         <xsl:when test="boolean(at-build-time)">
          <xsl:text>x</xsl:text>
         </xsl:when>
         <xsl:otherwise>
          <xsl:text> </xsl:text>
         </xsl:otherwise>
        </xsl:choose>
       </xsl:element>
       <xsl:element name="td">
        <xsl:choose>
         <xsl:when test="boolean(at-run-time)">
          <xsl:text>x</xsl:text>
         </xsl:when>
         <xsl:otherwise>
          <xsl:text> </xsl:text>
         </xsl:otherwise>
        </xsl:choose>
       </xsl:element>
       <xsl:element name="td">
        <xsl:value-of select="@compartment" />
       </xsl:element>
      </xsl:element>
     </xsl:for-each>
    </xsl:element>
   </xsl:element>
  </p>
  <h3>abhängige Entwicklungskomponenten</h3>
  <p>
   <xsl:call-template name="image">
    <xsl:with-param name="name">
     <xsl:value-of select="concat(@vendor, '~', translate(@name,'/','~'), '-usingDCs')" />
    </xsl:with-param>
   </xsl:call-template>
  </p>
 </xsl:template>
 <xsl:template name="image">
  <xsl:param name="name" />
  <xsl:variable name="url" select="concat($imageLocation, '/', $name, '.', $imageFormat)" />
    <xsl:element name="img">
     <xsl:attribute name="src">
      <xsl:value-of select="concat($imageLocation, '/', $name, '.', $imageFormat)" />
	 </xsl:attribute>
    </xsl:element>
 </xsl:template>
</xsl:stylesheet>