<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
								xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl">

<!-- 
  This XSLT is used by intermediate2java.xsl to generate JPA annotations and JPA specific java code.
  
  Java 1.5+ is required by JPA 1.0.
-->

  <!-- common DDL templates used -->
  <xsl:import href="common-ddl.xsl"/>
  
  <xsl:output name="persistenceInfo" method="xml" encoding="UTF-8" indent="yes"  />


  <xsl:key name="element" match="*//*" use="@xmiid"/>

  
  
  <xsl:template match="objectType" mode="JPAAnnotation">
    <xsl:variable name="className" select="name" />
    <xsl:variable name="xmiid" select="@xmiid" />
    <xsl:variable name="hasChild">
      <xsl:choose>
        <xsl:when test="count(//extends[@xmiidref = $xmiid]) > 0">1</xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="extMod">
      <xsl:choose>
        <xsl:when test="count(extends) = 1">1</xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="hasName">
      <xsl:choose>
        <xsl:when test="count(attribute[name = 'name']) > 0">1</xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
  @Entity
  @Table( name = "<xsl:apply-templates select="." mode="tableName"/>" )
<!--  always generate discriminator column, looks nicer in the database. -->
<!--   <xsl:if test="$hasChild = 1">  -->
&cr;    
  <!-- JOINED strategy for inheritance -->
  @Inheritance( strategy = InheritanceType.JOINED )
  @DiscriminatorColumn( name = "<xsl:value-of select="$discriminatorColumnName"/>", discriminatorType = DiscriminatorType.STRING, length = <xsl:value-of select="$discriminatorColumnLength"/>)
