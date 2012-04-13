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
  
  <xsl:import href="common.xsl"/>
  
  <xsl:output method="text" encoding="UTF-8" indent="no" />
  
  <xsl:strip-space elements="*" />
  
  <xsl:key name="element" match="*" use="@xmiid"/>

  <xsl:param name="project.name"/>
  <xsl:param name="usesubgraph" select="'F'"/>
  
  <xsl:variable name="packages" select="//package/@xmiid"/>

  <xsl:template match="/">
    <xsl:apply-templates select="model"/>
  </xsl:template>
  
  <xsl:template match="model">  
digraph GVmap {  <!-- name must not be too long. the cmap that is generated uses this name and that must not be too long to work apparently -->
	label = "\n\n<xsl:value-of select="name"/> data model"
	rankdir=TB
	node [ 
	  shape=tab
	  style=filled
	]
	subgraph cluster_packages {
	  label="Packages"
	  rankdir=LR
	  <xsl:apply-templates select="package" />
	  <xsl:if test="//package[depends]">
	      edge [color="black", arrowhead="open", arrowtail="none", style="dashed"]
	    <xsl:apply-templates select="//package[depends]" mode="depends"/>
	  </xsl:if>
	}
	
	
	
	node [
	shape=record
	fontsize=8
	style=filled] 
  <xsl:apply-templates select="package" mode="types"/>
<!--   <xsl:apply-templates select="//objectType"/>  -->
  <xsl:if test="//extends">
<!--    edge [color="red", arrowhead="empty", headport="s", tailport="n"] --> 
    edge [color="red", arrowtail="none", arrowhead="empty"]
    <xsl:apply-templates select="//objectType/extends"/>
  </xsl:if>
  <xsl:if test="//collection">
<!--    edge [color="blue", arrowhead="open", arrowtail="diamond", headport="n", tailport="s"] --> 
    edge [color="blue", arrowhead="open", arrowtail="diamond",dir="both"]
    <xsl:apply-templates select="//objectType/collection"/>
  </xsl:if>
  <xsl:if test="//reference">
<!--    edge [color="green", arrowhead="open", headport="w", tailport="e"]   --> 
    edge [color="green", arrowhead="open", arrowtail="none"]
    <xsl:apply-templates select="//objectType/reference"/>
  </xsl:if>  
}
  </xsl:template>
  
  

  <xsl:template match="package">
    <xsl:variable name="color">
      <xsl:value-of select="concat('/set312/',index-of($packages,@xmiid))"/> 
    </xsl:variable>
    <xsl:value-of select="@xmiid"/> [
    URL="#<xsl:value-of select="@xmiid"/>"
    label = "<xsl:value-of select="name"/>"
    fillcolor="<xsl:value-of select="$color"/>"
    ] ;
    <xsl:if test="package">
    subgraph cluster_<xsl:value-of select="@xmiid"/> {
      label="<xsl:value-of select="name"/>"
      style=filled
      fillcolor="<xsl:value-of select="$color"/>"
      <xsl:apply-templates select="package"/>
    }
    </xsl:if>
  </xsl:template>



  <xsl:template match="package" mode="depends">
    <xsl:for-each select="depends">
      <xsl:value-of select="../@xmiid"/> -> <xsl:value-of select="@xmiidref"/>&cr;
    </xsl:for-each>
  </xsl:template>




  
  <!-- If the name starts with "cluster" than the nodes inside the package will be  -->
  <xsl:template match="package" mode="types">
    <xsl:choose>
    <xsl:when test="$usesubgraph = 'T'">
    subgraph cluster_<xsl:value-of select="name"/> {
      label="<xsl:value-of select="name"/>"
      <xsl:apply-templates select="objectType|dataType|enumeration"/>
    }
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="objectType|dataType|enumeration"/>
    </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="package" mode="types"/>
  </xsl:template>
  
  
  
  <xsl:template match="objectType">
    <xsl:variable name="color">
      <xsl:value-of select="concat('/set312/',index-of($packages,../@xmiid))"/> 
    </xsl:variable>
    <xsl:variable name="nodename">
        <xsl:apply-templates select="." mode="nodename"/>
    </xsl:variable>
    <xsl:variable name="label">
        <xsl:apply-templates select="." mode="nodelabel"/>
    </xsl:variable>
	<xsl:value-of select="$nodename"/> [
    URL="#<xsl:value-of select="@xmiid"/>"
    label = "{<xsl:value-of select="$label"/><xsl:if test="attribute">|<xsl:apply-templates select="attribute"/></xsl:if>}"
    fillcolor="<xsl:value-of select="$color"/>"
    ] ;
  </xsl:template>



  <xsl:template match="dataType">
    <xsl:variable name="color">
      <xsl:value-of select="concat('/set312/',index-of($packages,../@xmiid))"/> 
    </xsl:variable>
    <xsl:value-of select="name"/> [
    URL="#<xsl:value-of select="@xmiid"/>"
    label = "{&amp;lt;&amp;lt;datatype&amp;gt;&amp;gt;\l<xsl:value-of select="name"/><xsl:if test="attribute">|<xsl:apply-templates select="attribute"/></xsl:if>}"
    fillcolor="<xsl:value-of select="$color"/>"
    ] ;
  </xsl:template>



  <xsl:template match="enumeration">
    <xsl:variable name="color">
      <xsl:value-of select="concat('/set312/',index-of($packages,../@xmiid))"/> 
    </xsl:variable>
    <xsl:value-of select="name"/> [
    URL="#<xsl:value-of select="@xmiid"/>"
    label = "{&amp;lt;&amp;lt;enumeration&amp;gt;&amp;gt;\l<xsl:value-of select="name"/><xsl:if test="literal">|<xsl:apply-templates select="literal"/></xsl:if>}"
    fillcolor="<xsl:value-of select="$color"/>"
    ] ;
  </xsl:template>



