<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>
<!-- 
This style sheet is VERY strongly influenced by the XMI to HTML transformation described in 
http://www.objectsbydesign.com/projects/xmi_to_html.html.
We take over the style sheet from that source:
http://www.objectsbydesign.com/projects/xmi.css
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:import href="utype.xsl"/>
  <xsl:import href="common.xsl"/>
  
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
  <xsl:param name="template_dir" select="."/>

  
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
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>
<xsl:value-of select="title"/>
</title>
    <link rel="stylesheet" href="http://ivoa.net/misc/ivoa_wg.css" type="text/css"/>
    <link rel="stylesheet" href="http://volute.googlecode.com/svn/trunk/projects/theory/snapdm/specification/html/xmi.css" type="text/css"/>
</head>
<body>
@@ here copy the body1_preamble.html file @@<br/><hr/>
<a name="abstract"></a>
@@ here copy the body2_abstract.html file @@<br/><hr/>
<a name="status"></a>
@@ here copy the body3_status.html file @@<br/><hr/>
<a name="acknowledgments"></a>
@@ here copy the body4_ackn.html file @@<br/><hr/>

<xsl:apply-templates select="." mode="TOC"/>

<xsl:apply-templates select="." mode="section"/>
  
<h1>2. <a name="types">Types</a></h1>  
<xsl:for-each select=".//package[not(ancestor::*/name() = 'profile')]/(objectType|dataType|enumeration)">
  <xsl:sort select="name"/>
<xsl:apply-templates select=".">
  <xsl:with-param name="section_number" select="concat('2.',position())"/>
  </xsl:apply-templates>
</xsl:for-each>
  
<!-- 
<xsl:apply-templates select="./package" mode="contents">
  <xsl:with-param name="section_number" select="concat('2.',position())"/>
  <xsl:sort select="name"/>
</xsl:apply-templates>
 -->
 
<h1>3. <a name="utypes">UTYPEs</a></h1>  
The following table shows all UTYPEs for this data model.
It is ordered alphabetically and the UTYPEs are hyper-linked to the location
in the document where the actual element is fully defined.
<xsl:apply-templates select="." mode="utypeslist"/>

<xsl:if test="profile">
<h1>4. <a name="profiles">Profiles</a></h1>
<hr/>
  <xsl:apply-templates select="profile" mode="contents">
  <xsl:with-param name="section_number" select="concat('4.',position())"/>
    <xsl:sort select="name"/>
  </xsl:apply-templates>
</xsl:if>  
</body>    
</html>
  </xsl:template>  
  
  
<xsl:template match="model" mode="TOC">
<h2><a id="contents" name="contents">Table of Contents</a></h2>
<div class="head">
<table class=".toc">
<tr><td/><td><a href="#abstract">Abstract</a></td></tr>
<tr><td/><td><a href="#status">Status</a></td></tr>
<tr><td/><td><a href="#acknowledgments">Acknowledgements</a></td></tr>
<tr><td/><td><a href="#contents">Table of Contents</a></td></tr>
<tr><td>1.</td><td><a href="#model"> The Model: <xsl:value-of select="title"/> (<xsl:value-of select="name"/>)</a></td></tr>
<tr><td>1.1</td><td><a href="#diagram">Diagram</a></td></tr>
<tr><td>1.2</td><td><a href="#packages">Packages</a></td></tr>
<xsl:for-each select=".//package[not(ancestor::*/name() = 'profile')]">
  <xsl:sort select="name"/>
  <xsl:variable name="xmiid" select="@xmiid"/>
<tr><td><xsl:value-of select="concat('1.2.',position())"/></td><td><a href="#{$xmiid}"><xsl:value-of select="name"/></a></td></tr>
</xsl:for-each>
<tr><td>2.</td><td><a href="#types">Types</a></td></tr>
<xsl:for-each select=".//package[not(ancestor::*/name() = 'profile')]/(objectType|dataType|enumeration)">
  <xsl:sort select="name"/>
  <xsl:variable name="xmiid" select="@xmiid"/>
