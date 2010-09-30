<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  
  <xsl:import href="utype.xsl"/>

<!-- possible values: postgres, mssqlserver, mysql, sql92 (default) -->  
  <xsl:param name="vendor"/>

<!-- Define parameters/variables that can be reused in this script an in others using it (JPA) -->
  <!-- next two might also be parameters, or obtained from a config file -->
  <xsl:variable name="defaultVarcharLength" select="'255'"/>
  <xsl:variable name="unboundedstringtype" select="'TEXT'"/> <!-- SQLServer specific but supported by postgres -->

  <xsl:variable name="discriminatorColumnName" select="'DTYPE'"/>
  <xsl:variable name="discriminatorColumnLength" select="'32'"/>

  <xsl:variable name="publisherDIDColumnName" select="'publisherDID'"/>
  <xsl:variable name="publisherDIDColumnLength" select="'256'"/>

  <xsl:variable name="discriminatorColumnType"> VARCHAR(<xsl:value-of select="$discriminatorColumnLength"/>) </xsl:variable>  
  
  <xsl:variable name="primaryKeyColumnName" select="'ID'"/>
  <!-- Auto Generated Primary Key -->
  <xsl:variable name="IDGentype">
    <xsl:choose>
      <xsl:when test="$vendor = 'mssqlserver'">BIGINT IDENTITY NOT NULL</xsl:when>
      <xsl:when test="$vendor = 'postgres'">SERIAL8</xsl:when>
      <xsl:when test="$vendor = 'mysql'">BIGINT PRIMARY KEY auto_increment NOT NULL</xsl:when>
      <xsl:otherwise>NUMERIC(18)</xsl:otherwise><!-- use SQL92 standard -->
    </xsl:choose>
  </xsl:variable>
  
  <!-- Base Type for Primary Key references -->
  <xsl:variable name="IDDatatype">
    <xsl:choose>
      <xsl:when test="$vendor = 'mssqlserver'">BIGINT</xsl:when>
      <xsl:when test="$vendor = 'postgres'">BIGINT</xsl:when>
      <xsl:when test="$vendor = 'mysql'">BIGINT</xsl:when> <!-- MySQL does not get a separate priaray key command -->
      <xsl:otherwise>NUMERIC(18)</xsl:otherwise><!-- use SQL92 standard -->
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="containerColumnName" select="'containerId'"/>



  <!-- for now no special camelcase 2 '_' transformation -->
  <xsl:template match="objectType" mode="tableName">
    <xsl:value-of select="concat('t_',name)"/>
  </xsl:template>



  <!-- for now no special camelcase 2 '_' transformation -->
  <xsl:template match="objectType" mode="viewName">
    <xsl:value-of select="name"/>
  </xsl:template>



<!-- 
Return the column name for a single attribute, assumed to be primitive 
Currently nothing special done, simply returns the name of the attribute.
-->
  <xsl:template match="attribute" mode="columnName">
    <xsl:value-of select="name"/>
  </xsl:template>



<!-- 
Return a node-set of columns for a single attribute, which may be structured.
when multiple columns, provide the JPA attribute override information
!!!!!
NOTE this template must be kept in synch with the <<match="attribute" mode="nested">
template in jpa.xsl 
!!!!!
-->
  <xsl:template match="attribute" mode="columns">
    <xsl:param name="prefix"/>
    <xsl:param name="utypeprefix"/>
    <xsl:param name="nullable" select="'false'" />
