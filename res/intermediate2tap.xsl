<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This XSLT script transforms a data model from our
intermediate representation to a VOTable describing the metadata
of a TAP-like service. 

At the current time TAP does not exist, we make therfore certain 
assumptions on the details of its metadata description.
We take over two proposals from
http://www.ivoa.net/internal/IVOA/TableAccess/tap-v0.2.pdf, 
the VOTable based "tableset" and RDB information_schema like "tap_schema".

We also generate a VODataService-like document as suggested in the TAP/QL draft in
http://www.ivoa.net/internal/IVOA/TableAccess/TAP-QL-0.1.pdf.

In all cases we use the View-s that are generated in intermediate2ddl as 
the public contents of the database for the UML data model.

 -->

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
<!ENTITY dot "<xsl:text>.</xsl:text>">
]>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
								xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:vot="http://www.ivoa.net/xml/VOTable/v1.1"
                xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0" 
                xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0" 
                xmlns:vs="http://www.ivoa.net/xml/VODataService/v1.1" 
                xmlns:stc="http://www.ivoa.net/xml/STC/stc-v1.30.xsd" 
                xmlns:xlink="http://www.w3.org/1999/xlink" 
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  

  <xsl:import href="common-ddl.xsl"/>
  <xsl:import href="intermediate2ddl.xsl"/>
  <xsl:import href="utype.xsl"/>

  <xsl:output method="xml"  version="1.0" encoding="UTF-8" indent="yes" />
  <xsl:output name="text-format" method="text" encoding="UTF-8" indent="no" />
  <xsl:strip-space elements="*" />
  
  <xsl:key name="element" match="*" use="@xmiid"/>
  
  <xsl:param name="mode" select='vodataservice'/> <!-- tap_schema, votable, tableset vodataservice -->

  <xsl:param name="vendor" select="sql92"/> <!--  -->
  <xsl:param name="target_database"/> <!-- name of schema into which the tables are created -->
  <xsl:param name="target_schema" /> <!-- name of schema into which the tables are created -->

  <xsl:param name="project_name" /> <!--  -->

  <xsl:variable name="tap_tableset_file" select="concat($project_name,'_tap_tableset.xml')"/>
  <xsl:variable name="tap_vodataservice_file" select="concat($project_name,'_tap_vodataservice.xml')"/>
  <xsl:variable name="tap_votable_file" select="concat($project_name,'_votable.xml')"/>




  <xsl:variable name="target_database_prefix">
    <xsl:value-of select="normalize-space($target_database)"/><xsl:if test="string-length(normalize-space($target_database)) > 0">&dot;</xsl:if>
  </xsl:variable>
  <xsl:variable name="target_schema_prefix">
    <xsl:value-of select="normalize-space($target_schema)"/><xsl:if test="string-length(normalize-space($target_schema)) > 0">&dot;</xsl:if>
  </xsl:variable>     
  <xsl:variable name="target_database_schema_prefix">
    <xsl:choose>
      <xsl:when test="string-length(normalize-space($target_database)) > 0">
        <xsl:value-of select="normalize-space($target_database)"/>&dot;<xsl:value-of select="normalize-space($target_schema)"/>&dot;
      </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="string-length(normalize-space($target_schema)) > 0">
              <xsl:value-of select="normalize-space($target_schema)"/>&dot;
            </xsl:when>
            <xsl:otherwise>
              <xsl:text/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:variable>

  <xsl:param name="lastModifiedText"/>
  
  <xsl:variable name="header">-- last modification date of the UML model <xsl:value-of select="$lastModifiedText"/>&cr;</xsl:variable>
  
  <xsl:variable name="votablenamespace" select="'http://www.ivoa.net/xml/VOTable/v1.1'"/>
  
  <xsl:variable name="commit">
      <xsl:choose>
        <xsl:when test="$vendor = 'mssqlserver'">
          <xsl:text>GO</xsl:text>
        </xsl:when>
        <xsl:when test="$vendor = 'postgres'">
          <xsl:text></xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>COMMIT</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
  

  
  <!-- start -->
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="$mode = 'tableset'">
        <xsl:apply-templates select="model" mode="tableset"/>
      </xsl:when>
      <xsl:when test="$mode = 'tap_schema'">
        <xsl:apply-templates select="model" mode="tap_schema"/>
      </xsl:when>
      <xsl:when test="$mode = 'votable'">
        <xsl:apply-templates select="model" mode="votable"/>
      </xsl:when>
      <xsl:when test="$mode = 'vodataservice'">
        <xsl:apply-templates select="model" mode="vodataservice"/>
      </xsl:when>
      <xsl:otherwise>
      UNKNOWN TAP metadata mode <xsl:value-of select="$mode"/>
      </xsl:otherwise>
    </xsl:choose>
      
  </xsl:template>
  
  
  


   
  <!-- 
  This template assumes that the public database schema is described as a collection
  of empty <TABLE>-s, with the <FIELD>-s describing the columns and no <DATA> element.
  -->
  <xsl:template match="model" mode="tableset">
    <xsl:message>Model = <xsl:value-of select="name"></xsl:value-of></xsl:message>
-- Generating TAP-tableset metadata-like VOTables for model <xsl:value-of select="name"/> and DB vendor <xsl:value-of select="$vendor"/>.
<xsl:value-of select="$header"/>
    
