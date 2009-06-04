<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This XSLT script transforms a data model from our
intermediate representation to a relational database 
Data Definition Language script.

By default we use the "joined" object-relational mapping strategy.
That is, each objectType has a table of its own, in which only those features defined on
the objectType are mapped to columns, the  inherited are mapped by base classes.

We also generate view definitions representing each objectType.

We assume that all tables are in a single schema.
For now we assume that objectType's names are unique over the complete model.
TODO We need to check this explicitly and modify the generation if not.


LAURENT : TODO : add foreign key on PK for inheritance :

CREATE TABLE TargetObjectType (
  id integer NOT NULL,     // inheritance
...
  CONSTRAINT TargetObjectType_pk PRIMARY KEY (id),
  CONSTRAINT TargetObjectType_ObjectType_fk          FOREIGN KEY (id)       REFERENCES ObjectType       (id), // inheritance
...
)

 -->

<!DOCTYPE stylesheet [
<!ENTITY cr  "<xsl:text>
</xsl:text>">
<!ENTITY bl  "<xsl:text> </xsl:text>">
<!ENTITY rem "<xsl:text>-- </xsl:text>">
]>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
								xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema">
  

  <xsl:import href="common-ddl.xsl"/>
  <xsl:import href="utype.xsl"/>

<!-- possible values: postgres, mssqlserver -->  
  <xsl:param name="vendor"/>
  <xsl:param name="project_name"/>
  
  
  <xsl:output method="text" encoding="UTF-8" indent="no" />
  
  <xsl:strip-space elements="*" />
  
  <xsl:key name="element" match="*" use="@xmiid"/>
  <xsl:key name="ptype" match="*//primitiveType" use="@xmiid"/>
  <xsl:key name="dtype" match="*//dataType" use="@xmiid"/>
  <xsl:key name="enum" match="*//enumeration" use="@xmiid"/>
  
  
  <xsl:param name="lastModifiedText"/>
  
  <xsl:variable name="header">&rem;last modification date of the UML model <xsl:value-of select="$lastModifiedText"/>&cr;</xsl:variable>
  
 
  
  
  <!-- start -->
  <xsl:template match="/">
    <xsl:apply-templates select="model"/>
  </xsl:template>
  
  
  
<!--  Topological sort/depth first ordering of input objectType-s -->
<!--  From http://www.biglist.com/lists/xsl-list/archives/200101/msg00161.html -->
  <xsl:template name="topsort">
    <xsl:param name="nodes"/>
    <xsl:param name="finished"/>
    <xsl:variable name="processed" select="$nodes|$finished"/>

    <xsl:for-each select="$nodes">
      <node><xsl:copy-of select="."/><utype><xsl:value-of select="utype"/></utype></node>
    </xsl:for-each>
    
    <xsl:if test="count(//objectType)>count($processed)">
      <xsl:variable name="nextnodes"
         select="//objectType[not($processed/@xmiid=@xmiid)
                 and count(reference)=count(reference[datatype/@xmiidref = $processed/@xmiid])
                 and count(container)=count(container[@xmiidref=$processed/@xmiid])
                 and count(extends)=count(extends[@xmiidref=$processed/@xmiid])]"/>
                 
      <xsl:if test="$nextnodes">
        <xsl:call-template name="topsort">
          <xsl:with-param name="nodes" select="$nextnodes"/>
          <xsl:with-param name="finished" select="$processed"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:if>
  </xsl:template>




  <!-- template dump : debugging purposes only -->
  <xsl:template match="@*|node()" mode="dump">
    <xsl:copy>
          <xsl:apply-templates select="@*|node()"  mode="dump"/>
    </xsl:copy>
  </xsl:template>



   
  <xsl:template match="model">
    <xsl:message>Model = <xsl:value-of select="name"></xsl:value-of></xsl:message>
&rem;Generating DDLs for model <xsl:value-of select="name"/> and DB vendor <xsl:value-of select="$vendor"/>.
<xsl:value-of select="$header"/>
    
  <!-- create object types sorted according to foreign key graph derived from container, inheritance and reference -->
    <xsl:variable name="sortedObjectTypes">
    <!--  add non-object types separately -->
      <xsl:for-each select="//primitiveType|//dataType|//enumeration">
        <vt><xsl:copy-of select="."/></vt>
      </xsl:for-each>
      <xsl:call-template name="topsort">
      <!-- need to add the value types as well as the objecttypes, otherwise when in the context -->
        <xsl:with-param name="nodes" select="//objectType[not(reference|extends|container)]"/>
        <xsl:with-param name="finished" select="/.."/>
      </xsl:call-template>    
    </xsl:variable>

        