&cr;    
<!--   </xsl:if>   -->

  <xsl:if test="$extMod = 1">
  @DiscriminatorValue( "<xsl:value-of select="$className"/>" ) <!-- TODO decide whether this should be a path -->
  </xsl:if>

  @NamedQueries( {
    @NamedQuery( name = "<xsl:value-of select="$className"/>.findById", query = "SELECT o FROM <xsl:value-of select="$className"/> o WHERE o.id = :id"),
    @NamedQuery( name = "<xsl:value-of select="$className"/>.findByPublisherDID", query = "SELECT o FROM <xsl:value-of select="$className"/> o WHERE o.identity.publisherDID = :publisherDID")
<!--  
,    @NamedQuery( name = "<xsl:value-of select="$className"/>.findByXmlId", query = "SELECT o FROM <xsl:value-of select="$className"/> o WHERE o.identity.xmlId = :xmlId")
,    @NamedQuery( name = "<xsl:value-of select="$className"/>.findByIvoId", query = "SELECT o FROM <xsl:value-of select="$className"/> o WHERE o.identity.ivoId = :ivoId")
-->
  <xsl:if test="$hasName = 1">
,     @NamedQuery( name = "<xsl:value-of select="$className"/>.findByName", query = "SELECT o FROM <xsl:value-of select="$className"/> o WHERE o.name = :name") 
  </xsl:if>
  } )
  </xsl:template>





  <xsl:template match="objectType|dataType" mode="JPASpecials">
    <xsl:param name="hasChild"/>
    <xsl:param name="hasExtends"/>

    <xsl:if test="name() = 'objectType' and $hasExtends = 0 and $hasChild = 1">
    /** classType gives the discriminator value stored in the database for an inheritance hierarchy */
    @Column( name = "<xsl:value-of select="$discriminatorColumnName"/>", insertable = false, updatable = false, nullable = false )
    protected String classType;
    </xsl:if>

    <xsl:if test="name() = 'objectType' and $hasExtends = 0">
    /** jpaVersion gives the current version number for that entity (used by pessimistic / optimistic locking in JPA) */
    @Version()
    @Column( name = "OPTLOCK" )
    protected int jpaVersion;
    </xsl:if>

    <xsl:if test="container">
      <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="container/@xmiidref"/></xsl:call-template></xsl:variable>
    /** container gives the parent entity which owns a collection containing instances of this class */
    @ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH } )
    @JoinColumn( name = "containerId", referencedColumnName = "id", nullable = false )
    protected <xsl:value-of select="$type"/> container;

    /** rank : position in the container collection  */
    @Basic( optional = false )
    @Column( name = "rank", nullable = false )
    protected int rank = 0;
    </xsl:if>
  </xsl:template>




  <xsl:template match="dataType" mode="JPAAnnotation">
    <xsl:text>@Embeddable</xsl:text>&cr;
  </xsl:template>




  <!-- temlate attribute : adds JPA annotations for primitive types, data types & enumerations -->
  <xsl:template match="attribute" mode="JPAAnnotation">
    <xsl:variable name="type" select="key('element', datatype/@xmiidref)"/>
    
    <xsl:choose>
      <xsl:when test="name($type) = 'primitiveType'">
        <xsl:choose>
          <xsl:when test="number(constraints/maxLength) = -1">
    @Basic( fetch = FetchType.LAZY, optional = <xsl:apply-templates select="." mode="nullable"/> )
    @Lob
    @Column( name = "<xsl:apply-templates select="." mode="columnName"/>", nullable = <xsl:apply-templates select="." mode="nullable"/> )
          </xsl:when>
          <xsl:when test="number(constraints/maxLength) > 0">
    @Basic( optional = <xsl:apply-templates select="." mode="nullable"/> )
    @Column( name = "<xsl:apply-templates select="." mode="columnName"/>", nullable = <xsl:apply-templates select="." mode="nullable"/>, length = <xsl:value-of select="constraints/maxLength"/> )
          </xsl:when>
          <xsl:when test="$type/name = 'datetime'">
    @Basic( optional = <xsl:apply-templates select="." mode="nullable"/> )
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "<xsl:apply-templates select="." mode="columnName"/>", nullable = <xsl:apply-templates select="." mode="nullable"/> )
          </xsl:when>
          <xsl:otherwise>
    @Basic( optional = <xsl:apply-templates select="." mode="nullable"/> )
    @Column( name = "<xsl:apply-templates select="." mode="columnName"/>", nullable = <xsl:apply-templates select="." mode="nullable"/> )
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="name($type) = 'enumeration'">
        <xsl:call-template name="enumPattern">
          <xsl:with-param name="columnName"><xsl:apply-templates select="." mode="columnName"/></xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="name($type) = 'dataType'">
        <xsl:variable name="columns">
          <xsl:apply-templates select="." mode="columns"/>
        </xsl:variable>
    @Embedded
    @AttributeOverrides ( {
       <xsl:for-each select="exsl:node-set($columns)/column">
         @AttributeOverride( name = "<xsl:value-of select="attrname"/>", column = @Column( name = "<xsl:value-of select="name"/>" ) )
         <xsl:if test="position() != last()"><xsl:text>,</xsl:text></xsl:if>
       </xsl:for-each>
    } )
      </xsl:when>
      <xsl:otherwise>
    [NOT_SUPPORTED_ATTRIBUTE]
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>




  <xsl:template match="attribute|reference|collection" mode="nullable">
    <xsl:choose>
      <xsl:when test="starts-with(multiplicity, '0')">true</xsl:when>
      <xsl:otherwise>false</xsl:otherwise>
    </xsl:choose>
  </xsl:template>




  <xsl:template match="reference" mode="JPAAnnotation">
    <xsl:variable name="type" select="key('element', datatype/@xmiidref)"/>
    
    <xsl:choose>
      <xsl:when test="name($type) = 'primitiveType' or name($type) = 'enumeration'">
    [NOT_SUPPORTED_REFERENCE]
      </xsl:when>
      <xsl:otherwise>
    <!-- do not remove referenced entity : do not cascade delete -->
    @ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH } )
    @JoinColumn( name = "<xsl:apply-templates select="." mode="columnName"/>", referencedColumnName = "id", nullable = <xsl:apply-templates select="." mode="nullable"/> )
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
    
    
    

  <xsl:template match="reference" mode="JPAAnnotation_reference">
  @Transient
  </xsl:template>




  <xsl:template match="collection" mode="JPAAnnotation">
    <xsl:variable name="type" select="key('element', datatype/@xmiidref)"/>
    
    <xsl:choose>
      <xsl:when test="name($type) = 'primitiveType' or name($type) = 'enumeration' or name($type) = 'dataType'">
    [NOT_SUPPORTED_COLLECTION]
      </xsl:when>
      <xsl:otherwise>
    @OrderBy( value = "rank" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="container" )
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  
  
  <xsl:template name="enumPattern">
    <xsl:param name="columnName"/>

    @Basic( optional=<xsl:apply-templates select="." mode="nullable"/> )
    @Enumerated( EnumType.STRING )
    @Column( name = "<xsl:apply-templates select="." mode="columnName"/>", nullable = <xsl:apply-templates select="." mode="nullable"/> )
  </xsl:template>

  
  
  
  <xsl:template match="objectType|dataType" mode="hashcode_equals">
    <xsl:variable name="name" select="name"/>

  /**
   * Returns equals from id attribute here. Child classes can override this method to allow deep equals with
   * attributes / references / collections
   *
   * @param object the reference object with which to compare.
   * @param isDeep true means to call hashCode(sb, true) for all attributes / references / collections which are
   *        MetadataElement implementations
   *
   * @return &lt;code&gt;true&lt;/code&gt; if this object is the same as the obj argument; &lt;code&gt;false&lt;/code&gt; otherwise.
   */
  @Override
  public boolean equals(final Object object, final boolean isDeep) {
    /* identity, nullable, class and identifiers checks */
    if( !(super.equals(object, isDeep))) {
		  return false;
		}
    
    /* do check values (attributes / references / collections) */  
    <xsl:choose>
      <xsl:when test="name() = 'dataType'">
    if (true) {
      </xsl:when>
      <xsl:otherwise>
    if (isDeep) {
      </xsl:otherwise>
    </xsl:choose>
    
      final <xsl:value-of select="$name"/> other = (<xsl:value-of select="$name"/>) object;
      <xsl:for-each select="attribute">
        if (! areEquals(this.<xsl:value-of select="name"/>, other.<xsl:value-of select="name"/>)) {
           return false;
        }
		  </xsl:for-each>
    }
		
		return true;
	}
  </xsl:template>

  
  
<!-- persistence.xml configuration file -->  
  
  <xsl:template match="model" mode="jpaConfig">
    <xsl:variable name="file" select="'META-INF/persistence.xml'"/>
    
    <!-- reading persistence-template.xml file : -->
    
    <xsl:variable name="jpaConf" select="document('../input/persistence.xml')"/>
    
    <!-- open file for global jpa configuration -->
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}" format="persistenceInfo">

