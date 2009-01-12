<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
<!ENTITY dotsep "<xsl:text>.</xsl:text>">
<!ENTITY colonsep "<xsl:text>:</xsl:text>">
<!ENTITY slashsep "<xsl:text>/</xsl:text>">
]>

<xsl:stylesheet version="2.0" 
                xmlns:xmi="http://schema.omg.org/spec/XMI/2.1"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:uml="http://schema.omg.org/spec/UML/2.0"
               	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <!--
    Templates used by XSLT scripts to derive UTYPE-s for attributes, references and collections
  -->
  
  
  
<!-- DEPRECATED was used only by old objectType's utype template-->
  <xsl:template match="model" mode="utype">
    <xsl:value-of select="name"/>
  </xsl:template>
  


<!-- DEPRECATED was used only by old objectType's utype template-->
  <xsl:template match="package" mode="utype">
    <xsl:param name="prefix"/>
    <xsl:choose>
      <xsl:when test="prefix">
        <xsl:value-of select="$prefix"/><xsl:value-of select="name"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="sep">
          <xsl:choose>
            <xsl:when test="../name() = 'package'">&slashsep;</xsl:when>
            <xsl:otherwise>&colonsep;</xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:apply-templates select=".." mode="utype"/><xsl:value-of select="$sep"/><xsl:value-of select="name"/>
      </xsl:otherwise>
    </xsl:choose>  </xsl:template>

  
<!-- DEPRECATED now use UTYPE generated for intermediate representation -->
  <xsl:template match="objectType" mode="utype_DEPRECATED">
    <xsl:param name="prefix"/>
    <xsl:choose>
      <xsl:when test="prefix">
        <xsl:value-of select="$prefix"/><xsl:value-of select="name"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select=".." mode="utype"/>&slashsep;<xsl:value-of select="name"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="objectType" mode="utype">
     <xsl:value-of select="utype"/>
  </xsl:template>


  <xsl:template match="dataType" mode="utype">
    <xsl:text/>
  </xsl:template>


 
  <!-- this template assumes one can always select=".."
  This breaks howevere for a node-set. Need an alternative solution there. -->
  <xsl:template match="attribute" mode="utype">
    <xsl:param name="prefix"/>
    
    <xsl:variable name="utype">
      <xsl:value-of select="$prefix"/>&dotsep;<xsl:value-of select="name"/>
    </xsl:variable>

    <xsl:variable name="type" select="key('element',datatype/@xmiidref)"/>
    <xsl:choose>
      <xsl:when test="$type/name() = 'primitiveType' or $type/name() = 'enumeration'">
        <xsl:value-of select="$utype"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$utype"/><br/>
        <xsl:for-each select="$type/attribute">
          <xsl:apply-templates select="." mode="utype">
            <xsl:with-param name="prefix">
              <xsl:value-of select="$utype"/>
            </xsl:with-param> 
          </xsl:apply-templates><br/>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <xsl:template match="collection" mode="utype">
    <xsl:apply-templates select=".." mode="utype"/>.<xsl:value-of select="name"/>
  </xsl:template>



  <xsl:template match="reference" mode="utype">
    <xsl:apply-templates select=".." mode="utype"/>.<xsl:value-of select="name"/>
  </xsl:template>


<!--  UTYPE templates for xmi2intermediate -->
    
  <!--
  GL 2008-10-04:
   somewhat ugly way of calling utypes, but did not quickly find more elegant solution
   -->
  <xsl:template name="intermediate_utype">
    <xsl:param name="member"/>

    <xsl:choose>
      <xsl:when test="$member/name() = 'uml:Model'">
        <xsl:value-of select="concat($member/@name,':')"/>
      </xsl:when>
      <xsl:when test="$member/@xmi:type='uml:Package' or $member/@xmi:type='uml:Class'">
        <xsl:variable name="prefix">
          <xsl:call-template name="intermediate_utype">
            <xsl:with-param name="member" select="$member/.."/>  
          </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="concat($prefix,$member/@name)"/><xsl:if test="$member/@xmi:type='uml:Package'"><xsl:text>/</xsl:text></xsl:if>
      </xsl:when>
    </xsl:choose>

  </xsl:template>





</xsl:stylesheet>