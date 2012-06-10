<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="html" indent="yes" encoding="UTF-8" />
  <xsl:strip-space elements="*" />
  <xsl:param name="imageLocation" />
  <xsl:param name="cssLocation" />
  <xsl:param name="jsLocation" />
  <xsl:template match="development-configurations">
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
          <xsl:attribute name="href"><xsl:value-of select="concat($cssLocation, '/report.css')" />
					</xsl:attribute>
        </xsl:element>
        <xsl:element name="script">
          <xsl:attribute name="type">text/javascript</xsl:attribute>
          <xsl:attribute name="src"><xsl:value-of select="concat($jsLocation, '/xpath.js')" /></xsl:attribute>
        </xsl:element>
        <xsl:element name="script">
          <xsl:attribute name="type">text/javascript</xsl:attribute>
          <xsl:attribute name="src"><xsl:value-of select="concat($jsLocation, '/search.js')" /></xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="body">
        <div>
          <span>
            Suchen:&#160;
            <input type="text" id="search" size="30" onkeyup="search();" onsubmit="search();" />
          </span>
        </div>
        <xsl:apply-templates />
      </xsl:element>
    </xsl:element>
  </xsl:template>
  <xsl:template match="development-configuration">
    <div>
      <div class="developmentConfiguration">
        <xsl:value-of select="@name" />
        <hr />
        <xsl:value-of select="@description" />
      </div>
      <xsl:apply-templates select="compartment" />
    </div>
  </xsl:template>
  <xsl:template match="compartment[count(development-components/development-component) &gt; 0]">
    <div class="compartentContainer">
      <div class="compartment">
        <xsl:choose>
          <xsl:when test="@archive-state='no'">
            <xsl:element name="a">
              <xsl:attribute name="href"><xsl:value-of select="concat(@sc-name, '/index.html')" /></xsl:attribute>
              <xsl:value-of select="@sc-name" />
            </xsl:element>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@sc-name" />
          </xsl:otherwise>
        </xsl:choose>
        <hr />
        <xsl:value-of select="@vendor" />
      </div>
      <xsl:apply-templates />
    </div>
  </xsl:template>
  <xsl:template match="development-component">
    <div class="developmentComponent">
      <xsl:attribute name="id"><xsl:value-of select="@name" /></xsl:attribute>
      <xsl:variable name="dcName">
        <xsl:value-of select="@name" />
        <xsl:text> (</xsl:text>
        <xsl:value-of select="@vendor" />
        <xsl:text>)</xsl:text>
      </xsl:variable>
      <xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="concat(../../@sc-name, '/', @vendor, '~', translate(@name, '/', '~'), '.html')" /></xsl:attribute>
        <xsl:value-of select="$dcName" />
      </xsl:element>
      <hr />
      <p>
        <xsl:value-of select="caption/text()" />
      </p>
      <p>
        <xsl:value-of select="description/text()" />
      </p>
      <xsl:if test="count(sourceFolders/package-folder) &gt; 0 and ../../@archive-state='no'">
        <hr />
        <xsl:variable name="componentName">
          <xsl:value-of select="concat(@vendor, '~', translate(@name, '/', '~'))" />
        </xsl:variable>
        <xsl:element name="a">
          <xsl:attribute name="href"><xsl:value-of select="concat('../javadoc/', $componentName, '/index.html')" /></xsl:attribute>
          <xsl:attribute name="target">_blank</xsl:attribute>
          <xsl:element name="img">
            <xsl:attribute name="border">0</xsl:attribute>
            <xsl:attribute name="src"><xsl:value-of select="concat($imageLocation,'/info16_1.gif')" /></xsl:attribute>
            <xsl:attribute name="alt">JavaDoc ansehen</xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:if>
    </div>
  </xsl:template>
</xsl:stylesheet>