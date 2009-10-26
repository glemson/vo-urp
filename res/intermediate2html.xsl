<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				>
  
  <xsl:import href="utype.xsl"/>
  
  <xsl:output method="html" encoding="UTF-8" indent="yes" />

  <xsl:strip-space elements="*" />
  
  <!-- xml index on xmlid -->
  <xsl:key name="element" match="*//*" use="@xmiid"/>
  <xsl:key name="package" match="*//package" use="@xmiid"/>

 <!-- Input parameters -->
  <xsl:param name="lastModifiedID"/>
  <xsl:param name="lastModifiedText"/>
  <xsl:param name="root.url" select="'http://volute.googlecode.com/svn/trunk/projects/theory/snapdm/specification/'"/>

  <xsl:param name="project_name"/>

  
  <!-- IF Graphviz png and map are available use these  -->
  <xsl:param name="graphviz_png"/>
  <xsl:param name="graphviz_map"/>
  
  
  <xsl:template match="/">
    <xsl:apply-templates select="model"/>
  </xsl:template>
  
  
  
  
  <xsl:template match="model">
  
    <xsl:message>Model = <xsl:value-of select="name"></xsl:value-of></xsl:message>
<html>
<head>
<title>
<xsl:value-of select="name"/>
</title>
  <link rel="stylesheet" href="http://ivoa.net/misc/ivoa_wg.css" type="text/css" /> 	
  <link rel="stylesheet" href="{$root.url}html/xmi.css" type="text/css" /> 	
</head>
<body>
<div class="head">
<a href="http://www.ivoa.net/"><img alt="IVOA" src="http://www.ivoa.net/pub/images/IVOA_wb_300.jpg" width="300" height="169"/></a>
<h1>model: <xsl:value-of select="name"/><br/>
Version 0.x</h1>
<h2>IVOA Theory Interest Group <br />Internal Draft <xsl:value-of select="$lastModifiedText"/> </h2>

  <dt>This version:</dt>
  <dd><a><xsl:attribute name="href">http://www.ivoa.net/Documents/.../<xsl:value-of select="lastModifiedID"/></xsl:attribute>
      http://www.ivoa.net/Documents/.../<xsl:value-of select="lastModifiedID"/></a></dd>

  <dt>Latest version:</dt>

  <dd>NA</dd>

  <dt>Previous versions:</dt>
  <dd>NA</dd>

  <dt>Theory Interest Group:</dt>
		<dd><a href="http://www.ivoa.net/twiki/bin/view/IVOA/IvoaTheory"> http://www.ivoa.net/twiki/bin/view/IVOA/IvoaTheory</a></dd>
	<dt>Author(s):</dt>
		<dd>
		...<br /></dd>

<hr/></div>

<h2><a name="abstract" id="abstract">Abstract</a></h2>
<p>This Note documents the structure of the <xsl:value-of select="name"/> data model.
It lays out its packages with their dependencies and defines all the object types, 
data types and enumerations with all their interrelations.
</p>

<div class="status">
<h2><a name="status" id="status">Status of this Document</a></h2>
This is a Note. The first release of this document was XXX. This version was generated automatically as described
in the Note <a href="">@@@ TODO Add title and link of relevant document@@</a>.
<p></p><br /> 
  
<p>This is an IVOA Note expressing suggestions from and opinions of the authors.<br/>
It is intended to share best practices, possible approaches, or other perspectives on interoperability with the Virtual Observatory.
It should not be referenced or otherwise interpreted as a standard specification.</p>


A list of <a href="http://www.ivoa.net/Documents/">current IVOA Recommendations and other technical documents</a> can be found at http://www.ivoa.net/Documents/.

</div><br />

