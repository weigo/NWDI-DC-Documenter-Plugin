<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:d="http://docbook.org/ns/docbook"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:ac="http://www.atlassian.com/schema/confluence/4/ac/" xmlns:ri="http://www.atlassian.com/schema/confluence/4/ri/"
  xmlns="http://www.atlassian.com/schema/confluence/4/">

  <xsl:output method="xml" encoding="utf-8" indent="yes" doctype-public="ac:confluence" omit-xml-declaration="yes" />

  <xsl:param name="wikiSpace" select="."></xsl:param>
  <xsl:param name="track" select="."></xsl:param>

  <xsl:template match="/">
    <xsl:element name="ac:confluence">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <xsl:template match="d:caption">
    <xsl:element name="caption">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <!-- imagedata -->
  <xsl:template match="d:imagedata">
    <xsl:choose>
      <xsl:when test="parent::d:imageobject[@condition='web']">
        <xsl:call-template name="image" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="parent::d:imageobject[@condition!=print and (@role!='fo' or not(@role))]">
          <xsl:call-template name="image" />
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="image">
    <xsl:element name="ac:image">
      <xsl:element name="ri:attachment">
        <xsl:attribute name="ri:filename">
            <xsl:call-template name="getfilename">
              <xsl:with-param name="fileref">
                <xsl:value-of select="@fileref" />
              </xsl:with-param>
            </xsl:call-template>
        </xsl:attribute>
        <xsl:choose>
          <xsl:when test="@align">
            <xsl:attribute name="ac:align">
          <xsl:value-of select="@align" />
          </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="ac:align">
          <xsl:text>center</xsl:text>
          </xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="@width">
          <xsl:attribute name="ac:width">
          <xsl:value-of select="@width" />
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="@depth">
          <xsl:attribute name="ac:heihgt">
          <xsl:value-of select="@height" />
          </xsl:attribute>
        </xsl:if>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <!-- chapter title -->
  <xsl:template match="d:title[parent::d:chapter]">
    <xsl:element name="h1">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <!-- section title -->
  <xsl:template match="d:title[parent::d:section]">
    <xsl:variable name="level" select="count(ancestor-or-self::*[d:section|d:chapter])" />
    <xsl:choose>
      <xsl:when test="$level &lt; 7">
        <xsl:variable name="heading">
          <xsl:text>h</xsl:text>
          <xsl:value-of select="$level" />
        </xsl:variable>
        <xsl:element name="{$heading}">
          <xsl:apply-templates />
        </xsl:element>
      </xsl:when>
      <!-- Treat section title at 7th level or below as bold text. -->
      <xsl:otherwise>
        <xsl:element name="strong">
          <xsl:apply-templates />
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- programlisting -->
  <xsl:template match="d:programlisting/text()">
    <xsl:element name="ac:structured-macro">
      <xsl:attribute name="ac:name"><xsl:text>code</xsl:text></xsl:attribute>
      <xsl:element name="ac:plain-text-body">
        <xsl:text disable-output-escaping="yes">&#60;![CDATA[</xsl:text>
        <xsl:apply-templates />
        <xsl:text disable-output-escaping="yes">]]&#62;</xsl:text>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <!-- book/title|subtitle|info -->

  <xsl:template match="d:subtitle|d:info[parent::d:book]|d:info[parent::d:article]" />

  <!-- index -->

  <xsl:template match="d:indexterm" />

  <!-- link -->

  <xsl:template match="d:link">
    <xsl:variable name="link-url" select="@xlink:href|@linkend" />
    <xsl:choose>
      <xsl:when test="@role = 'wiki'">
        <xsl:element name="ac:link">
          <xsl:element name="ri:page">
            <xsl:attribute name="ri:content-title"><xsl:value-of select="concat($track, '_', $link-url)" /></xsl:attribute>
          </xsl:element>
          <ac:plain-text-link-body>
            <xsl:text disable-output-escaping="yes">&#60;![CDATA[</xsl:text>
            <xsl:apply-templates />
            <xsl:text disable-output-escaping="yes">]]&#62;</xsl:text>
          </ac:plain-text-link-body>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="a">
          <xsl:attribute name="href"><xsl:value-of select="$link-url" /></xsl:attribute>
          <xsl:apply-templates />
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- itemizedlist -->

  <xsl:template match="d:itemizedlist">
    <xsl:element name="ul">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <!-- orderedlist -->

  <xsl:template match="d:orderedlist">
    <xsl:element name="ol">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <!-- listitem -->

  <xsl:template match="d:listitem">
    <xsl:element name="li">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <!-- varlistentry/term -->

  <xsl:template match="d:varlistentry/d:term">
    <xsl:apply-templates />
  </xsl:template>

  <!-- emphasis -->

  <xsl:template match="d:emphasis">
    <xsl:choose>
      <xsl:when test="@role = 'underline'">
        <xsl:element name="u">
          <xsl:apply-templates />
        </xsl:element>
      </xsl:when>
      <xsl:when test="@role = 'bold'">
        <xsl:element name="strong">
          <xsl:apply-templates />
        </xsl:element>
      </xsl:when>
      <xsl:when test="@role = 'strikethrough'">
        <xsl:element name="span">
          <xsl:attribute name="style"><xsl:text>text-decoration: line-through;</xsl:text>
        </xsl:attribute>
          <xsl:apply-templates />
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="em">
          <xsl:apply-templates />
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- variablelist -->

  <xsl:template match="d:variablelist">
    <xsl:apply-templates />
  </xsl:template>

  <!-- para -->

  <xsl:template match="d:para">
    <xsl:element name="p">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <xsl:template match="d:para" mode="centering">
    <xsl:element name="p">
      <xsl:attribute name="style"><xsl:text>text-align: center;</xsl:text></xsl:attribute>
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <!-- getfilename -->

  <xsl:template name="getfilename">
    <xsl:param name="fileref" />
    <xsl:choose>
      <xsl:when test="contains($fileref,'\')">
        <xsl:call-template name="getfilename">
          <xsl:with-param name="fileref">
            <xsl:value-of select="substring-after($fileref,'\')" />
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="contains($fileref,'/')">
        <xsl:call-template name="getfilename">
          <xsl:with-param name="fileref">
            <xsl:value-of select="substring-after($fileref,'/')" />
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$fileref" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- HTML table -->
  <!-- Here, only <tr> needs to worry about indentation. -->

  <xsl:template match="d:tbody/d:tr|d:thead/d:tr">
    <xsl:element name="tr">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <xsl:template match="d:th">
    <xsl:element name="th">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <xsl:template match="d:td">
    <xsl:element name="td">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <xsl:template match="d:title[parent::d:table]">
    <xsl:apply-templates />
  </xsl:template>

  <!-- XXX: CALS table -->

  <xsl:template match="d:table">
    <xsl:if test="./d:title">
      <h4>
        <xsl:apply-templates select="./d:title" />
      </h4>
    </xsl:if>
    <xsl:element name="table">
      <xsl:apply-templates select="d:tgroup" />
    </xsl:element>
  </xsl:template>

  <xsl:template match="d:colspec">
    <!-- xsl:element name="col" -->
    <!-- xsl:attribute name="width"><xsl:value-of select="@colwidth" /></xsl:attribute -->
    <!-- /xsl:element -->
  </xsl:template>

  <xsl:template match="d:tgroup">
    <xsl:choose>
      <!-- Special informaltable for text centering -->
      <xsl:when test="count(./*) = 1 and
                    @align='center' and
                    @cols='1'">
        <xsl:text>&lt;div style="text-align: center"&gt;
