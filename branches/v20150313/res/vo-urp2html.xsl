<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
<!ENTITY nbsp "&#160;">
<!ENTITY tab "&#160;&#160;&#160;&#160;">
]>
<!-- 
This style sheet is VERY strongly influenced by the XMI to HTML transformation described in 
http://www.objectsbydesign.com/projects/xmi_to_html.html.
We take over the style sheet from that source:
http://www.objectsbydesign.com/projects/xmi.css
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:vo-urp="http://vo-urp.googlecode.com/xsd/v0.9">
  
  <xsl:import href="utype.xsl"/>
  <xsl:import href="common.xsl"/>
  
  <xsl:output method="html" encoding="UTF-8" indent="yes" />

  <xsl:strip-space elements="*" />
  
  <!-- xml index on xmlid -->
  <xsl:key name="element" match="*//identifier" use="utype"/>
  <xsl:key name="package" match="*//package/identifier" use="utype"/>

 <!-- Input parameters -->
  <xsl:param name="lastModifiedID"/>
  <xsl:param name="lastModifiedText"/>

  <xsl:param name="project_name"/>
  <!-- 
  The root directoryr which should contain the folowing files: preamble.html, abstract.html, status.html and acknowledgment.html 
  These will be copied at particular places in the generated document.
  -->
  <xsl:param name="preamble"/> 
  
  <!-- IF Graphviz png and map are available use these  -->
  <xsl:param name="graphviz_png"/>
  <xsl:param name="graphviz_map"/>
  
  <!-- Section numbering -->
  <xsl:variable name="model_section_number" select="'1.'"/>
  <xsl:variable name="contents_section_number" select="'2.'"/>
  <xsl:variable name="utypes_section_number" select="'3.'"/>
  <xsl:variable name="modelimports_section_number" select="'4.'"/>
  
  <xsl:template match="/">
    <xsl:apply-templates select="vo-urp:model"/>
  </xsl:template>
  
  <xsl:template match="@*|node()" mode="copy">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="copy"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="vo-urp:model">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>
<xsl:value-of select="title"/>
</title>
    <link rel="stylesheet" href="../ivoa_wg.css" type="text/css"/>
    <link rel="stylesheet" href="../xmi.css" type="text/css"/>
<!--
    <link rel="stylesheet" href="http://vo-urp.googlecode.com/svn/trunk/IVOA/ivoa_wg.css" type="text/css"/>
    <link rel="stylesheet" href="http://vo-urp.googlecode.com/svn/trunk/IVOA/xmi.css" type="text/css"/>
 -->
</head>
<body>
  
<xsl:if test="$preamble != ''">
  <xsl:apply-templates select="document($preamble)" mode="copy"/>
<br/>
<hr/>
</xsl:if>
<xsl:apply-templates select="." mode="TOC"/>
<hr/>  
<xsl:apply-templates select="." mode="section"/>
<hr/>  
<xsl:apply-templates select="." mode="contents"/>
<hr/>  
<xsl:apply-templates select="." mode="utypes"/>
<xsl:if test="import">
  <xsl:apply-templates select="." mode="imports"/>
</xsl:if>  

</body>    
</html>

  </xsl:template>  
  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
<!--            START  Table of contents                       -->  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
  
  <xsl:template match="vo-urp:model" mode="TOC">
  <h1><xsl:value-of select="title"/></h1>
<h2><a id="contents" name="contents">Table of Contents</a></h2>
<div class="head">
<table class=".toc">
<xsl:if test="$preamble != ''">
<tr><td/><td>&tab;<a href="#abstract">Abstract</a></td></tr>
<tr><td/><td>&tab;<a href="#status">Status</a></td></tr>
<tr><td/><td>&tab;<a href="#acknowledgments">Acknowledgements</a></td></tr>
</xsl:if>
<tr>
<td><xsl:value-of select="$model_section_number"/></td>
<td>&tab;<a href="#model">model:&bl;<xsl:value-of select="name"/></a></td>
</tr>
<tr>
<td><xsl:value-of select="$contents_section_number"/></td>
<td>&tab;<a href="#packages">Packages and Types</a></td>
</tr>
<tr>
<td><xsl:apply-templates select="." mode="section_label"/></td>
<td>&tab;<a href="#rootpackage">[root package]</a></td>
</tr>
  <xsl:for-each select="objectType|dataType|enumeration|primitiveType">
    <xsl:sort select="name"/>
    <xsl:variable name="utype" select="identifier/utype"/>
    <xsl:variable name="section_label">
      <xsl:apply-templates select="." mode="section_label"/>
    </xsl:variable>
