<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This XSLT script transforms a data model from our
intermediate representation to a relational database 
Data Definition Language script.

It assumes that the object-relational mapping
prescription stores full inheritance hierarchies in a single table.
THIS IS CURRENTLY NOT OUR FOCUS AND THIS STRATEGY IS NOT SUPPORTED BY US

We also generate view definitions representing each objectType.

We assume that all tables are in a single schema.
For now we assume that objectType's names are unique over the complete model.
TODO We need to check this explicitly and modify the generation if not.

 -->

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  
  <xsl:output method="text" encoding="UTF-8" indent="no" />
  
  <xsl:strip-space elements="*" />
  
  <xsl:key name="element" match="*//*" use="@xmiid"/>
  
  
  <xsl:param name="lastModifiedText"/>
  
  
  <xsl:param name="schema"/> <!--  select="'SNAP.'" -->
  
  
<!-- Define parameters/variables that can be reused in this script an in others using it (JPA) -->
  <!-- next two might also be parameters, or obtained from a config file -->
  <xsl:variable name="defaultVarcharLength" select="'256'"/>
  <xsl:variable name="unboundedstringtype" select="'text'"/> <!-- SQLServer specific -->

  <xsl:variable name="discriminatorColumnName" select="'DTYPE'"/>
  <xsl:variable name="discriminatorColumnLength" select="'128'"/>
  <xsl:variable name="discriminatorColumnType"> varchar(<xsl:value-of select="$discriminatorColumnLength"/>) </xsl:variable>  
  
  <xsl:variable name="primaryKeyColumnName" select="'ID'"/>
  
  
  
  <!-- start -->
  <xsl:template match="/">
    <xsl:apply-templates select="model"/>
  </xsl:template>
  
  
  
  
  <xsl:template match="model">
    <xsl:message>Model = <xsl:value-of select="name"></xsl:value-of></xsl:message>
    
    -- last modification date of the UML model <xsl:value-of select="$lastModifiedText"/>&cr;
    
      <xsl:apply-templates select=".//objectType"/>
    
  </xsl:template>  
  
  
  
  
  <xsl:template match="objectType" mode="hierarchical">
    <!-- generate a single table for the whole object hierarchy below the matched objectType -->
    <xsl:variable name="tableName">
      <xsl:apply-templates select="." mode="tableName"/>
    </xsl:variable>
    <xsl:variable name="containers">
    </xsl:variable>