<!--  NOTE keep starting an ending <xsl:text> elements -->
  <xsl:template match="attribute">
  <xsl:text>+</xsl:text> <xsl:value-of select="name"/> : <xsl:value-of select="datatype/@name"/><xsl:text>\l</xsl:text>
  </xsl:template>




  <xsl:template match="literal">
  <xsl:text>+</xsl:text> <xsl:value-of select="value"/><xsl:text>\l</xsl:text>
  </xsl:template>



  <xsl:template match="extends">
    <xsl:variable name="fromnode">
        <xsl:apply-templates select=".." mode="nodename">
    </xsl:apply-templates>
    </xsl:variable>
    <xsl:variable name="tonode">
        <xsl:apply-templates select="key('element', @xmiidref)" mode="nodename">
    </xsl:apply-templates>
    </xsl:variable>
    <xsl:value-of select="$fromnode"/> -> <xsl:value-of select="$tonode"/> ;
  </xsl:template>


  <xsl:template match="collection|reference">
    <xsl:variable name="fromnode">
        <xsl:apply-templates select=".." mode="nodename">
    </xsl:apply-templates>
    </xsl:variable>
    <xsl:variable name="tonode">
        <xsl:apply-templates select="key('element', datatype/@xmiidref)" mode="nodename">
    </xsl:apply-templates>
    </xsl:variable>

    <xsl:value-of select="$fromnode"/> -> <xsl:value-of select="$tonode"/> ;
  </xsl:template>

<!-- 
  <xsl:template match="reference">
    <xsl:value-of select="../name"/> -> <xsl:value-of select="datatype/@name"/> ;
  </xsl:template>
 -->

  <xsl:template match="objectType" mode="nodename">
    <xsl:call-template name="package-path">
    <xsl:with-param name="packageid" select="../@xmiid"/>
    <xsl:with-param name="delimiter" select="'_'"/>
    <xsl:with-param name="suffix" select="name"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="objectType" mode="nodelabel">
    <xsl:call-template name="package-path">
    <xsl:with-param name="packageid" select="../@xmiid"/>
    <xsl:with-param name="delimiter" select="'/'"/>
    <xsl:with-param name="suffix" select="name"/>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>