<!-- CREATE TAP-type 1 votable -->
    <xsl:variable name="file" select="concat($project_name,'_tap_tableset.xml')"/>
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}">

    <xsl:element name="VOTABLE">
      <xsl:attribute name="xlmns" select="$votablenamespace"/>
      <xsl:attribute name="version" select="'1.1'"/>
      <xsl:element name="RESOURCE">
        <xsl:attribute name="name" select="name"/>
        <xsl:element name="DESCRIPTION">
          <xsl:value-of select="description"/>
        </xsl:element>
        <xsl:apply-templates select="package[objectType]" mode="tableset"/>
      </xsl:element>
    </xsl:element>
    </xsl:result-document>
  </xsl:template>  
      




  <xsl:template match="package" mode="tableset">
    <xsl:param name="utypeprefix"/>
    <xsl:element name="RESOURCE">
      <xsl:attribute name="name" select="name"/>
      <xsl:element name="DESCRIPTION">
        <xsl:value-of select="description"/>
      </xsl:element>
      <xsl:apply-templates select="objectType" mode="tableset">
        <xsl:sort select="name"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="package" mode="tableset"/>
    </xsl:element>
  </xsl:template>





  <xsl:template match="objectType" mode="tableset">
    <xsl:variable name="utype">
      <xsl:apply-templates select="." mode="utype"/>
    </xsl:variable>
    
    <xsl:element name="TABLE">
      <xsl:attribute name="name">
        <xsl:apply-templates select="." mode="viewName"/>
      </xsl:attribute>
      <xsl:element name="DESCRIPTION">
        <xsl:value-of select="description"/>
      </xsl:element>

      <xsl:apply-templates select="." mode="tap_fields"/>
    </xsl:element>    
    
  </xsl:template>
    
  <xsl:template match="objectType" mode="tap_fields">
  
    <xsl:variable name="utype">
      <xsl:apply-templates select="." mode="utype"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="extends">
        <xsl:apply-templates select="key('element',extends/@xmiidref)" mode="tap_fields"/>
      </xsl:when>
      <xsl:otherwise>
          <xsl:element name="FIELD">
            <xsl:attribute name="name" select="$primaryKeyColumnName"/>
            <xsl:attribute name="datatype" select="'long'"/>
            <xsl:attribute name="ucd" select="'TBD'"/>
            <xsl:attribute name="utype" select="concat($utype,'.',$primaryKeyColumnName)"/>
            <xsl:element name="DESCRIPTION">
              <xsl:text>The unique, primary key column on this table.</xsl:text>
            </xsl:element>
          </xsl:element>
          <xsl:element name="FIELD">
            <xsl:attribute name="name" select="$discriminatorColumnName"/>
            <xsl:attribute name="datatype" select="'char'"/>
            <xsl:attribute name="width" select="$discriminatorColumnLength"/>
            <xsl:attribute name="ucd" select="'TBD'"/>
            <xsl:attribute name="utype" select="'TBD'"/>
            <xsl:element name="DESCRIPTION">
              <xsl:text>This column stores the name of the object type from the data model stored in the row.</xsl:text>
            </xsl:element>
          </xsl:element>
        </xsl:otherwise>
      </xsl:choose>

    
    <xsl:if test="container">
      <xsl:element name="FIELD">
        <xsl:attribute name="name" select="'containerId'"/>
        <xsl:attribute name="datatype" select="'long'"/>
        <xsl:attribute name="ucd" select="'TBD'"/>
        <xsl:attribute name="utype" select="concat($utype,'.CONTAINER')"/>
        <xsl:element name="DESCRIPTION">
          <xsl:text>This column is a foreign key pointing to the containing object in </xsl:text>
          <xsl:apply-templates select="key('element',container/@xmiidref)" mode="viewName"/>
        </xsl:element>
      </xsl:element>
    </xsl:if>        
    <xsl:for-each select="attribute">
      <xsl:variable name="columns">
        <xsl:apply-templates select="." mode="columns">
          <xsl:with-param name="utypeprefix" select="$utype"/>
        </xsl:apply-templates>
      </xsl:variable> 
      <xsl:for-each select="exsl:node-set($columns)/column">
      <xsl:element name="FIELD">
        <xsl:attribute name="name">
          <xsl:value-of select="name"/>
        </xsl:attribute>
        <xsl:variable name="votabletype">
          <xsl:call-template name="votabletype">
            <xsl:with-param name="type" select="type"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:attribute name="datatype" select="$votabletype"/>
        <xsl:if test="$votabletype='char'">
          <xsl:attribute name="width">
            <xsl:variable name ="length">
              <xsl:call-template name="stringlength">
                <xsl:with-param name="constraints" select="constraints"/>
              </xsl:call-template>
            </xsl:variable>
            <xsl:choose>
              <xsl:when test="number($length) &lt;= 0"><xsl:text>*</xsl:text></xsl:when>
              <xsl:otherwise><xsl:value-of select="$length"/></xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </xsl:if>
        <xsl:attribute name="utype" select="utype"/>
        <xsl:element name="DESCRIPTION" >
          <xsl:value-of select="description"/>
        </xsl:element>
      </xsl:element>
      </xsl:for-each>   
    </xsl:for-each>
    <xsl:for-each select="reference[not(subsets)]">
      <xsl:element name="FIELD">
        <xsl:attribute name="name">
          <xsl:apply-templates select="." mode="columnName"/>
        </xsl:attribute>
        <xsl:attribute name="datatype" select="'long'"/>
        <xsl:attribute name="ucd" select="'TBD'"/>
        <xsl:attribute name="utype" select="'TBD'"/>
        <xsl:element name="DESCRIPTION">
          <xsl:value-of select="description"/>&cr;
          <xsl:text>This column is a foreign key pointing to the referenced object in </xsl:text>
          <xsl:apply-templates select="key('element',datatype/@xmiidref)" mode="viewName"/><xsl:text>.
          </xsl:text>
        </xsl:element>
      </xsl:element>
    </xsl:for-each>        
  </xsl:template>




<!-- mode = VOTable  -->
<!-- 
In a single VOTable document, represents the TAP_SCHEMA tables with as content the actual tables.

-->
  <xsl:template match="model" mode="votable">
    <xsl:message>Model = <xsl:value-of select="name"></xsl:value-of></xsl:message>
-- Generating TAP-tableset metadata-like VOTables for model <xsl:value-of select="name"/> and DB vendor <xsl:value-of select="$vendor"/>.
<xsl:value-of select="$header"/>
    