</xsl:text>
        <xsl:apply-templates select="d:tbody/d:row/d:entry" mode="centering" />
        <xsl:text>&lt;/div&gt;
</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <!-- xsl:if test="@cols &gt; 0"> <xsl:element name="colgroup"> <xsl:apply-templates select="d:colspec" /> </xsl:element> </xsl:if -->
        <xsl:apply-templates select="d:thead" />
        <xsl:apply-templates select="d:tbody" />
        <xsl:apply-templates select="d:tfoot" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="d:thead/d:row|d:tbody/d:row|d:tfoot/d:row">
    <xsl:element name="tr">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <xsl:template match="d:thead/d:row/d:entry|d:tfoot/d:row/d:entry">
    <xsl:element name="th">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <xsl:template match="d:tbody/d:row/d:entry">
    <xsl:element name="td">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <xsl:template match="d:tbody/d:row/d:entry" mode="centering">
    <xsl:apply-templates mode="centering" />
  </xsl:template>

  <!-- Replace characters `&', `<', and `>' with `&amp;', `&lt;' and `&gt;' respectively. -->

  <xsl:template name="entity-replace">
    <xsl:param name="input-str" />
    <xsl:call-template name="string-replace">
      <xsl:with-param name="to" select="'&amp;gt;'" />
      <xsl:with-param name="from" select="'&gt;'" />
      <xsl:with-param name="string">
        <xsl:call-template name="string-replace">
          <xsl:with-param name="to" select="'&amp;lt;'" />
          <xsl:with-param name="from" select="'&lt;'" />
          <xsl:with-param name="string">
            <xsl:call-template name="string-replace">
              <xsl:with-param name="to" select="'&amp;amp;'" />
              <xsl:with-param name="from" select="'&amp;'" />
              <xsl:with-param name="string" select="$input-str" />
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- Replace all occurences of the character(s) `from' in the string `string' with the string `to'. -->

  <xsl:template name="string-replace">
    <xsl:param name="string" />
    <xsl:param name="from" />
    <xsl:param name="to" />
    <xsl:choose>
      <xsl:when test="contains($string,$from)">
        <xsl:value-of select="substring-before($string,$from)" />
        <xsl:value-of select="$to" />
        <xsl:call-template name="string-replace">
          <xsl:with-param name="string" select="substring-after($string,$from)" />
          <xsl:with-param name="from" select="$from" />
          <xsl:with-param name="to" select="$to" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$string" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