CREATE TABLE <xsl:value-of select="$tableName"/> (
  id bigint not null 
<xsl:apply-templates select="." mode="discriminatorColumnDeclaration"/>
<xsl:apply-templates select="." mode="containers"/>
<xsl:apply-templates select="." mode="hierarchy"/>);&cr;&cr;
  </xsl:template>
  
  
  
  
  <xsl:template match="objectType" mode="containers">
    <xsl:variable name="xmiid" select="@xmiid"/>
    <xsl:if test="/model//objectType[collection/datatype/@xmiidref = $xmiid]">
      , containerId bigint not null -- <xsl:value-of select="/model//objectType[collection/datatype/@xmiidref = $xmiid]/name"/>
    </xsl:if>
    <xsl:apply-templates select="/model//objectType[extends/datatype/@xmiidref = $xmiid]" mode="containers"/>
  </xsl:template>  
  
  
  
  
  <xsl:template match="objectType" mode="hierarchy">
    <xsl:variable name="xmiid" select="@xmiid"/>
    -- fields from <xsl:value-of select="name"/>
    <xsl:apply-templates select="attribute" />
    <xsl:apply-templates select="reference" />
    <xsl:apply-templates select="/model//objectType[extends/datatype/@xmiidref = $xmiid]" mode="hierarchy"/>
  </xsl:template>
  
  
  
  
  
  <xsl:template match="objectType" mode="container">
    <xsl:variable name="xmiid" select="@xmiid"/>
    <xsl:if test="/model//objectType[collection/datatype/@xmiidref = $xmiid]">, containerId bigint not null -- <xsl:value-of select="/model//objectType[collection/datatype/@xmiidref = $xmiid]/name"/>&cr;
    </xsl:if>
  </xsl:template>
  
  
  
  
  <xsl:template match="attribute" >
    <xsl:param name="prefix"/>
    
    <xsl:variable name="columnname">
      <xsl:choose>
        <xsl:when test="$prefix">
          <xsl:value-of select="concat($prefix,'_',name)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:choose>
      <xsl:when test="datatype/@class"><xsl:apply-templates select="key('element',datatype/@xmiidref)" mode="columns"><xsl:with-param name="prefix" select="$columnname"/></xsl:apply-templates></xsl:when>
      <xsl:otherwise>
        <xsl:variable name="sqltype">
          <xsl:call-template name="sqltype">
            <xsl:with-param name="type" select="datatype/@type"/>
            <xsl:with-param name="constraints" select="constraints"/>
          </xsl:call-template>
        </xsl:variable>, <xsl:value-of select="$columnname"/>&bl;<xsl:value-of select="$sqltype"/><xsl:if test="multiplicity = '1'"> not null</xsl:if>&cr;
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>
  
  
  
  
  <!-- We need lengths for (var)char datatypes -->
  <xsl:template name="sqltype">
    <xsl:param name="type"/>
    <xsl:param name="constraints"/>
    
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
    <xsl:choose>
      <xsl:when test="$type = 'boolean'">bit</xsl:when>
      <xsl:when test="@type = 'short'">integer</xsl:when>
      <xsl:when test="$type = 'integer'">integer</xsl:when>
      <xsl:when test="$type = 'long'">bigint</xsl:when>
      <xsl:when test="@type = 'float'">float</xsl:when>
      <xsl:when test="@type = 'double'">real</xsl:when>
      <xsl:when test="@type = 'datetime'">timestamp</xsl:when>
      <xsl:otherwise>
        <xsl:variable name="length">
          <xsl:choose>
            <xsl:when test="$constraints/length">
              <xsl:value-of select="$constraints/length"/>
            </xsl:when>
            <xsl:when test="$constraints/maxLength">
              <xsl:value-of select="$constraints/maxLength"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$defaultVarcharLength"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="$length &lt;= 0"><xsl:value-of select="$unboundedstringtype"/></xsl:when>
          <xsl:otherwise>varchar(<xsl:value-of select="$length"/>)</xsl:otherwise>
      </xsl:choose></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <!-- We need lengths for (var)char datatypes -->
  <xsl:template match="*/dataType" mode="columns">
    <xsl:param name="prefix"/>
    <xsl:choose>
      <xsl:when test="not(attribute)">, <xsl:value-of select="$prefix"/> varchar(256) -- <xsl:value-of select="name"/>&cr;</xsl:when>
      <xsl:otherwise><xsl:apply-templates select="attribute"><xsl:with-param name="prefix" select="$prefix"/></xsl:apply-templates></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <!-- We need lengths for (var)char datatypes -->
  <xsl:template match="*/enumeration" mode="columns">
    <xsl:param name="prefix"/>, <xsl:value-of select="$prefix"/> varchar(256) -- <xsl:value-of select="name"/>&cr;
  </xsl:template>
  
  
  
  
  <xsl:template match="reference">
    <xsl:if test="not(subsets)">, <xsl:value-of select="name"/>Id&bl;bigint <xsl:call-template name="nullity"><xsl:with-param name="multiplicity" select="multiplicity"/></xsl:call-template>-- <xsl:value-of select="datatype/@class"/>&cr;
    </xsl:if>
  </xsl:template>
  
  
  
  
  <xsl:template name="nullity">
    <xsl:param name="multiplicity"/>
    <xsl:choose>
      <xsl:when test="$multiplicity = '1' or $multiplicity = '1..*'">not null</xsl:when>
      <xsl:otherwise>null</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <xsl:template name="combineNullity">
    <xsl:param name="first"/>
    <xsl:param name="second"/>
    <xsl:choose>
      <xsl:when test="$first = 'not null' and $second = 'not null'"><xsl:value-of select="'not null'"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="'null'"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <!-- TODO
  Add templates retrieving for a given objectType the table it is in
  for a given attribute the column(s) it is in
  for a given reference the column it is in
  for a given collection the containerId column it is in.
   -->
  
  <!-- for now no special camelcase 2 '_' transformation -->
  <xsl:template match="objectType" mode="tableName">
    <xsl:value-of select="name"/>
  </xsl:template>
  

<!-- Maybe somewhat too much indirection here? -->
  <xsl:template match="objectType" mode="PK_COLUMN">
    <xsl:value-of select="$primaryKeyColumnName"/> bigint not null
  </xsl:template>




<!-- Discriminator column templates -->
  <xsl:template match="objectType" mode="discriminatorColumnDeclaration">, <xsl:value-of select="$discriminatorColumnName"/> <xsl:value-of select="$discriminatorColumnType"/>&cr;
  </xsl:template>
  


  <xsl:template match="attribute" mode="columnName">
    <xsl:value-of select="name"/>
  </xsl:template>
  




  <xsl:template match="reference" mode="columnName">
    <xsl:value-of select="concat(name,'Id')"/>
  </xsl:template>
      
</xsl:stylesheet>
