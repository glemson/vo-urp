<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
<!ENTITY nbsp "&#160;">
<!ENTITY tab "&#160;&#160;&#160;&#160;">
]>
<!-- 
This style sheet creates a model import for a given VO-URP model.
This contains some metadata and for each contained type a corresponding ImportedType representation.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:vo-urp="http://vo-urp.googlecode.com/xsd/v0.9">
  
  <xsl:output method="xml" encoding="UTF-8" indent="yes" />
  <xsl:param name="url" />
  <xsl:param name="documentationURL" />

  <xsl:strip-space elements="*" />
  <xsl:template match="/">
    <xsl:apply-templates select="vo-urp:model"/>
  </xsl:template>
  
    <xsl:template match="vo-urp:model">

     <xsl:element name="import" >
       <xsl:element name="identifier">
         <xsl:element name="utype">
           <xsl:value-of select="identifier/utype"/>
         </xsl:element>
         <xsl:element name="name">
           <xsl:value-of select="name"/>
         </xsl:element>
         <xsl:element name="url">
           <xsl:value-of select="$url"/>
         </xsl:element>
         <xsl:element name="documentationURL">
           <xsl:value-of select="$documentationURL"/>
         </xsl:element>
       </xsl:element>
    <xsl:for-each select=".//objectType[not(ancestor::*/name() = 'import')]">
      <xsl:sort select="identifier/utype"/>
      <xsl:element name="objectType">
        <xsl:apply-templates select="."/>
      </xsl:element>
      </xsl:for-each>
    <xsl:for-each select=".//dataType[not(ancestor::*/name() = 'import')]">
      <xsl:sort select="identifier/utype"/>
      <xsl:element name="dataType">
        <xsl:apply-templates select="."/>
      </xsl:element>
      </xsl:for-each>
    <xsl:for-each select=".//enumeration[not(ancestor::*/name() = 'import')]">
      <xsl:sort select="identifier/utype"/>
      <xsl:element name="enumeration">
        <xsl:apply-templates select="."/>
      </xsl:element>
      </xsl:for-each>
    <xsl:for-each select=".//primitiveType[not(ancestor::*/name() = 'import')]">
      <xsl:sort select="identifier/utype"/>
      <xsl:element name="primitiveType">
        <xsl:apply-templates select="."/>
      </xsl:element>
      </xsl:for-each>
     </xsl:element> 
  </xsl:template>  
  
  <xsl:template match="objectType|dataType|enumeration|primitiveType">
    <xsl:element name="name"><xsl:value-of select="name"/></xsl:element>
      <xsl:element name="identifier">
        <xsl:element name="utype"><xsl:value-of select="identifier/utype"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>  
  
  
</xsl:stylesheet>
