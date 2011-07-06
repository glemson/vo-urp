<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This stylesheet transforms a normalised domain XML representation of a SimDB
Experiment into a de-normalised one.

The main transformations create "name" attributes out of references.
It is the inverse transformation of the one in normalise_experiment.xsl.

 -->
<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns2="http://www.ivoa.net/xml/SNAP/v0.1/SimDB"
                xmlns:sim="http://www.ivoa.net/xml/SNAP/v0.1/SimDB"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl">

  <xsl:import href="../util.xsl"/>
  

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
  <xsl:strip-space elements="*" />
  
  <!-- 'forward' (default) or 'inverse'. 'inverse' transform from denormalised to normalised  -->
  <xsl:param name="mode" select="'forward'"/> 
  <!-- the SimDB from which other documents can be obtained using the VO-URP standard interface-->
  <xsl:param name="resolverURL"/> 

<!-- 
  for debug purposes, or running offline, 
  a file where the protocol for this experiment can be found in case the
  protocol IVO Id can not be resolved for the given experiment.
-->
  <xsl:param name="debug_protocol"/> 
 
  
  <!-- determine the appropriate URL to use to retrieve the protocol document
       decide between a local (debug) or remote (simdb) copy -->
  <xsl:variable name="theURL">
    <xsl:choose>
      <xsl:when test="$debug_protocol">
        <xsl:value-of select="$debug_protocol"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat($resolverURL,'#',//protocol[1]/@ivoId)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <!-- retrieve the protocol as a document.
      NB for newbies (like me): when combining this definition
       with the previous, putting the latter in the content of the variable, we do not get a node-set, only a value!
       need to use the select="..." in a variable to get the nodes. -->
   <xsl:variable name="theProtocol" select="document($theURL)/child::*[1]"/>

<!--  Read namespace mappings between normalised and de-normalised -->
   <xsl:variable name="namespaceMapping" select="document('./namespaceMapping.xml')/namespaceMapping"/>



<!-- get all namespaces and their prefixes  -->
<!-- my previous start at finding all namespace declarations -->
<!-- 
  <xsl:for-each select="//*">
    <xsl:variable name="v" select="."/>
    <xsl:for-each select="in-scope-prefixes(.)">
    <xsl:message>
<xsl:value-of select="."/> = <xsl:value-of select="namespace-uri-for-prefix(.,$v)"/>
    </xsl:message>
    </xsl:for-each>
  </xsl:for-each>
 -->    
<!-- from : http://www.xmlportfolio.com/namespaces/normalize-namespaces.xsl -->
<!-- TODO use XSLT 2 alternative for namespace::* -->
  <xsl:variable name="almost-unique-uri-namespace-nodes" select="//namespace::*[name()!='xml'][not(.=../preceding::*/namespace::* or .=ancestor::*[position()>1]/namespace::*)]"/>
<!-- Create a table of URI-prefix bindings -->
  <xsl:variable name="almost-unique-uri-bindings-tree">
    <xsl:for-each select="$almost-unique-uri-namespace-nodes">
      <binding>
        <prefix>
          <xsl:choose>
            <xsl:when test="not(name()) and (//*[namespace-uri()=''] or //@*[namespace-uri()=current()])">
              <xsl:variable name="alternate-prefix-candidate" select="//namespace::*[count(.|current())!=1][.=current()][name()!=''][1]"/>
              <xsl:choose>
                <xsl:when test="$alternate-prefix-candidate">
                  <xsl:value-of select="name($alternate-prefix-candidate)"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="generate-id()"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="name()"/>
            </xsl:otherwise>
          </xsl:choose>
        </prefix>
        <uri>
          <xsl:value-of select="."/>
        </uri>
      </binding>
    </xsl:for-each>
  </xsl:variable>
  <xsl:variable name="unique-uri-bindings" select="exsl:node-set($almost-unique-uri-bindings-tree)/binding[not(uri=preceding::uri)]"/>


  <!-- Utility template for mapping namespaces 
  using namspace mapping file, otherwise suffix
  -->
  <xsl:template name="namespaceMapping">
    <xsl:param name="ns_uri"/>
        <xsl:choose>
          <xsl:when test="$mode='forward' and $namespaceMapping/map[norm=$ns_uri]/denorm">
            <xsl:value-of select="$namespaceMapping/map[norm=$ns_uri]/denorm"/>
          </xsl:when>
          <xsl:when test="$namespaceMapping/map[denorm=$ns_uri]/norm">
            <xsl:value-of select="$namespaceMapping/map[denorm=$ns_uri]/norm"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$ns_uri"/>
          </xsl:otherwise>
        </xsl:choose>
  </xsl:template>
  


<!-- Main node -->
  <xsl:template match="/">

<!-- check whether the protocol is the one that is expected  -->
    <xsl:choose>
      <xsl:when test="./protocol/@ivoId != $theProtocol/identity/@ivoId">
        <xsl:message terminate="yes">Supplied protocol not same as required protocol</xsl:message>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$mode='forward'">
           <xsl:apply-templates select="./child::*[1]" mode="forward"/>
         </xsl:when>
         <xsl:otherwise>
           <xsl:apply-templates select="./child::*[1]" mode="inverse"/>
         </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise> 
    </xsl:choose>
  </xsl:template>



<!--  Identity Transform: copy everything (except for overrides below) -->
  <xsl:template match="@*|node()" mode="forward">
    <xsl:copy copy-namespaces="no">
      <xsl:apply-templates select="@*|node()" mode="forward"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="@*|node()" mode="inverse">
    <xsl:copy copy-namespaces="no">
      <xsl:apply-templates select="@*|node()" mode="inverse"/>
    </xsl:copy>
  </xsl:template>


<!-- ========================================================================================================== -->

<!-- 
root node treated specially, adding all xmlns declarations here.
after that there is no copying of namespaces, prefixes are kept the same
need mapping between domain <=> denormalised namespaces 
TBD may not need a named match here, likely we can simply use a node()[!parent()] or something like that.
All the 
 -->
  <xsl:template match="node()[not(parent::*)]" mode="forward">

    <xsl:variable name="nsuri">
      <xsl:call-template name="namespaceMapping">
        <xsl:with-param name="ns_uri" select="namespace-uri()"/>
      </xsl:call-template>
    </xsl:variable>   
    
    <xsl:element name="{name()}" namespace="{$nsuri}"> 
    <!--  first get all namespaces and prefixes and define them here -->
      <xsl:for-each select="$unique-uri-bindings">
        <xsl:namespace name="{./prefix}">
          <xsl:call-template name="namespaceMapping">
            <xsl:with-param name="ns_uri" select="uri"/>
          </xsl:call-template>
        </xsl:namespace>
      </xsl:for-each>
      <xsl:apply-templates select="@*|node()" mode="forward"/>
    </xsl:element>
  </xsl:template>




  <xsl:template match="node()[not(parent::*)]" mode="inverse">

    <xsl:variable name="nsuri">
      <xsl:call-template name="namespaceMapping">
        <xsl:with-param name="ns_uri" select="namespace-uri()"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:element name="{name()}" namespace="{$nsuri}"> 
    <!--  first get all namespaces and prefixes and define them here -->
      <xsl:for-each select="$unique-uri-bindings">
        <xsl:namespace name="{./prefix}">
          <xsl:call-template name="namespaceMapping">
            <xsl:with-param name="ns_uri" select="uri"/>
          </xsl:call-template>
        </xsl:namespace>
      </xsl:for-each>
      <xsl:apply-templates select="@*|node()" mode="inverse"/>
    </xsl:element>
  </xsl:template>




<!-- 
       =====================================
       Experiment transformations.
       =====================================
 -->
 <!-- Experiment.snapshot.objectCollection.objectType -->
 <!-- NB assume that not only references to remote Protocol.representation object types
 can be made using only the name of the object type, but also to the local 
 TargetObjectType and TargetProcess. 
 This requires that names are rather too unique maybe.
 To avoid that requirement we either need to treat instances of TargetObjectType and TargetProcess differently,
 not through ObjectCollection and characterisation, which may be too general anyway.
 Or we do not use the name as reference, but the xmlId. That must be unique
 within the document and would not clash with the properly unique name of the protocol's representation object types.-->
  <xsl:template match="objectType[parent::*[local-name()='objectCollection']]" mode="forward">
    <xsl:variable name="ivoId" select="@ivoId"/> 
    <xsl:variable name="publisherDID" select="@publisherDID"/>
    <xsl:variable name="xmlId" select="@xmlId"/>

    <xsl:element name="objectType">
        <xsl:choose>
          <xsl:when test="$theProtocol/representation/identity[@ivoId=$ivoId or publisherDID=$publisherDID]">
            <xsl:value-of select="$theProtocol/representation/identity[@ivoId=$ivoId or publisherDID=$publisherDID]/../name"/>
          </xsl:when>
          <xsl:when test="//targetObject/identity[@xmlId=$xmlId or @ivoId=$ivoId or publisherDID=$publisherDID]">
            <xsl:value-of select="//targetObject/identity[@xmlId=$xmlId or @ivoId=$ivoId or publisherDID=$publisherDID]/../name"/>
          </xsl:when>
          <xsl:when test="//targetProcess/identity[@xmlId=$xmlId or @ivoId=$ivoId or publisherDID=$publisherDID]">
            <xsl:value-of select="//targetProcess/identity[@xmlId=$xmlId or @ivoId=$ivoId or publisherDID=$publisherDID]/../name"/>
          </xsl:when>
          <xsl:otherwise>
          <xsl:message terminate="yes">No ObjectType found for axis with
          - ivoId = <xsl:value-of select="$ivoId"/>
          - publisherDID = <xsl:value-of select="$publisherDID"/>
          - xmlId = <xsl:value-of select="$xmlId"/>
          </xsl:message>
          </xsl:otherwise>
        </xsl:choose> 
    </xsl:element>
  </xsl:template>


  <xsl:template match="objectType[parent::*[local-name()='objectCollection']]" mode="inverse">
    <xsl:variable name="name" select="./text()"/>
    <xsl:element name="objectType">
        <xsl:choose>
          <xsl:when test="$theProtocol/representation[name=$name]">
            <xsl:attribute name="ivoId">
              <xsl:value-of select="$theProtocol/representation[name = $name]/identity/@ivoId"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="//targetObject[name=./text()]">
            <xsl:attribute name="xmlId">
              <xsl:value-of select="//targetObject[name=$name]/identity/@xmlId"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="//targetProcess[name=$name]">
            <xsl:attribute name="xmlId">
              <xsl:value-of select="//targetProcess[name=$name]/identity/@xmlId"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message terminate="yes">No ObjectType found with name=<xsl:value-of select="$name"/> in either this document or the corresponding protocol.</xsl:message>
          </xsl:otherwise>
        </xsl:choose> 
    </xsl:element>
  </xsl:template>





  <xsl:template match="axis[parent::*[local-name()='characterisation']]" mode="forward">
    <xsl:variable name="ivoId" select="@ivoId"/> 
    <xsl:variable name="publisherDID" select="@publisherDID"/> 
    <xsl:variable name="xmlId" select="@xmlId"/> 
    <xsl:element name="axis">
      <xsl:choose>
        <xsl:when test="$theProtocol//identity[@ivoId=$ivoId or publisherDID = $publisherDID]">
          <xsl:value-of select="$theProtocol//identity[@ivoId=$ivoId]/../name"/>
        </xsl:when>
        <xsl:when test="//targetObject/identity[@xmlId=$xmlId or publisherDID = $publisherDID]">
          <xsl:value-of select="//targetObject/identity[@xmlId=$xmlId or publisherDID = $publisherDID]/../name"/>
        </xsl:when>
        <xsl:when test="//targetProcess/identity[@xmlId=$xmlId or publisherDID = $publisherDID]">
          <xsl:value-of select="//targetProcess/identity[@xmlId=$xmlId or publisherDID = $publisherDID]/../name"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message terminate="yes">No property found for axis with
          - ivoId = <xsl:value-of select="$ivoId"/>
          - publisherDID = <xsl:value-of select="$publisherDID"/>
          - xmlId = <xsl:value-of select="$xmlId"/>
          </xsl:message>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:element>
  </xsl:template>



<!-- need to get the object type containing the property that the axis points to.
  As in the objectType abovem three options: representation, targetObject and targetProcess -->
  <xsl:template match="axis[parent::*[local-name()='characterisation']]" mode="inverse">
    <xsl:variable name="objectTypeName" select="./../../objectType"/> <!-- parent::*[local-name()='characterisation']/parent::*[local-name()='objectCollection'] -->
    <xsl:variable name="axis" select="."/>
    <xsl:element name="axis">
      <xsl:choose>
        <xsl:when test="$theProtocol/representation[name=$objectTypeName]/property[name=$axis]">
          <xsl:attribute name="ivoId">
            <xsl:value-of select="$theProtocol/representation[name=$objectTypeName]/property[name = $axis]/identity/@ivoId"/>
          </xsl:attribute>
        </xsl:when>
        <xsl:when test="//targetObject[name=$objectTypeName]/property[name=$axis]">
          <xsl:attribute name="xmlId">
            <xsl:value-of select="//targetObject[name=$objectTypeName]/property[name=$axis]/identity/@xmlId"/>
          </xsl:attribute>
        </xsl:when>
        <xsl:when test="//targetProcess[name=$objectTypeName]/property[name=$axis]">
          <xsl:attribute name="xmlId">
            <xsl:value-of select="//targetProcess[name=$objectTypeName]/property[name=$axis]/identity/@xmlId"/>
          </xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message terminate="yes">No property found with name <xsl:value-of select="$axis"/> 
           for objectType of name <xsl:value-of select="$objectTypeName"/> 
           in either this document or the corresponding protocol.</xsl:message>
        </xsl:otherwise>
      </xsl:choose> 
    </xsl:element>
  
  </xsl:template>



<!-- Experiment,representationObject.type -->
  <xsl:template match="type[parent::*[local-name()='representationObject']]" mode="forward">
    <xsl:variable name="ivoId" select="@ivoId"/> 
    <xsl:variable name="publisherDID" select="@publisherDID"/> 
    <xsl:element name="type">
      <xsl:value-of select="$theProtocol/representation/identity[@ivoId=$ivoId or @publisherDID=$publisherDID]/../name"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="type[parent::*[local-name()='representationObject']]" mode="inverse">
    <xsl:variable name="type" select="."/> 
    <xsl:element name="type">
      <xsl:attribute name="ivoId">
        <xsl:value-of select="$theProtocol/representation[name=$type]/identity/@ivoId"/>
      </xsl:attribute>
      <xsl:if test="$theProtocol/representation[name=$type]/identity/@publisherDID">
        <xsl:attribute name="publisherDID">
          <xsl:value-of select="$theProtocol/representation[name=$type]/identity/@publisherDID"/>
        </xsl:attribute>
      </xsl:if>
    </xsl:element>
  </xsl:template>
  
  
  <!-- Experiment.representationObject.property.property -->
  <xsl:template match="property[parent::*[local-name()='property' and ./parent::*[local-name()='representationObject']]]" mode="forward">
    <xsl:variable name="ivoId" select="@ivoId"/> 
    <xsl:if test="not($theProtocol/representation/property/identity[@ivoId=$ivoId])">
      <xsl:message terminate="yes">Improper Experiment.representationObject.property.property.ivoId = <xsl:value-of select="$ivoId"/></xsl:message>
    </xsl:if>
    <xsl:element name="property">
      <xsl:value-of select="$theProtocol/representation/property/identity[@ivoId=$ivoId]/../name"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="property[parent::*[local-name()='property' and ./parent::*[local-name()='representationObject']]]" mode="inverse">
    <xsl:variable name="objectTypeName" select="../../type"/> 
    <xsl:variable name="property" select="."/>
    <xsl:if test="not($theProtocol/representation[name=$objectTypeName]/property[name=$property])">
      <xsl:message terminate="yes">Improper Experiment.representationObject.property.property = <xsl:value-of select="$property"/></xsl:message>
    </xsl:if>
    
    <xsl:element name="property">
      <xsl:attribute name="ivoId">
        <xsl:value-of select="$theProtocol/representation[name=$objectTypeName]/property[name=$property]/identity/@ivoId"/>
      </xsl:attribute>
      <xsl:if test="$theProtocol/representation[name=$objectTypeName]/property[name=$property]/identity/@publisherDID">
        <xsl:attribute name="publisherDID">
          <xsl:value-of select="$theProtocol/representation[name=$objectTypeName]/property[name=$property]/identity/@publisherDID"/>
        </xsl:attribute>
      </xsl:if>
    </xsl:element>
  </xsl:template>



<!-- Experiment.parameter.inputParameter -->
<!-- TODO check that this is unique -->
  <xsl:template match="inputParameter[parent::*[local-name()='parameter']]" mode="forward">
    <xsl:variable name="ivoId" select="@ivoId"/> 
    <xsl:variable name="publisherDID" select="@publisherDID"/> 
    <!-- test for existence -->
    <xsl:if test="not($theProtocol/parameter/identity[@ivoId=$ivoId or @publisherDID=$publisherDID])">
      <xsl:message terminate="yes">There is no InputParameter on the protocol with ivoID = <xsl:value-of select="$ivoId"/></xsl:message>
    </xsl:if>
    <xsl:element name="inputParameter">
      <xsl:value-of select="$theProtocol/parameter/identity[@ivoId=$ivoId or @publisherDID=$publisherDID]/../name"/>
    </xsl:element>
  </xsl:template>


  <xsl:template match="inputParameter[parent::*[local-name()='parameter']]" mode="inverse">
    <xsl:variable name="inputParameter" select="."/> 
    <!-- test for existence -->
    <xsl:if test="not($theProtocol/parameter[name=$inputParameter])">
      <xsl:message terminate="yes">There is no InputParameter on the protocol with name = <xsl:value-of select="$inputParameter"/></xsl:message>
    </xsl:if>
    <xsl:element name="inputParameter">
      <xsl:attribute name="ivoId">
        <xsl:value-of select="$theProtocol/parameter[name=$inputParameter]/identity/@ivoId"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>



<!-- Algorithm-s --> 
  <xsl:template match="algorithm[parent::*[local-name()='appliedAlgorithm']]" mode="forward">
    <xsl:variable name="ivoId" select="@ivoId"/> 
    <xsl:element name="algorithm">
      <xsl:value-of select="$theProtocol//identity[@ivoId=$ivoId]/../name"/>
    </xsl:element>
  </xsl:template>



  <xsl:template match="algorithm[parent::*[local-name()='appliedAlgorithm']]" mode="inverse">
    <xsl:variable name="algorithm" select="."/> 
    <xsl:element name="algorithm">
      <xsl:attribute name="ivoId">
        <xsl:value-of select="$theProtocol/algorithm[name=$algorithm]/identity/@ivoId"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>



<!-- ===========================================
         Simulation specific transformations
     =========================================== -->
     <!-- Simulation.appliedPhysics.physics -->
  <xsl:template match="physics[parent::*[local-name()='appliedPhysics']]" mode="forward">
    <xsl:variable name="ivoId" select="@ivoId"/> 
    <xsl:variable name="publisherDID" select="@publisherDID"/> 
    <!-- test -->
    <xsl:if test="not($theProtocol/physicalProcess/identity[@ivoId=$ivoId or @publisherDID=$publisherDID])">
      <xsl:message terminate="yes">Improper Simulation.appliedPhysics.physics.ivoId = <xsl:value-of select="$ivoId"/></xsl:message>
    </xsl:if>
    
    <xsl:element name="physics">
      <xsl:value-of select="$theProtocol/physicalProcess/identity[@ivoId=$ivoId or @publisherDID=$publisherDID]/../name"/>
    </xsl:element>
  </xsl:template>




  <xsl:template match="physics[parent::*[local-name()='appliedPhysics']]" mode="inverse">
    <xsl:variable name="physics" select="."/> 
    <!-- test -->
    <xsl:if test="not($theProtocol/physicalProcess[name=$physics])">
      <xsl:message terminate="yes">There is no Physics on the protocol with name = <xsl:value-of select="$physics"/></xsl:message>
    </xsl:if>

    <xsl:element name="physics">
      <xsl:attribute name="ivoId">
        <xsl:value-of select="$theProtocol/physicalProcess[name=$physics]/identity/@ivoId"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>
  
</xsl:stylesheet>