<!-- CREATE TABLES -->
    <xsl:variable name="file" select="concat($vendor,'/',$project_name,'_createTables.sql')"/>
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}">
      <xsl:value-of select="$header"/>&cr;&cr;
 
      <xsl:for-each select="exsl:node-set($sortedObjectTypes)/node/objectType">   
        <xsl:sort select="position()" data-type="number" order="ascending"/> 

        &rem;<xsl:text>------------------------------------------------------------------------------</xsl:text>&cr;
        &rem;<xsl:text> Table representation of the objectType </xsl:text><xsl:value-of select="name"/>&cr;
        &rem;<xsl:text> XMI-ID = </xsl:text><xsl:value-of select="@xmiid"/>&cr;
        &rem;<xsl:text>------------------------------------------------------------------------------</xsl:text>&cr;
        &rem;<xsl:value-of select="replace(description,'\n','  ')"/>&cr;
        &rem;<xsl:text>------------------------------------------------------------------------------</xsl:text>&cr;
        
        <xsl:apply-templates select="." mode="createTable"/>

        <xsl:apply-templates select="." mode="createIndexes"/>
        <!-- because of topologically sorted nodes, can define the foreign keys directly after the table definition -->
        <xsl:apply-templates select="." mode="createFKs"/>
        
        &cr;&cr;&cr;
      </xsl:for-each>
    </xsl:result-document>

    
<!-- CREATE VIEWS -->
    <xsl:variable name="file" select="concat($vendor,'/',$project_name,'_createViews.sql')"/>
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}">
      <xsl:value-of select="$header"/>

      <xsl:for-each select="exsl:node-set($sortedObjectTypes)/node/objectType">   
        <xsl:sort select="position()" data-type="number" order="ascending"/> 
        <xsl:apply-templates select="." mode="createView"/>

      </xsl:for-each>
    </xsl:result-document>


<!-- DROP VIEWS -->
    <xsl:variable name="file" select="concat($vendor,'/',$project_name,'_dropViews.sql')"/>
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}">
      <xsl:value-of select="$header"/>

      <xsl:for-each select="exsl:node-set($sortedObjectTypes)/node/objectType">   
        <xsl:sort select="position()" data-type="number" order="descending"/> 
        <xsl:apply-templates select="." mode="dropView"/>

      </xsl:for-each>
    </xsl:result-document>


<!--  DROP TABLES -->
    <xsl:variable name="file" select="concat($vendor,'/',$project_name,'_dropTables.sql')"/>
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}">
      <xsl:value-of select="$header"/>

      <xsl:for-each select="exsl:node-set($sortedObjectTypes)/node/objectType">   
        <xsl:sort select="position()" data-type="number" order="descending"/> 
        <xsl:apply-templates select="." mode="dropTable"/>

      </xsl:for-each>
    </xsl:result-document>

  </xsl:template>  




  <xsl:template match="objectType" mode="createTable">
    <!-- generate a single table for the whole object hierarchy below the matched objectType -->
    <xsl:variable name="tableName">
      <xsl:apply-templates select="." mode="tableName"/>
    </xsl:variable>
<xsl:text>CREATE TABLE </xsl:text><xsl:value-of select="$tableName"/><xsl:text> (</xsl:text>&cr;
&bl;&bl;<xsl:value-of select="$primaryKeyColumnName"/>&bl;
<xsl:choose>
  <xsl:when test="not(extends)">
    <xsl:value-of select="$IDGentype"/>
  </xsl:when>
  <xsl:otherwise>
    <xsl:value-of select="$IDDatatype"/>&bl;<xsl:text>NOT NULL</xsl:text>
  </xsl:otherwise>
</xsl:choose>
&cr; 
<xsl:if test="not(extends)">
  <xsl:apply-templates select="." mode="discriminatorColumnDeclaration"/>
  <xsl:text>, OPTLOCK INTEGER</xsl:text>&cr;  
<!--
  <xsl:text>, xmlId VARCHAR(32)</xsl:text>&cr;  
  <xsl:text>, ivoId VARCHAR(255)</xsl:text>&cr;  
 -->
  <xsl:text>, </xsl:text><xsl:value-of select="$publisherDIDColumnName"/> <xsl:text> VARCHAR(</xsl:text><xsl:value-of select="$publisherDIDColumnLength"/>)&cr;  
</xsl:if>
<xsl:apply-templates select="." mode="container"/>
<xsl:apply-templates select="attribute" />
<xsl:apply-templates select="reference" />

<xsl:apply-templates select="." mode="users"/>

<xsl:text>);</xsl:text>&cr;
  </xsl:template>
  



  <xsl:template match="objectType" mode="dropTable">
    <!-- generate a single table for the whole object hierarchy below the matched objectType -->
    <xsl:variable name="tableName">
      <xsl:apply-templates select="." mode="tableName"/>
    </xsl:variable>