<h2><a name="acknowledgments" id="acknowledgments">Acknowledgments</a></h2>
<p>This document was automatically generated from a UML data model serialised to 
<a href="http://www.omg.org/technology/documents/formal/xmi.htm">XMI</a>  and is meant to accompany an IVOA document describing the project
within which this model was created. The XSLT scripts implementing this transformation were created by Gerard Lemson and Laurent Bourges
can be found <a href="{$root.url}/xslt">here</a>. 
For acknowledgments concerning the contents of the current model we refer the reader to the project document. 
</p>
<h2><a id="contents" name="contents">Contents</a></h2>
<div class="head">
<ul>
<li><a href="#abstract">Abstract</a></li>
<li><a href="#status">Status</a></li>
<li><a href="#acknowledgments">Acknowledgements</a></li>
<li><a href="#contents">Contents</a></li>
<li>Model:<a href="#model"><xsl:value-of select="name"/></a></li>
<li>Packages:
<ul>
  <xsl:apply-templates select="package" mode="TOC">
    <xsl:sort select="name"/>
  </xsl:apply-templates>
  </ul></li>
<xsl:if test="profile">
  <xsl:apply-templates select="profile" mode="TOC">
    <xsl:sort select="name"/>
  </xsl:apply-templates>
</xsl:if>
</ul>
</div>
  <xsl:apply-templates select="." mode="section"/>
  
  <xsl:apply-templates select="./package" mode="contents">
    <xsl:sort select="name"/>
  </xsl:apply-templates>
<xsl:if test="profile">
<h1>Profiles</h1>
<hr/>
  <xsl:apply-templates select="profile" mode="contents">
    <xsl:sort select="name"/>
  </xsl:apply-templates>
</xsl:if>  
</body>    
</html>
  </xsl:template>  
  
  
  
  
  <xsl:template match="package" mode="TOC">
  <li><a>
  <xsl:attribute name="href" select="concat('#',@xmiid)"/><xsl:value-of select="name"/>
  </a> 
  <xsl:if test="package">
  <ul>
    <xsl:apply-templates select="package" mode="TOC">
    <xsl:sort select="name"/>
  </xsl:apply-templates>
  </ul>
  </xsl:if>
  </li>
  </xsl:template>




  <xsl:template match="profile" mode="TOC">
  <li>Profile: <a><xsl:attribute name="href" select="concat('#',@xmiid)"/><xsl:value-of select="name"/></a> 
  <xsl:if test="package">
  <ul>
    <xsl:apply-templates select="package" mode="TOC">
    <xsl:sort select="name"/>
  </xsl:apply-templates>
  </ul>
  </xsl:if>
  </li>
  </xsl:template>




  
  <xsl:template match="model" mode="section">
  <h2><a name="model">Model: <xsl:value-of select="name"/></a></h2>
    <p><xsl:value-of select="description"/></p>
    <xsl:call-template name="graphviz"/>
    <xsl:apply-templates select="." mode="packages"/> 
    
  </xsl:template>
  
  
  
  
  <xsl:template match="model" mode="packages">
    <table border="1" cellspacing="2">
      <tr>
        <td  class="table-title">Packages</td>
      </tr>
          <tr>
    <td colspan="2" width="100%">
      <xsl:apply-templates select="package">
      <xsl:sort select="name"/>
      </xsl:apply-templates> 
      </td></tr>
    </table>    
  </xsl:template>
  



  
  <xsl:template match="package">
  <xsl:variable name="xmiid" select="@xmiid"/>
    <table border="1" cellspacing="2" width="100%">
      <tr>
        <td colspan="2" class="objecttype-name">
          <a name="{$xmiid}"><xsl:value-of select="name"/></a> 
          <xsl:if test="count(objectType|dataType|enumeration)>0">
          (<a><xsl:attribute name="href" select="concat('#',$xmiid,'_contents')"/>contents</a>)
          </xsl:if>
        </td>
      </tr>
      <tr>
        <td colspan="2" class="info">
          <xsl:value-of select="description"/>
        </td>
      </tr>

    <xsl:if test="depends">
        <xsl:apply-templates select="." mode="depends"/>
    </xsl:if>
    <xsl:if test="objectType">
        <xsl:apply-templates select="." mode="objectType"/>
    </xsl:if>
    <xsl:if test="dataType">
        <xsl:apply-templates select="." mode="dataType"/>
    </xsl:if>
    <xsl:if test="enumeration">
        <xsl:apply-templates select="." mode="enumeration"/>
    </xsl:if>
    <xsl:if test="primitiveType">
      <xsl:apply-templates select="." mode="primitiveType"/>
    </xsl:if>
    
    <xsl:if test="package">
      <tr>
        <td class="info-title" colspan="2" >Contained packages</td>
      </tr>
    <tr>
    <td colspan="2" width="100%">
      <xsl:apply-templates select="package">
      <xsl:sort select="name"/>
      </xsl:apply-templates> 
    </td>
    </tr>
    </xsl:if>    
    </table>

  </xsl:template>
  
  
  
  <xsl:template match="package" mode="depends">
        <tr>
            <td width="20%" class="info-title">Depends</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="key('element',depends/@xmiidref)">
            <xsl:sort select="name"/>