<tr>
<td><xsl:value-of select="$section_label" /></td>
<td>&tab;<a href="#{$utype}"><xsl:value-of select="concat(name(),':',name)"/></a></td>
</tr>
  </xsl:for-each>

<xsl:for-each select="//package">
  <xsl:sort select="identifier/utype"/>
  <xsl:variable name="utype" select="identifier/utype"/>
  <xsl:variable name="section_label">
    <xsl:apply-templates select="." mode="section_label"/>
  </xsl:variable>
<tr>
<td><xsl:value-of select="$section_label"/></td>
<td>&tab;<a href="#{$utype}">package:&bl;<xsl:value-of select="name"/></a></td>
</tr>
  <xsl:for-each select="objectType|dataType|enumeration|primitiveType">
    <xsl:sort select="name"/>
    <xsl:variable name="utype" select="identifier/utype"/>
    <xsl:variable name="section_label">
      <xsl:apply-templates select="." mode="section_label"/>
    </xsl:variable>
<tr>
<td><xsl:value-of select="$section_label" /></td>
<td>&tab;<a href="#{$utype}"><xsl:value-of select="concat(name(),': ',name)"/></a></td>
</tr>
  </xsl:for-each>
</xsl:for-each>
<tr><td><xsl:value-of select="$utypes_section_number"/></td><td>
&tab;<a href="#utypes">UTYPE-s</a></td></tr>
<xsl:if test="import">
  <tr><td><xsl:value-of select="$modelimports_section_number"/></td><td>
&tab;<a href="#modelimports">Imported Models</a></td></tr>
<xsl:for-each select="import">
  <tr><td><xsl:value-of select="concat($modelimports_section_number,position())"/></td><td>
&tab;<a><xsl:attribute name="href" select="concat('#',identifier/utype)"/><xsl:value-of select="name"/></a></td></tr>
  </xsl:for-each>
</xsl:if>

</table>
</div>
</xsl:template>  
  
 
  <xsl:template match="package" mode="section_label">
    <xsl:variable name="rank">
      <xsl:apply-templates select="." mode="rank"/>
    </xsl:variable>
    <xsl:value-of select="concat($contents_section_number,$rank+1) "/> 
  </xsl:template>  
  
  <xsl:template match="objectType|dataType|enumeration|primitiveType" mode="section_label">
    <xsl:variable name="prefix">
      <xsl:apply-templates select=".." mode="section_label"/>
    </xsl:variable>
    <xsl:variable name="rank">
      <xsl:apply-templates select="." mode="rank"/>
    </xsl:variable>
    <xsl:value-of select="concat($prefix,'.',$rank)"/> 
  </xsl:template>  
 
  <xsl:template match="vo-urp:model" mode="section_label">
    <xsl:value-of select="concat($contents_section_number,'1')"/> 
  </xsl:template> 

    <xsl:template match="objectType|dataType|enumeration|primitiveType" mode="rank">
      <xsl:variable name="me" select="."/>
      <xsl:for-each select="../(objectType|dataType|enumeration|primitiveType)">
        <xsl:sort select="name"/>
        <xsl:if test=". = $me">
          <xsl:value-of select="position()"/>
        </xsl:if>
      </xsl:for-each>
  </xsl:template> 
  
    <xsl:template match="package" mode="rank">
      <xsl:variable name="me" select="."/>
      <xsl:for-each select="/vo-urp:model//package">
        <xsl:sort select="identifier/utype"/>
        <xsl:if test=". = $me">
          <xsl:value-of select="position()"/>
        </xsl:if>
      </xsl:for-each>
  </xsl:template> 

<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
<!--            END  Table of contents                       -->  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  

