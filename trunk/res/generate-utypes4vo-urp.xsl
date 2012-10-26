<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
<!ENTITY dotsep "<xsl:text>.</xsl:text>">
<!ENTITY colonsep "<xsl:text>:</xsl:text>">
<!ENTITY slashsep "<xsl:text>/</xsl:text>">
<!ENTITY modelsep "<xsl:text>:</xsl:text>"> <!-- separator between model and child -->
<!ENTITY ppsep "<xsl:text>/</xsl:text>"> <!-- separator between packages -->
<!ENTITY packagesuffix "<xsl:text>/</xsl:text>"> <!-- separator between packages -->
<!ENTITY pcsep "<xsl:text></xsl:text>"> <!-- separator between package and class -->
<!ENTITY casep "<xsl:text>.</xsl:text>"> <!-- separator between class and attribute -->
<!ENTITY aasep "<xsl:text>.</xsl:text>"> <!-- separator between attributes -->
]>

<xsl:stylesheet version="2.0" 
                xmlns:xmi="http://schema.omg.org/spec/XMI/2.1"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:uml="http://schema.omg.org/spec/UML/2.0"
               	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:vo-urp="http://vo-urp.googlecode.com/xsd/v0.9">
  
  <!--
    Templates used by XSLT scripts to derive UTYPE-s for attributes, references and collections
  -->
  	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />
		
  <xsl:template match="/">
  	<xsl:apply-templates select="vo-urp:model"/>
  </xsl:template>
  
  <xsl:template match="@*|node()">
  	<xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
  	</xsl:copy>
  </xsl:template>
 
  <xsl:template match="@*|node()" mode="copy">
  	<xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="copy"/>
  	</xsl:copy>
  </xsl:template>

  <xsl:template match="utype">
    <xsl:if test="../name() = 'identifier'">
      <xsl:choose>
        <xsl:when test="../@id = .">
      <xsl:element name="utype">
        <xsl:apply-templates select="../.." mode="utype"/>
      </xsl:element>
      </xsl:when>
      <xsl:otherwise>
      <xsl:element name="utype">
        <xsl:value-of select="."/>
      </xsl:element>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:if>    
  </xsl:template>
  
  <xsl:template match="utyperef">
  <xsl:variable name="utype" select="."/>
    <xsl:variable name="identifier" select="/vo-urp:model//identifier[@id = $utype]"/>
    <xsl:if test="$identifier">
    </xsl:if>
    <xsl:choose>
      <xsl:when test="$identifier/@id = .">
      <xsl:element name="utyperef">
        <xsl:apply-templates select="$identifier/.." mode="utype"/>
      </xsl:element>
      </xsl:when>
      <xsl:otherwise>
      <xsl:element name="utyperef">
        <xsl:value-of select="."/>
      </xsl:element>
      </xsl:otherwise>
      </xsl:choose>
  </xsl:template>
  
  <xsl:template match="vo-urp:model" mode="utype">
<!--     <xsl:value-of select="identifier/utype"/> -->
      <xsl:choose>
        <xsl:when test="identifier/@id = identifier/utype">
    <xsl:value-of select="name"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="identifier/utype"/>
      </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template match="import" mode="utype">
    <xsl:value-of select="identifier/utype"/>
  </xsl:template>

  <xsl:template match="package" mode="utype">
    <xsl:choose>
      <xsl:when  test="../name() = 'vo-urp:model'">
        <xsl:apply-templates select=".." mode="utype"/>&modelsep;<xsl:value-of select="concat(name,'/')"/>
      </xsl:when>
      <xsl:when  test="../name() = 'package'">
        <xsl:apply-templates select=".." mode="utype"/><xsl:value-of select="concat(name,'/')"/>
      </xsl:when>
    <xsl:otherwise>
  	<xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="objectType|dataType|enumeration|primitiveType" mode="utype">
    <xsl:apply-templates select=".." mode="utype"/><xsl:value-of select="name"/>
  </xsl:template>

  <xsl:template match="reference|collection" mode="utype">
    <xsl:apply-templates select=".." mode="utype"/>&casep;<xsl:value-of select="name"/>
  </xsl:template>
 
  <xsl:template match="literal" mode="utype">
    <xsl:apply-templates select=".." mode="utype"/>&casep;<xsl:value-of select="value"/>
  </xsl:template>
  
    <xsl:template match="extends" mode="utype">
    <xsl:apply-templates select=".." mode="utype"/>&casep;<xsl:value-of select="'EXTENDS'"/>
  </xsl:template>

  <!-- this template assumes one can always select=".."
  This breaks however for a node-set. Need an alternative solution there. -->
  <xsl:template match="attribute" mode="utype">
    <xsl:param name="prefix"/>
    <xsl:choose> <!-- if we want to start assigning separate utype-s for "attributes of attributes" we may need to use the prefix, otherwise keep the "false" -->
      <xsl:when test="../name() = 'dataType' and $prefix and false">
        <xsl:value-of select="$prefix"/>&aasep;<xsl:value-of select="name"/>
      </xsl:when>
      <xsl:otherwise>
    <xsl:apply-templates select=".." mode="utype"/>&casep;<xsl:value-of select="name"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>