<!-- CREATE TAP-type 1 votable -->
    <xsl:variable name="file" select="concat($project_name,'_votable.xml')"/>
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}">

    <xsl:element name="VOTABLE">
      <xsl:attribute name="xlmns" select="$votablenamespace"/>
      <xsl:attribute name="version" select="'1.1'"/>
      <xsl:element name="RESOURCE">
        <xsl:attribute name="name" select="name"/>
        <xsl:element name="DESCRIPTION">
          <xsl:value-of select="description"/>
        </xsl:element>
        <xsl:apply-templates select="." mode="votable.schemas"/>
        <xsl:apply-templates select="." mode="votable.tables"/>
        <xsl:apply-templates select="." mode="votable.columns"/>
      </xsl:element>
    </xsl:element>
    </xsl:result-document>
  </xsl:template>  
      
  <xsl:template match="model" mode="votable.schemas">
  <xsl:element name="TABLE">
    <xsl:attribute name="name" select="'TAP_SCHEMA.schemas'"/>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'schema_name'"/>
    <xsl:attribute name="datatype" select="'char'"/>
    <xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'description'"/>
    <xsl:attribute name="datatype" select="'char'"/>
    <xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'utype'"/>
    <xsl:attribute name="datatype" select="'char'"/>
    <xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="DATA">
      <xsl:element name="TABLEDATA">
      	<xsl:element name="TR">
      		<xsl:element name="TD">TAP_SCHEMA</xsl:element>
      		<xsl:element name="TD">The schema containing the TAP metadata tables.</xsl:element>
      		<xsl:element name="TD"/>
      	</xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
  </xsl:template>





  <xsl:template match="model" mode="votable.tables">
  <xsl:element name="TABLE">
    <xsl:attribute name="name" select="'TAP_SCHEMA.tables'"/>
    <xsl:element name="FIELD">
    	<xsl:attribute name="name" select="'schema_name'"/>
    	<xsl:attribute name="datatype" select="'char'"/>
    	<xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    	<xsl:attribute name="name" select="'table_name'"/>
    	<xsl:attribute name="datatype" select="'char'"/>
    	<xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    	<xsl:attribute name="name" select="'table_type'"/>
    	<xsl:attribute name="datatype" select="'char'"/>
    	<xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    	<xsl:attribute name="name" select="'description'"/>
    	<xsl:attribute name="datatype" select="'char'"/>
    	<xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    	<xsl:attribute name="name" select="'utype'"/>
    	<xsl:attribute name="datatype" select="'char'"/>
    	<xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="DATA">
      <xsl:element name="TABLEDATA">
      	<xsl:element name="TR">
      		<xsl:element name="TD">TAP_SCHEMA</xsl:element>
      		<xsl:element name="TD">TAP_SCHEMA.schemas</xsl:element>
      		<xsl:element name="TD">BASE_TABLE</xsl:element>
      		<xsl:element name="TD">The TAP metadata table containing all schemas.</xsl:element>
      		<xsl:element name="TD"></xsl:element>
        </xsl:element>
      	<xsl:element name="TR">
      		<xsl:element name="TD">TAP_SCHEMA</xsl:element>
      		<xsl:element name="TD">TAP_SCHEMA.tables</xsl:element>
      		<xsl:element name="TD">BASE_TABLE</xsl:element>
      		<xsl:element name="TD">The TAP metadata table containing all tables.</xsl:element>
      		<xsl:element name="TD"></xsl:element>
      	</xsl:element>
      	<xsl:element name="TR">
      		<xsl:element name="TD">TAP_SCHEMA</xsl:element>
      		<xsl:element name="TD">TAP_SCHEMA.columns</xsl:element>
      		<xsl:element name="TD">BASE_TABLE</xsl:element>
      		<xsl:element name="TD">The TAP metadata table containing all columns.</xsl:element>
      		<xsl:element name="TD"></xsl:element>
      	</xsl:element>
        <xsl:apply-templates select="//objectType" mode="votable.tables"/>
      </xsl:element>
    </xsl:element>
  </xsl:element>
  </xsl:template>




  <xsl:template match="model" mode="votable.columns">
  <xsl:element name="TABLE">
    <xsl:attribute name="name" select="'TAP_SCHEMA.columns'"/>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'column_name'"/>
    <xsl:attribute name="datatype" select="'char'"/>
    <xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'table_name'"/>
    <xsl:attribute name="datatype" select="'char'"/>
    <xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'description'"/>
    <xsl:attribute name="datatype" select="'char'"/>
    <xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'unit'"/>
    <xsl:attribute name="datatype" select="'char'"/>
    <xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'ucd'"/>
    <xsl:attribute name="datatype" select="'char'"/>
    <xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'utype'"/>
    <xsl:attribute name="datatype" select="'char'"/>
    <xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'datatype'"/>
    <xsl:attribute name="datatype" select="'char'"/>
    <xsl:attribute name="arraysize" select="'*'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'primary'"/>
    <xsl:attribute name="datatype" select="'boolean'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'indexed'"/>
    <xsl:attribute name="datatype" select="'boolean'"/>
    </xsl:element>
    <xsl:element name="FIELD">
    <xsl:attribute name="name" select="'std'"/>
    <xsl:attribute name="datatype" select="'boolean'"/>
    </xsl:element>
    <xsl:element name="DATA">
      <xsl:element name="TABLEDATA">
      <xsl:apply-templates select="//objectType" mode="votable.columns"/>
      </xsl:element>
    </xsl:element>
  </xsl:element>
  </xsl:template>



  <xsl:template match="objectType" mode="votable.tables">
    <xsl:variable name="table_name">
      <xsl:apply-templates select="." mode="viewName"/>
    </xsl:variable>
    <xsl:variable name="utype">
      <xsl:apply-templates select="." mode="utype"/>
    </xsl:variable>
  <xsl:element name="TR">