<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
<!--            START  model section/diagram                   -->  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
    
  <xsl:template match="vo-urp:model" mode="section">
  <xsl:variable name="utype" select="identifier/utype"/>
  <h1><a name="{$utype}"/><a name="model">1. Model: <xsl:value-of select="title"/> (<xsl:value-of select="name"/>)</a></h1>
  <table>
  <tr><td align="right"><b>Authors</b></td><td>&bl;:&bl;</td><td> <xsl:for-each select="author"><xsl:if test="position()>1">,&bl;</xsl:if><xsl:value-of select="."/></xsl:for-each></td></tr>
  <tr><td align="right"><b>Date</b></td><td>&bl;:&bl;</td><td><xsl:value-of select="lastModified"/></td></tr>
  <tr><td align="right"><b>Version</b></td><td>&bl;:&bl;</td><td><xsl:value-of select="version"/></td></tr>
  <xsl:if test="previousVersion">
  <tr><td align="right">Previous version:</td><td>&bl;:&bl;</td><td><a><xsl:attribute name="href" select="previousVersion"/><xsl:value-of select="previousVersion"/></a></td></tr>
  </xsl:if>
  <tr><td align="right" valign="top"><b>Abstract</b></td><td valign="top">&bl;:&bl;</td><td><xsl:value-of select="description"/></td></tr>

    <xsl:if test="$graphviz_png">
    <tr><td align="right"  valign="top"><b>Diagram</b></td><td valign="top">&bl;:&bl;</td>
    <td>The following diagram has been generated from the model using the <a href="http://www.graphviz.org/" target="_blank">GraphViz</a> tool.<br/>
    The classes and packages in the diagram can be clicked and are mapped to the descriptions of the corresponding element elsewhere in the document. 
    </td></tr>
    <tr><td colspan="3">
    </td></tr>
    </xsl:if>
  </table>
      <xsl:if test="$graphviz_png">
    <xsl:call-template name="graphviz"/>
  </xsl:if>
  </xsl:template>
  
  
  <xsl:template name="graphviz">
      <xsl:element name="img">
        <xsl:attribute name="src">
          <xsl:value-of select="$graphviz_png"/>
        </xsl:attribute>
        <xsl:if test="$graphviz_map">
          <xsl:attribute name="usemap" select="'#GVmap'"/>
        </xsl:if>
      </xsl:element>
      <xsl:if test="$graphviz_map">
        <xsl:value-of select="$graphviz_map"/>
      </xsl:if>
  </xsl:template>
  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
<!--            END  model section/diagram                     -->  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  

<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
<!--                  Start  model contents                    -->  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  

<xsl:template match="vo-urp:model" mode="contents">

<h1><xsl:value-of select="$contents_section_number"/> <a name="packages">Model contents: Packages and Types</a></h1>
<p>
The following sub-sections present all packages in the model with their types.
The packages are listed here in alphabetical order.
Each sub-section contains a description of the package and a table containing its various features.
</p>
      <h3><a name="rootpackage"/><xsl:apply-templates select="." mode="section_label"/>&bl;[root package]</h3>
          
      <table border="1" cellspacing="2" width="100%">
      <tr>
        <td class="objecttype-title" width="20%">Model</td>
        <td class="objecttype-name">
          <xsl:value-of select="identifier/utype"/>
        </td>
      </tr>

    <xsl:if test="package">
      <xsl:apply-templates select="." mode="containedpackages"/>
    </xsl:if>
    <xsl:apply-templates select="." mode="typerows"/>
    </table>
      
    <xsl:apply-templates select="." mode="types"/>
    
    <xsl:for-each select="//package">
      <xsl:sort select="identifier/utype"/>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>



  <xsl:template match="vo-urp:model|package" mode="typerows">
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
  </xsl:template>  

  <xsl:template match="vo-urp:model|package" mode="objectType">
        <tr>
            <td width="20%" class="info-title">Object types</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="objectType">
            <xsl:sort select="name"/>
<a><xsl:attribute name="href" select="concat('#',identifier/utype)"/><xsl:value-of select="name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>

  <xsl:template match="vo-urp:model|package" mode="enumeration">
        <tr>
            <td width="20%" class="info-title">Enumerations</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="enumeration">
            <xsl:sort select="name"/>
<a><xsl:attribute name="href" select="concat('#',identifier/utype)"/><xsl:value-of select="name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>

  <xsl:template match="vo-urp:model|package" mode="dataType">
        <tr>
            <td width="20%" class="info-title">Data types</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="dataType">
            <xsl:sort select="name"/>