<tr><td><xsl:value-of select="concat('2.',position())"/></td><td><a href="#{$xmiid}"><xsl:value-of select="name"/></a></td></tr>
</xsl:for-each>
<!-- 
IF we want to hacve all classes in ToC,
ensure all classes are here, but not those form any profile 
-->
<!-- 
<xsl:for-each select="objectType|dataType|enumeration">
    <xsl:sort select="name"/>
<tr><td><xsl:value-of select="concat('2.',position())"/></td><td><xsl:value-of select="name"/></td></tr>
</xsl:for-each>
 -->
<tr><td>3.</td><td><a href="#utypes">Utypes</a></td></tr>
<xsl:if test="profile">
<tr><td>4.</td><td><a href="#profiles">Profiles</a></td></tr>
</xsl:if>
</table>
</div>
</xsl:template>  
  
  
  <xsl:template match="model" mode="section">
  <xsl:variable name="xmiid" select="@xmiid"/>
  <h2><a name="{$xmiid}"/><a name="model">1. Model: <xsl:value-of select="title"/> (<xsl:value-of select="name"/>)</a></h2>
    <p><xsl:value-of select="description"/></p>
    <h3>1.1 <a name="diagram">Diagram</a></h3>
    <xsl:call-template name="graphviz"/>
    <h3>1.2 <a name="packages">Packages</a></h3>
    <xsl:apply-templates select="." mode="packages"/> 
    
  </xsl:template>
  
  
  
  
  <xsl:template match="model" mode="packages">
<!-- 
    <table border="1" cellspacing="2">
      <tr>
        <td  class="table-title"><a name="packages">1.2 Packages</a></td>
      </tr>
          <tr>
    <td colspan="2" width="100%">
 -->
    <xsl:for-each select=".//package[not(ancestor::*/name() = 'profile')]">
      <xsl:sort select="name"/>
      <xsl:apply-templates select=".">
        <xsl:with-param name="section_number" select="concat('1.2.',position())"/>
        <xsl:sort select="name"/>
      </xsl:apply-templates> <br/>
      </xsl:for-each>
<!-- 
    </td></tr>
    </table>    
 -->  </xsl:template>
  



  
  <xsl:template match="package">
  <xsl:param name="section_number"/>
  <xsl:variable name="utype" select="utype"/>
  <xsl:variable name="xmiid" select="@xmiid"/>
  
<h3><a name="{$utype}"/> 
    <a name="{$xmiid}"><xsl:value-of select="$section_number"/>&bl;<xsl:value-of select="name"/></a></h3> 
    <xsl:value-of select="description"/>
  
    <table border="1" cellspacing="2" width="100%">
      <tr>
        <td class="objecttype-title" width="20%">Package</td>
        <td class="objecttype-name">
          <xsl:value-of select="utype"/>
        </td>
      </tr>
<!-- 
      <tr>
        <td colspan="2" class="info">
          <xsl:value-of select="description"/>
        </td>
      </tr>
 -->
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
      <xsl:apply-templates select="." mode="containedpackages"/>
    </xsl:if>
    <xsl:if test="../name() = 'package'">
      <xsl:apply-templates select="." mode="parentpackage"/>
    </xsl:if>
<!-- 
    <xsl:if test="package">
      <tr>
        <td class="info-title" colspan="2" >Contained packages</td>
      </tr>
    <tr>
    <td colspan="2" width="100%">
      <xsl:for-each select="package">
        <xsl:sort select="name"/>
      <xsl:apply-templates select=".">
        <xsl:with-param name="section_number" select="concat($section_number,'.',position())"/>
      </xsl:apply-templates> 
      </xsl:for-each>
    </td>
    </tr>
    </xsl:if>    
 -->
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

  <xsl:template match="package" mode="containedpackages">
        <tr>
            <td width="20%" class="info-title">Contained packages</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="package">
            <xsl:sort select="name"/>
