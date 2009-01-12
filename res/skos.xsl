<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:skos="http://www.w3.org/2004/02/skos/core#">
  

<!-- This style sheet extracts SKOS concepts from a SKOS dovument in RDF/XML format.
To be used to simplify parsing of the document by Java code (as no XML schema seems to be available
that would allow us to use JAXB... -->

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

  <xsl:template match="/">
    <xsl:apply-templates select="rdf:RDF"/>
  </xsl:template>



  <xsl:template match="rdf:RDF">
    <xsl:element name="skos">
      <xsl:apply-templates select="skos:Concept" />
    </xsl:element>
  </xsl:template>



  <xsl:template match="skos:Concept">
    <concept><xsl:value-of select="skos:prefLabel[@xml:lang='en']"/></concept>
  </xsl:template>





</xsl:stylesheet>