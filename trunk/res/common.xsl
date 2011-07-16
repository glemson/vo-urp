<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0" 
                xmlns:xmi="http://schema.omg.org/spec/XMI/2.1"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:uml="http://schema.omg.org/spec/UML/2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- 
  This XSLT script contains common xsl:templates used by other XSLT scripts.
-->


  <xsl:template name="upperFirst">
    <xsl:param name="val"/>

    <xsl:variable name="prem" select="substring($val,1,1)"/>
    <xsl:variable name="first" select="upper-case($prem)"/>
    <xsl:variable name="end" select="substring($val,2,string-length($val)-1)"/>
    <xsl:value-of select="concat($first,$end)"/>
  </xsl:template>




  <xsl:template name="constant">
    <xsl:param name="text"/>

    <xsl:variable name="v" select="replace($text, '-', '_')"/>
    <xsl:variable name="v0" select="replace($v, '0', 'ZERO')"/>
    <xsl:variable name="v1" select="replace($v0,   '1', 'ONE')"/>
    <xsl:variable name="v2" select="replace($v1,   '2', 'TWO')"/>
    <xsl:variable name="v3" select="replace($v2,   '3', 'THREE')"/>
    <xsl:variable name="v4" select="replace($v3,   '4', 'FOUR')"/>
    <xsl:variable name="v5" select="replace($v4,   '5', 'FIVE')"/>
    <xsl:variable name="v6" select="replace($v5,   '-', '_')"/>
    <xsl:value-of select="translate(upper-case($v6),' .','___N')"/>
  </xsl:template>




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



<!-- Return a javatype for the type indicated with the given xmiid and having the specified length.
The length is used to decide on which numerical type to use. It is interpreted as number of bytes. -->
  <xsl:template name="JavaType">
    <xsl:param name="xmiid"/>
    <xsl:param name="length" select="''"/> 
<!-- 
    <xsl:variable name="length">
      <xsl:choose>
        <xsl:when test="$constraints/length">
          <xsl:value-of select="$constraints/length"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'-1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
 -->
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
<!-- 
        <xsl:choose>
          <xsl:when test="$type/name = 'boolean'">Boolean</xsl:when>
          <xsl:when test="$type/name = 'short'">Short</xsl:when>
          <xsl:when test="$type/name = 'integer'">Integer</xsl:when>
          <xsl:when test="$type/name = 'long'">Long</xsl:when>
          <xsl:when test="$type/name = 'float'">Float</xsl:when>
          <xsl:when test="$type/name = 'real'">Double</xsl:when>
          <xsl:when test="$type/name = 'double'">Double</xsl:when>
          <xsl:when test="$type/name = 'datetime'">Date</xsl:when>
          <xsl:when test="$type/name = 'string'">String</xsl:when>
          <xsl:otherwise>String</xsl:otherwise>
        </xsl:choose>
-->
        <xsl:choose>
      <xsl:when test="$type/name = 'boolean'">Boolean</xsl:when>
      <xsl:when test="$type/name = 'integer' or $type/name = 'long' or $type/name = 'short'">
	      <xsl:choose> <!-- all integral types can represent 2, 4 or (default) 8 byte integers-->
  		    <xsl:when test="$length = '2'">Short</xsl:when>
      		<xsl:when test="$length = '4'">Integer</xsl:when>
      		<xsl:otherwise>Long</xsl:otherwise>
      	</xsl:choose>
      </xsl:when>
      <xsl:when test="$type/name = 'real' or $type/name = 'double' or $type/name = 'float'">
	      <xsl:choose> <!-- all floating types can represent 4 or (default) 8 byte float -->
      		<xsl:when test="$length = '4'">Float</xsl:when>
      		<xsl:otherwise>Double</xsl:otherwise>
      	</xsl:choose>
      </xsl:when>
      <xsl:when test="$type/name = 'datetime'">Date</xsl:when>
      <xsl:otherwise>String</xsl:otherwise>
    </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="val">
          <xsl:call-template name="upperFirst">
            <xsl:with-param name="val" select="$type/name"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="$val"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>




  <!-- Only counts whether this class or subclass is contained, not if a base class is contained -->
  <xsl:template match="*[@xmi:type='uml:Class']" mode="testrootelements">
    <xsl:param name="count" select="0"/>
    <xsl:variable name="xmiid" select="@xmi:id"/>

    <xsl:choose>
      <xsl:when test="//ownedMember/ownedAttribute[@xmi:type='uml:Property' and @association and @aggregation='composite' and @type = $xmiid]">
        <xsl:value-of select="number($count)+1"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="childcount" >
          <xsl:choose>
            <xsl:when test="//ownedMember[@xmi:type='uml:Class' and generalization/@general = $xmiid]">
            <xsl:for-each select="//ownedMember[@xmi:type='uml:Class' and generalization/@general = $xmiid]">
              <xsl:apply-templates select="." mode="testrootelements">
                <xsl:with-param name="count" select="$count"/>
              </xsl:apply-templates>
              </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="0"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="number($count)+number($childcount)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>




  <xsl:template name="findRootId">
    <xsl:param name="xmiid"/>
    <xsl:variable name="class" select="key('classid',$xmiid)"/>
    <xsl:choose>
      <xsl:when test="$class/generalization">
        <xsl:call-template name="findRootId">
          <xsl:with-param name="xmiid" select="$class/generalization/@general"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$xmiid"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>




  <xsl:template match="*[@xmi:type='uml:Class']" mode="dummy">
    <xsl:message><xsl:value-of select="@name"/></xsl:message>
    <xsl:value-of select="'0'"/>
  </xsl:template>


</xsl:stylesheet>