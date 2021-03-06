<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>

<!-- 
This stylesheet takes the intermediate representation of the data model and 
transforms it into a valid RDF/XML document.
We use here the following mapping between our metadata model and that of RDF [TBD is this a meaningful statement?]

Intermediate		|	RDF
=====================
objectType			|   
valueType				|
attribute				|
reference				|
collection			|
enumeration			|
datatype				|
...
 -->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:dc="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
  
  <xsl:import href="common.xsl"/>
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
  
  <xsl:strip-space elements="*" />
  
  <!-- xml index on xmlid -->
  <xsl:key name="element" match="*//*" use="@xmiid"/>
  <xsl:key name="package" match="*//package" use="@xmiid"/>
  
  <!-- Input parameters -->
  <xsl:param name="lastModifiedText"/>

  <xsl:param name="targetnamespace_root"/> 
  <xsl:param name="schemalocation_root" />

  
  <xsl:variable name="xsd-ns">http://www.w3.org/2001/XMLSchema</xsl:variable>
  <xsl:variable name="xsd-prefix">xsd</xsl:variable>
  
  
  <xsl:variable name="base-prefix">base</xsl:variable>
  <xsl:variable name="base-schemanamespace" select="'http://www.ivoa.net/xml/dm/base/v0.1'"/>
  <xsl:variable name="base-schemalocation" select="concat($schemalocation_root,'base.xsd')"/>
  
  <xsl:variable name="referenceType" select="concat($base-prefix,':Reference')"/>

  <xsl:variable name="metadataObjectType" select="concat($base-prefix,':MetadataObject')"/>
  
  
  
  
  <xsl:template match="/">
    <xsl:message>Target namespace root = <xsl:value-of select="$targetnamespace_root"/></xsl:message>
    <xsl:message>Schema location root = <xsl:value-of select="$schemalocation_root"/></xsl:message>
    <xsl:apply-templates select="model"/>
  </xsl:template>
  
  
  
  
  <xsl:template match="model">
    <xsl:message>Model = <xsl:value-of select="name"></xsl:value-of></xsl:message>
-- Generating RDFs for model <xsl:value-of select="name"/>.
-- last modification date of the UML model <xsl:value-of select="$lastModifiedText"/>

    <xsl:apply-templates select="package"/>
  </xsl:template>  
  
  
  
  <xsl:template match="package">
    
    <xsl:variable name="path">
      <xsl:call-template name="package-path">
        <xsl:with-param name="packageid" select="@xmiid"/>
        <xsl:with-param name="delimiter" select="'/'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:variable name="targetschema">
      <xsl:call-template name="namespace-for-package">
        <xsl:with-param name="packageid" select="./@xmiid"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:variable name="file" select="concat($path,'.rdf')"/>
    <!-- open file for the schema file corresponding to this package -->
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}">
      
   <rdf:RDF>
        <xsl:namespace name="rdf">
          <xsl:value-of select="'http://www.w3.org/1999/02/22-rdf-syntax-ns#'"/>
        </xsl:namespace>
        <xsl:namespace name="dc">
          <xsl:value-of select="'http://purl.org/dc/elements/1.1/'"/>
        </xsl:namespace>
        <xsl:comment>
            <xsl:text>Generated from UML->XMI->intermediate->RDF.</xsl:text>
        </xsl:comment>
<!--         
        <xsl:if test="../name() = 'package'">
          <xsl:apply-templates select=".." mode="ns-import"/>
        </xsl:if>
        <xsl:apply-templates select="depends" mode="ns-import"/>
        <xsl:call-template name="import-rootnamespaces"/>
        
        <xsl:apply-templates select="objectType" mode="declare"/>
        <xsl:apply-templates select="dataType" mode="declare"/>
        <xsl:apply-templates select="enumeration" mode="declare"/>