<a><xsl:attribute name="href" select="concat('#',@xmiid)"/><xsl:value-of select="name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>





  <xsl:template match="package" mode="objectType">
        <tr>
            <td width="20%" class="info-title">Object types</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="objectType">
            <xsl:sort select="name"/>
<a><xsl:attribute name="href" select="concat('#',@xmiid)"/><xsl:value-of select="name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>



  <xsl:template match="package" mode="enumeration">
        <tr>
            <td width="20%" class="info-title">Enumerations</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="enumeration">
            <xsl:sort select="name"/>
<a><xsl:attribute name="href" select="concat('#',@xmiid)"/><xsl:value-of select="name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>



  <xsl:template match="package" mode="dataType">
        <tr>
            <td width="20%" class="info-title">Data types</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="dataType">
            <xsl:sort select="name"/>
<a><xsl:attribute name="href" select="concat('#',@xmiid)"/><xsl:value-of select="name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>



  <xsl:template match="package" mode="primitiveType">
        <tr>
            <td colspan="2" class="info-title">Primitive Types</td></tr>
            <xsl:for-each select="primitiveType">
            <xsl:sort select="name"/>
         <tr>
            <td colspan="1" width="25%" class="feature-detail">
<a><xsl:attribute name="name" select="@xmiid"/></a><xsl:value-of select="name"/>
            </td><td width="75%" class="feature-detail"><xsl:value-of select="description"/></td></tr>
            </xsl:for-each>
  </xsl:template>



  
  <xsl:template match="profile" mode="contents">
    <h2><a><xsl:attribute name="name" select="@xmiid"/></a><xsl:value-of select="name"></xsl:value-of></h2>
    <xsl:value-of select="description"/>
    <xsl:if test="package">
    <br/><br/>
    <xsl:apply-templates select="package"/>
    <xsl:apply-templates select="package" mode="contents"/>
    </xsl:if>
  </xsl:template>






  <xsl:template match="package" mode="contents">
    <xsl:variable name="xmiid" select="@xmiid"/>
  <xsl:if test="count(objectType|dataType|enumeration) > 0">
<hr/>
  <h1><a><xsl:attribute name="href" select="concat('#',$xmiid)"/>
  <xsl:attribute name="name" select="concat($xmiid,'_contents')"/>
  <xsl:value-of select="name"/></a> [<xsl:value-of select="utype"/>]</h1>
  <xsl:value-of select="description"/><br/><br/>
    <xsl:apply-templates select="objectType">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="dataType">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="enumeration">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
