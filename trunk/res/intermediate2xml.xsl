<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>
<!--  
Generates a template XML document conforming to the XML schema.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl"
                xmlns:p0="http://www.ivoa.net/xml/SNAP/v0.1/SimDB"
                xmlns:vo-urp="http://vo-urp.googlecode.com/"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  
  <xsl:import href="common.xsl"/>
  <xsl:import href="common-xsd.xsl"/>
  <xsl:import href="utype.xsl"/>
  <xsl:import href="prettyfier.xsl"/>
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
  
  <xsl:strip-space elements="*" />
  
  <!-- xml index on xmlid -->
  <xsl:key name="element" match="*//*" use="@xmiid"/>
  
  <!-- Input parameters -->
  <xsl:param name="lastModifiedText"/>

  <xsl:param name="targetnamespace_root"/> 
  <xsl:variable name="targetschema">
    <xsl:value-of select="concat($targetnamespace_root,'/',//model/name)"/>
  </xsl:variable>
  
  <xsl:param name="ivoIdHost" select="'ivo://some.vo.host/'"/>
  
  
  <xsl:param name="ISDEBUG"/>
  
  <xsl:template match="/">
    <xsl:message>Target namespace root = <xsl:value-of select="$targetnamespace_root"/></xsl:message>
    <xsl:apply-templates select="model"/>
  </xsl:template>
  
  
  
  
  <xsl:template match="model">
    <xsl:message>Model = <xsl:value-of select="name"></xsl:value-of></xsl:message>
-- Generating XML documents for model <xsl:value-of select="name"/>.
-- last modification date of the UML model <xsl:value-of select="$lastModifiedText"/>


    <xsl:for-each select=".//objectType[not(extends)]">
      <xsl:variable name="isContained">
        <xsl:apply-templates select="." mode="testrootelements">
          <xsl:with-param name="count" select="'0'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:if test="number($isContained) = 0">
        <xsl:apply-templates select="." mode="root"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>  
  
  
  

  
  
  <xsl:template match="objectType" mode="root">
    <xsl:variable name="xmiid" select="@xmiid"/>
    <xsl:if test="not(@abstract='true')">
      <xsl:variable name="name">
        <xsl:apply-templates select="." mode="root-element-name"/>
      </xsl:variable>
<xsl:message>Root : <xsl:value-of select="name"/></xsl:message>

<xsl:variable name="theXMLDoc">
    <xsl:comment>
This is a sample XML document holding on to a(n) <xsl:apply-templates select="." mode="utype"/>.
sample values are given for each attribute, contained class and reference.
The multiplicity of each element is indicated in a comment.
in all cases 1 instance is created, except where due to polymorphism
in collections there may be more than one concrete type.
Not that this may lead to XML documents that are invalid according to the schema.
It will be indicated in the comment that only a subset must be chosen according to the 
cardinality.
NB We COULD have chose to represent some of these choices only in a comment,
but that would imply none of the contained elements could have comments.
    </xsl:comment>

    <xsl:element name="p0:{$name}" namespace="{$targetschema}">    
<!-- next is overdoing it .. -->
      <xsl:namespace name="xsi">
        <xsl:text>http://www.w3.org/2001/XMLSchema-instance</xsl:text>
      </xsl:namespace>
      <xsl:for-each select="//package">
        <xsl:call-template name="xmlns">
          <xsl:with-param name="packageid" select="@xmiid"/>
        </xsl:call-template>
      </xsl:for-each>
 
      <xsl:apply-templates select="." />
    </xsl:element>
    
    