<a><xsl:attribute name="href" select="concat('#',@xmiid)"/><xsl:value-of select="name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>
  
  <xsl:template match="package" mode="parentpackage">
        <tr>
            <td width="20%" class="info-title">Parent package</td>
            <td colspan="2" class="feature-detail">
<a><xsl:attribute name="href" select="concat('#',../@xmiid)"/><xsl:value-of select="../name"/></a>&bl;
            </td>
            </tr>
  </xsl:template>

  
  <xsl:template match="profile" mode="contents">
    <xsl:param name="section_number"/>
    <h2><a><xsl:attribute name="name" select="@xmiid"/></a><xsl:value-of select="concat($section_number,' ',name)"></xsl:value-of></h2>
    <xsl:value-of select="description"/>
    <br/><br/>
    <h3><xsl:value-of select="concat($section_number,'.1')"/>Packages</h3>
    <xsl:for-each select=".//package">
      <xsl:sort select="name"/>
      <xsl:apply-templates select=".">
        <xsl:with-param name="section_number" select="concat($section_number,'.1.',position())"/>
      </xsl:apply-templates>
    </xsl:for-each>
    <h3><xsl:value-of select="concat($section_number,'.2 Types')"/></h3>
      <xsl:for-each select=".//(objectType|dataType|enumeration)">
      <xsl:sort select="name"/>
      <xsl:apply-templates select=".">
        <xsl:with-param name="section_number" select="concat($section_number,'.2.',position())"/>
      </xsl:apply-templates>
      </xsl:for-each>
  </xsl:template>



  <xsl:template match="profile" mode="package">
<!-- 
    <table border="1" cellspacing="2">
      <tr>
        <td  class="table-title"><a name="packages">1.2 Packages</a></td>
      </tr>
          <tr>
    <td colspan="2" width="100%">
 -->
    <xsl:for-each select=".//package">
      <xsl:sort select="name"/>
      <xsl:apply-templates select=".">
        <xsl:with-param name="section_number" select="concat('4.1.',position())"/>
        <xsl:sort select="name"/>
      </xsl:apply-templates> <br/>
      </xsl:for-each>
<!-- 
    </td></tr>
    </table>    
 -->  </xsl:template>



<!-- This template should not be used anymore... -->
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
    <xsl:param name="section_number"/>
    <xsl:variable name="utype" select="utype"/>
    <xsl:variable name="xmiid" select="@xmiid"/>

    <h3><a name="{$utype}"/><a name="{$xmiid}"><xsl:value-of select="concat($section_number,' ',name)"/></a></h3>
        <xsl:choose>
            <xsl:when test="description">
            <xsl:value-of select="description"/>
          </xsl:when>
          <xsl:otherwise>TBD</xsl:otherwise>
        </xsl:choose>
    <xsl:variable name="title">
    <xsl:choose>
    <xsl:when test="name() = 'objectType'">Object Type/Class</xsl:when>
    <xsl:when test="name() = 'dataType'">Data Type</xsl:when>
    </xsl:choose>
    </xsl:variable>
    <div align="center">
    <table border="1" width="100%" cellspacing="2">
      <tr>
        <td class="objecttype-title" width="20%"><xsl:value-of select="$title"/></td>
        <td class="objecttype-name">
          <xsl:value-of select="utype"/>
        </td>
      </tr>
    <tr>
    <td colspan="2" >
    <table width="100%" cellpadding="0" cellspacing="0" border="0">

    <tr>
        <td colspan="2" bgcolor="#cacaca">
        <table width="100%" border="0" cellpadding="3" cellspacing="1">