<hr/>
    </xsl:if>
    
    <xsl:apply-templates select="package" mode="contents"/>
  
  </xsl:template>


  
  
  <xsl:template match="objectType|dataType" >
    <xsl:variable name="xmiid" select="@xmiid"/>
    <xsl:variable name="title">
    <xsl:choose>
    <xsl:when test="name() = 'objectType'">Object Type</xsl:when>
    <xsl:when test="name() = 'dataType'">Data Type</xsl:when>
    </xsl:choose>
    </xsl:variable>
    <div align="center">
    <table border="1" width="100%" cellspacing="2">
      <tr>
        <td class="objecttype-title" width="20%"><xsl:value-of select="$title"/></td>
        <td class="objecttype-name">
          <a name="{$xmiid}"><xsl:value-of select="name"/></a> [<xsl:value-of select="utype"/>]
        </td>
      </tr>
    <tr>
    <td colspan="2" >
    <table width="100%" cellpadding="0" cellspacing="0" border="0">

    <tr>
        <td colspan="2" bgcolor="#cacaca">
        <table width="100%" border="0" cellpadding="3" cellspacing="1">

          <tr>
            <td colspan="4" class="feature-detail">
        <xsl:choose>
            <xsl:when test="description">
            <xsl:value-of select="description"/>
          </xsl:when>
          <xsl:otherwise>TBD</xsl:otherwise>
        </xsl:choose>
          </td></tr>
        <xsl:apply-templates select="." mode="package"/>
        <xsl:if test="extends">
          <xsl:apply-templates select="." mode="extends"/>
        </xsl:if>  
        <xsl:apply-templates select="." mode="subclasses"/>
        <xsl:if test="container">
          <xsl:apply-templates select="." mode="container"/>
        </xsl:if>  
        <xsl:if test="referrer">
          <xsl:apply-templates select="." mode="referrer"/>
        </xsl:if>  


        <xsl:if test="attribute">
        <xsl:call-template name="feature-rows">
          <xsl:with-param name="title" select="'Attributes'"/>
        </xsl:call-template>
        <xsl:apply-templates select="attribute">
          <xsl:sort select="name"/>
        </xsl:apply-templates>
        </xsl:if>       
        
        <xsl:if test="reference">
        <xsl:call-template name="feature-rows">
          <xsl:with-param name="title" select="'References'"/>
        </xsl:call-template>
        <xsl:apply-templates select="reference">
          <xsl:sort select="name"/>
        </xsl:apply-templates>
        </xsl:if>       

        <xsl:if test="collection">
        <xsl:call-template name="feature-rows">
          <xsl:with-param name="title" select="'Collections'"/>
        </xsl:call-template>
        <xsl:apply-templates select="collection">
          <xsl:sort select="name"/>
        </xsl:apply-templates>
        
        </xsl:if>       
        </table>
        </td>
    </tr>

    </table>
    </td>
    </tr>
    </table>
    </div>
    <br/>
  </xsl:template>




  <xsl:template match="objectType|dataType|enumeration|primitiveType" mode="package">
    <xsl:variable name="package" select="key('element',../@xmiid)"/>
        <tr>
            <td width="20%" class="info-title">Package</td>
            <td colspan="3" class="feature-detail">
<a><xsl:attribute name="href" select="concat('#',$package/@xmiid)"/><xsl:value-of select="$package/name"/></a>
            </td>
            </tr>
  </xsl:template>



  <xsl:template match="objectType|dataType|enumeration" mode="subclasses">
    <xsl:variable name="xmiid" select="@xmiid"/>
    <xsl:if test="//extends[@xmiidref = $xmiid]">
          <tr>
            <td class="info-title">Subclasses</td>
            <td class="feature-detail" colspan="3">
       <xsl:for-each select="key('element',//extends[@xmiidref = $xmiid]/../@xmiid)">
          <xsl:sort select="name"/>
 <a><xsl:attribute name="href" select="concat('#',@xmiid)"/><xsl:value-of select="name"/></a>&bl;
        </xsl:for-each>
            </td>
          </tr>
    </xsl:if>
  </xsl:template>


  <xsl:template match="objectType" mode="referrer">
    <xsl:variable name="xmiid" select="@xmiid"/>
    <xsl:if test="referrer">
          <tr>
            <td class="info-title">Referrers</td>
            <td class="feature-detail" colspan="3">
       <xsl:for-each select="key('element',referrer/@xmiidref)">
          <xsl:sort select="name"/>
 <a><xsl:attribute name="href" select="concat('#',@xmiid)"/><xsl:value-of select="name"/></a>&bl;
        </xsl:for-each>
            </td>
          </tr>
    </xsl:if>
  </xsl:template>




  <xsl:template match="objectType" mode="extends">
    <xsl:variable name="baseclass" select="key('element',extends/@xmiidref)"/>
        <tr>
            <td width="20%" class="info-title">Base class</td>
            <td colspan="3" class="feature-detail">