</xsl:variable>

    <xsl:variable name="file" select="concat($name,'.xml')"/>
    <!-- open file for the schema file corresponding to this package -->
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}">
 
      <xsl:apply-templates select="exsl:node-set($theXMLDoc)" mode="pretty"/>    
    
    </xsl:result-document>
    
    </xsl:if>
    <xsl:apply-templates select="/model//objectType[extends/@xmiidref = $xmiid]" mode="root"/>
  </xsl:template>
  
 

  <xsl:template match="objectType">
    <xsl:param name="contextObjectId" select="@xmiid"/> <!-- the object for which the XML doc is actually created. 
                                             May have influence through subsetting of properties for example. -->
    <xsl:if test="$ISDEBUG">
      <xsl:message>In content for <xsl:value-of select="name"/></xsl:message>
    </xsl:if>
    <xsl:if test="extends">
      <xsl:apply-templates select="key('element',extends/@xmiidref)">
        <xsl:with-param name="contextObjectId" select="$contextObjectId"/>
      </xsl:apply-templates>
    </xsl:if>
       <xsl:apply-templates select="attribute">
        <xsl:with-param name="contextObjectId" select="$contextObjectId"/>
      </xsl:apply-templates>
       <xsl:apply-templates select="collection[not(subsets)]">
        <xsl:with-param name="contextObjectId" select="$contextObjectId"/>
      </xsl:apply-templates>
       <xsl:apply-templates select="reference[not(subsets)]">
        <xsl:with-param name="contextObjectId" select="$contextObjectId"/>
      </xsl:apply-templates>
  </xsl:template> 

 
 
  <xsl:template match="dataType">
     <xsl:apply-templates select="attribute"/>
  </xsl:template>
  
  
  <xsl:template match="dataType" mode="comment">
     <xsl:comment>A(n) <xsl:value-of select="name"/></xsl:comment>
  </xsl:template>
  



  
  <xsl:template match="enumeration">
    <xsl:value-of select="literal[1]/value"/>
  </xsl:template>
  
  
  <xsl:template match="enumeration" mode="comment">
    <xsl:comment>A(n) <xsl:value-of select="name"/>.</xsl:comment>
    <xsl:comment>An enumeration with possible values:
      <xsl:for-each select="literal">&bl;'<xsl:value-of select="value"/>'</xsl:for-each>
    </xsl:comment>    
  </xsl:template>
  
  
  <xsl:template match="attribute" >
    <xsl:param name="contextObjectId"/> <!-- the object for which the XML doc is actually created. 
                                             May have influence through subsetting of properties for example. -->
   <xsl:if test="$ISDEBUG">
     <xsl:message>in attribute type = <xsl:value-of select="name(key('element',./datatype/@xmiidref))"/></xsl:message>
   </xsl:if>
     <xsl:variable name="name" select="name"/>
     <xsl:variable name="datatype" select="key('element',./datatype/@xmiidref)"/>
     <xsl:apply-templates select="$datatype" mode="comment"/>
     <xsl:comment>Cardinality : <xsl:value-of select="multiplicity"/></xsl:comment>
     <xsl:if test="ontologyterm">
     <xsl:comment>This attribute gets its values from the SKOS list of concepts in <xsl:value-of select="ontologyterm/ontologyURI"/>.</xsl:comment>
     </xsl:if>
     <xsl:element name="{$name}">    
      <xsl:apply-templates select="$datatype"/>
    </xsl:element>
  </xsl:template>
  

  


  
  <xsl:template match="collection">
    <xsl:param name="contextObjectId"/> <!-- the object for which the XML doc is actually created. 
                                             May have influence through subsetting of properties for example. -->
     <xsl:variable name="datatype" select="key('element',./datatype/@xmiidref)"/>
     <xsl:comment> 
     A collection of <xsl:value-of select="$datatype/utype"/>.
     Cardinality = <xsl:value-of select="multiplicity"/>.
     </xsl:comment>
     <!--  TODO add all concrete subclasses -->
     <xsl:apply-templates select="$datatype" mode="collectionContent">
       <xsl:with-param name="collectionName" select="name"/>
       <xsl:with-param name="rootId" select="datatype/@xmiidref"/>       
     </xsl:apply-templates>
  </xsl:template>
  
  
  <xsl:template match="objectType" mode="collectionContent">
    <xsl:param name="collectionName" />
    <xsl:param name="rootId"/>
    <xsl:param name="contextObjectId"/> <!-- the object for which the XML doc is actually created. 
                                             May have influence through subsetting of properties for example. -->
    <xsl:variable name="xmiid" select="@xmiid"/>
    <xsl:variable name="datatype" select="key('element',datatype/@xmiidref)"/>
     <xsl:if test="not(@abstract='true')">
       <xsl:element name="{$collectionName}">
         <xsl:if test="not(@xmiid = $rootId)">
           <xsl:apply-templates select="." mode="xsi-type"/>
         </xsl:if>
         <xsl:apply-templates select="."/>
       </xsl:element>
     </xsl:if>
     <xsl:apply-templates select="//objectType[extends/@xmiidref=$xmiid]" mode="collectionContent">
       <xsl:with-param name="collectionName" select="$collectionName"/>
       <xsl:with-param name="rootId" select="$rootId"/>       
     </xsl:apply-templates>
     
  </xsl:template>
  
  


  <xsl:template match="objectType" mode="xsi-type">
    <xsl:variable name="prefix">
      <xsl:apply-templates select="." mode="package-prefix"/>
    </xsl:variable>
    <xsl:attribute name="xsi:type">
       <xsl:value-of select="concat($prefix,':',name)"/>
    </xsl:attribute>
  </xsl:template>
  
  
  <!-- TODO create an appropriate ID for the refernce, one based
  on the root URL of the service and the UTYPE etc. -->
  <xsl:template match="reference" >
    <xsl:param name="contextObjectId"/> <!-- the object for which the XML doc is actually created. 
                                             May have influence through subsetting of properties for example. -->
     <xsl:variable name="name" select="name"/>
     <!-- Retrieve the relevant reference for the context object -->
     <xsl:variable name="theReferenceId">
       <xsl:apply-templates select="key('element',$contextObjectId)" mode="subsettedProperty">
         <xsl:with-param name="property" select="$name"/>
       </xsl:apply-templates>
     </xsl:variable>
     <xsl:variable name="theReference" select="key('element',$theReferenceId)"/>
     <xsl:variable name="datatype" select="key('element',$theReference/datatype/@xmiidref)"/>
     <xsl:comment> 
     A reference to <xsl:value-of select="$datatype/utype"/>
     Cardinality = <xsl:value-of select="$theReference/multiplicity"/>.
     The reference is implemented using a sample ivoId, based on the utype of the referenced
     object.
     Other attributes are publisherDID and xmlId.
     The latter MUST be used when the reference is to another element in this same XML document.
     </xsl:comment>
     <!--  TODO add all concrete subclasses -->
     <xsl:element name="{$name}">    
      <xsl:attribute name="ivoId" select="concat($ivoIdHost,'#',$datatype/utype,'/12345')"/>
    </xsl:element>
  </xsl:template>
  
  
  
  
  <!-- TODO treat anyURI and possibly more -->
  <xsl:template match="primitiveType">
    <xsl:if test="$ISDEBUG">
      <xsl:message>In primitiveType for <xsl:value-of select="name"/></xsl:message>
    </xsl:if>
        <xsl:choose>
          <xsl:when test="name = 'boolean'">true</xsl:when>
          <xsl:when test="name = 'short'">-1</xsl:when>
          <xsl:when test="name = 'integer'">-1</xsl:when>
          <xsl:when test="name = 'long'">-1</xsl:when>
          <xsl:when test="name = 'float'">-1.0</xsl:when>
          <xsl:when test="name = 'real'">-1.0</xsl:when>
          <xsl:when test="name = 'double'">-1.0</xsl:when>
          <xsl:when test="name = 'datetime'">1970-01-01T00:00:00</xsl:when>
          <xsl:when test="name = 'string'">abcde</xsl:when>
          <xsl:when test="name = 'anyURI'">http://some.uri.somewhere/</xsl:when>
          <xsl:otherwise>abcdefg</xsl:otherwise>
        </xsl:choose>
  </xsl:template>
  
  
  
  <xsl:template match="primitiveType" mode="comment">
    <xsl:comment>A(n) <xsl:value-of select="name"/></xsl:comment>
  </xsl:template>
  
  
  <xsl:template name="xmlns" >
    <xsl:param name="packageid"/>
    <xsl:variable name="p">
      <xsl:call-template name="package-prefix">
        <xsl:with-param name="packageid" select="$packageid"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="ns">
      <xsl:call-template name="namespace-for-package">
        <xsl:with-param name="packageid" select="$packageid"/>
      </xsl:call-template>
    </xsl:variable>    
    <xsl:namespace name="{$p}">
      <xsl:value-of select="$ns"/>
    </xsl:namespace>
  </xsl:template>
  
  
  <!--  Find the (possibly subsetted) version of the specified property that is closest (in inheritance sense)
  to the specified object.
  We use simply the name of the property, assuming the precondition that a subsetted property must have the same name.
  The latter MUST be supported in the (analysis of the) -->
  <xsl:template match="objectType" mode="subsettedProperty">
    <xsl:param name="property"/>
    <xsl:choose>
      <xsl:when test="*[name=$property]">
        <xsl:value-of select="*[name=$property]/@xmiid"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="key('element', extends/@xmiidref)" mode="subsettedProperty">
          <xsl:with-param name="property" select="$property"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  
  
</xsl:stylesheet>