-->        
      </rdf:RDF>
    </xsl:result-document>
    
    <xsl:apply-templates select="package"/>
    
  </xsl:template>
  
  
  
  
  <xsl:template name="ns-rootnamespaces">
    <xsl:namespace name="{$base-prefix}">
      <xsl:value-of select="$base-schemanamespace"/>
    </xsl:namespace>
  </xsl:template>
  
  
  
  
  <xsl:template name="import-rootnamespaces">
    <xsl:element name="xsd:import">
      <xsl:attribute name="namespace">
        <xsl:value-of select="$base-schemanamespace"/>
      </xsl:attribute>
      <xsl:attribute name="schemaLocation">
        <xsl:value-of select="$base-schemalocation"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>
  
  
  
  
  <xsl:template match="package" mode="parentxmlns">
    <xsl:variable name="p">
      <xsl:call-template name="package-prefix">
        <xsl:with-param name="packageid" select="@xmiid"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="ns">
      <xsl:call-template name="namespace-for-package">
        <xsl:with-param name="packageid" select="@xmiid"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:namespace name="{$p}">
      <xsl:value-of select="$ns"/>
    </xsl:namespace>
    <xsl:if test="../name() = 'package'">
      <xsl:apply-templates select=".." mode="parentxmlns"/>
    </xsl:if>
  </xsl:template>
  
  
  
  
  <xsl:template match="package" mode="ns-import">
    <xsl:variable name="ns">
      <xsl:call-template name="namespace-for-package">
        <xsl:with-param name="packageid" select="@xmiid"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="sl">
      <xsl:call-template name="schemalocation-for-package">
        <xsl:with-param name="packageid" select="@xmiid"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:element name="xsd:import">
      <xsl:attribute name="namespace">
        <xsl:value-of select="$ns"/>
      </xsl:attribute>
      <xsl:attribute name="schemaLocation">
        <xsl:value-of select="$sl"/>
      </xsl:attribute>
    </xsl:element>
    <xsl:if test="../name() = 'package'">
      <xsl:apply-templates select=".." mode="ns-import"/>
    </xsl:if>
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
  
  
  
  
  <xsl:template name="ns-import">
    <xsl:param name="packageid"/>
    <xsl:variable name="ns">
      <xsl:call-template name="namespace-for-package">
        <xsl:with-param name="packageid" select="$packageid"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="sl">
      <xsl:call-template name="schemalocation-for-package">
        <xsl:with-param name="packageid" select="$packageid"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:element name="xsd:import">
      <xsl:attribute name="namespace">
        <xsl:value-of select="$ns"/>
      </xsl:attribute>
      <xsl:attribute name="schemaLocation">
        <xsl:value-of select="$sl"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>
  
  
  
  
  <xsl:template match="depends" mode="xmlns">
    <xsl:call-template name="xmlns">
      <xsl:with-param name="packageid" select="@xmiidref"/>
    </xsl:call-template>
  </xsl:template>
  
  
  
  
  <xsl:template match="depends" mode="ns-import">
    <xsl:call-template name="ns-import">
      <xsl:with-param name="packageid" select="@xmiidref"/>
    </xsl:call-template>
  </xsl:template>
  
  
  
  
  <xsl:template match="objectType" mode="declare">
    <xsd:complexType>
      <xsl:attribute name="name">
        <xsl:value-of select="name"/>
      </xsl:attribute>
      <xsl:if test="@abstract = 'true'">
        <xsl:attribute name="abstract">
          <xsl:value-of select="'true'"/>
        </xsl:attribute>
      </xsl:if>
      <xsd:annotation>
        <xsd:documentation>
          <xsl:value-of select="description"/>
        </xsd:documentation>
        <xsd:appinfo>xmiid=<xsl:value-of select="@xmiid"/></xsd:appinfo>
      </xsd:annotation>
      <xsl:choose>
        <xsl:when test="extends">
          <xsd:complexContent>
            <xsd:extension>
              <xsl:attribute name="base">
                <xsl:call-template name="XSDType">
                  <xsl:with-param name="xmiid" select="extends/@xmiidref"/>
                  <xsl:with-param name="contextpackageid" select="../@xmiid"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:apply-templates select="." mode="content"/>
            </xsd:extension>
          </xsd:complexContent>
        </xsl:when>
        <xsl:otherwise>

          <!-- fix : MetaDataObject -->
          <xsd:complexContent>
            <xsd:extension>
              <xsl:attribute name="base">
                <xsl:value-of select="$metadataObjectType"/>
              </xsl:attribute>
              <xsl:apply-templates select="." mode="content"/>
            </xsd:extension>
          </xsd:complexContent>
          
        </xsl:otherwise>
      </xsl:choose>
    </xsd:complexType>&cr;&cr;
  </xsl:template>
  
  
  
  
  <xsl:template match="objectType" mode="content">
    <xsl:variable name="numprops" select="count(attribute|reference[not(subsets)]|collection)"/>
    <xsl:if test="number($numprops) > 0">
      <xsd:sequence>
        <xsl:apply-templates select="attribute"/>
        <xsl:apply-templates select="collection[not(subsets)]"/>
        <xsl:apply-templates select="reference[not(subsets)]"/>
      </xsd:sequence>
    </xsl:if>
  </xsl:template>
  
  
  

  
  
  
  
  
  <xsl:template match="objectType" mode="rootelements">
    <xsl:variable name="xmiid" select="@xmiid"/>
    <xsl:if test="not(@abstract='true')">
      <xsl:variable name="prefix">
        <xsl:call-template name="package-prefix">
          <xsl:with-param name="packageid" select="../@xmiid"/>
        </xsl:call-template>
      </xsl:variable>
      <xsd:element>
        <xsl:attribute name="name">
          <xsl:apply-templates select="." mode="root-element-name"/>
        </xsl:attribute>
        <xsl:attribute name="type" select="concat($prefix,':',name)"/>
      </xsd:element>
    </xsl:if>
    <xsl:apply-templates select="/model//objectType[extends/@xmiidref = $xmiid]" mode="rootelements"/>
  </xsl:template>
  
  
  
  
  <xsl:template match="dataType" mode="declare">
    <xsd:complexType>
      <xsl:attribute name="name">
        <xsl:value-of select="name"/>
      </xsl:attribute>
      <xsl:if test="@abstract = 'true'">
        <xsl:attribute name="abstract">
          <xsl:value-of select="'true'"/>
        </xsl:attribute>
      </xsl:if>
      <xsd:annotation>
        <xsd:documentation>
          <xsl:value-of select="description"/>
        </xsd:documentation>
        <xsd:appinfo>xmiid=<xsl:value-of select="@xmiid"/></xsd:appinfo>
      </xsd:annotation>
      <xsl:choose>
        <xsl:when test="extends">
          <xsd:complexContent>
            <xsd:extension>
              <xsl:attribute name="base">
                <xsl:call-template name="XSDType">
                  <xsl:with-param name="xmiid" select="extends/@xmiidref"/>
                  <xsl:with-param name="contextpackageid" select="../@xmiid"/>
                </xsl:call-template>

              </xsl:attribute>
              <xsl:apply-templates select="." mode="content"/>
            </xsd:extension>
          </xsd:complexContent>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="." mode="content"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsd:complexType>&cr;&cr;
  </xsl:template>
  
  
  
  
  <xsl:template match="dataType" mode="content">
    <xsl:variable name="numprops" select="count(attribute)"/>
    <xsl:if test="number($numprops) > 0">
      <xsd:sequence>
        <xsl:apply-templates select="attribute"/>
      </xsd:sequence>
    </xsl:if>
  </xsl:template>
  
  
  
  
  <xsl:template match="enumeration" mode="declare">
    <xsd:simpleType>
      <xsl:attribute name="name">
        <xsl:value-of select="name"/>
      </xsl:attribute>
      <xsd:annotation>
        <xsd:documentation>
          <xsl:value-of select="description"/>
        </xsd:documentation>
        <xsd:appinfo>xmiid=<xsl:value-of select="@xmiid"/></xsd:appinfo>
      </xsd:annotation>
      <xsd:restriction base="xsd:string">
        <xsl:apply-templates select="literal"/>
      </xsd:restriction>
    </xsd:simpleType>&cr;&cr;
  </xsl:template>
  
  
  
  
  <xsl:template match="enumeration/literal">
    <xsd:enumeration>
      <xsl:attribute name="value"><xsl:value-of select="value"/></xsl:attribute>
      <xsd:annotation>
        <xsd:documentation>
          <xsl:value-of select="description"/>
        </xsd:documentation>
      </xsd:annotation>
    </xsd:enumeration>
  </xsl:template>
  
  
  
  
  <xsl:template match="attribute" >
    <xsl:variable name="type">
      <xsl:call-template name="XSDType">
        <xsl:with-param name="xmiid" select="datatype/@xmiidref"/>
        <xsl:with-param name="contextpackageid" select="../../@xmiid"/>
      </xsl:call-template>
    </xsl:variable>
    <xsd:element>
      <xsl:attribute name="name" >
        <xsl:value-of select="name"/>
      </xsl:attribute>
      <xsl:attribute name="type" >
        <xsl:value-of select="$type"/>
      </xsl:attribute>
      <!--  only legal values: 0..1   1   0..*   1..* -->      
      <xsl:if test="multiplicity = '0..1' or multiplicity = '0..*'">
        <xsl:attribute name="minOccurs" >0</xsl:attribute>
      </xsl:if>
      <xsl:if test="multiplicity = '0..*' or multiplicity = '1..*'">
        <xsl:attribute name="maxOccurs" >unbounded</xsl:attribute>
      </xsl:if>
    </xsd:element>
  </xsl:template>
  
  
  
  
  <xsl:template match="collection" >
    <xsl:variable name="type">
      <xsl:call-template name="XSDType">
        <xsl:with-param name="xmiid" select="datatype/@xmiidref"/>
        <xsl:with-param name="contextpackageid" select="../../@xmiid"/>
      </xsl:call-template>
    </xsl:variable>
    <xsd:element>
      <xsl:attribute name="name" >
        <xsl:value-of select="name"/>
      </xsl:attribute>
      <xsl:attribute name="type" >
        <xsl:value-of select="$type"/>
      </xsl:attribute>
      <!--  only legal values: 0..1   1   0..*   1..* -->      
      <xsl:if test="multiplicity = '0..1' or multiplicity = '0..*'">
        <xsl:attribute name="minOccurs" >0</xsl:attribute>
      </xsl:if>
      <xsl:if test="multiplicity = '0..*' or multiplicity = '1..*'">
        <xsl:attribute name="maxOccurs" >unbounded</xsl:attribute>
      </xsl:if>
    </xsd:element>
  </xsl:template>
  
  
  
  
  <xsl:template match="reference" >
    <xsl:variable name="type">
      <xsl:call-template name="XSDType">
        <xsl:with-param name="xmiid" select="datatype/@xmiidref"/>
        <xsl:with-param name="contextpackageid" select="../../@xmiid"/>
      </xsl:call-template>

    </xsl:variable>
    <xsd:element>
      <xsl:attribute name="name" >
        <xsl:value-of select="name"/>
      </xsl:attribute>
      <xsl:attribute name="type" >
        <xsl:value-of select="$referenceType"/>
      </xsl:attribute>
      <xsl:if test="multiplicity = '0..1' or multiplicity = '*'">
        <xsl:attribute name="minOccurs" >0</xsl:attribute>
      </xsl:if>
      <!--  references should be '1' or '0..1' -->
      <!--  only legal values: 0..1   1   0..*   1..* -->      
      <xsl:if test="multiplicity = '0..1'">
        <xsl:attribute name="minOccurs" >0</xsl:attribute>
      </xsl:if>
      <xsd:annotation><xsd:appinfo>xmiidref=<xsl:value-of select="datatype/@xmiidref"/></xsd:appinfo></xsd:annotation>
    </xsd:element>
  </xsl:template>
  
  
  
  
  <!-- TODo treat anyURI and possibly more -->
  <xsl:template name="XSDType">
    <xsl:param name="xmiid"/>
    <xsl:param name="contextpackageid"/>
    <!--
    Primitive types :
        boolean
        short
        int
        long
        float
        double

    Date type :
        datetime

    Characters type :
        string
        
    Unsupported type (later) => string :
        complex
        rational