<xsl:text>DROP TABLE </xsl:text><xsl:value-of select="$tableName"/>;&cr;&cr;
  </xsl:template>




  <!-- View definition for a given objectType.
  Returns all columns for the given type, including inherited ones.
  Uses view for base class, if available. -->
  <xsl:template match="objectType" mode="createView">
    <xsl:variable name="tableName">
      <xsl:apply-templates select="." mode="tableName"/>
    </xsl:variable>
    <xsl:variable name="viewName">
      <xsl:apply-templates select="." mode="viewName"/>
    </xsl:variable>
    <xsl:variable name="base" select="key('element',extends/@xmiidref)"/>
<xsl:text>CREATE VIEW </xsl:text><xsl:value-of select="$viewName"/><xsl:text> AS</xsl:text>&cr;
    <xsl:choose>
      <xsl:when test="extends">
        <xsl:text>  SELECT b.*</xsl:text>&cr;
        <xsl:if test="container">
          <xsl:text>  ,      t.containerId</xsl:text>&cr;
        </xsl:if>        
        <xsl:for-each select="attribute">
          <xsl:variable name="columns">
            <xsl:apply-templates select="." mode="columns"/>
          </xsl:variable> 
          <xsl:for-each select="exsl:node-set($columns)/column">
            <xsl:text>  ,      t.</xsl:text><xsl:value-of select="name"/>&cr;
          </xsl:for-each>   
        </xsl:for-each>
        <xsl:for-each select="reference[not(subsets)]">
          <xsl:text>  ,      t.</xsl:text><xsl:apply-templates select="." mode="columnName"/>&cr;
        </xsl:for-each>        
        <xsl:text>    FROM </xsl:text><xsl:value-of select="$tableName"/><xsl:text> t</xsl:text>&cr;
        <xsl:text>    ,    </xsl:text><xsl:apply-templates select="$base" mode="viewName"/> b
<xsl:text>   WHERE b.</xsl:text><xsl:value-of select="$primaryKeyColumnName"/><xsl:text> = t.</xsl:text><xsl:value-of select="$primaryKeyColumnName"/>
      </xsl:when>
      <xsl:otherwise>
<xsl:text>  SELECT </xsl:text><xsl:value-of select="$primaryKeyColumnName"/>&cr;
          <xsl:text>  ,      </xsl:text><xsl:value-of select="$publisherDIDColumnName"/>&cr;
          <xsl:text>  ,      </xsl:text><xsl:value-of select="$discriminatorColumnName"/>&cr;
        <xsl:if test="container">
          <xsl:text>  ,      containerId</xsl:text>&cr;
        </xsl:if>        
        <xsl:for-each select="attribute">
          <xsl:variable name="columns">
            <xsl:apply-templates select="." mode="columns"/>
          </xsl:variable> 
          <xsl:for-each select="exsl:node-set($columns)/column">
            <xsl:text>  ,      </xsl:text><xsl:value-of select="name"/>&cr;
          </xsl:for-each>   
        </xsl:for-each>
        <xsl:for-each select="reference[not(subsets)]">
          <xsl:text>  ,      </xsl:text><xsl:apply-templates select="." mode="columnName"/>&cr;
        </xsl:for-each>        
<xsl:text>    FROM </xsl:text><xsl:value-of select="$tableName"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:choose>
    <xsl:when test="$vendor='mssqlserver'">
    &cr;<xsl:text>GO</xsl:text>
    </xsl:when>
    <xsl:otherwise>
    <xsl:text>;</xsl:text>
    </xsl:otherwise>
    </xsl:choose>
    &cr;&cr;
  </xsl:template>
  



  <xsl:template match="objectType" mode="dropView">
    <!-- generate a single table for the whole object hierarchy below the matched objectType -->
    <xsl:variable name="viewName">
      <xsl:apply-templates select="." mode="viewName"/>
    </xsl:variable>
<xsl:text>DROP VIEW </xsl:text><xsl:value-of select="$viewName"/>;&cr;&cr;
  </xsl:template>




  <xsl:template match="objectType" mode="createFKs">
    <!-- generate a foreign key for each relation -->
    <xsl:variable name="tableName">
      <xsl:apply-templates select="." mode="tableName"/>
    </xsl:variable>
    <xsl:if test="container">
    <xsl:variable name="otherTable">
      <xsl:apply-templates select="key('element',container/@xmiidref)" mode="tableName"/>
    </xsl:variable>