<xsl:apply-templates select="$jpaConf" mode="otherXml">
  <xsl:with-param name="model" select="."/>
</xsl:apply-templates>

    </xsl:result-document>
  </xsl:template>




  <xsl:template match="@*|node()" mode="otherXml">
    <xsl:param name="model"/>

    <xsl:choose>
      <xsl:when test="name() = 'properties'">
&cr;
&cr;
<xsl:comment>generated JPA entities</xsl:comment>
&cr;
&cr;
    <xsl:element name="class" namespace="http://java.sun.com/xml/ns/persistence">
      <xsl:text>org.ivoa.tap.Schemas</xsl:text>
    </xsl:element>
    <xsl:element name="class" namespace="http://java.sun.com/xml/ns/persistence">
      <xsl:text>org.ivoa.tap.Tables</xsl:text>
    </xsl:element>
    <xsl:element name="class" namespace="http://java.sun.com/xml/ns/persistence">
      <xsl:text>org.ivoa.tap.Columns</xsl:text>
    </xsl:element>

<xsl:for-each select="$model/package">
  <xsl:call-template name="packageJpaConfig">
    <xsl:with-param name="package" select="."/>
    <xsl:with-param name="path" select="$root_package"/>
  </xsl:call-template>
</xsl:for-each>
&cr;
&cr;
<xsl:comment>JPA Properties</xsl:comment>
&cr;
&cr;
<xsl:element name="properties" namespace="http://java.sun.com/xml/ns/persistence">
  
  <xsl:apply-templates select="child::*"  mode="otherXml"/>
  
&cr;
&cr;
<xsl:comment>
  &lt;property name="eclipseLink.cache.type.Cardinality" value="Full"/&gt;
  &lt;property name="eclipseLink.cache.type.DataType" value="Full"/&gt;
</xsl:comment>

</xsl:element>

      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:apply-templates select="@*|node()"  mode="otherXml">
            <xsl:with-param name="model" select="$model"/>
          </xsl:apply-templates>        
        </xsl:copy>    
        </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>
  
  
  
  
  <xsl:template name="packageJpaConfig">
    <xsl:param name="package"/>
    <xsl:param name="path"/>
    
    <xsl:variable name="newpath">
      <xsl:choose>
        <xsl:when test="$path">
          <xsl:value-of select="concat($path,'.',$package/name)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$package/name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:message>package = <xsl:value-of select="$newpath"></xsl:value-of></xsl:message>
    
    <xsl:for-each select="$package/objectType|$package/dataType">
      <xsl:element name="class" namespace="http://java.sun.com/xml/ns/persistence">
        <xsl:value-of select="$newpath"/><xsl:text>.</xsl:text><xsl:value-of select="name"/>
      </xsl:element>
    </xsl:for-each>
    
    <xsl:for-each select="$package/package">
      <xsl:call-template name="packageJpaConfig">
        <xsl:with-param name="package" select="."/>
        <xsl:with-param name="path" select="$newpath"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  
</xsl:stylesheet>