<xsl:element name="TD"><xsl:value-of select="$target_schema"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$target_database_schema_prefix"/><xsl:value-of select="$table_name"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="replace(description,&quot;'&quot;,&quot;''&quot;)"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="'VIEW'"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$utype"/></xsl:element>
</xsl:element> 
  </xsl:template>




  <xsl:template match="objectType" mode="votable.columns">
    <xsl:variable name="tableName">
      <xsl:apply-templates select="." mode="viewName"/>
    </xsl:variable>
    <xsl:variable name="utype">
      <xsl:apply-templates select="." mode="utype"/>
    </xsl:variable>
  
    <xsl:choose>
      <xsl:when test="extends">
        <xsl:apply-templates select="key('element',extends/@xmiidref)" mode="votable.columns">
          <xsl:with-param name="tableName" select="$tableName"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="votable.column">
          <xsl:with-param name="column_name"><xsl:value-of select="$primaryKeyColumnName"/></xsl:with-param>
          <xsl:with-param name="table_name"><xsl:value-of select="$tableName"/></xsl:with-param>
          <xsl:with-param name="description"><xsl:text>The unique, primary key column on this table.</xsl:text></xsl:with-param>
          <xsl:with-param name="ucd"><xsl:text>TBD</xsl:text></xsl:with-param>
          <xsl:with-param name="utype"><xsl:value-of select="concat($utype,'.',$primaryKeyColumnName)"/></xsl:with-param>
          <xsl:with-param name="datatype"><xsl:text>BIGINT</xsl:text></xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="votable.column">
          <xsl:with-param name="column_name"><xsl:value-of select="$discriminatorColumnName"/></xsl:with-param>
          <xsl:with-param name="table_name"><xsl:value-of select="$tableName"/></xsl:with-param>
          <xsl:with-param name="description"><xsl:text>This column stores the name of the object type from the data model stored in the row.</xsl:text></xsl:with-param>
          <xsl:with-param name="ucd"><xsl:text>TBD</xsl:text></xsl:with-param>
          <xsl:with-param name="utype"><xsl:text>TBD</xsl:text></xsl:with-param>
          <xsl:with-param name="datatype"><xsl:text>VARCHAR</xsl:text></xsl:with-param>
          <xsl:with-param name="arraysize"><xsl:value-of select="$discriminatorColumnLength"/></xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    
    <xsl:if test="container">
      <xsl:call-template name="votable.column">
        <xsl:with-param name="column_name"><xsl:value-of select="$containerColumnName"/></xsl:with-param>
        <xsl:with-param name="table_name"><xsl:value-of select="$tableName"/></xsl:with-param>
        <xsl:with-param name="description"><xsl:text>This column is a foreign key pointing to the containing object in </xsl:text>
          <xsl:apply-templates select="key('element',container/@xmiidref)" mode="viewName"/>
        </xsl:with-param>
        <xsl:with-param name="ucd"><xsl:text>TBD</xsl:text></xsl:with-param>
        <xsl:with-param name="utype"><xsl:value-of select="concat($utype,'.CONTAINER')"/></xsl:with-param>
        <xsl:with-param name="datatype"><xsl:text>BIGINT</xsl:text></xsl:with-param>
      </xsl:call-template>
    </xsl:if>        

    <xsl:for-each select="attribute">
      <xsl:variable name="columns">
        <xsl:apply-templates select="." mode="columns">
          <xsl:with-param name="utypeprefix" select="$utype"/>
        </xsl:apply-templates>
      </xsl:variable> 
      <xsl:for-each select="exsl:node-set($columns)/column">
        <xsl:variable name="adqltype">
          <xsl:call-template name="adqltype">
            <xsl:with-param name="type" select="type"/>
          </xsl:call-template>
        </xsl:variable>
      
        <xsl:call-template name="votable.column">
          <xsl:with-param name="column_name"><xsl:value-of select="name"/></xsl:with-param>
          <xsl:with-param name="table_name"><xsl:value-of select="$tableName"/></xsl:with-param>
          <xsl:with-param name="description"><xsl:value-of select="description"/></xsl:with-param>
          <xsl:with-param name="ucd"><xsl:text>TBD</xsl:text></xsl:with-param>
          <xsl:with-param name="utype"><xsl:value-of select="utype"/></xsl:with-param>
          <xsl:with-param name="datatype"><xsl:value-of select="$adqltype"/></xsl:with-param>
          <xsl:with-param name="arraysize" >
            <xsl:choose>
              <xsl:when test="$adqltype = 'VARCHAR'">
                <xsl:variable name ="length">
                  <xsl:call-template name="stringlength">
                    <xsl:with-param name="constraints" select="constraints"/>
                  </xsl:call-template>
                </xsl:variable>
                <xsl:choose>
                  <xsl:when test="number($length) &lt;= 0"><xsl:text>*</xsl:text></xsl:when>
                  <xsl:otherwise><xsl:value-of select="$length"/></xsl:otherwise>
                </xsl:choose>
              </xsl:when>
              <xsl:otherwise><xsl:text>1</xsl:text></xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
    </xsl:for-each>
    <xsl:for-each select="reference[not(subsets)]">
      <xsl:call-template name="votable.column">
        <xsl:with-param name="column_name"><xsl:apply-templates select="." mode="columnName"/></xsl:with-param>
        <xsl:with-param name="table_name"><xsl:value-of select="$tableName"/></xsl:with-param>
        <xsl:with-param name="description"><xsl:value-of select="description"/>&cr;
        <xsl:text>[This column is a foreign key pointing to the referenced object in </xsl:text>
        <xsl:apply-templates select="key('element',datatype/@xmiidref)" mode="viewName"/><xsl:text>].
        </xsl:text></xsl:with-param>
        <xsl:with-param name="ucd"><xsl:text>TBD</xsl:text></xsl:with-param>
        <xsl:with-param name="utype"><xsl:text>TBD</xsl:text></xsl:with-param>
        <xsl:with-param name="datatype"><xsl:text>long</xsl:text></xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>        
  
  </xsl:template>



  <xsl:template name="votable.column">
    <xsl:param name="column_name"/>
    <xsl:param name="table_name"/>
    <xsl:param name="description"/>
    <xsl:param name="unit" select="''"/>
    <xsl:param name="ucd" select="''"/>
    <xsl:param name="utype" select="''"/>
    <xsl:param name="datatype"/>
    <xsl:param name="arraysize" select="'1'"/>
    <xsl:param name="primary" select="'T'"/>
    <xsl:param name="indexed" select="'T'"/>
    <xsl:param name="std" select="'T'"/>
  <!-- TODO this can be simplieified, the INSERT INTO ... does not have to be repeated, 
  only comma-separated list of values (within () ) is required. More efficient as well. -->
<xsl:element name="TR">
<xsl:element name="TD"><xsl:value-of select="$column_name"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$target_database_schema_prefix"/><xsl:value-of select="$table_name"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="replace($description,&quot;'&quot;,&quot;''&quot;)"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$unit"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$ucd"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$utype"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$datatype"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$arraysize"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$primary"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$indexed"/></xsl:element>
<xsl:element name="TD"><xsl:value-of select="$std"/></xsl:element>
</xsl:element> 
</xsl:template>





<!-- ========================================================================================== -->
  <!--  TAP_SCHEMA stype -->

<!-- 
Generates metadata for the TAP_SCHEMA approach to metadata.
Does this by creating a result that creates tables in the TAP_SCHEMA describing the tables and columns
available to users.
Then fills these with the metadata using imsple insert statements.
-->
  <xsl:template match="model" mode="tap_schema">
        <xsl:message>Model = <xsl:value-of select="name"></xsl:value-of></xsl:message>
-- Generating TAP-tap_schema like metadata for model <xsl:value-of select="name"/> and DB vendor <xsl:value-of select="$vendor"/>.
<xsl:value-of select="$header"/>
    
    <xsl:variable name="dropfile" select="concat($project_name,'_drop_tap_schema.sql')"/>
    <xsl:message >Opening file <xsl:value-of select="$dropfile"/></xsl:message>
    <xsl:result-document href="{$dropfile}" format="text-format">
    <xsl:call-template name="drop_TAP_SCHEMA_tables"/>&cr;&cr;
    </xsl:result-document>
    
    <xsl:variable name="file" select="concat($project_name,'_create_tap_schema.sql')"/>
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}" format="text-format">
<xsl:text>-- TAP_SCHEMA definition </xsl:text>
    <xsl:call-template name="create_TAP_SCHEMA_tables"/>&cr;&cr;
<xsl:text>-- tables and column inserts per objecttype</xsl:text>&cr;&cr;
    <xsl:apply-templates select=".//objectType" mode="tap_schema"/>

<xsl:value-of select="$commit"/>

    </xsl:result-document>
  </xsl:template>


  <xsl:template name="drop_TAP_SCHEMA_tables">
<xsl:text>
DROP TABLE TAP_SCHEMA.columns;
DROP TABLE TAP_SCHEMA.tables;
DROP TABLE TAP_SCHEMA.schemas;
DROP SCHEMA TAP_SCHEMA;
</xsl:text>
<xsl:value-of select="$commit"/>
</xsl:template>





  <xsl:template name="create_TAP_SCHEMA_tables">
    <xsl:variable name="IDGentype">
    <xsl:choose>
      <xsl:when test="$vendor = 'mssqlserver'">BIGINT IDENTITY NOT NULL</xsl:when>
      <xsl:when test="$vendor = 'postgres'">SERIAL8</xsl:when>
      <xsl:otherwise>NUMERIC(18)</xsl:otherwise><!-- use SQL92 standard -->
    </xsl:choose>
  </xsl:variable>
<xsl:text>
CREATE SCHEMA TAP_SCHEMA;
</xsl:text>
<xsl:value-of select="$commit"/>
<xsl:text>
CREATE TABLE TAP_SCHEMA.schemas (
  schema_name varchar(128) not null,
  description </xsl:text><xsl:value-of select="$unboundedstringtype"/><xsl:text>,
  utype varchar(128)
);

CREATE TABLE TAP_SCHEMA.tables (
  schema_name varchar(128) not null,
  table_name varchar(128) not null,
  table_type varchar(16),
  description </xsl:text><xsl:value-of select="$unboundedstringtype"/><xsl:text>,
  utype varchar(128)
);

CREATE TABLE TAP_SCHEMA.columns (
  id </xsl:text><xsl:value-of select="$IDGentype"/><xsl:text>,
  column_name varchar(128) not null,
  table_name varchar(128) not null,
  description </xsl:text><xsl:value-of select="$unboundedstringtype"/><xsl:text>,
  unit varchar(64),
  ucd varchar(64),
  utype varchar(128),
  datatype varchar(64),
  arraysize varchar(16),
  "primary" char(1), -- assume T or F
  indexed char(1), -- assume T or F
  std char(1), -- assume T or F
  rank integer 
);
</xsl:text>
<xsl:value-of select="$commit"/>

INSERT INTO TAP_SCHEMA.schemas (schema_name,description, utype) VALUES('<xsl:value-of select="$target_database_prefix"/>TAP_SCHEMA','the schema containing the TAP metadata tables',null);

INSERT INTO TAP_SCHEMA.schemas (schema_name,description, utype) VALUES('<xsl:value-of select="$target_database_prefix"/><xsl:value-of select="$target_schema"/>','the schema containing the data model tables and views metadata tables',null);


INSERT INTO TAP_SCHEMA.tables (schema_name,table_name,table_type,description,utype)VALUES('<xsl:value-of select="$target_database_prefix"/>TAP_SCHEMA','TAP_SCHEMA.schemas','base_table','the table storing metadata about the different schemas in this TAP service',null);
INSERT INTO TAP_SCHEMA.tables (schema_name,table_name,table_type,description,utype)VALUES('<xsl:value-of select="$target_database_prefix"/>TAP_SCHEMA','TAP_SCHEMA.tables','base_table','the table storing metadata about the tables in this TAP service',null);
INSERT INTO TAP_SCHEMA.tables (schema_name,table_name,table_type,description,utype)VALUES('<xsl:value-of select="$target_database_prefix"/>TAP_SCHEMA','TAP_SCHEMA.columns','base_table','the table storing metadata about the columns in this TAP service',null);
-- TODO add TAP_SCHEMA table columns
<xsl:value-of select="$commit"/>
  </xsl:template>



  <xsl:template match="objectType" mode="tap_schema">
    <xsl:variable name="tap_name">
      <xsl:apply-templates select="." mode="viewName"/>
    </xsl:variable>
    <xsl:variable name="utype">
      <xsl:apply-templates select="." mode="utype"/>
    </xsl:variable>
    
<xsl:text>INSERT INTO TAP_SCHEMA.tables(schema_name, table_name,table_type,description,utype) values(</xsl:text>
   '<xsl:value-of select="$target_database_prefix"/><xsl:value-of select="$target_schema"/>'
,  '<xsl:value-of select="$target_database_schema_prefix"/><xsl:value-of select="$tap_name"/>'
,  'view'
,  '<xsl:value-of select="replace(description,&quot;'&quot;,&quot;''&quot;)"/>'
,  '<xsl:value-of select="$utype"/>'
);

    <xsl:apply-templates select="." mode="tap_schema_columns">
      <xsl:with-param name="tableName">
        <xsl:value-of select="$tap_name"/>
      </xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="objectType" mode="tap_schema_columns">
    <xsl:param name="tableName"/>
    <xsl:variable name="utype">
      <xsl:apply-templates select="." mode="utype"/>
    </xsl:variable>
    
    <xsl:choose>
      <xsl:when test="extends">
        <xsl:apply-templates select="key('element',extends/@xmiidref)" mode="tap_schema_columns">
          <xsl:with-param name="tableName" select="$tableName"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="insertcolumn">
          <xsl:with-param name="column_name"><xsl:value-of select="$primaryKeyColumnName"/></xsl:with-param>
          <xsl:with-param name="table_name"><xsl:value-of select="$tableName"/></xsl:with-param>
          <xsl:with-param name="description"><xsl:text>The unique, primary key column on this table.</xsl:text></xsl:with-param>
          <xsl:with-param name="ucd"><xsl:text>TBD</xsl:text></xsl:with-param>
          <xsl:with-param name="utype"><xsl:value-of select="concat($utype,'.',$primaryKeyColumnName)"/></xsl:with-param>
          <xsl:with-param name="datatype"><xsl:text>BIGINT</xsl:text></xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="insertcolumn">
          <xsl:with-param name="column_name"><xsl:value-of select="$discriminatorColumnName"/></xsl:with-param>
          <xsl:with-param name="table_name"><xsl:value-of select="$tableName"/></xsl:with-param>
          <xsl:with-param name="description"><xsl:text>This column stores the name of the object type from the data model stored in the row.</xsl:text></xsl:with-param>
          <xsl:with-param name="ucd"><xsl:text>TBD</xsl:text></xsl:with-param>
          <xsl:with-param name="utype"><xsl:text>TBD</xsl:text></xsl:with-param>
          <xsl:with-param name="datatype"><xsl:text>VARCHAR</xsl:text></xsl:with-param>
          <xsl:with-param name="arraysize"><xsl:value-of select="$discriminatorColumnLength"/></xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    
    <xsl:if test="container">
      <xsl:call-template name="insertcolumn">
        <xsl:with-param name="column_name"><xsl:value-of select="$containerColumnName"/></xsl:with-param>
        <xsl:with-param name="table_name"><xsl:value-of select="$tableName"/></xsl:with-param>
        <xsl:with-param name="description"><xsl:text>This column is a foreign key pointing to the containing object in </xsl:text>
          <xsl:apply-templates select="key('element',container/@xmiidref)" mode="viewName"/>
        </xsl:with-param>
        <xsl:with-param name="ucd"><xsl:text>TBD</xsl:text></xsl:with-param>
        <xsl:with-param name="utype"><xsl:value-of select="concat($utype,'.CONTAINER')"/></xsl:with-param>
        <xsl:with-param name="datatype"><xsl:text>BIGINT</xsl:text></xsl:with-param>
      </xsl:call-template>
    </xsl:if>        

    <xsl:for-each select="attribute">
      <xsl:variable name="columns">
        <xsl:apply-templates select="." mode="columns">
          <xsl:with-param name="utypeprefix" select="$utype"/>
        </xsl:apply-templates>
      </xsl:variable> 
      <xsl:for-each select="exsl:node-set($columns)/column">
        <xsl:variable name="adqltype">
          <xsl:call-template name="adqltype">
            <xsl:with-param name="type" select="type"/>
          </xsl:call-template>
        </xsl:variable>
      
        <xsl:call-template name="insertcolumn">
          <xsl:with-param name="column_name"><xsl:value-of select="name"/></xsl:with-param>
          <xsl:with-param name="table_name"><xsl:value-of select="$tableName"/></xsl:with-param>
          <xsl:with-param name="description"><xsl:value-of select="description"/></xsl:with-param>
          <xsl:with-param name="ucd"><xsl:text>TBD</xsl:text></xsl:with-param>
          <xsl:with-param name="utype"><xsl:value-of select="utype"/></xsl:with-param>
          <xsl:with-param name="datatype"><xsl:value-of select="$adqltype"/></xsl:with-param>
          <xsl:with-param name="arraysize" >
            <xsl:choose>
              <xsl:when test="$adqltype = 'VARCHAR'">
                <xsl:variable name ="length">
                  <xsl:call-template name="stringlength">
                    <xsl:with-param name="constraints" select="constraints"/>
                  </xsl:call-template>
                </xsl:variable>
                <xsl:choose>
                  <xsl:when test="number($length) &lt;= 0"><xsl:text>*</xsl:text></xsl:when>
                  <xsl:otherwise><xsl:value-of select="$length"/></xsl:otherwise>
                </xsl:choose>
              </xsl:when>
              <xsl:otherwise><xsl:text>1</xsl:text></xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
    </xsl:for-each>
    <xsl:for-each select="reference[not(subsets)]">
      <xsl:call-template name="insertcolumn">
        <xsl:with-param name="column_name"><xsl:apply-templates select="." mode="columnName"/></xsl:with-param>
        <xsl:with-param name="table_name"><xsl:value-of select="$tableName"/></xsl:with-param>
        <xsl:with-param name="description"><xsl:value-of select="description"/>&cr;
        <xsl:text>[This column is a foreign key pointing to the referenced object in </xsl:text>
        <xsl:apply-templates select="key('element',datatype/@xmiidref)" mode="viewName"/><xsl:text>].
        </xsl:text></xsl:with-param>
        <xsl:with-param name="ucd"><xsl:text>TBD</xsl:text></xsl:with-param>
        <xsl:with-param name="utype"><xsl:text>TBD</xsl:text></xsl:with-param>
        <xsl:with-param name="datatype"><xsl:text>long</xsl:text></xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>        

  </xsl:template>

  <xsl:template name="insertcolumn">
    <xsl:param name="column_name"/>
    <xsl:param name="table_name"/>
    <xsl:param name="description"/>
    <xsl:param name="unit"/>
    <xsl:param name="ucd"/>
    <xsl:param name="utype"/>
    <xsl:param name="datatype"/>
    <xsl:param name="arraysize" select="'1'"/>
    <xsl:param name="primary" select="'T'"/>
    <xsl:param name="indexed" select="'T'"/>
    <xsl:param name="std" select="'T'"/>
  <!-- TODO this can be simplieified, the INSERT INTO ... does not have to be repeated, 
  only comma-separated list of values (within () ) is required. More efficient as well. -->
  <xsl:text>INSERT INTO TAP_SCHEMA.Columns(column_name,table_name,description,unit,ucd,utype,datatype,arraysize,"primary",indexed,std) values(</xsl:text>
   '<xsl:value-of select="$column_name"/>'
,  '<xsl:value-of select="$target_database_schema_prefix"/><xsl:value-of select="$table_name"/>'
,  '<xsl:value-of select="replace($description,&quot;'&quot;,&quot;''&quot;)"/>'
,  <xsl:choose><xsl:when test="not($unit)">null</xsl:when><xsl:otherwise>'<xsl:value-of select="$unit"/>'</xsl:otherwise></xsl:choose>
,  <xsl:choose><xsl:when test="not($ucd)">null</xsl:when><xsl:otherwise>'<xsl:value-of select="$ucd"/>'</xsl:otherwise></xsl:choose>
,  <xsl:choose><xsl:when test="not($utype)">null</xsl:when><xsl:otherwise>'<xsl:value-of select="$utype"/>'</xsl:otherwise></xsl:choose>
,  '<xsl:value-of select="$datatype"/>'
,  '<xsl:value-of select="$arraysize"/>'
,  '<xsl:value-of select="$primary"/>'
,  '<xsl:value-of select="$indexed"/>'
,  '<xsl:value-of select="$std"/>');&cr;
</xsl:template>



