<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  
  <xsl:import href="common.xsl"/>

  <xsl:param name="targetnamespace_root"/>


  <!-- return the targetnamespace for the schema document for the package with the given id -->
  <xsl:template name="namespace-for-package">
    <xsl:param name="packageid"/>
    <xsl:variable name="path">
      <xsl:call-template name="package-path">
        <xsl:with-param name="packageid" select="$packageid"/>
        <xsl:with-param name="delimiter" select="'/'"/>
      </xsl:call-template>
    </xsl:variable>    
    <xsl:value-of select="concat($targetnamespace_root,'/',$path)"/>
  </xsl:template>
  


  <!-- calculate a prefix for the package with the given id -->
  <xsl:template name="package-prefix">
    <xsl:param name="packageid"/>
    <xsl:variable name="rank">
      <xsl:value-of select="count(/*//package[@xmiid &lt; $packageid])+1"/>
    </xsl:variable>
    <xsl:value-of select="concat('p',$rank)"/>
  </xsl:template>



  <!-- calculate a prefix for the given object -->
  <xsl:template match="objectType" mode="package-prefix">
    <xsl:call-template name="package-prefix">
      <xsl:with-param name="packageid" select="./ancestor::package[1]/@xmiid"/>
    </xsl:call-template>
  </xsl:template>


  
  <!-- Does not really properly count number of contained types in hierarchy, 
  but at least will provide 0 if there are none. -->
  <xsl:template match="objectType" mode="testrootelements">
    <xsl:param name="count" select="0"/>
    <xsl:variable name="xmiid" select="@xmiid"/>
    <xsl:choose>
      <xsl:when test="container">
        <xsl:value-of select="number($count)+1"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="childcount" >
          <xsl:choose>
            <xsl:when test="/model//objectType[extends/@xmiidref = $xmiid]">
              <xsl:apply-templates select="/model//objectType[extends/@xmiidref = $xmiid]" mode="testrootelements">
                <xsl:with-param name="count" select="$count"/>
              </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="0"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="number($count)+number($childcount)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
<!--  Do we potentially want to generate root elements even for dadaType-s?
See similar comment in jaxb.xsl:  <xsl:template match="objectType|dataType" mode="JAXBAnnotation">
 -->
  <xsl:template match="objectType|dataType" mode="root-element-name">
      <xsl:variable name="firstletterisvowel" select="translate(substring(name,1,1),'AEIOU','11111')"/>
      <xsl:variable name="article">
        <xsl:choose>
          <xsl:when test="$firstletterisvowel = '1'" >
            <xsl:text>an</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>a</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:value-of select="concat($article,name)"/>
  </xsl:template>
  
  
  
  
</xsl:stylesheet>