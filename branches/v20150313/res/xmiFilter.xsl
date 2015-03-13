<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:uml="http://schema.omg.org/spec/UML/2.0"
                xmlns:xmi="http://schema.omg.org/spec/XMI/2.1">
  
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
  <xsl:strip-space elements="*" />
  
  <xsl:param name="lastModified"/>
  
  
  
  
  <!-- main -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:choose>
        <xsl:when test="name() = 'uml:Model'">
          <xsl:apply-templates select="@*"/>
          <xsl:attribute name="lastModifiedDate"><xsl:value-of select="$lastModified"></xsl:value-of></xsl:attribute> 
          <xsl:apply-templates select="node()"/>
        </xsl:when>
        <!-- sort package elements by type & name -->
        <xsl:when test="@xmi:type = 'uml:Package'">
          <xsl:apply-templates select="@*"/>
          <xsl:apply-templates select="node()">
            <!-- primitive types, enumeration, data type, class, associations -->
            <xsl:sort select="@xmi:type"/> 
            <xsl:sort select="@isAbstract" order="descending"/> 
            <xsl:sort select="@name"/>
          </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="@*|node()" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>
  
  
  
  
  <xsl:template match="xmi:Extension"/>
  
  
</xsl:stylesheet>