<a><xsl:attribute name="href" select="concat('#',$baseclass/@xmiid)"/><xsl:value-of select="$baseclass/name"/></a>
            </td>
            </tr>
  </xsl:template>


  <xsl:template match="objectType" mode="container">
    <xsl:variable name="container" select="key('element',container/@xmiidref)"/>
        <tr>
            <td width="20%" class="info-title">Container</td>
            <td colspan="3" class="feature-detail">
<a><xsl:attribute name="href" select="concat('#',$container/@xmiid)"/><xsl:value-of select="$container/name"/></a> [<xsl:value-of select="container/utype"/>]
            </td>
            </tr>
  </xsl:template>







  <xsl:template name="feature-rows">
    <xsl:param name="title"/>
        <tr>
        <td colspan="3" class="info-title"><xsl:value-of select="$title"/></td>
    </tr>
    <tr>
        <td class="feature-heading" width="20%">name</td>
        <td class="feature-heading" width="10%">feature</td>
        <td class="feature-heading" width="70%">value</td>
    </tr>
  </xsl:template>


  
  
  
  <xsl:template match="enumeration">
    <xsl:variable name="xmiid" select="@xmiid"/>
    <div align="center">
    <table border="1" width="100%" cellspacing="2">
      <tr>
        <td class="objecttype-title" width="20%">Enumeration</td>
        <td class="objecttype-name">
          <a name="{$xmiid}"><xsl:value-of select="name"/></a>
        </td>
      </tr>
    <tr>
    <td colspan="2" >
    <table width="100%" cellpadding="0" cellspacing="0" border="0">

    <tr>
        <td colspan="2" bgcolor="#cacaca">
        <table width="100%" border="0" cellpadding="3" cellspacing="1">

          <tr>
            <td colspan="4" class="feature-detail">
        <xsl:choose>
            <xsl:when test="description">
            <xsl:value-of select="description"/>
          </xsl:when>
          <xsl:otherwise>TBD</xsl:otherwise>
        </xsl:choose>
          </td></tr>

        <xsl:apply-templates select="." mode="package"/>

        <tr>
        <td colspan="2" class="info-title" align="center">Literals</td>
    </tr>
    <tr>
        <td class="feature-heading" width="25%">value</td>
        <td class="feature-heading" width="75%">description</td>
    </tr>
        <xsl:apply-templates select="literal"/>
        </table>
        </td>
    </tr>
    </table>
    </td>
    </tr>
    </table>
    </div>
    <br/>
  </xsl:template>
  
  

    
  <xsl:template match="literal" >
    <tr>
        <td class="feature-detail" >
        <a><xsl:value-of select="value"/></a>
        </td>
        <td class="feature-detail">
      <xsl:choose>
<xsl:when test="description">
    <xsl:value-of select="description"/>
</xsl:when>
<xsl:otherwise>TBD</xsl:otherwise>
      </xsl:choose>
        </td>
    </tr>
  </xsl:template>
  
     
     
     
  <!--  single row per feature, very cramped -->   
  <xsl:template match="attribute|reference|collection" mode="deprecated">
    <tr>
        <td class="feature-detail" >
        <a><xsl:attribute name="href" select="concat('#',datatype/@xmiidref)"/> <xsl:apply-templates select="datatype/@xmiidref" mode="classifier"/></a>
        </td>
        <td class="feature-detail"><b>
        <a><xsl:attribute name="name" select="@xmiid"/></a>
        <xsl:value-of select="name"/></b>
        <xsl:if test="subsets">
        <xsl:variable name="prop" select="key('element',subsets)"/>
        &bl;&bl;{subsets <a><xsl:attribute name="href" select="concat('#',subsets)"/>
        <xsl:value-of select="concat($prop/../name,':',$prop/name)"/></a> } 
        </xsl:if>
        <br/>
        <xsl:apply-templates select="." mode="utype">
          <xsl:with-param name="prefix">
            <xsl:value-of select="../utype"/>
          </xsl:with-param>
        </xsl:apply-templates>
        </td>
        <td class="feature-detail">
        <xsl:value-of select="multiplicity"/>
        </td>
        <td class="feature-detail">
        <xsl:choose><xsl:when test="description"><xsl:value-of select="description"/></xsl:when>
        <xsl:otherwise>TBD</xsl:otherwise>
        </xsl:choose>
        </td>
    </tr>
  </xsl:template>

    