-->
    <xsl:variable name="type" select="key('element',$xmiid)"/>
    <xsl:choose>
      <xsl:when test="name($type) = 'primitiveType'">
        <xsl:choose>
          <xsl:when test="$type/name = 'boolean'">xsd:boolean</xsl:when>
          <xsl:when test="$type/name = 'short'">xsd:short</xsl:when>
          <xsl:when test="$type/name = 'integer'">xsd:int</xsl:when>
          <xsl:when test="$type/name = 'long'">xsd:long</xsl:when>
          <xsl:when test="$type/name = 'float'">xsd:float</xsl:when>
          <xsl:when test="$type/name = 'real'">xsd:double</xsl:when>
          <xsl:when test="$type/name = 'double'">xsd:double</xsl:when>
          <xsl:when test="$type/name = 'datetime'">xsd:dateTime</xsl:when>
          <xsl:when test="$type/name = 'string'">xsd:string</xsl:when>
          <xsl:otherwise>xsd:string</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="otherpackageid" select="$type/../@xmiid"/>
        <xsl:choose>
          <xsl:when test="$contextpackageid != $otherpackageid">
            <xsl:variable name="prefix">
              <xsl:call-template name="package-prefix">
                <xsl:with-param name="packageid" select="$otherpackageid"/>
              </xsl:call-template>
            </xsl:variable>
            <xsl:value-of select="$prefix"/>:<xsl:value-of select="$type/name"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$type/name"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <xsl:template match="attribute" mode="declare">
    <xsl:variable name="type">
      <xsl:call-template name="XSDType">
        <xsl:with-param name="xmiid" select="datatype/@xmiidref"/>
        <xsl:with-param name="contextpackageid" select="../../@xmiid"/>
      </xsl:call-template>
    </xsl:variable>
    <xsd:element>
      <xsl:attribute name="name">
        <xsl:value-of select="name"/>
      </xsl:attribute>
      <xsl:attribute name="type">
        <xsl:value-of select="$type"/>
      </xsl:attribute>
    </xsd:element>
  </xsl:template>
  
  
 
  
  
  
  <!--    named util templates    -->  
  <!-- return the schema location for the schema document for the package with the given id -->
  <xsl:template name="schemalocation-for-package">
    <xsl:param name="packageid"/>
    <xsl:variable name="path">
      <xsl:call-template name="package-path">
        <xsl:with-param name="packageid" select="$packageid"/>
        <xsl:with-param name="delimiter" select="'/'"/>
      </xsl:call-template>
    </xsl:variable>    
    <xsl:value-of select="concat($schemalocation_root,$path,'.xsd')"/>
  </xsl:template>
  
  
  
  

  
  
  
  <!-- calculate a prefix for the package with the given id -->
  <xsl:template name="package-prefix">
    <xsl:param name="packageid"/>
    <xsl:variable name="rank">
      <xsl:value-of select="count(/*//package[@xmiid &lt; $packageid])+1"/>
    </xsl:variable>
    <xsl:value-of select="concat('p',$rank)"/>
  </xsl:template>
  
  
</xsl:stylesheet>