<!-- 
          <tr>
            <td colspan="4" class="feature-detail">
        <xsl:choose>
            <xsl:when test="description">
            <xsl:value-of select="description"/>
          </xsl:when>
          <xsl:otherwise>TBD</xsl:otherwise>
        </xsl:choose>
          </td></tr>
 -->
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
    <xsl:param name="section_number"/>
    <xsl:variable name="utype" select="utype"/>
    <xsl:variable name="xmiid" select="@xmiid"/>
    <h3><a name="{$utype}"/><a name="{$xmiid}"><xsl:value-of select="concat($section_number,' ',name)"/></a></h3>
        <xsl:choose>
            <xsl:when test="description">
            <xsl:value-of select="description"/>
          </xsl:when>
          <xsl:otherwise>TBD</xsl:otherwise>
        </xsl:choose>
    <table border="1" width="100%" cellspacing="2">
      <tr>
        <td class="objecttype-title" width="20%">Enumeration</td>
        <td class="objecttype-name">
          <xsl:value-of select="utype"/>
        </td>
      </tr>
    <tr>
    <td colspan="2" >
    <table width="100%" cellpadding="0" cellspacing="0" border="0">

    <tr>
        <td colspan="2" bgcolor="#cacaca">
        <table width="100%" border="0" cellpadding="3" cellspacing="1">

<!-- 
       <tr>
            <td colspan="4" class="feature-detail">
        <xsl:choose>
            <xsl:when test="description">
            <xsl:value-of select="description"/>
          </xsl:when>
          <xsl:otherwise>TBD</xsl:otherwise>
        </xsl:choose>
          </td></tr>
 -->
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
  <xsl:variable name="utype">
        <xsl:apply-templates select="." mode="utype">
          <xsl:with-param name="prefix">
            <xsl:value-of select="../utype"/>
          </xsl:with-param>
        </xsl:apply-templates>
  </xsl:variable>
  
    <tr>
        <td class="feature-detail" rowspan="4"><b>
        <a><xsl:attribute name="name" select="$utype"/></a>
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
        <td class="feature-detail"><xsl:value-of select="$utype"/>
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
<!-- 
Commented this out, instead use the one from common.xsl !
TBD check they are the same -->
<!-- 
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
 
-->



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



  <xsl:template match="model" mode="utypeslist">
  <table style="border-style:solid;border-width:1px;" border="1" cellspacing="0" cellpadding="0"> 
  <tr><td class="feature-heading">UTYPE</td>
        <td class="feature-heading">description</td>
  </tr>  
    <xsl:variable name="xmiid" select="@xmiid"/>
  <tr><td class="feature-detail"><a href="#{$xmiid}"><xsl:value-of select="utype"/></a></td><td class="feature-detail"><xsl:value-of select="description"/></td></tr>  
  <xsl:apply-templates select="package" mode="utypeslist">
      <xsl:sort select="name"/>
  </xsl:apply-templates>
  </table>
  </xsl:template>




  <xsl:template match="package" mode="utypeslist">
  <xsl:variable name="xmiid_package" select="@xmiid"/>
  <tr><td class="feature-detail"><a href="#{$xmiid_package}"><xsl:value-of select="utype"/></a></td><td class="feature-detail"><xsl:value-of select="description"/></td></tr>  
  <xsl:for-each select="objectType|dataType|enumeration">
      <xsl:sort select="name"/>
    <xsl:variable name="xmiid_class" select="@xmiid"/>
      <tr><td class="feature-detail"><a href="#{$xmiid_class}"><xsl:value-of select="utype"/></a></td><td class="feature-detail"><xsl:value-of select="description"/></td></tr>  
      <xsl:for-each select="attribute|reference|collection">
          <xsl:sort select="name"/>
        <xsl:variable name="xmiid" select="@xmiid"/>
          <tr><td class="feature-detail"><a href="#{$xmiid}"><xsl:value-of select="utype"/></a></td><td class="feature-detail"><xsl:value-of select="description"/></td></tr>  
      </xsl:for-each>
  </xsl:for-each>
    <xsl:apply-templates select="package" mode="utypeslist">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
  </xsl:template>



  
  
</xsl:stylesheet>