<xsl:text>ALTER TABLE </xsl:text><xsl:value-of select="$tableName"/> ADD CONSTRAINT fk_<xsl:value-of select="$tableName"/>_container&cr; 
<xsl:text>    FOREIGN KEY (containerId) REFERENCES </xsl:text><xsl:value-of select="$otherTable"/>(<xsl:value-of select="$primaryKeyColumnName"/>);&cr;&cr;
    </xsl:if>
    <xsl:if test="extends">
    <xsl:variable name="otherTable">
      <xsl:apply-templates select="key('element',extends/@xmiidref)" mode="tableName"/>
    </xsl:variable>
<xsl:text>ALTER TABLE </xsl:text><xsl:value-of select="$tableName"/> ADD CONSTRAINT fk_<xsl:value-of select="$tableName"/>_extends&cr; 
<xsl:text>    FOREIGN KEY (</xsl:text><xsl:value-of select="$primaryKeyColumnName"/><xsl:text>) REFERENCES </xsl:text><xsl:value-of select="$otherTable"/>(<xsl:value-of select="$primaryKeyColumnName"/>);&cr;&cr;
    </xsl:if>
    <xsl:for-each select="reference[not(subsets)]">
    <xsl:variable name="otherTable">
      <xsl:apply-templates select="key('element',datatype/@xmiidref)" mode="tableName"/>
    </xsl:variable>
<xsl:text>ALTER TABLE </xsl:text><xsl:value-of select="$tableName"/> ADD CONSTRAINT fk_<xsl:value-of select="$tableName"/>_<xsl:value-of select="name"/>&cr; 
<xsl:text>    FOREIGN KEY (</xsl:text><xsl:apply-templates select="." mode="columnName"/>) REFERENCES <xsl:value-of select="$otherTable"/>(<xsl:value-of select="$primaryKeyColumnName"/>);&cr;&cr;
</xsl:for-each>
  </xsl:template>




  <xsl:template match="objectType" mode="dropFKs">
    <!-- generate a foreign key for each relation -->
    <xsl:variable name="tableName">
      <xsl:apply-templates select="." mode="tableName"/>
    </xsl:variable>
    <xsl:for-each select="reference">
DROP FOREIGN KEY fk_<xsl:value-of select="$tableName"/>_<xsl:value-of select="name"/> (<xsl:apply-templates select="." mode="columnName"/>);&cr;&cr;
</xsl:for-each>
  </xsl:template>

  
  
  
  <xsl:template match="objectType" mode="createIndexes">
    <!-- generate a foreign key for each relation -->
    <xsl:variable name="tableName">
      <xsl:apply-templates select="." mode="tableName"/>
    </xsl:variable>
ALTER TABLE <xsl:value-of select="$tableName"/> ADD CONSTRAINT pk_<xsl:value-of select="$tableName"/>_<xsl:value-of select="$primaryKeyColumnName"/> PRIMARY KEY(<xsl:value-of select="$primaryKeyColumnName"/>);&cr;&cr;
    
    <xsl:for-each select="reference[not(subsets)]">
CREATE INDEX ix_<xsl:value-of select="$tableName"/>_<xsl:value-of select="name"/> ON <xsl:value-of select="$tableName"/>(<xsl:apply-templates select="." mode="columnName"/>);&cr;&cr;
</xsl:for-each>
  </xsl:template>




  <xsl:template match="objectType" mode="dropIndexes">
    <!-- generate a foreign key for each relation -->
    <xsl:variable name="tableName">
      <xsl:apply-templates select="." mode="tableName"/>
    </xsl:variable>
    <xsl:for-each select="reference">
