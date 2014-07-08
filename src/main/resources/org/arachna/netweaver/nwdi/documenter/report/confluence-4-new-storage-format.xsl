<?xml version="1.0" encoding="utf-8"?>
<!-- *******************************************************************

     File: docbook2cfl.xsl
     Author: Baoqiu Cui <cbaoqiu AT yahoo DOT com>
   
     Copyright (C) 2011 Baoqiu Cui
   
     This is an XSL stylesheet that converts DocBook documents
     (http://www.docbook.org/) to Confluence Wiki format
     (http://www.atlassian.com/software/confluence/).
   
     docbook2cfl.xsl is free software: you can redistribute it and/or
     modify it under the terms of the GNU General Public License as
     published by the Free Software Foundation, either version 3 of the
     License, or (at your option) any later version.
     
     This stylesheet is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
     General Public License for more details.
     
     A copy of the GNU General Public License can be made from
     <http://www.gnu.org/licenses/>.
   
     Because DocBook contains more features than Confluence Wiki does, this
     stylesheet only (and will always) support a subset of DocBook
     elements.
     
     Currently this stylesheet works best for DocBook files exported
     from Emacs Org-mode files by the DocBook exporter in Org-mode
     (http://orgmode.org/).  Most formatting features supported by Org
     mode are supported.
   
     Some code and ideas are copied from DocBook2Wiki.xsl that is
     included in the contrib/ directory of DocBook 1.74.0.
   
     $Id: docbook2cfl.xsl,v 1.6 2011/04/25 03:34:16 bcui Exp $

     *******************************************************************
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:d = "http://docbook.org/ns/docbook"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                version="1.0">

<xsl:output method="text" encoding="utf-8" indent="yes"/>    
<xsl:strip-space elements="*"/>

<xsl:param name="wikiSpace" select="." ></xsl:param>
<xsl:param name="track" select="."></xsl:param>

<xsl:template match="/">
  <xsl:apply-templates />
</xsl:template>

<xsl:template match="d:caption" />

<xsl:template name="caption">
  <xsl:if test="d:caption">
    <xsl:text>*</xsl:text>
    <xsl:value-of select="d:caption/text()" />
    <xsl:text>*</xsl:text>
    <xsl:call-template name="newline"/>
    <xsl:call-template name="newline"/>
  </xsl:if>
  <xsl:if test="d:title">
    <xsl:text>*</xsl:text>
    <xsl:value-of select="d:title/text()" />
    <xsl:text>*</xsl:text>
    <xsl:call-template name="newline"/>
    <xsl:call-template name="newline"/>
  </xsl:if>
</xsl:template>

<xsl:template match="d:mediaobject">
  <xsl:apply-templates select="d:imageobject" />
  <xsl:call-template name="caption" />
</xsl:template>

<!-- imagedata -->

<xsl:template match="d:imagedata">
<xsl:choose>
    <xsl:when test="parent::d:imageobject[@condition='web']">
      <xsl:text>{svgweb4a:</xsl:text>
      <xsl:call-template name="image"/>
      <xsl:text>}</xsl:text>
      <xsl:call-template name="newline"/>
    </xsl:when>
    <xsl:otherwise>
    <xsl:if test="parent::d:imageobject[@condition!=print and (@role!='fo' or not(@role))]">
      <xsl:text>!</xsl:text>
      <xsl:call-template name="image"/>
      <xsl:text>!</xsl:text>
      <xsl:call-template name="newline"/>
    </xsl:if>
    </xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template name="image">
    <xsl:call-template name="getfilename">
      <xsl:with-param name="fileref">
        <xsl:value-of select="@fileref"/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:text>|</xsl:text>
    <xsl:choose>
      <xsl:when test="@align = 'left'">
        <xsl:text>align=left</xsl:text>
      </xsl:when>
      <xsl:when test="@align = 'right'">
        <xsl:text>align=right</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>align=center</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="@width">
        <xsl:text>|width=</xsl:text><xsl:value-of select="@width" />
    </xsl:if>
    <xsl:if test="@depth">
        <xsl:text>|height=</xsl:text><xsl:value-of select="@depth" />
    </xsl:if>
</xsl:template>
<!-- chapter title -->

<xsl:template match="d:title[parent::d:chapter]">
  <xsl:call-template name="newline"/>
  <xsl:text>h1. </xsl:text>
  <xsl:apply-templates/>
  <xsl:call-template name="newline"/>
  <xsl:call-template name="newline"/>
</xsl:template>

<!-- section title -->

<xsl:template match="d:title[parent::d:section]">
  <xsl:variable name="level" select="count(ancestor-or-self::*[d:section|d:chapter])"/>
  <xsl:choose>
    <xsl:when test="$level &lt; 7">
      <xsl:call-template name="newline"/>
      <xsl:text>h</xsl:text>
      <xsl:value-of select="$level"/>
      <xsl:text>. </xsl:text>
      <xsl:apply-templates/>
      <xsl:call-template name="newline"/>
      <xsl:call-template name="newline"/>
    </xsl:when>
    <!-- Treat section title at 7th level or below as bold text. -->
    <xsl:otherwise>
      <xsl:text>*</xsl:text>
      <xsl:call-template name="remove-trailing-ws">
        <xsl:with-param name="lines" select="."/>
      </xsl:call-template>
      <xsl:text>*</xsl:text>
      <xsl:call-template name="newline"/>
      <xsl:call-template name="newline"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- programlisting -->

<xsl:template match="d:programlisting/text()">
  <xsl:call-template name="indent"/>
  <xsl:text>{code}
</xsl:text>

  <xsl:variable name="before" select="count(preceding-sibling::*)"/>
  <xsl:variable name="after" select="count(following-sibling::*)"/>
  <xsl:variable name="proglist" select="."/>

  <xsl:variable name="proglist-1">
    <xsl:choose>
      <xsl:when test="$before = 0">
        <xsl:call-template name="remove-leading-newlines">
          <xsl:with-param name="lines" select="$proglist"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$proglist"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="proglist-2">
    <xsl:choose>
      <xsl:when test="$after = 0">
        <xsl:call-template name="remove-trailing-ws">
          <xsl:with-param name="lines" select="$proglist-1"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$proglist-1"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:value-of select="$proglist-2"/>
  <xsl:text>
{code}
</xsl:text>
</xsl:template>

<!-- book/title|subtitle|info -->

<xsl:template match="d:title|d:subtitle|d:info[parent::d:book]"/>

<!-- article/title|info -->

<xsl:template match="d:title|d:info[parent::d:article]"/>

<!--  index -->

<xsl:template match="d:indexterm"/>

<!-- link -->

<xsl:template match="d:link">
  <xsl:variable name="link-url" select="@xlink:href|@linkend"/>
  <xsl:variable name="desc" select="."/>
  <xsl:text>[</xsl:text>
  <xsl:apply-templates/> <!-- The desc -->
  <xsl:text>|</xsl:text>
  <xsl:choose>
    <xsl:when test="@role = 'wiki'">
      <xsl:value-of select="concat($wikiSpace, ':', $track, '_', $link-url)"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$link-url"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>]</xsl:text>
</xsl:template>

<!-- itemizedlist -->

<xsl:template match="d:itemizedlist">
  <xsl:variable name="listlevel" select="count(ancestor::d:listitem)"/>
  <xsl:apply-templates/>
  <xsl:if test="$listlevel = 0">
    <xsl:call-template name="newline"/>
  </xsl:if>
</xsl:template>
   
<!-- orderedlist -->

<xsl:template match="d:orderedlist">
  <xsl:variable name="listlevel" select="count(ancestor::d:listitem)"/>
  <xsl:apply-templates/>
  <xsl:if test="$listlevel = 0">
    <xsl:call-template name="newline"/>
  </xsl:if>
</xsl:template>

<!-- listitem -->

<xsl:template match="d:listitem">
  <xsl:variable name="listlevel" select="count(ancestor-or-self::d:listitem)"/>
  <!-- XXX
  <xsl:if test="not(parent::d:varlistentry)">
    <xsl:value-of select="substring('                              ', 1, $listlevel * 3)"/>
  </xsl:if>
  -->
  <xsl:choose>
    <xsl:when test="parent::d:itemizedlist">
      <xsl:value-of select="substring('********', 1, $listlevel)"/>
      <xsl:text> </xsl:text>
    </xsl:when>
    <xsl:when test="parent::d:orderedlist">
      <xsl:value-of select="substring('########', 1, $listlevel)"/>
      <xsl:text> </xsl:text>
    </xsl:when>
    <!-- XXX
    <xsl:when test="parent::d:varlistentry">
      <xsl:text>: </xsl:text>
    </xsl:when>
    -->
  </xsl:choose>
  <xsl:apply-templates/>
</xsl:template>

<!-- warning -->

<xsl:template match="d:warning">
  <xsl:call-template name="indent"/>
  <xsl:text>%X% *Warning*: </xsl:text>
  <xsl:apply-templates/>    
  <xsl:call-template name="newline"/>
  <xsl:call-template name="newline"/>
</xsl:template>
     
<!-- tip -->

<xsl:template match="d:tip">
  <xsl:call-template name="indent"/>
  <xsl:text>%T% *Tip*: </xsl:text>
  <xsl:apply-templates/>    
  <xsl:call-template name="newline"/>
  <xsl:call-template name="newline"/>
</xsl:template>
     
<!-- caution -->

<xsl:template match="d:caution">
  <xsl:call-template name="indent"/>
  <xsl:text>%X% *Caution*: </xsl:text>
  <xsl:apply-templates/>    
  <xsl:call-template name="newline"/>
  <xsl:call-template name="newline"/>
</xsl:template>

<!-- note -->

<xsl:template match="d:note">
  <xsl:call-template name="indent"/>
  <xsl:text>*Note*: </xsl:text>
  <xsl:apply-templates/>    
  <xsl:call-template name="newline"/>
  <xsl:call-template name="newline"/>
</xsl:template>

<!-- varlistentry/term -->

<xsl:template match="d:varlistentry/d:term">
  <xsl:variable name="listlevel" select="count(ancestor-or-self::d:listitem)+1"/>
  <xsl:value-of select="substring('                              ', 1, $listlevel * 3)"/>
  <xsl:text>$ </xsl:text>
  <xsl:apply-templates/>  
</xsl:template>

<!-- emphasis -->

<xsl:template match="d:emphasis">
  <!-- Make sure there is a space before the emphasis markup when it is
       needed. -->
  <xsl:if test="count(preceding-sibling::*) &gt; 0 and
                not(preceding-sibling::node()[1][self::text()])">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="@role = 'underline'">
      <xsl:text>+</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>+</xsl:text>
    </xsl:when>
    <xsl:when test="@role = 'bold'">
      <xsl:text>*</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>*</xsl:text>
    </xsl:when>
    <xsl:when test="@role = 'strikethrough'">
      <xsl:text>-</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>-</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>_</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>_</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- footnote -->

<xsl:template match="d:footnote">
  <xsl:text>{{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}}</xsl:text>
</xsl:template>

<!-- footnoteref -->

<xsl:template match="d:footnoteref">
  <xsl:variable name="footnote-id" select="@linkend"/>
  <xsl:apply-templates select="//d:footnote[@xml:id = $footnote-id]"/>
</xsl:template>

<!-- literal -->

<xsl:template match="d:literal">
  <xsl:text>{{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}}</xsl:text>
</xsl:template>

<!-- code -->

<xsl:template match="d:code">
  <!-- Make sure there is a space before the `=' markup when it is
       needed. -->
  <xsl:if test="count(preceding-sibling::*) &gt; 0 and
                not(preceding-sibling::node()[1][self::text()])">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:text>{{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}}</xsl:text>
</xsl:template>

<!-- superscript -->

<xsl:template match="d:superscript">
  <xsl:text> ^</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>^ </xsl:text>
</xsl:template>

<!-- subscript -->

<xsl:template match="d:subscript">
  <xsl:text> ~</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>~ </xsl:text>
</xsl:template>

<!-- variablelist -->

<xsl:template match="d:variablelist">
  <xsl:apply-templates/>    
</xsl:template>

<!-- para -->

<xsl:template match="d:para">
  <xsl:call-template name="indent"/>
  <xsl:apply-templates/>    
  <!-- Add a newline for a para in listitem or section.  This newline is
       always required in these two situations (and we have special
       handling for listitem too). -->
  <xsl:choose>
    <xsl:when test="parent::d:listitem or
                    parent::d:section or
                    parent::d:chapter">
      <xsl:call-template name="newline"/>
    </xsl:when>
  </xsl:choose>
  <!-- Add a second newline if this para is in a section but itself is
       NOT the last para in that section. -->
  <xsl:choose>
    <xsl:when test="(parent::d:section or parent::d:chapter) and
                    following-sibling::*">
      <xsl:call-template name="newline"/>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="d:para" mode="centering">
  <xsl:apply-templates/>
  <xsl:call-template name="newline"/>
  <xsl:call-template name="newline"/>
</xsl:template>

<!-- getfilename -->

<xsl:template name="getfilename">
  <xsl:param name="fileref"/>
  <xsl:choose>  
    <xsl:when test="contains($fileref,'\')">
      <xsl:call-template name="getfilename">
        <xsl:with-param name="fileref">
          <xsl:value-of select="substring-after($fileref,'\')"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="contains($fileref,'/')">
      <xsl:call-template name="getfilename">
        <xsl:with-param name="fileref">
          <xsl:value-of select="substring-after($fileref,'/')"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$fileref"/>
    </xsl:otherwise>
  </xsl:choose>  
</xsl:template>


<!-- HTML table -->
<!-- Here, only <tr> needs to worry about indentation. -->

<xsl:template match="d:table/d:caption"/>

<xsl:template match="d:thead/d:tr">
  <xsl:call-template name="indent"/>
  <xsl:text>|| </xsl:text>
  <xsl:apply-templates/>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template match="d:tbody/d:tr">
  <xsl:call-template name="indent"/>
  <xsl:text>| </xsl:text>
  <xsl:apply-templates/>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template match="d:th">
  <xsl:apply-templates/>
  <xsl:text> || </xsl:text>
</xsl:template>

<xsl:template match="d:td">
  <xsl:apply-templates/>
  <xsl:text> | </xsl:text>
</xsl:template>

<!-- XXX: CLAS table -->

<xsl:template match="d:table">
  <xsl:apply-templates />
  <xsl:call-template name="caption" />
</xsl:template>

<xsl:template match="d:tgroup">
  <xsl:choose>
    <!-- Special informaltable for text centering -->
    <xsl:when test="count(./*) = 1 and
                    @align='center' and
                    @cols='1'">
      <xsl:text>&lt;div style="text-align: center"&gt;
</xsl:text>
      <xsl:apply-templates select="d:tbody/d:row/d:entry" mode="centering"/>
      <xsl:text>&lt;/div&gt;
</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="d:thead"/>
      <xsl:apply-templates select="d:tbody"/>
      <xsl:apply-templates select="d:tfoot"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="d:thead/d:row|d:tbody/d:row|d:tfoot/d:row">
  <xsl:call-template name="indent"/>
  <xsl:text>| </xsl:text>
  <xsl:apply-templates/>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template match="d:thead/d:row/d:entry|d:tfoot/d:row/d:entry">
  <xsl:text>*</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>* | </xsl:text>
</xsl:template>

<xsl:template match="d:tbody/d:row/d:entry">
  <xsl:apply-templates/>
  <xsl:text> | </xsl:text>
</xsl:template>

<xsl:template match="d:tbody/d:row/d:entry" mode="centering">
  <xsl:apply-templates mode="centering"/>
</xsl:template>

<xsl:template match="text()|@*">
  <xsl:variable name="oneline">
    <xsl:call-template name="entity-replace">
      <xsl:with-param name="input-str"
                      select="translate(., '&#xA;&#xD;', '  ')"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:choose>
    <!-- Remove leading spaces in a para -->
    <xsl:when test="parent::d:para and count(preceding-sibling::*) = 0">
      <xsl:call-template name="remove-leading-spaces">
        <xsl:with-param name="text" select="$oneline"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$oneline"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="remove-leading-spaces">
  <xsl:param name="text"/>
  <xsl:if test="starts-with($text, ' ')">
    <xsl:call-template name="remove-leading-spaces">
      <xsl:with-param name="text">
        <xsl:value-of select="substring($text, 2)"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:if>
  <xsl:if test="not(starts-with($text, ' '))">
    <xsl:value-of select="$text"/>
  </xsl:if>
</xsl:template>

<!-- XXX: Indent is not needed for Confluence wiki ?? -->
<xsl:template name="indent">
  <xsl:variable name="listlevel" select="count(ancestor-or-self::d:listitem)"/>
  <xsl:if test="(self::d:para and position() != 1) or
                parent::d:programlisting or
                self::d:programlisting or
                self::d:tr">
    <xsl:if test="self::d:para and
                  $listlevel &gt; 0 and
                  count(preceding-sibling::*) &gt; 0 and
                  not(preceding-sibling::node()[1][self::d:programlisting])">
      <xsl:text>\\
</xsl:text>
    </xsl:if>
  </xsl:if>
</xsl:template>

<!-- Insert a newline -->

<xsl:template name="newline">
  <xsl:text>
</xsl:text>
</xsl:template>

<!-- Replace characters `&', `<', and `>' with `&amp;', `&lt;' and
     `&gt;' respectively. -->

<xsl:template name="entity-replace">
  <xsl:param name="input-str"/>
  <xsl:call-template name="string-replace">
    <xsl:with-param name="to" select="'&amp;gt;'"/>
    <xsl:with-param name="from" select="'&gt;'"/>
    <xsl:with-param name="string">
      <xsl:call-template name="string-replace">
        <xsl:with-param name="to" select="'&amp;lt;'"/>
        <xsl:with-param name="from" select="'&lt;'"/>
        <xsl:with-param name="string">
          <xsl:call-template name="string-replace">
            <xsl:with-param name="to" select="'&amp;amp;'"/>
            <xsl:with-param name="from" select="'&amp;'"/>
            <xsl:with-param name="string" select="$input-str"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- Replace all occurences of the character(s) `from' in the string
     `string' with the string `to'. -->

<xsl:template name="string-replace">
  <xsl:param name="string"/>
  <xsl:param name="from"/>
  <xsl:param name="to"/>
  <xsl:choose>
    <xsl:when test="contains($string,$from)">
      <xsl:value-of select="substring-before($string,$from)"/>
      <xsl:value-of select="$to"/>
      <xsl:call-template name="string-replace">
        <xsl:with-param name="string" select="substring-after($string,$from)"/>
        <xsl:with-param name="from" select="$from"/>
        <xsl:with-param name="to" select="$to"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$string"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Remove leading newlines -->

<xsl:template name="remove-leading-newlines">
  <xsl:param name="lines"/>
  <xsl:choose>
    <xsl:when test="starts-with($lines,'&#xA;') or
                    starts-with($lines,'&#xD;')">
      <xsl:call-template name="remove-leading-newlines">
        <xsl:with-param name="lines" select="substring($lines, 2)"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$lines"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Remove trailing whitespaces -->

<xsl:template name="remove-trailing-ws">
  <xsl:param name="lines"/>
  <xsl:variable name="last-char">
    <xsl:value-of select="substring($lines, string-length($lines), 1)"/>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="($last-char = '&#xA;') or
                    ($last-char = '&#xD;') or
                    ($last-char = '&#x20;') or
                    ($last-char = '&#x9;')">
      <xsl:call-template name="remove-trailing-ws">
        <xsl:with-param name="lines"
                        select="substring($lines, 1,
                                string-length($lines) - 1)"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$lines"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