<!-- 
Use VOSI/VODataService specification.
Following example in http://wfaudata.roe.ac.uk/ukidssWorld-dsa/wsa/vosi/tables
as suggested in http://www.ivoa.net/internal/IVOA/TableAccess/TAP-QL-0.1.pdf
-->
  <!-- 
  This template assumes that the public database schema is described as a collection
  of <table>-s, with <column>-s describing the columns.
  -->
  <xsl:template match="model" mode="vodataservice">
    <xsl:message>Model = <xsl:value-of select="name"></xsl:value-of></xsl:message>
-- Generating VODataService like metadata for model <xsl:value-of select="name"/>.
<xsl:value-of select="$header"/>
    
    <xsl:variable name="file" select="concat($project_name,'_vodataservice.xml')"/>
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    
    <xsl:result-document href="{$file}">

    <xsl:element name="ri:Resource">
      <xsl:namespace name="ri">http://www.ivoa.net/xml/RegistryInterface/v1.0</xsl:namespace>
      <xsl:namespace name="vr">http://www.ivoa.net/xml/VOResource/v1.0</xsl:namespace>
      <xsl:namespace name="vs">http://www.ivoa.net/xml/VODataService/v1.1</xsl:namespace>
      <xsl:namespace name="xsi">http://www.w3.org/2001/XMLSchema-instance</xsl:namespace>
      <xsl:attribute name="xsi:schemaLocation">