<a><xsl:attribute name="href" select="concat('#',identifier/utype)"/><xsl:value-of select="name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>

  <xsl:template match="vo-urp:model|package" mode="primitiveType">
        <tr>
            <td width="20%" class="info-title">Primitive types</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="primitiveType">
            <xsl:sort select="name"/>
<a><xsl:attribute name="href" select="concat('#',identifier/utype)"/><xsl:value-of select="name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>


<xsl:template match="vo-urp:model|package" mode="types">
  <xsl:for-each select="objectType|dataType|enumeration|primitiveType">
    <xsl:sort select="name"/>
    <xsl:apply-templates select="."/>
  </xsl:for-each>
</xsl:template>


<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
<!--                  START  model imports                     -->  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  

<xsl:template match="vo-urp:model" mode="imports">
<hr/>  
<h1><xsl:value-of select="$modelimports_section_number"/> <a name="modelimports">Imported Models</a></h1>
<p>This section lists the external models imported by the current data model.
For each imported model we list URL, prefix used and the types that have been imported.
Of the imported types we only provide the name and utype.</p>
  <xsl:apply-templates select="import" mode="contents">
  <xsl:with-param name="section_number" select="concat($modelimports_section_number,position())"/>
    <xsl:sort select="name"/>
  </xsl:apply-templates>

</xsl:template>

  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
<!--                   END  model imports                      -->  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  
<!--                  Start package contents                   -->  
<!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->  

  
  <xsl:template match="package">
  <xsl:variable name="utype" select="identifier/utype"/>
  
<h3><a name="{$utype}"/> 
    <xsl:apply-templates select="." mode="section_label"/>&bl;package:&bl;<xsl:value-of select="name"/></h3> 

    <table border="1" cellspacing="2" width="100%">
    <xsl:apply-templates select="." mode="utype"/>
    <xsl:apply-templates select="." mode="description"/>

    <xsl:apply-templates select="." mode="parent"/>
    <xsl:if test="depends">
        <xsl:apply-templates select="." mode="depends"/>
    </xsl:if>
    <xsl:apply-templates select="." mode="typerows"/>
    
    <xsl:if test="package">
      <xsl:apply-templates select="." mode="containedpackages"/>
    </xsl:if>
    <xsl:if test="../name() = 'package'">
      <xsl:apply-templates select="." mode="parentpackage"/>
    </xsl:if>
    </table>

    <xsl:for-each select="objectType|dataType|enumeration|primitiveType">
      <xsl:sort select="name"/>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>
  

  
  <xsl:template match="package" mode="parent">
        <tr>
            <td width="20%" class="info-title">parent</td>
            <td colspan="2" class="feature-detail">
<a><xsl:attribute name="href" select="concat('#',../identifier/utype)"/><xsl:value-of select="../name"/></a>
            </td>
            </tr>
  </xsl:template>

  <xsl:template match="package" mode="depends">
        <tr>
            <td width="20%" class="info-title">depends</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="key('package',depends/utyperef)">
            <xsl:sort select="../name"/>
<a><xsl:attribute name="href" select="concat('#',utype)"/><xsl:value-of select="../name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>

  <xsl:template match="vo-urp:model|package" mode="containedpackages">
        <tr>
            <td width="20%" class="info-title">child package(s)</td>
            <td colspan="2" class="feature-detail">
            <xsl:for-each select="package">
            <xsl:sort select="name"/>
<a><xsl:attribute name="href" select="concat('#',identifier/utype)"/><xsl:value-of select="name"/></a>&bl;
            </xsl:for-each>
            </td>
            </tr>
  </xsl:template>
  
  <xsl:template match="package" mode="parentpackage">
        <tr>
            <td width="20%" class="info-title">Parent package</td>
            <td colspan="2" class="feature-detail">