DROP INDEX <xsl:value-of select="$tableName"/>.ix_<xsl:value-of select="$tableName"/>_<xsl:value-of select="name"/> (<xsl:apply-templates select="." mode="columnName"/>);&cr;&cr;
</xsl:for-each>
  </xsl:template>  
  
  
  
  
  <!-- determine whether there is a class that contains this class.
  If so generate a containerID column.
  NOTE we should ensure that there is only 1  -->
  <xsl:template match="objectType" mode="container">
    <xsl:variable name="xmiid" select="@xmiid"/>
    <xsl:if test="container">
      <xsl:text>, containerId </xsl:text><xsl:value-of select="$IDDatatype"/><xsl:text> NOT NULL </xsl:text>&rem;<xsl:value-of select="key('element',container/@xmiidref)/name"/>&cr;
      <xsl:text>, rank INTEGER NOT NULL </xsl:text>&cr;      
    </xsl:if>
  </xsl:template>
  
  <!-- IF this is a root entity class, add user columnsdetermine whether there is a class that contains this class.
  If so generate a containerID column.
  NOTE we should ensure that there is only 1  -->
  <xsl:template match="objectType" mode="users">
    <xsl:if test="entity='true' and not(extends)">
      <xsl:text>, ownerUser VARCHAR(32)</xsl:text>&cr;
      <xsl:text>, updateUser VARCHAR(32)</xsl:text>&cr;      
    </xsl:if>
  </xsl:template>
  
  

  
  <xsl:template match="attribute">
     <xsl:variable name="columns">
        <xsl:apply-templates select="." mode="columns"/>
     </xsl:variable> 
     <xsl:for-each select="exsl:node-set($columns)/column">
       <xsl:text>, </xsl:text><xsl:value-of select="name"/>&bl;<xsl:value-of select="sqltype"/><xsl:if test="multiplicity = '1'"> NOT NULL</xsl:if>&cr;
     </xsl:for-each>   
  </xsl:template>
  
  
  
  
  <xsl:template match="attribute" mode="old">
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
    <xsl:variable name="type" select="key('element',datatype/@xmiidref)"/>
    <xsl:choose>
      <xsl:when test="name($type) != 'primitiveType'">
        <xsl:apply-templates select="$type" mode="columns"><xsl:with-param name="prefix" select="$columnname"/></xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="sqltype">
          <xsl:call-template name="sqltype">
            <xsl:with-param name="type" select="$type"/>
            <xsl:with-param name="constraints" select="constraints"/>
          </xsl:call-template>
        </xsl:variable>, <xsl:value-of select="$columnname"/>&bl;<xsl:value-of select="$sqltype"/><xsl:if test="multiplicity = '1'"> NOT NULL</xsl:if>&cr;
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>
  
  
  

  <!--  DEBUG template -->    
  <xsl:template match="datatype/@xmiidref" mode="classifier">
    <xsl:variable name="type" select="key('element',.)"/>
    <xsl:value-of select="$type/name"/>
  </xsl:template>
  <!-- END DEBUG -->
  
  
  
  
  <!-- We need lengths for (var)char datatypes -->
  <xsl:template match="dataType" mode="columns">
    <xsl:param name="prefix"/>
    <xsl:choose>
      <xsl:when test="not(attribute)">, <xsl:value-of select="$prefix"/> VARCHAR(256) &rem;<xsl:value-of select="name"/>&cr;</xsl:when>
      <xsl:otherwise><xsl:apply-templates select="attribute"><xsl:with-param name="prefix" select="$prefix"/></xsl:apply-templates></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <!-- We need lengths for (var)char datatypes -->
  <xsl:template match="enumeration" mode="columns">
    <xsl:param name="prefix"/>, <xsl:value-of select="$prefix"/> VARCHAR(256) &rem;<xsl:value-of select="name"/>&cr;
  </xsl:template>
  
  
  
  
  <xsl:template match="reference">
    <xsl:if test="not(subsets)">, <xsl:apply-templates select="." mode="columnName"/>&bl;<xsl:value-of select="$IDDatatype"/>&bl;<xsl:call-template name="nullity"><xsl:with-param name="multiplicity" select="multiplicity"/></xsl:call-template> &rem;<xsl:value-of select="key('element',datatype/@xmiidref)/name"/>&cr;
    </xsl:if>
  </xsl:template>
  
  
  
  
  <xsl:template name="nullity">
    <xsl:param name="multiplicity"/>
    <xsl:choose>
      <xsl:when test="$multiplicity = '1' or $multiplicity = '1..*'">NOT NULL</xsl:when>
      <xsl:otherwise>null</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <!-- TODO
  Add templates retrieving for a given objectType the table it is in
  for a given attribute the column(s) it is in
  for a given reference the column it is in
  for a given collection the containerId column it is in.
   -->
  
  

<!-- Maybe somewhat too much indirection here? -->
  <xsl:template match="objectType" mode="PK_COLUMN">
    <xsl:value-of select="concat($primaryKeyColumnName,' ',$IDDatatype)"/><xsl:text> NOT NULL</xsl:text>
  </xsl:template>




<!-- Discriminator column templates -->
  <xsl:template match="objectType" mode="discriminatorColumnDeclaration">, <xsl:value-of select="$discriminatorColumnName"/> <xsl:value-of select="$discriminatorColumnType"/>&cr;
  </xsl:template>

      
</xsl:stylesheet>