<xsl:text>http://www.ivoa.net/xml/VOResource/v1.0 http://www.ivoa.net/xml/VOResource/v1.0 http://www.ivoa.net/xml/VODataService/v1.1 http://www.ivoa.net/xml/VODataService/v1.1 http://www.ivoa.net/xml/STC/stc-v1.30.xsd http://www.ivoa.net/xml/STC/stc-v1.30.xsd</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="xsi:type" select="'vs:CatalogService'"/>
      
<!--
  xsi:schemaLocation="http://www.ivoa.net/xml/VOResource/v1.0 http://software.astrogrid.org/schema/vo-resource-types/VOResource/v1.0/VOResource.xsd http://www.ivoa.net/xml/VODataService/v1.0 http://software.astrogrid.org/schema/vo-resource-types/VODataService/v1.0/VODataService.xsd urn:astrogrid:schema:TableMetadata http://wfaudata.roe.ac.uk/ukidssWorld-dsa/schema/Tables.xsd
 -->
     <xsl:apply-templates select="." mode="vodataservice.resource"/>
     <xsl:element name="tableset">
     <xsl:element name="schema">
     <xsl:element name="name"/>
      <xsl:apply-templates select="//objectType" mode="vodataservice">
        <xsl:sort select="name"/>
      </xsl:apply-templates>
      </xsl:element>
    </xsl:element>
    </xsl:element>
    </xsl:result-document>
  </xsl:template>  



  <xsl:template match="model" mode="vodataservice.resource">
  <xsl:element name="title">
  <xsl:value-of select="title"/>
  </xsl:element>
  <xsl:element name="shortName">
  <xsl:value-of select="name"/>
  </xsl:element>
  <xsl:element name="identifier"><xsl:comment>TBD</xsl:comment></xsl:element>
  <xsl:element name="curation">
    <xsl:element name="publisher"><xsl:comment>TBD</xsl:comment></xsl:element>
    <xsl:element name="contact">
      <xsl:element name="name"><xsl:comment>TBD</xsl:comment></xsl:element>
      <xsl:element name="email"><xsl:comment>TBD</xsl:comment></xsl:element>
    </xsl:element>
  </xsl:element>
  <xsl:element name="content">
    <xsl:for-each select="subject">
      <xsl:element name="subject"><xsl:value-of select="."/></xsl:element>
    </xsl:for-each>
    <xsl:element name="description"><xsl:value-of select="description"/></xsl:element>
    <xsl:element name="referenceURL"><xsl:comment>TBD</xsl:comment></xsl:element>
    <xsl:element name="type"><xsl:comment>TBD</xsl:comment></xsl:element>
    <xsl:element name="contentLevel"><xsl:comment>TBD</xsl:comment></xsl:element>
  </xsl:element>  
  <xsl:apply-templates select="." mode="vodataservice.capability"/>
  </xsl:template>




  <xsl:template match="model" mode="vodataservice.capability">
  <xsl:element name="capability">
  <xsl:comment>TBD</xsl:comment>
  </xsl:element>
  </xsl:template>




  <!-- 
  This template assumes that the public database schema is described as a collection
  of <table>-s, with <column>-s describing the columns.
  -->

  <xsl:template match="objectType" mode="vodataservice">
    
    <xsl:element name="table">
      <xsl:attribute name="type" select="'view'"/>
      <xsl:element name="name">
        <xsl:apply-templates select="." mode="viewName"/>
      </xsl:element>
      <xsl:element name="title">
        <xsl:apply-templates select="." mode="viewName"/>
      </xsl:element>
      <xsl:element name="description">
        <xsl:value-of select="description"/>
      </xsl:element>
      <xsl:element name="utype">
      <xsl:apply-templates select="." mode="utype"/>
      </xsl:element>
      
      <xsl:apply-templates select="." mode="vodataservice.columns"/>
      <xsl:apply-templates select="." mode="vodataservice.foreignkeys"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="objectType" mode="vodataservice.columns">
    <xsl:variable name="utype">
      <xsl:apply-templates select="." mode="utype"/>
    </xsl:variable>
    <xsl:choose>
    <xsl:when test="extends">
      <xsl:apply-templates select="key('element',extends/@xmiidref)" mode="vodataservice.columns"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="vodataservice_column">
        <xsl:with-param name="name" select="$primaryKeyColumnName"/>
        <xsl:with-param name="description">
          <xsl:text>The unique, primary key column on this table.</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="datatype" select="'BIGINT'"/>
        <xsl:with-param name="ucd" select="'TBD'"/>
        <xsl:with-param name="utype" select="concat($utype,'.',$primaryKeyColumnName)"/>
        <xsl:with-param name="flag" select="'primary'"/>
        
      </xsl:call-template>
      
      <xsl:call-template name="vodataservice_column">
        <xsl:with-param name="name" select="$discriminatorColumnName"/>
        <xsl:with-param name="description">
          <xsl:text>This column stores the name of the object type from the data model stored in the row.</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="datatype" select="'VARCHAR'"/>
        <xsl:with-param name="ucd" select="'TBD'"/>
        <xsl:with-param name="utype" select="concat($utype,'.CONTAINER')"/>
      </xsl:call-template>

    </xsl:otherwise>
    </xsl:choose>

    <xsl:if test="container">
      <xsl:call-template name="vodataservice_column">
        <xsl:with-param name="name" select="'containerId'"/> <!-- TODO make this a shared variable somewhere -->
        <xsl:with-param name="description">
          <xsl:text>This column is a foreign key pointing to the containing object in </xsl:text>
          <xsl:apply-templates select="key('element',container/@xmiidref)" mode="viewName"/>
        </xsl:with-param>
        <xsl:with-param name="datatype" select="'BIGINT'"/>
        <xsl:with-param name="ucd" select="'TBD'"/>
        <xsl:with-param name="utype" select="concat($utype,'.CONTAINER')"/>
      </xsl:call-template>
    </xsl:if>        
    <xsl:for-each select="attribute">
      <xsl:variable name="columns">
        <xsl:apply-templates select="." mode="columns">
          <xsl:with-param name="utypeprefix" select="$utype"/>
        </xsl:apply-templates>
      </xsl:variable> 
      <xsl:for-each select="exsl:node-set($columns)/column">
        <xsl:variable name="adqltype">
          <xsl:call-template name="adqltype">
            <xsl:with-param name="type" select="type"/>
          </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="vodataservice_column">
          <xsl:with-param name="name" select="name"/>
          <xsl:with-param name="description" select="description"/>
          <xsl:with-param name="datatype" select="$adqltype"/>
          <xsl:with-param name="ucd" select="'TBD'"/>
          <xsl:with-param name="utype" select="utype"/>
        </xsl:call-template>
      </xsl:for-each>   
    </xsl:for-each>

    <xsl:for-each select="reference[not(subsets)]">
      <xsl:call-template name="vodataservice_column">
        <xsl:with-param name="name">
          <xsl:apply-templates select="." mode="columnName"/>
        </xsl:with-param>
        <xsl:with-param name="description" select="description"/>
        <xsl:with-param name="datatype" select="'long'"/>
        <xsl:with-param name="ucd" select="'TBD'"/>
        <xsl:with-param name="utype" select="'TBD'"/>
      </xsl:call-template>
    </xsl:for-each>        
  </xsl:template>


  <xsl:template match="objectType" mode="vodataservice.foreignkeys">
    <xsl:if test="container">
      <xsl:element name="foreignKey">
        <xsl:element name="targetTable">
          <xsl:apply-templates select="key('element',container/@xmiidref)" mode="viewName"/>
        </xsl:element>
        <xsl:element name="fkColumn">
          <xsl:element name="fromColumn">containerId</xsl:element>
          <xsl:element name="targetColumn"><xsl:value-of select="$primaryKeyColumnName"/></xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:if>        
  
    <xsl:for-each select="reference[not(subsets)]">
      <xsl:element name="foreignKey">
        <xsl:element name="targetTable">
          <xsl:apply-templates select="key('element',datatype/@xmiidref)" mode="viewName"/>
        </xsl:element>
        <xsl:element name="fkColumn">
          <xsl:element name="fromColumn">
             <xsl:apply-templates select="." mode="columnName"/>
          </xsl:element>
          <xsl:element name="targetColumn"><xsl:value-of select="$primaryKeyColumnName"/></xsl:element>
        </xsl:element>
      </xsl:element>
      </xsl:for-each>
  </xsl:template>




  <xsl:template name="vodataservice_column">
    <xsl:param name="name"/>
    <xsl:param name="tableName"/>
    <xsl:param name="description"/>
    <xsl:param name="unit"/>
    <xsl:param name="ucd"/>
    <xsl:param name="utype"/>
    <xsl:param name="datatype"/>
    <xsl:param name="flag"/>
    <xsl:param name="std" select="'true'"/>

    <xsl:element name="column">
      <xsl:attribute name="std" select="$std"/>
      <xsl:element name="name"><xsl:value-of select="$name"/></xsl:element>
      <xsl:element name="description"><xsl:value-of select="$description"/></xsl:element>
      <xsl:if test="$unit"><xsl:element name="unit"><xsl:value-of select="$unit"/></xsl:element></xsl:if>
      <xsl:if test="$ucd"><xsl:element name="ucd"><xsl:value-of select="$ucd"/></xsl:element></xsl:if>
      <xsl:if test="$utype"><xsl:element name="utype"><xsl:value-of select="$utype"/></xsl:element></xsl:if> 
      <xsl:element name="dataType"><xsl:value-of select="$datatype"/></xsl:element>
      <xsl:if test="$flag"><xsl:element name="flag"><xsl:value-of select="$flag"/></xsl:element></xsl:if>
    </xsl:element>
  </xsl:template>