<a><xsl:attribute name="href" select="concat('#',../identifier/utype)"/><xsl:value-of select="../name"/></a>&bl;
            </td>
            </tr>
  </xsl:template>

  
  <xsl:template match="import" mode="contents">
    <xsl:param name="section_number"/>
    <h2><a><xsl:attribute name="name" select="identifier/utype"/></a>
    <xsl:value-of select="concat($section_number,' ',identifier/utype)"/></h2>
    <table border="1" cellspacing="2" width="100%">
      <tr>
        <td class="objecttype-title" width="20%">Model</td>
        <td class="objecttype-name">
          <xsl:value-of select="identifier/utype"/>
        </td>
      </tr>
    <tr><td width="30%" class="info-title">url</td><td><a><xsl:attribute name="href" select="url"/><xsl:value-of select="url"/></a></td></tr>
    <tr><td width="30%" class="info-title">documentation url</td><td><a><xsl:attribute name="href" select="documentationURL"/><xsl:value-of select="documentationURL"/></a></td></tr>
<!-- 
    <tr><td width="30%" class="info-title">description</td><td><xsl:value-of select="description"/></td></tr>
 -->
    <xsl:if test="objectType">
      <xsl:call-template name="ImportedTypeHeader">
        <xsl:with-param name="title" select="'Object Types'"/>
      </xsl:call-template>
    <xsl:apply-templates select="objectType" mode="ImportedType">
    <xsl:with-param name="rooturl" select="documentationURL"/>
    <xsl:sort select="identifier/utype"/>
    </xsl:apply-templates>
    </xsl:if>
    <xsl:if test="dataType">
      <xsl:call-template name="ImportedTypeHeader">
        <xsl:with-param name="title" select="'Datatypes'"/>
      </xsl:call-template>
    <xsl:apply-templates select="dataType" mode="ImportedType">
    <xsl:with-param name="rooturl" select="documentationURL"/>
    <xsl:sort select="identifier/utype"/>
    </xsl:apply-templates>
    </xsl:if>
    <xsl:if test="enumeration">
      <xsl:call-template name="ImportedTypeHeader">
        <xsl:with-param name="title" select="'Enumerations'"/>
      </xsl:call-template>
    <xsl:apply-templates select="enumeration" mode="ImportedType">
    <xsl:with-param name="rooturl" select="documentationURL"/>
    <xsl:sort select="identifier/utype"/>
    </xsl:apply-templates>
    </xsl:if>
    <xsl:if test="primitiveType">
      <xsl:call-template name="ImportedTypeHeader">
        <xsl:with-param name="title" select="'Primitive Types'"/>
      </xsl:call-template>
      <xsl:apply-templates select="primitiveType" mode="ImportedType">
        <xsl:with-param name="rooturl" select="documentationURL"/>
        <xsl:sort select="identifier/utype"/>
      </xsl:apply-templates>
    </xsl:if>
      </table>
  </xsl:template>

  <xsl:template name="ImportedTypeHeader">
  <xsl:param name="title"/>
    <tr>
     <td colspan="2" class="info-title"><xsl:value-of select="$title"/></td>
    </tr>
    <tr>
        <td class="feature-heading" width="30%">name</td>
        <td class="feature-heading" width="70%">utype</td>
    </tr>
  </xsl:template>

  <xsl:template match="objectType|dataType|enumeration|primitiveType" mode="ImportedType">
  <xsl:param name="rooturl"/>
      <tr><td><a><xsl:attribute name="name" select="identifier/utype"/></a><xsl:value-of select="name"/></td>
      <td><a><xsl:attribute name="href" select="concat($rooturl,'#',identifier/utype)"/><xsl:value-of select="identifier/utype"/></a></td>
      </tr>
  </xsl:template>



  <xsl:template match="objectType|dataType" >
    <xsl:variable name="utype" select="identifier/utype"/>

    <h3><a name="{$utype}"/><xsl:apply-templates select="." mode="section_label"/>&bl;<xsl:value-of select="concat(name(),': ',name)"/></h3>
    <div align="center">
    <table border="1" width="100%" cellspacing="2">
    <xsl:apply-templates select="." mode="utype"/>
    <xsl:apply-templates select="." mode="description"/>
    <tr>
    <td colspan="2" >
    <table width="100%" cellpadding="0" cellspacing="0" border="0">

    <tr>
        <td colspan="2" bgcolor="#cacaca">
        <table width="100%" border="0" cellpadding="3" cellspacing="1">
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
          <xsl:with-param name="title" select="'attributes'"/>
        </xsl:call-template>
        <xsl:apply-templates select="attribute">
          <!-- <xsl:sort select="name"/> -->
        </xsl:apply-templates>
        </xsl:if>       
        
        <xsl:if test="reference">
        <xsl:call-template name="feature-rows">
          <xsl:with-param name="title" select="'references'"/>
        </xsl:call-template>
        <xsl:apply-templates select="reference">
          <xsl:sort select="name"/>
        </xsl:apply-templates>
        </xsl:if>       

        <xsl:if test="collection">
        <xsl:call-template name="feature-rows">
          <xsl:with-param name="title" select="'collections'"/>
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
    <xsl:variable name="package" select="key('package',../identifier/utype)"/>
  <xsl:if test="$package">
        <tr>
            <td width="20%" class="info-title">package</td>
            <td colspan="3" class="feature-detail">
