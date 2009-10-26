<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This style sheet transforms a UML model, expressed in XMI, into an
"intermediate" XMl representation.
That representation follows the schema in intermediateModel.xsd.

This XSLT is tested to work on XMI generated with MagicDraw Community Edition v12.1.

 -->
<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xmi="http://schema.omg.org/spec/XMI/2.1"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:uml="http://schema.omg.org/spec/UML/2.0"
                xmlns:IVOA_Profile='http://www.magicdraw.com/schemas/IVOA_Profile.xmi'>
  
  <xsl:import href="common.xsl"/>
  <xsl:import href="utype.xsl"/>
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  
  <xsl:param name="lastModified"/>
  
  <!-- xml index on xml:id -->
  <!--  problem with match="*" is that MagicDraw creates a <proxy> for Resource (for example) when it uses a stereotype 
  and Resource shows then up twice with the same xmi:id. -->
  <xsl:key name="classid" match="*/uml:Model//*" use="@xmi:id"/>

  <xsl:variable name="dmmd_namespace" select="'http://ivoa.org/theory/datamodel/generationmetadata/v0.1'"/>
  <xsl:variable name="xmi_namespace" select="'http://schema.omg.org/spec/XMI/2.1'"/>
  <xsl:variable name="uml_namespace" select="'http://schema.omg.org/spec/UML/2.0'"/>
  

  
  
  <!-- main -->
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="namespace-uri(/*) != 'http://schema.omg.org/spec/XMI/2.1'">
        <xsl:message>ERROR Wrong namespace: this script can convert only XMI v2.1</xsl:message>
      </xsl:when>
      <xsl:when test="not(*/uml:Model)">
        <xsl:message>ERROR No uml:Model found. Possibly wrong version of uml namespace? Should be <xsl:value-of select="$uml_namespace"/></xsl:message>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="*/uml:Model" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <!-- filters uml:Model : process only uml:Package nodes -->
  <xsl:template match="uml:Model">
  
    <xsl:comment>This XML document is generated without explicit xmlns specification as it complicates
    writing XSLT scripts against it [TBD add a link to some web dicsussions about it].
    It is understood that the XML schema in 
    http://volute.googlecode.com/svn/trunk/projects/theory/snapdm/input/intermediateModel.xsd
    is to be used for validating this generated document.</xsl:comment>&cr;
      <xsl:element name="model">
      <xsl:attribute name="xmiid"><xsl:value-of select="@xmi:id"></xsl:value-of></xsl:attribute> 
      <xsl:element name="name"><xsl:value-of select="@name"/></xsl:element>
    <xsl:call-template name="description">
      <xsl:with-param name="ownedComment" select="./ownedComment"/>
    </xsl:call-template>
    <xsl:element name="lastModifiedDate"><xsl:value-of select="$lastModified"/></xsl:element>
      <xsl:apply-templates select="." mode="model.tags"/>
      <xsl:apply-templates select="./*[@xmi:type='uml:Profile' and @name='IVOA_Profile']"/>
      <xsl:apply-templates select="./*[@xmi:type='uml:Package']"/>
    </xsl:element>
  </xsl:template>
  
  
  
  
  <xsl:template match="uml:Model" mode="model.tags">
    <xsl:variable name="xmiid" select="@xmi:id"/>

    <xsl:variable name="modeltags" select="/xmi:XMI/IVOA_Profile:model[@base_Model = $xmiid]"/>

    <xsl:if test="$modeltags">
      <xsl:for-each select="$modeltags/author">
      <xsl:element name="author">
          <xsl:value-of select="."/>
      </xsl:element>
      </xsl:for-each>
      <xsl:element name="title">
      <xsl:value-of select="$modeltags/@title"/>
      </xsl:element>
    </xsl:if>
    
  </xsl:template>
  
  
  
  

  <xsl:template match="*[@xmi:type='uml:Profile']">
    <xsl:element name="profile">
      <xsl:attribute name="xmiid" select="@xmi:id"/>
      <xsl:element name="name"><xsl:value-of select="@name"/></xsl:element>
      <xsl:call-template name="description">
        <xsl:with-param name="ownedComment" select="./ownedComment"/>
      </xsl:call-template>
      <xsl:apply-templates select="./*[@xmi:type='uml:Package']"/>
    </xsl:element>    
  </xsl:template>




  
  
  
  <!-- filters uml:Package : process uml:DataType, uml:Enumeration, uml:Class nodes -->
  <xsl:template match="*[@xmi:type='uml:Package']">
    
    <!-- check if a name is defined -->
    <xsl:if test="count(@name) > 0">
      <!-- explicitly process only datatypes, enumeration, class -->
      &cr;&cr;
      <xsl:element name="package">
        <xsl:attribute name="xmiid"><xsl:value-of select="@xmi:id"></xsl:value-of></xsl:attribute> 
        <xsl:element name="name">
          <xsl:value-of select="@name"/>
        </xsl:element>
    <xsl:call-template name="description">
      <xsl:with-param name="ownedComment" select="ownedComment"/>
    </xsl:call-template>
    &cr;
        <xsl:element name="utype">
          <xsl:call-template name="intermediate_utype">
            <xsl:with-param name="member" select="."/>
          </xsl:call-template>
        </xsl:element>
        
        <xsl:if test="count(./*[@xmi:type='uml:Dependency']) > 0">
          &cr;
          <xsl:comment> Dependencies </xsl:comment>&cr;&cr;
          
          <xsl:apply-templates select="./*[@xmi:type='uml:Dependency']"/>
        </xsl:if>
        <xsl:if test="count(./*[@xmi:type='uml:Class']) > 0">
          &cr;
          <xsl:comment> Classes </xsl:comment>&cr;&cr;
          
          <xsl:apply-templates select="./*[@xmi:type='uml:Class']" />
        </xsl:if>

        <xsl:if test="count(./*[@xmi:type='uml:DataType']) > 0">
          &cr;
          <xsl:comment> Data types </xsl:comment>&cr;&cr;
          
          <xsl:apply-templates select="./*[@xmi:type='uml:DataType']" />
        </xsl:if>
        
        <xsl:if test="count(./*[@xmi:type='uml:Enumeration']) > 0">
          &cr;
          <xsl:comment> Enumerations </xsl:comment>&cr;&cr;
          
          <xsl:apply-templates select="./*[@xmi:type='uml:Enumeration']" />
        </xsl:if>
        
        <xsl:if test="count(./*[@xmi:type='uml:PrimitiveType']) > 0">
          &cr;
          <xsl:comment> Primitive Types </xsl:comment>&cr;&cr;
          
          <xsl:apply-templates select="./*[@xmi:type='uml:PrimitiveType']" />
        </xsl:if>
        
        <xsl:apply-templates select="./*[@xmi:type='uml:Package']"/>
        
      </xsl:element>
      &cr;&cr;
    </xsl:if>
  </xsl:template>

  
  
  
  <xsl:template match="*[@xmi:type='uml:Dependency']">
    <xsl:element name="depends">
      <xsl:attribute name="xmiidref"><xsl:value-of select="supplier/@xmi:idref"/></xsl:attribute>
    </xsl:element>
  </xsl:template>
  



  <xsl:template match="*[@xmi:type='uml:Class']">
    <xsl:variable name="xmiid" select="@xmi:id"/>
    
    <!-- Check whether this class is in a tree that has a contained class
    to do so, find first root base class, then find for it whether anay of its children
    is contained, if so, this class is also NOT a root element -->
    <xsl:variable name="rootid">
      <xsl:call-template name="findRootId">
        <xsl:with-param name="xmiid" select="$xmiid"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:variable name="isContained">
      <xsl:apply-templates select="key('classid',$rootid)" mode="testrootelements">
        <xsl:with-param name="count" select="'0'"/>
      </xsl:apply-templates>
    </xsl:variable>
    
    
    
    <xsl:element name="objectType">
      <xsl:attribute name="xmiid"><xsl:value-of select="@xmi:id"></xsl:value-of></xsl:attribute> 
      <xsl:if test="@isAbstract">
        <xsl:attribute name="abstract">
          <xsl:text>true</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="number($isContained) = 0">
        <xsl:attribute name="entity"><xsl:value-of select="'true'"/></xsl:attribute>
      </xsl:if>
      
      <xsl:element name="name">
        <xsl:value-of select="@name"/>
      </xsl:element>
      <xsl:call-template name="description">
        <xsl:with-param name="ownedComment" select="ownedComment"/>
      </xsl:call-template>
      <xsl:variable name="utype">
          <xsl:call-template name="intermediate_utype">
            <xsl:with-param name="member" select="."/>
          </xsl:call-template>
      </xsl:variable>
      <xsl:element name="utype">
        <xsl:call-template name="otherutype">
          <xsl:with-param name="xmiid" select="@xmi:id"/>
        </xsl:call-template>
        <xsl:value-of select="$utype"/>
        </xsl:element>
      <xsl:if test="*[@xmi:type='uml:Generalization']">
        <xsl:apply-templates select="*[@xmi:type='uml:Generalization']"/>
      </xsl:if>
      <xsl:if test="//ownedMember/ownedAttribute[@xmi:type='uml:Property' and @association and @aggregation='composite' and @type = $xmiid]">
        <xsl:variable name="idref" select="//ownedMember/ownedAttribute[@xmi:type='uml:Property' and @association and @aggregation='composite' and @type = $xmiid]/../@xmi:id"/>
        <xsl:variable name="relation" select="//ownedMember/ownedAttribute[@xmi:type='uml:Property' and @association and @aggregation='composite' and @type = $xmiid]/@name"/>
        <xsl:element name="container">
          <xsl:attribute name="name" select="key('classid',$idref)/@name"/>
          <xsl:attribute name="xmiidref" select="$idref"/>
          <xsl:attribute name="relation" select="$relation"/>
          <xsl:element name="utype">
            <xsl:call-template name="intermediate_container_utype">
              <xsl:with-param name="prefix" select="$utype"/>
            </xsl:call-template>
          </xsl:element>
        </xsl:element>
      </xsl:if>

      <xsl:for-each select="//ownedMember/ownedAttribute[@xmi:type='uml:Property' and @association and (not(@aggregation) or @aggregation='shared') and @type = $xmiid]">
        <xsl:variable name="idref" select="../@xmi:id"/>
        <xsl:variable name="relation" select="@name"/>
        <xsl:element name="referrer">
          <xsl:attribute name="name" select="key('classid',$idref)/@name"/>
          <xsl:attribute name="xmiidref" select="$idref"/>
          <xsl:attribute name="relation" select="$relation"/>
        </xsl:element>
      </xsl:for-each>

      <xsl:apply-templates select=".//*[@xmi:type='uml:Property' and not(@association)]" mode="attributes"/>
      <xsl:apply-templates select=".//*[@xmi:type='uml:Property' and @association and @aggregation='composite']" mode="collections"/>
      <xsl:apply-templates select=".//*[@xmi:type='uml:Property' and @association and (not(@aggregation) or @aggregation='shared')]" mode="references"/>
      
    </xsl:element>
    &cr;&cr;
  </xsl:template>  
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:PrimitiveType']">
    <xsl:element name="primitiveType">
      <xsl:attribute name="xmiid"><xsl:value-of select="@xmi:id"></xsl:value-of></xsl:attribute> 
      <xsl:element name="name">
        <xsl:value-of select="@name"/>
      </xsl:element>
    <xsl:call-template name="description">
      <xsl:with-param name="ownedComment" select="ownedComment"/>
    </xsl:call-template>
          <xsl:if test="*[@xmi:type='uml:Generalization']">
        <xsl:apply-templates select="*[@xmi:type='uml:Generalization']"/>
      </xsl:if>
    </xsl:element>
    &cr;&cr;
  </xsl:template>  
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:DataType']">
    <xsl:element name="dataType">
      <xsl:attribute name="xmiid"><xsl:value-of select="@xmi:id"></xsl:value-of></xsl:attribute> 
      <xsl:if test="@isAbstract">
        <xsl:attribute name="abstract">
          <xsl:text>true</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <xsl:element name="name">
        <xsl:value-of select="@name"/>
      </xsl:element>
    <xsl:call-template name="description">
      <xsl:with-param name="ownedComment" select="ownedComment"/>
    </xsl:call-template>
      <xsl:if test="*[@xmi:type='uml:Generalization']">
        <xsl:apply-templates select="*[@xmi:type='uml:Generalization']"/>
      </xsl:if>
      
      <xsl:apply-templates select=".//*[@xmi:type='uml:Property' and not(@association)]" mode="attributes"/>
      
    </xsl:element>
    &cr;&cr;
  </xsl:template>  
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:Generalization']">
    <xsl:variable name="c" select="key('classid',@general)"/>
    <xsl:element name="extends">
      <xsl:attribute name="name" select="$c/@name"/>
      <xsl:attribute name="xmiidref" select="@general"/>
    </xsl:element>
  </xsl:template>
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:Enumeration']">
    <xsl:element name="enumeration">
      <xsl:attribute name="xmiid"><xsl:value-of select="@xmi:id"></xsl:value-of></xsl:attribute> 
      <xsl:element name="name">
        <xsl:value-of select="@name"/>
      </xsl:element>
    <xsl:call-template name="description">
      <xsl:with-param name="ownedComment" select="ownedComment"/>
    </xsl:call-template>
      <xsl:apply-templates select="*[@xmi:type='uml:EnumerationLiteral']"/>
    </xsl:element>
    &cr;&cr;
  </xsl:template>
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:EnumerationLiteral']">
    <xsl:element name="literal">
      <xsl:element name="value">
        <xsl:value-of select="@name"/>
      </xsl:element>
    <xsl:call-template name="description">
      <xsl:with-param name="ownedComment" select="ownedComment"/>
    </xsl:call-template>
    </xsl:element>
  </xsl:template>
  
  
  
  
  <xsl:template name="description">
    <xsl:param name="ownedComment"/>
    <xsl:element name="description">
      <xsl:choose>
        <xsl:when test="$ownedComment/@body">
          <xsl:value-of select="$ownedComment/@body"/>
        </xsl:when>
        <xsl:otherwise>TODO : Missing description : please, update your UML model asap.</xsl:otherwise>
      </xsl:choose>        
    </xsl:element>
  </xsl:template>
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:Property']" mode="attributes">
    <xsl:element name="attribute">
      <xsl:apply-templates select="." mode="properties"/>
      <xsl:call-template name="attributestereotype">
        <xsl:with-param name="xmiid" select="@xmi:id"/>
      </xsl:call-template>
      <xsl:call-template name="ontologytermstereotype">
        <xsl:with-param name="xmiid" select="@xmi:id"/>
      </xsl:call-template>
    </xsl:element>
  </xsl:template> 
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:Property']" mode="references">
    <xsl:element name="reference">
      <xsl:apply-templates select="." mode="properties"/>
      <xsl:if test="subsettedProperty">
        <xsl:element name="subsets">
          <xsl:value-of select="subsettedProperty/@xmi:idref"/>
        </xsl:element>
      </xsl:if>
    </xsl:element>
  </xsl:template> 
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:Property']" mode="collections">
    <xsl:element name="collection">
      <xsl:apply-templates select="." mode="properties"/>
    </xsl:element>
  </xsl:template> 
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:Property']" mode="properties">
    <xsl:attribute name="xmiid"><xsl:value-of select="@xmi:id"></xsl:value-of></xsl:attribute> 
    <xsl:variable name="id" select="key('classid',@type)"/> 
    <xsl:element name="name">
      <xsl:value-of select="@name"/>
    </xsl:element>
    <xsl:call-template name="description">
      <xsl:with-param name="ownedComment" select="ownedComment"/>
    </xsl:call-template>
    <xsl:element name="utype">
      <xsl:call-template name="intermediate_utype">
        <xsl:with-param name="member" select="."/>
      </xsl:call-template>
    </xsl:element>
    <xsl:call-template name="get-class-from-id">
      <xsl:with-param name="id" select="@type"/>
    </xsl:call-template> 
    <xsl:element name="multiplicity">
      <xsl:call-template name="multiplicity">
        <xsl:with-param name="lower" select="lowerValue/@value"/>
        <xsl:with-param name="upper" select="upperValue/@value"/>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>
  
  
  
  <xsl:template name="attributestereotype">
    <xsl:param name="xmiid"/>
    <xsl:variable name="attribute" select="/xmi:XMI/IVOA_Profile:attribute[@base_Property = $xmiid]"/>
    <xsl:if test="$attribute">
      <xsl:element name="constraints">
        <xsl:if test="$attribute/@maxLength">
          <xsl:element name="maxLength"><xsl:value-of select="$attribute/@maxLength"/></xsl:element>
        </xsl:if>
        <xsl:if test="$attribute[@uniqueGlobally = 'true']">
          <xsl:element name="uniqueGlobally">true</xsl:element>
        </xsl:if>
        <xsl:if test="$attribute[@uniqueInCollection = 'true']">
          <xsl:element name="uniqueInCollection">true</xsl:element>
        </xsl:if>
      </xsl:element>
    </xsl:if>
  </xsl:template>
      



  <xsl:template name="ontologytermstereotype">
    <xsl:param name="xmiid"/>
    <xsl:variable name="attribute" select="/xmi:XMI/IVOA_Profile:ontologyterm[@base_Property = $xmiid]"/>
    <xsl:if test="$attribute">
      <xsl:element name="ontologyterm">
        <xsl:if test="$attribute/@ontologyURI">
          <xsl:element name="ontologyURI"><xsl:value-of select="$attribute/@ontologyURI"/></xsl:element>
        </xsl:if>
      </xsl:element>
    </xsl:if>
  </xsl:template>


  
  <!-- 
   only legal values: 0..1   1   0..*   1..*
         
  If no multiplicity is defined (no upper and no lower): 0..1
   -->
  
  <xsl:template name="multiplicity">
    <xsl:param name="lower"/>
    <xsl:param name="upper"/>
    <xsl:choose>
      <xsl:when test="not($lower) and (not($upper) or $upper = 1)">
        <xsl:value-of select="'0..1'"/>
      </xsl:when>
      <xsl:when test="$upper = 1 and $lower = 1">
        <xsl:value-of select="'1'"/>
      </xsl:when>
      <xsl:when test="$upper = -1 and not($lower)">
        <xsl:value-of select="'0..*'"/>
      </xsl:when>
      <xsl:when test="$upper = -1 and $lower = 1">
        <xsl:value-of select="'1..*'"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <!-- resolve class type for both data types (primitive or specific) and classes -->
  <xsl:template name="get-class-from-id">
    <xsl:param name="id"/>
    <xsl:variable name="c" select="key('classid',$id)"/>
    <xsl:element name="datatype">
      <xsl:attribute name="name" select="$c/@name"/>
      <xsl:attribute name="xmiidref" select="$id"/>
    </xsl:element>
  </xsl:template>
  
  

  
  <xsl:template name="get-package-from-id">
    <xsl:param name="id"/>
    <xsl:variable name="p" select="key('classid',$id)"/>
    <xsl:value-of select="$p/@name"/>
  </xsl:template>
  
  
  <!-- ==================================================================================== -->
  <!-- print the full path up to the specified package and append the specified suffix -->
  <!-- ==================================================================================== -->
  <xsl:template name="full-path">
    <xsl:param name="id"/>
    <xsl:param name="delimiter"/>
    <xsl:param name="suffix"/>
    
    <xsl:variable name="package" select="key('classid',$id)"/>
    <xsl:variable name="path">
      <xsl:choose>
        <xsl:when test="$suffix">
          <xsl:value-of select="concat($package/@name,$delimiter,$suffix)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$package/@name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <!-- determine whether parent is a Package or not, if not, this is the root package which should be ignored. -->   
    <xsl:variable name="parent" select="$package/..[@xmi:type='uml:Package']"/>
    
    <xsl:choose>
      <xsl:when test="not($parent)"> 
        <xsl:value-of select="$path"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="full-path">
          <xsl:with-param name="id" select="$parent/@xmi:id"/>
          <xsl:with-param name="delimiter" select="$delimiter"/>
          <xsl:with-param name="suffix" select="$path"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
</xsl:stylesheet>