<!--  <xsl:param name="subtypeid"/>   --> 

    <xsl:variable name="typeid" select="datatype/@xmiidref"/>
    <xsl:variable name="utype">
      <xsl:value-of select="concat($utypeprefix,'.',name)"/>
    </xsl:variable>

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
        <xsl:variable name="isnullable">
      <xsl:choose>
        <xsl:when test="$nullable='true'">true</xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="multiplicity = '1'">false</xsl:when>
            <xsl:otherwise>true</xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="type" select="key('element',$typeid)"/>
    <xsl:choose>
      <xsl:when test="name($type) = 'primitiveType' or name($type) = 'enumeration'">
        <xsl:variable name="sqltype">
          <xsl:call-template name="sqltype">
            <xsl:with-param name="type" select="$type"/>
            <xsl:with-param name="constraints" select="constraints"/>
          </xsl:call-template>
        </xsl:variable>
        <column>
          <name><xsl:value-of select="$columnname"/></name>
          <type><xsl:value-of select="$type/name"/></type>
          <sqltype><xsl:value-of select="$sqltype"/></sqltype>
          <xsl:copy-of select="constraints"/>
          <xsl:copy-of select="multiplicity"/>
          <description><xsl:value-of select="description"/></description>
          <utype><xsl:value-of select="$utype"/></utype>
          <nullable><xsl:value-of select="$isnullable"/></nullable>
        </column> 
      </xsl:when>
      <xsl:when test="name($type) = 'dataType'">
        <xsl:for-each select="$type/attribute">
          <xsl:apply-templates select="." mode="columns">
            <xsl:with-param name="prefix" select="$columnname"/>
            <xsl:with-param name="utypeprefix" select="$utype"/>
            <xsl:with-param name="nullable" select="$isnullable"/>
          </xsl:apply-templates>
        </xsl:for-each>
        
    		<xsl:if test="key('element',//extends[@xmiidref = $typeid]/../@xmiid)">
		      <xsl:message>**** WARNING *** Found subclasses of datatype <xsl:value-of select="name"/>. VO-URP does currently not properly support such patterns properly.</xsl:message>
		    </xsl:if>
      </xsl:when>
    </xsl:choose>
    
  </xsl:template>




  <!-- return the column name a reference is mapped to -->
  <xsl:template match="reference" mode="columnName">
      <xsl:value-of select="concat(name,'Id')"/>
  </xsl:template>


  
  <!-- We MUST have lengths for (var)char datatypes, if not provided use a default.
  For numerical datatypes we will interpret a possible length constraint as size in bytes.
  Default there is 8 bytes-->
  <xsl:template name="sqltype">
    <xsl:param name="type"/>
    <xsl:param name="constraints"/>

    <xsl:variable name="length">
      <xsl:choose>
        <xsl:when test="$constraints/length">
          <xsl:value-of select="$constraints/length"/>
          <!-- 
          <xsl:message>type = <xsl:value-of select="$type/name"/> length = $constraints/length</xsl:message>
           --> 
        </xsl:when>
        <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="$constraints/maxLength">
          <xsl:value-of select="$constraints/maxLength"/>
<!-- 
          <xsl:message>type = <xsl:value-of select="$type/name"/> length = <xsl:value-of select="$constraints/maxLength"/></xsl:message>
 --> 
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$defaultVarcharLength"/>
        </xsl:otherwise>
      </xsl:choose>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
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
      <xsl:when test="$type/name = 'boolean'">
        <xsl:choose>
          <xsl:when test="$vendor = 'mssqlserver'">BIT</xsl:when>
          <xsl:when test="$vendor = 'postgres'">BOOLEAN</xsl:when>
          <xsl:when test="$vendor = 'mysql'">BOOLEAN</xsl:when>
          <xsl:otherwise>[VENDOR_NOT_SUPPORTED]</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$type/name = 'short'">SMALLINT</xsl:when> <!-- 2010-06-17 GL: short does not exists in Profile (yet) -->
      <xsl:when test="$type/name = 'integer'">
	      <xsl:choose>
  		    <xsl:when test="$length = '2'">SMALLINT</xsl:when>
      		<xsl:when test="$length = '8'">BIGINT</xsl:when>
      		<xsl:otherwise>INTEGER</xsl:otherwise>
      	</xsl:choose>
      </xsl:when>
      <xsl:when test="$type/name = 'long'">BIGINT</xsl:when><!-- 2010-06-17 GL: long does not exists in Profile (yet) -->
      <xsl:when test="$type/name = 'float'">FLOAT</xsl:when><!-- 2010-06-17 GL: float does not exists in Profile (yet) -->
      <xsl:when test="$type/name = 'real' or $type/name = 'double' or $type/name = 'float'">
	      <xsl:choose>
      		<xsl:when test="$length = '4'">REAL</xsl:when> <!--  only valid length taht does not produce a double precision -->
      		<xsl:otherwise>            
      		  <xsl:choose>
              <xsl:when test="$vendor = 'mssqlserver'">FLOAT</xsl:when>
              <xsl:when test="$vendor = 'postgres'">DOUBLE PRECISION</xsl:when>
              <xsl:when test="$vendor = 'mysql'">DOUBLE</xsl:when>
              <xsl:otherwise>[VENDOR_NOT_SUPPORTED]</xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
      	</xsl:choose>
      </xsl:when>
      <xsl:when test="$type/name = 'datetime'">
        <xsl:call-template name="datetimeType"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="strlength">
          <xsl:call-template name="stringlength">
            <xsl:with-param name="constraints" select="constraints"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="number($length) &lt;= 0"><xsl:value-of select="$unboundedstringtype"/></xsl:when>
          <xsl:otherwise>VARCHAR(<xsl:value-of select="$strlength"/>)</xsl:otherwise>
      </xsl:choose></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template name="datetimeType">
    <xsl:choose>
      <xsl:when test="$vendor = 'mssqlserver'">DATETIME</xsl:when>
      <xsl:when test="$vendor = 'postgres'">TIMESTAMP</xsl:when>
      <xsl:when test="$vendor = 'mysql'">DATETIME</xsl:when>
      <xsl:otherwise>[VENDOR_NOT_SUPPORTED]</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  

<!-- constraints utility templates -->
  <xsl:template name="stringlength">
    <xsl:param name="constraints"/>
    <xsl:choose>
      <xsl:when test="$constraints">
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
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$defaultVarcharLength"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>



  
</xsl:stylesheet>