<a><xsl:attribute name="href" select="concat('#',$package/utype)"/><xsl:value-of select="$package/../name"/></a>
            </td>
            </tr>
            </xsl:if>
  </xsl:template>



  <xsl:template match="objectType|dataType|enumeration|primitiveType" mode="subclasses">
    <xsl:variable name="utype" select="identifier/utype"/>
    <xsl:if test="//extends[type/utyperef = $utype]">
          <tr>
            <td class="info-title">Subclasses</td>
            <td class="feature-detail" colspan="3">
       <xsl:for-each select="key('element',//extends[type/utyperef = $utype]/../identifier/utype)">
          <xsl:sort select="../name"/>
 <a><xsl:attribute name="href" select="concat('#',utype)"/><xsl:value-of select="../name"/></a>&bl;
        </xsl:for-each>
            </td>
          </tr>
    </xsl:if>
  </xsl:template>


  <xsl:template match="objectType" mode="referrer">
    <xsl:variable name="utype" select="identifier/utype"/>
    <xsl:if test="referrer">
          <tr>
            <td class="info-title">Referrers</td>
            <td class="feature-detail" colspan="3">
       <xsl:for-each select="//reference[datatype/utyperef = $utype]/..">
          <xsl:sort select="name"/>
 <a><xsl:attribute name="href" select="concat('#',identifier/utype)"/><xsl:value-of select="name"/></a>&bl;
        </xsl:for-each>
            </td>
          </tr>
    </xsl:if>
  </xsl:template>




  <xsl:template match="objectType|dataType|enumeration|primitiveType" mode="extends">
    <xsl:variable name="baseclass" select="key('element',extends/type/utyperef)"/>
        <tr>
            <td width="20%" class="info-title">extends</td>
            <td colspan="3" class="feature-detail">
<a><xsl:attribute name="href" select="concat('#',extends/type/utyperef)"/><xsl:value-of select="$baseclass/../name"/></a>
            </td>
            </tr>
  </xsl:template>


  <xsl:template match="objectType" mode="container">
    <xsl:variable name="collection" select="key('element',container/collectionref/utyperef)/.."/>
    <xsl:variable name="container" select="$collection/.."/>
        <tr>
            <td width="20%" class="info-title">container</td>
            <td colspan="3" class="feature-detail">
<a><xsl:attribute name="href" select="concat('#',$container/identifier/utype)"/><xsl:value-of select="$container/name"/></a>.<a><xsl:attribute name="href" select="concat('#',$collection/identifier/utype)"/><xsl:value-of select="$collection/name"/></a>
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


  <xsl:template match="*" mode="description">
    <xsl:param name="colspan" select="'1'"/>
        <tr><td  class="info-title">description</td><td class="feature-detail" colspan="{$colspan}">
          <xsl:choose>
            <xsl:when test="description">
            <xsl:value-of select="description"/>
          </xsl:when>
          <xsl:otherwise>[TODO add description!]</xsl:otherwise>
        </xsl:choose>
      </td></tr>
  </xsl:template>  
  
  <xsl:template match="*" mode="utype">
    <xsl:param name="colspan" select="'1'"/>
      <tr>
        <td class="objecttype-title" width="20%">utype</td>
        <td class="objecttype-name" colspan="{$colspan}">
          <xsl:value-of select="identifier/utype"/>
        </td>
      </tr>
  </xsl:template>  
  
  <xsl:template match="enumeration">
    <xsl:param name="section_number"/>
    <xsl:variable name="utype" select="identifier/utype"/>
    <h3><a name="{$utype}"/><xsl:apply-templates select="." mode="section_label"/>&bl;<xsl:value-of select="concat(name(),': ',name)"/></h3>
    <table border="1" width="100%" cellspacing="2">
    <xsl:apply-templates select="." mode="utype">
    <xsl:with-param name="colspan" select="'2'"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="." mode="description">
    <xsl:with-param name="colspan" select="'2'"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="." mode="package"/>
        <tr>
        <td colspan="3" class="info-title" align="center">literals</td>
    </tr>
    <tr>
        <td class="feature-heading" width="25%">name</td>
        <td class="feature-heading" width="25%">feature</td>
        <td class="feature-heading" width="50%">value</td>
    </tr>
        <xsl:apply-templates select="literal"/>