<!-- UTIL -->

  <xsl:template name="adqltype">
    <xsl:param name="type"/>
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
      <xsl:when test="$type = 'boolean'">BOOLEAN</xsl:when>
<!--       <xsl:when test="$type = 'complex'">NOT SUPPORTED</xsl:when>   -->
      <xsl:when test="$type = 'short'">SMALLINT</xsl:when>
      <xsl:when test="$type = 'integer'">INTEGER</xsl:when>
      <xsl:when test="$type = 'long'">BIGINT</xsl:when>
      <xsl:when test="$type = 'float'">REAL</xsl:when>
      <xsl:when test="$type = 'real'">DOUBLE</xsl:when>
      <xsl:when test="$type = 'double'">DOUBLE</xsl:when>
      <xsl:when test="$type = 'datetime'">TIMESTAMP</xsl:when>
      <xsl:otherwise>VARCHAR</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  

  <xsl:template name="votabletype">
    <xsl:param name="type"/>
    <xsl:variable name="adqltype">
      <xsl:call-template name="adqltype">
        <xsl:with-param name="type" select="$type"/>
      </xsl:call-template>
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
      <xsl:when test="$adqltype = 'BOOLEAN'">boolean</xsl:when>
      <xsl:when test="$adqltype = 'SMALLINT'">short</xsl:when>
      <xsl:when test="$adqltype = 'INTEGER'">integer</xsl:when>
      <xsl:when test="$adqltype = 'BIGINT'">long</xsl:when>
      <xsl:when test="$adqltype = 'REAL'">float</xsl:when>
      <xsl:when test="$adqltype = 'FLOAT'">double</xsl:when>
      <xsl:when test="$adqltype = 'DOUBLE'">double</xsl:when>
      <xsl:when test="$adqltype = 'TIMESTAMP'">datetime</xsl:when>
      <xsl:otherwise>char</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  

</xsl:stylesheet>