<!-- multiple rows per feature -->
  <xsl:template match="attribute|reference|collection" >
    <tr>
        <td class="feature-detail" rowspan="4"><b>
        <a><xsl:attribute name="name" select="@xmiid"/></a>
        <xsl:value-of select="name"/></b>
        <xsl:if test="subsets">
        <xsl:variable name="prop" select="key('element',subsets)"/>
        <br/>{subsets <a><xsl:attribute name="href" select="concat('#',subsets)"/>
        <xsl:value-of select="concat($prop/../name,':',$prop/name)"/></a> } 
        </xsl:if>
        </td>
        <td class="feature-heading">type</td>
        <td class="feature-detail" >
        <a><xsl:attribute name="href" select="concat('#',datatype/@xmiidref)"/> <xsl:apply-templates select="datatype/@xmiidref" mode="classifier"/></a>
        <xsl:if test="ontologyterm">
        <br/>Valid values from semantic vocabulary at:<br/><a><xsl:attribute name="href" select="ontologyterm/ontologyURI"/>
        <xsl:value-of select="ontologyterm/ontologyURI"/></a>
        </xsl:if>
        </td>
     </tr>
     <tr>
        <td class="feature-heading">utype(s)</td>
        <td class="feature-detail">
        <xsl:apply-templates select="." mode="utype">
          <xsl:with-param name="prefix">
            <xsl:value-of select="../utype"/>
          </xsl:with-param>
        </xsl:apply-templates>
        </td>
      </tr>
      <tr>
        <td class="feature-heading">cardinality</td>
        <td class="feature-detail">
        <xsl:value-of select="multiplicity"/>
        </td>
      </tr>
      <tr>
        <td class="feature-heading">description</td>
        <td class="feature-detail">
        <xsl:choose><xsl:when test="description"><xsl:value-of select="description"/></xsl:when>
        <xsl:otherwise>TBD</xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
  </xsl:template>


    
    
  <xsl:template match="datatype/@xmiidref" mode="classifier">
    <xsl:variable name="type" select="key('element',.)"/>
    <xsl:value-of select="$type/name"/>
  </xsl:template>

  
  


<!--    named util templates    -->
    <!-- Calculate the full path to the package identified by the packageid
      Use the specified delimiter. -->
  <xsl:template name="package-path">
    <xsl:param name="packageid"/>
    <xsl:param name="delimiter"/>
    <xsl:param name="suffix"/>
    
    <xsl:variable name="p" select="key('element',$packageid)"/>
    <xsl:choose>
      <xsl:when test="name($p) = 'package'">
        <xsl:variable name="newsuffix">
          <xsl:choose>
            <xsl:when test="$suffix">
              <xsl:value-of select="concat($p/name,$delimiter,$suffix)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$p/name"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:call-template name="package-path">
          <xsl:with-param name="packageid" select="$p/../@xmiid"/>
          <xsl:with-param name="suffix" select="$newsuffix"/>
          <xsl:with-param name="delimiter" select="$delimiter"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$suffix"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
 




  <xsl:template name="graphviz">
    <xsl:if test="$graphviz_png">
      <xsl:element name="hr"></xsl:element>
      <xsl:element name="img">
        <xsl:attribute name="src">
          <xsl:value-of select="$graphviz_png"/>
        </xsl:attribute>
        <xsl:if test="$graphviz_map">
          <xsl:attribute name="usemap" select="concat('#',$project_name)"/>
        </xsl:if>
      </xsl:element>
      <xsl:if test="$graphviz_map">
        <xsl:value-of select="$graphviz_map"/>
      </xsl:if>
    </xsl:if>
  </xsl:template>








  
  
</xsl:stylesheet>