<!-- 
        </table>
        </td>
    </tr>
     </table>
    </td>
    </tr>
-->
    </table >
    <br/>
  </xsl:template>
  
  
  <xsl:template match="primitiveType">
    <xsl:variable name="utype" select="identifier/utype"/>
    <h3><a name="{$utype}"/><xsl:apply-templates select="." mode="section_label"/>&bl;<xsl:value-of select="concat(name(),': ',name)"/></h3>
   <table border="1" width="100%" cellspacing="2">
    <xsl:apply-templates select="." mode="utype"/>
    <xsl:apply-templates select="." mode="description"/>
    <xsl:apply-templates select="." mode="package"/>
    </table>
    <br/>
  </xsl:template>
  



    
  <xsl:template match="literal" >
    <tr>
        <td class="feature-detail" rowspan="2" valign="top">
        <a><xsl:attribute name="name" select="identifier/utype"/></a><xsl:value-of select="value"/>
        </td>
        <td class="feature-heading">utype</td><td class="feature-detail"><xsl:value-of select="identifier/utype"/></td></tr>
        <tr><td class="feature-heading">description</td><td class="feature-detail">
      <xsl:choose>
<xsl:when test="description">
    <xsl:value-of select="description"/>
</xsl:when>
<xsl:otherwise>TBD</xsl:otherwise>
      </xsl:choose>
        </td>
    </tr>
  </xsl:template>
  
     

    
  <xsl:template match="attribute|reference|collection" >
  <xsl:variable name="utype">
    <xsl:value-of select="identifier/utype"/>
  </xsl:variable>
    <tr>
        <td>
          <xsl:attribute name="class" select="'feature-detail'"/>
          <xsl:attribute name="valign" select="'top'"/>
          <xsl:attribute name="rowspan">
        <xsl:choose>
        <xsl:when test="skosconcept or name() = 'collection'">
          <xsl:value-of select="'5'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'4'"/>
        </xsl:otherwise>
        </xsl:choose> 
          </xsl:attribute>        
        <a><xsl:attribute name="name" select="$utype"/></a>
        <b><xsl:value-of select="name"/></b>
        <xsl:if test="subsets">
        <xsl:variable name="prop" select="key('element',subsets/utyperef)"/>
        <br/>{subsets <a><xsl:attribute name="href" select="concat('#',subsets/utyperef)"/>
        <xsl:value-of select="concat($prop/../../name,':',$prop/../name)"/></a> } 
        </xsl:if>
        </td>
        <td class="feature-heading">type</td>
        <td class="feature-detail" >
        <a><xsl:attribute name="href" select="concat('#',datatype/utyperef)"/> <xsl:apply-templates select="datatype/utyperef" mode="classifier"/></a>
        </td>
     </tr>
     <xsl:if test="skosconcept">
     <tr>
        <td class="feature-heading">&lt;&lt;skosconcept&gt;&gt;</td>
        <td class="feature-detail" >
        Broadest SKOS concept:<br/> 
        <a><xsl:attribute name="href" select="skosconcept/broadestSKOSConcept"/><xsl:attribute name="target" select="'_blank'"/>
        <xsl:value-of select="skosconcept/broadestSKOSConcept"/></a>
        <xsl:if test="skosconcept/vocabularyURI">
        <br/>Vocabulary URI:<br/>  
        <xsl:for-each select="skosconcept/vocabularyURI">
        <xsl:if test="position() > 1"><br/></xsl:if>
        <a><xsl:attribute name="href" select="."/><xsl:attribute name="target" select="'_blank'"/>
        <xsl:value-of select="."/></a>
        </xsl:for-each>
        </xsl:if>
        </td>
     </tr>
     </xsl:if>
     <tr>
        <td class="feature-heading">utype(s)</td>
        <td class="feature-detail"><xsl:value-of select="$utype"/>
          <xsl:if test="otherutype">
            <xsl:value-of select="concat(';',otherutype)"/>
          </xsl:if>
        

