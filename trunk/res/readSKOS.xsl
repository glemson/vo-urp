<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>

<!-- 
This stylesheet reads all refeerences to SKOS vocabularies from the intermediate representation of the data model,
downloads the corresponding SKOS files and transforms them to a simpler representation. 
 -->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:sk="http://www.w3.org/2004/02/skos/core#">
  
  <xsl:import href="common.xsl"/>
  
  <xsl:output method="text" encoding="UTF-8" indent="yes" />
  
  <xsl:strip-space elements="*" />
  
  <xsl:key name="uris" match="//ontologyURI" use="."/>
  
  <xsl:template match="/">
    <xsl:for-each select="//ontologyURI[generate-id()=generate-id(key('uris',.)[1])]">
      <xsl:sort select="."/>
      <xsl:call-template name="skosvocabulary">
        <xsl:with-param name="uri" select="."/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>
  
  
  
  
  <xsl:template name="skosvocabulary">
    <xsl:param name="uri"/>
    <xsl:message>Downloading SKOS vocabulary from <xsl:value-of select="$uri"/>.</xsl:message>
    <xsl:apply-templates select="document($uri)//sk:Concept"/>
  </xsl:template>  




  <xsl:template match="sk:Concept">
 <xsl:message><xsl:value-of select="sk:prefLabel"/></xsl:message>
  </xsl:template>

  
  
</xsl:stylesheet>
