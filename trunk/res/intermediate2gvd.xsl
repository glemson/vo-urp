<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This XSLT script transforms a data model from our
intermediate representation to a GraphViz dot file.
 -->

<!DOCTYPE stylesheet [
<!ENTITY cr  "<xsl:text>
</xsl:text>">
<!ENTITY bl  "<xsl:text> </xsl:text>">
<!ENTITY rem "<xsl:text>-- </xsl:text>">
]>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
								xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema">
  
  
  <xsl:output method="text" encoding="UTF-8" indent="no" />
  
  <xsl:strip-space elements="*" />
  
  <xsl:key name="element" match="*" use="@xmiid"/>


  <xsl:template match="/">
    <xsl:apply-templates select="model"/>
  </xsl:template>
  
  <xsl:template match="model">  
digraph <xsl:value-of select="name"/> {
	label = "\n\nSimDB data model";
	fontsize=20;	
	node [shape=box] 
  <xsl:apply-templates select="//objectType"/>
  <xsl:if test="//extends">
<!--    edge [color="red", arrowhead="empty", headport="s", tailport="n"] --> 
    edge [color="red", arrowtail="empty", arrowhead="none"]
    <xsl:apply-templates select="//objectType/extends"/>
  </xsl:if>
  <xsl:if test="//collection">
<!--    edge [color="blue", arrowhead="open", arrowtail="diamond", headport="n", tailport="s"] --> 
    edge [color="blue", arrowhead="open", arrowtail="diamond"]
    <xsl:apply-templates select="//objectType/collection"/>
  </xsl:if>
  <xsl:if test="//reference">
<!--    edge [color="green", arrowhead="open", headport="w", tailport="e"]   --> 
    graph[rankdir="LR"];
    edge [color="green", arrowhead="open", arrowtail="none"]
    <xsl:apply-templates select="//objectType/reference"/>
  </xsl:if>  
}
  </xsl:template>
  
  <xsl:template match="objectType">
    <xsl:value-of select="name"/> [URL="#<xsl:value-of select="@xmiid"/>"] ;
  </xsl:template>
  
  <xsl:template match="extends">
    <xsl:value-of select="@name"/> -> <xsl:value-of select="../name"/> ;
  </xsl:template>


  <xsl:template match="collection">
    <xsl:value-of select="../name"/> -> <xsl:value-of select="datatype/@name"/> ;
  </xsl:template>


  <xsl:template match="reference">
    <xsl:value-of select="../name"/> -> <xsl:value-of select="datatype/@name"/> ;
  </xsl:template>

</xsl:stylesheet>