<!-- check whether the datatype is a structured, then create hierarchy of possible UTYPEs  -->
        <xsl:if test="name()='attribute' and key('element',datatype/utyperef)/../attribute"> 
          <br/>[datatype is structured, utype can be extended]         
        </xsl:if>
        </td>
      </tr>
      <tr>
        <td class="feature-heading">cardinality</td>
        <td class="feature-detail">
        <xsl:value-of select="multiplicity"/>
        </td>
      </tr>
      <xsl:if test="name() = 'collection'">
      <tr>
        <td class="feature-heading">isOrdered</td>
        <td class="feature-detail">
        <xsl:choose>
          <xsl:when test="isOrdered">
        <xsl:value-of select="isOrdered"/>
          </xsl:when>
          <xsl:otherwise>
                  <xsl:value-of select="'false'"/>
          </xsl:otherwise>
        </xsl:choose>
        </td>
      </tr>
      </xsl:if>
      <tr>
        <td class="feature-heading">description</td>
        <td class="feature-detail">
        <xsl:choose><xsl:when test="description"><xsl:value-of select="description"/></xsl:when>
        <xsl:otherwise>TBD</xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
  </xsl:template>


    
    
  <xsl:template match="datatype/utyperef" mode="classifier">
    <xsl:variable name="type" select="key('element',.)"/>
    <xsl:value-of select="$type/../name"/>
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





  <xsl:template match="vo-urp:model" mode="utypes">
  <h1><xsl:value-of select="$utypes_section_number"/> <a name="utypes">UTYPEs</a></h1>  
The following table shows all UTYPEs for this data model.
It is ordered alphabetically and the UTYPEs are hyper-linked to the location
in the document where the actual element is fully defined.
  
  <table style="border-style:solid;border-width:1px;" border="1" cellspacing="0" cellpadding="0"> 
  <tr><td class="feature-heading">UTYPE</td>
        <td class="feature-heading">feature type</td>
        <td class="feature-heading">description</td>
  </tr>  
  <xsl:apply-templates select="." mode="utypeslist"/>
  </table>
  </xsl:template>




  <xsl:template match="package|vo-urp:model" mode="utypeslist">
  <xsl:variable name="utype_package" select="identifier/utype"/>
  <tr><td class="feature-detail"><a href="#{$utype_package}"><xsl:value-of select="$utype_package"/></a></td>
      <td class="feature-detail"><xsl:value-of select="name()"/></td>
  <td class="feature-detail"><xsl:value-of select="description"/></td></tr>  
  <xsl:for-each select="objectType|dataType|enumeration|primitiveType">
      <xsl:sort select="name"/>
    <xsl:variable name="utype_class" select="identifier/utype"/>
      <tr><td class="feature-detail"><a href="#{$utype_class}"><xsl:value-of select="$utype_class"/></a></td>
            <td class="feature-detail"><xsl:value-of select="name()"/></td>
      <td class="feature-detail"><xsl:value-of select="description"/></td></tr>  
      <xsl:for-each select="attribute|reference|collection|literal">
          <xsl:sort select="name"/>
        <xsl:variable name="utype" select="identifier/utype"/>
          <tr><td class="feature-detail"><a href="#{$utype}"><xsl:value-of select="$utype"/></a></td>
                <td class="feature-detail"><xsl:value-of select="name()"/></td>
          <td class="feature-detail"><xsl:value-of select="description"/></td></tr>  
      </xsl:for-each>
  </xsl:for-each>
    <xsl:apply-templates select="package" mode="utypeslist">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
  </xsl:template>

 
  
</xsl:stylesheet>
