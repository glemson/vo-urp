<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
]>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- 
  This XSLT script transforms a data model from our intermediate representation to 
  Java classes compliant with JPA 1.0 & JAXB 2.1 specifications.
  
  Java 1.5+ is required by these two libraries.
-->


  <xsl:import href="common.xsl"/>
  <xsl:import href="jpa.xsl"/>
  <xsl:import href="jaxb.xsl"/>
  
  
  <xsl:output method="text" encoding="UTF-8" indent="yes" />
  <xsl:output name="packageInfo" method="html" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  
  <xsl:strip-space elements="*" />
  
  <xsl:key name="element" match="*//*" use="@xmiid"/>
  <!-- Next is to check wheher package needs handling, or is internal -->
  <!-- <xsl:key name="modelpackage" match="*//package[not(name='IVOAValueTypes')]" use="@xmiid"/>  -->
  <xsl:key name="modelpackage" match="*//package" use="@xmiid"/>
  
  <xsl:param name="lastModified"/>
  <xsl:param name="lastModifiedText"/>
  
  
  <xsl:param name="root_package"/> <!--  select="'org.ivoa.'" -->
  <xsl:param name="model_package"/>
  <xsl:param name="targetnamespace_root"/> 
  <xsl:param name="persistence.xml"/>
  
  <!-- next could be parameters -->
  <xsl:variable name="root_package_dir" select="replace($root_package,'[.]','/')"/>
  
  
  <!-- 
    Use '{NOTE/TODO} [JPA_COMPLIANCE] :' to indicate a violated rule and explain why.

  
    JPA 1.0 specification extract for Entity definition :
    

    "2.1 Requirements on the Entity Class 

    The entity class must be annotated with the Entity annotation or denoted in the XMLdescriptor as an entity. 

    The entity class must have a no-arg constructor. The entity class may have other constructors as well. 
    The no-arg constructor must be public or protected. 

    The entity class must be a top-level class. An enum or interface should not be designated as an entity. 

The entity class must not be final. No methods or persistent instance variables of the entity class may be final.

    Entities support inheritance, polymorphic associations, and polymorphic queries. 

    Both abstract and concrete classes can be entities. Entities may extend non-entity classes as well as entity classes, 
    and non-entity classes may extend entity classes. 

    The persistent state of an entity is accessed by the persistence provider runtime either via JavaBeans
    style property accessors or via instance variables. A single access type (field or property access) applies
    to an entity hierarchy. When annotations are used, the placement of the mapping annotations on either
    the persistent fields or persistent properties of the entity class specifies the access type as being either
    field- or property-based access respectively.

        - If the entity has field-based access, the persistence provider runtime accesses instance variables
        directly. All non-transient instance variables that are not annotated with the Transient
        annotation are persistent. When field-based access is used, the object/relational mapping annotations
        for the entity class annotate the instance variables.

        - If the entity has property-based access, the persistence provider runtime accesses persistent
        state via the property accessor methods. All properties not annotated with the Transient
        annotation are persistent. The property accessor methods must be public or protected. When
        property-based access is used, the object/relational mapping annotations for the entity class
        annotate the getter property accessors.

        - Mapping annotations cannot be applied to fields or properties that are transient or Transient.

        - The behavior is unspecified if mapping annotations are applied to both persistent fields and
        properties or if the XML descriptor specifies use of different access types within a class hierarchy.

NOTE [JPA_COMPLIANCE] : FIELD-BASES ACCESS is the strategy chosen for persistent attributes.

    It is required that the entity class follow the method signature conventions for JavaBeans read/write
    properties (as defined by the JavaBeans Introspector class) for persistent properties when persistent
    properties are used.

    In this case, for every persistent property property of type T of the entity, there is a getter method, get-
    Property, and setter method setProperty. For boolean properties, isProperty is an alternative name for
    the getter method.

    For single-valued persistent properties, these method signatures are:
    - T getProperty()
    - void setProperty(T t)

    Collection-valued persistent fields and properties must be defined in terms of one of the following collection-
    valued interfaces regardless of whether the entity class otherwise adheres to the JavaBeans
    method conventions noted above and whether field or property-based access is used:
    java.util.Collection, java.util.Set, java.util.List[4], java.util.Map.[5]

  [4] Portable applications should not expect the order of lists to be maintained across persistence contexts unless the OrderBy construct
  is used and the modifications to the list observe the specified ordering. The order is not otherwise persistent.

  [5] The implementation type may be used by the application to initialize fields or properties before the entity is made persistent; subsequent
  access must be through the interface type once the entity becomes managed (or detached).

    For collection-valued persistent properties, type T must be one of these collection interface types in the
    method signatures above. Generic variants of these collection types may also be used (for example,
    Set<Order>).

    In addition to returning and setting the persistent state of the instance, the property accessor methods
    may contain other business logic as well, for example, to perform validation. The persistence provider
    runtime executes this logic when property-based access is used.

        Caution should be exercised in adding business logic to the accessor methods when property-
        based access is used. The order in which the persistence provider runtime calls these
        methods when loading or storing persistent state is not defined. Logic contained in such methods
        therefore cannot rely upon a specific invocation order.

    If property-based access is used and lazy fetching is specified, portable applications should not directly
    access the entity state underlying the property methods of managed instances until after it has been
    fetched by the persistence provider.[6]

  [6] Lazy fetching is a hint to the persistence provider and can be specified by means of the Basic, OneToOne, OneToMany, Many-
  ToOne, and ManyToMany annotations and their XML equivalents. See chapter 9.

    Runtime exceptions thrown by property accessor methods cause the current transaction to be rolled
    back. Exceptions thrown by such methods when used by the persistence runtime to load or store persistent
    state cause the persistence runtime to rollback the current transaction and to throw a PersistenceException
    that wraps the application exception.

    Entity subclasses may override the property accessor methods. However, portable applications must not
    override the object/relational mapping metadata that applies to the persistent fields or properties of
    entity superclasses.

    The persistent fields or properties of an entity may be of the following types: Java primitive types;
    java.lang.String; other Java serializable types (including wrappers of the primitive types,
    java.math.BigInteger, java.math.BigDecimal, java.util.Date,
    java.util.Calendar[7], java.sql.Date, java.sql.Time, java.sql.Timestamp,
    user-defined serializable types, byte[], Byte[], char[], and Character[]); enums; entity
    types and/or collections of entity types; and embeddable classes (see section 2.1.5).
    
    Object/relational mapping metadata may be specified to customize the object-relational mapping, and
    the loading and storing of the entity state and relationships. See Chapter 9.

  -->




  <!-- main pattern : processes for root node model -->
  <xsl:template match="/">
    <xsl:message>Root package = <xsl:value-of select="$root_package"/></xsl:message>
    <xsl:message>Root package dir= <xsl:value-of select="$root_package_dir"/></xsl:message>
    <xsl:message>Model package = <xsl:value-of select="$model_package"/></xsl:message>
    <xsl:apply-templates select="model"/>
  </xsl:template>
  
  
  
  
  <!-- model pattern : generates gen-log and processes nodes package and generates the ModelVersion class and persistence.xml -->
  <xsl:template match="model">
    <xsl:message>Model = <xsl:value-of select="name"></xsl:value-of></xsl:message>
-- Generating Java code for model <xsl:value-of select="name"/>.
-- last modification date of the UML model <xsl:value-of select="$lastModifiedText"/>
    
    <xsl:for-each select="package[key('modelpackage',@xmiid)]">
      <xsl:call-template name="package">
        <xsl:with-param name="dir" select="$root_package_dir"/>
        <xsl:with-param name="path" select="$root_package"/>
      </xsl:call-template>
    </xsl:for-each>
    
    <xsl:for-each select="profile/package[key('modelpackage',@xmiid)]">
      <xsl:call-template name="package">
        <xsl:with-param name="dir" select="$root_package_dir"/>
        <xsl:with-param name="path" select="$root_package"/>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:apply-templates select="." mode="modelVersion" />
    
    <xsl:apply-templates select="." mode="jpaConfig" />
    
    <xsl:apply-templates select="." mode="jaxb.context.classpath" />
    
  </xsl:template>  
  
  
  
  
  <xsl:template name="package">
    <xsl:param name="dir"/>
    <xsl:param name="path"/>
    
    <xsl:variable name="newdir">
      <xsl:choose>
        <xsl:when test="$dir">
          <xsl:value-of select="concat($dir,'/',name)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="newpath">
      <xsl:choose>
        <xsl:when test="$path">
          <xsl:value-of select="concat($path,'.',name)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:message>package = <xsl:value-of select="$newpath"></xsl:value-of></xsl:message>
    
    <xsl:apply-templates select="." mode="packageDesc">
      <xsl:with-param name="dir" select="$newdir"/>
    </xsl:apply-templates>
    
    <xsl:apply-templates select="." mode="jaxb.index">
      <xsl:with-param name="dir" select="$newdir"/>
    </xsl:apply-templates>

    <xsl:for-each select="objectType">
      <xsl:call-template name="objectType">
        <xsl:with-param name="dir" select="$newdir"/>
        <xsl:with-param name="path" select="$newpath"/>
      </xsl:call-template>
    </xsl:for-each>
    
    <xsl:for-each select="dataType">
      <xsl:call-template name="dataType">
        <xsl:with-param name="dir" select="$newdir"/>
        <xsl:with-param name="path" select="$newpath"/>
      </xsl:call-template>
    </xsl:for-each>
    
    <xsl:for-each select="enumeration">
      <xsl:call-template name="enumeration">
        <xsl:with-param name="dir" select="$newdir"/>
        <xsl:with-param name="path" select="$newpath"/>
      </xsl:call-template>
    </xsl:for-each>
    
    <xsl:for-each select="package">
      <xsl:call-template name="package">
        <xsl:with-param name="dir" select="$newdir"/>
        <xsl:with-param name="path" select="$newpath"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>
  
  
  
  
  <xsl:template name="objectType">
    <xsl:param name="dir"/>
    <xsl:param name="path"/>
    
    <xsl:variable name="className" select="name" />
    <xsl:variable name="xmiid" select="@xmiid" />
    
    <xsl:variable name="hasChild">
      <xsl:choose>
        <xsl:when test="count(//objectType[extends/@xmiidref = $xmiid]) > 0">1</xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="hasExtends">
      <xsl:choose>
        <xsl:when test="count(extends) = 1">1</xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="hasList">
      <xsl:choose>
        <xsl:when test="count(collection) > 0">1</xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="file" select="concat('src/', $dir, '/', name, '.java')"/>

    <!-- open file for this class -->
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>

    <xsl:result-document href="{$file}">

      <xsl:apply-templates select="." mode="class">
        <xsl:with-param name="path" select="$path"/>
        <xsl:with-param name="className" select="$className"/>
        <xsl:with-param name="hasChild" select="$hasChild"/>
        <xsl:with-param name="hasExtends" select="$hasExtends"/>
        <xsl:with-param name="hasList" select="$hasList"/>
      </xsl:apply-templates>

    </xsl:result-document>
  </xsl:template>
  
  

  
  <xsl:template match="objectType|dataType" mode="typeimports">
    <xsl:variable name="thispackageid" select="./../@xmiid"/>
    <xsl:for-each select="key('element',distinct-values(extends/@xmiidref|container/@xmiidref|attribute/datatype/@xmiidref|reference/datatype/@xmiidref|collection/datatype/@xmiidref))" >
      <xsl:if test="./../@xmiid != $thispackageid and name(.) != 'primitiveType'"> 
        <xsl:variable name="qualifiedtype">
          <xsl:call-template name="QualifiedJavaType"><xsl:with-param name="type" select="."/></xsl:call-template>
        </xsl:variable>
        import <xsl:value-of select="$qualifiedtype"/>;
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  
  
  
  
  <xsl:template name="dataType">
    <xsl:param name="dir"/>
    <xsl:param name="path"/>
    
    <xsl:variable name="className" select="name" />

    <xsl:variable name="xmiid" select="@xmiid" />
    <xsl:variable name="hasChild">
      <xsl:choose>
        <xsl:when test="count(//extends[@xmiidref = $xmiid]) > 0">1</xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="hasExtends">
      <xsl:choose>
        <xsl:when test="count(extends) = 1">1</xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="file" select="concat('src/', $dir, '/', name, '.java')"/>

    <!-- open file for this class -->
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>

    <xsl:result-document href="{$file}">

      <xsl:apply-templates select="." mode="class">
        <xsl:with-param name="path" select="$path"/>
        <xsl:with-param name="className" select="$className"/>
        <xsl:with-param name="hasChild" select="$hasChild"/>
        <xsl:with-param name="hasExtends" select="$hasExtends"/>
        <xsl:with-param name="hasList" select="0"/>
      </xsl:apply-templates>
      
    </xsl:result-document>
  </xsl:template>




  
  <!-- template class creates a java class (JPA compliant) for UML object & data types -->
  <xsl:template match="objectType|dataType" mode="class">
    <xsl:param name="path"/>
    <xsl:param name="className"/>
    <xsl:param name="hasChild"/>
    <xsl:param name="hasExtends"/>
    <xsl:param name="hasList"/>

    <xsl:variable name="kind">
      <xsl:choose>
        <xsl:when test="name() = 'dataType'">DataType</xsl:when>
        <xsl:when test="name() = 'objectType'">Object</xsl:when>
      </xsl:choose>
    </xsl:variable>

    package <xsl:value-of select="$path"/>;

    <!-- imports -->
    import java.util.Date;
    <xsl:if test="$hasList = 1">
      import java.util.List;
      import java.util.ArrayList;
    </xsl:if>
    import java.util.Map;

    import javax.persistence.*;
    import javax.xml.bind.annotation.*;

    import static <xsl:value-of select="$root_package"/>.ModelVersion.LAST_MODIFICATION_DATE;

    import <xsl:value-of select="$model_package"/>.MetadataElement;
    <xsl:if test="reference[not(extends)]">
    import <xsl:value-of select="$model_package"/>.Reference;
    </xsl:if>
    
    <xsl:if test="$hasExtends = 0 or ($kind='Object' and reference)">
      import <xsl:value-of select="$model_package"/>.Metadata<xsl:value-of select="$kind"/>;
    </xsl:if>
    
    <xsl:apply-templates select="." mode="typeimports"/>

    /**
    * UML <xsl:value-of select="$kind"/>&bl;<xsl:value-of select="name"/> :
    *
    * <xsl:apply-templates select="." mode="desc" />
    *
    * @author generated by UML2 Generator tool (UML->XMI->XSLT->java code)
    */

    <xsl:apply-templates select="." mode="JPAAnnotation"/>
    <xsl:apply-templates select="." mode="JAXBAnnotation"/>

    public&bl;<xsl:if test="@abstract='true'">abstract</xsl:if>&bl;class <xsl:value-of select="$className"/>&bl;
    <xsl:choose>
      <xsl:when test="$hasExtends = 1">extends <xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="extends/@xmiidref"/></xsl:call-template></xsl:when>
      <xsl:otherwise>extends Metadata<xsl:value-of select="$kind"/></xsl:otherwise>
    </xsl:choose>
    &bl;{

      /** serial uid = last modification date of the UML model */
      private static final long serialVersionUID = LAST_MODIFICATION_DATE;

      <xsl:apply-templates select="." mode="JPASpecials">
        <xsl:with-param name="hasChild" select="$hasChild"/>
        <xsl:with-param name="hasExtends" select="$hasExtends"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="attribute" mode="declare" />
      <xsl:apply-templates select="collection" mode="declare" />
      <xsl:apply-templates select="reference" mode="declare" />
      
      /**
       * Creates a new <xsl:value-of select="$className"/>
       */
      public <xsl:value-of select="$className"/>() {
        super();
      }

      <xsl:choose>
        <xsl:when test="container">
        <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="container/@xmiidref"/></xsl:call-template></xsl:variable>
      /**
       * Creates a new <xsl:value-of select="$className"/> for the given Container Entity. &lt;br&gt;
       *
       * The Parent Container CAN NOT BE NULL
       *
       * @param pContainer the parent container CAN NOT BE NULL
       */
      public <xsl:value-of select="$className"/>(final <xsl:value-of select="$type"/> pContainer) {
        super();
        this.setContainer(pContainer);
      }
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="parentContainer"><xsl:call-template name="findParentContainer"><xsl:with-param name="xmiid" select="@xmiid"/></xsl:call-template></xsl:variable>
          
          <xsl:if test="string-length($parentContainer) > 0">
        <!-- if current object is a child of a contained class then add parent constructor for container management -->
        <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="$parentContainer"/></xsl:call-template></xsl:variable>
      /**
       * Creates a new <xsl:value-of select="$className"/> for the given Container Entity. &lt;br&gt;
       *
       * The Parent Container CAN NOT BE NULL
       *
       * @param pContainer the parent container CAN NOT BE NULL
       */
      public <xsl:value-of select="$className"/>(final <xsl:value-of select="$type"/> pContainer) {
        super(pContainer);
      }
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:apply-templates select="attribute|reference|collection" mode="getset"/>

      <xsl:if test="container">
        <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="container/@xmiidref"/></xsl:call-template></xsl:variable>
        <xsl:variable name="collection">
          <xsl:call-template name="upperFirst">
            <xsl:with-param name="val" select="container/@relation"/>
          </xsl:call-template>
        </xsl:variable>
        /** 
         * Returns the Container Entity == 'Parent'
         * @return the parent container Entity
         */
        public <xsl:value-of select="$type"/> getContainer() {
          return this.container;
        } 

        /** 
         * Sets the Container Entity == 'Parent' ONLY
         * @param pContainer the parent container
         */
        private void setContainerField(final <xsl:value-of select="$type"/> pContainer) {
          this.container = pContainer;
        }

        /** 
         * Sets the Container Entity == 'Parent' and adds this to the appropriate collection on the container.
         *
         * @param pContainer the parent container CAN NOT BE NULL
         *
         * @throws IllegalStateException if pContainer is null !
         */
        protected void setContainer(final <xsl:value-of select="$type"/> pContainer) {
          if (pContainer == null) {
            throw new IllegalStateException("The parent container can not be null !"); 
          }
          setContainerField(pContainer);
          pContainer.add<xsl:value-of select="$collection"/>(this);
        } 

        /** 
         * Returns the Rank in the collection
         * @return rank
         */
        public int getRank() {
          return this.rank;
        } 

        /** 
         * Sets the Rank
         * @param pRank rank in the parent collection
         */
        public void setRank(final int pRank) {
          this.rank = pRank;
        }
      </xsl:if>

      <xsl:if test="$hasExtends = 0">
        <xsl:if test="name() = 'objectType'">
          /** 
           * Returns Jpa version for optimistic locking
           * @return jpa version number
           */
          protected int getJpaVersion() {
            return this.jpaVersion;
          } 
          <xsl:if test="$hasChild = 1">
            /** 
             * Returns Class type (discriminator value) in inheritance hierarchy
             * @return class type
             */
            protected String getClassType() {
              return this.classType;
            } 
          </xsl:if>
        </xsl:if>
      </xsl:if>

      <xsl:apply-templates select="." mode="hashcode_equals"/>

      /**
       * Returns the property value given the property name.<br/>
       * Can be any property (internal, attribute, reference, collection) and all type must be supported (dataType,
       * objectType, enumeration)
       *
       * @param propertyName name of the property (like in UML model)
       *
       * @return property value or null if unknown or not defined
       */
      @Override
      public Object getProperty(final String propertyName) {
        // first : checks if propertyName is null or empty :
        if (propertyName == null) {
          return null;
        }
        // second : search in parent classes (maybe null) :
        Object res = super.getProperty(propertyName);

        <xsl:if test="container">
          if (PROPERTY_CONTAINER.equals(propertyName)) {
            return getContainer();
          }
          if (PROPERTY_RANK.equals(propertyName)) {
            return Integer.valueOf(getRank());
          }
        </xsl:if>

        <!-- TODO collection support ? -->
        <xsl:apply-templates select="attribute|reference|collection" mode="getProperty" />
        
        return res;
      }

      /**
       * Sets the property value to the given property name. <br/>
       * Can be any property (internal, attribute, reference, collection) and all type must be supported (dataType,
       * objectType, enumeration)
       *
       * @param propertyName name of the property (like in UML model)
       *
       * @param value to be set
       * 
       * @return true if property has been set
       */
      @Override
      public boolean setProperty(final String propertyName, final Object value) {
        // first : checks if propertyName is null or empty :
        if (propertyName == null) {
          return false;
        }
        // second : search in parent classes (maybe null) :
        boolean res = super.setProperty(propertyName, value);
        
        if (!res) {
        <xsl:if test="container">
          <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="container/@xmiidref"/></xsl:call-template></xsl:variable>
          <xsl:variable name="collection">
            <xsl:call-template name="upperFirst">
              <xsl:with-param name="val" select="container/@relation"/>
            </xsl:call-template>
          </xsl:variable>
          if (PROPERTY_CONTAINER.equals(propertyName)) {
            setContainerField((<xsl:value-of select="$type"/>) value);
            return true;
          }
          if (PROPERTY_RANK.equals(propertyName)) {
            setRank(((Integer)value).intValue());
            return true;
          }
        </xsl:if>

        <!-- TODO collection support ? -->
        <xsl:apply-templates select="attribute|reference" mode="setProperty" />
        }
        return res;
      }

      <xsl:if test="reference[not(subsets)]">
      /**
       * Sets all Reference fields to their appropriate value.&lt;br/&gt;
       */
      @Override
      protected void prepareReferencesForMarshalling()
      {
        super.prepareReferencesForMarshalling();
        <xsl:for-each select="reference">
          <xsl:variable name="name">
            <xsl:call-template name="upperFirst">
              <xsl:with-param name="val" select="name"/>
            </xsl:call-template>
          </xsl:variable>
        
        if(get<xsl:value-of select="$name"/>() != null) 
        {
          this.p_<xsl:value-of select="name"/> = get<xsl:value-of select="$name"/>().asReference();
          if(getStateFor(get<xsl:value-of select="$name"/>()).isToBeMarshalled())
               get<xsl:value-of select="$name"/>().setXmlId();
            
        }
        </xsl:for-each>
      }

      /**
       * Resets all Reference fields to null.&lt;br/&gt;
       */
      @Override
      protected void resetReferencesAfterMarshalling()
      {
        super.prepareReferencesForMarshalling();
        <xsl:for-each select="reference">
        this.p_<xsl:value-of select="name"/> = null;
        </xsl:for-each>
      }
      
      </xsl:if>
      /**
       * Puts the string representation in the given string buffer : &lt;br&gt; 
       * "Type =[class name @ hashcode] : { field name = field value , ...}"
       *
       * @param sb given string buffer to fill
       * @param isDeep true means to call deepToString(sb, true, ids) for all attributes / references / collections which are
       *        MetadataElement implementations
       * @param ids identity map to avoid cyclic loops
       *
       * @return stringbuffer the given string buffer filled with the string representation
       */
      <xsl:if test="$hasExtends = 1">
      @Override
      </xsl:if>
      protected StringBuilder deepToString(final StringBuilder sb, final boolean isDeep, final Map&lt;MetadataElement, Object&gt; ids) {
      <xsl:if test="$hasExtends = 1">
        super.deepToString(sb, isDeep, ids);
      </xsl:if>

        sb.append("\n[ <xsl:value-of select="$className"/>");
        sb.append("={");

      <xsl:if test="container">
        sb.append("container=");
        if (getContainer() != null) {
          // short toString :
          MetadataElement.deepToString(sb, false, ids, getContainer());
        }
        sb.append(" | ");
      </xsl:if>

        <xsl:apply-templates select="attribute|reference|collection" mode="tostring" />

        return sb.append("} ]");
      }

    }
  </xsl:template>
  
  
  
  
  <xsl:template name="enumeration">
    <xsl:param name="dir"/>
    <xsl:param name="path"/>
    
    <xsl:variable name="file" select="concat('src/', $dir, '/', name, '.java')"/>

    <!-- open file for this class -->
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>

    <xsl:result-document href="{$file}">

      package <xsl:value-of select="$path"/>;

      <!-- imports -->
      import javax.persistence.*;
      import javax.xml.bind.annotation.*;
      
      /**
      * UML Enumeration <xsl:value-of select="name"/> :
      *
      * <xsl:apply-templates select="." mode="desc" />
      *
      * @author generated by UML2 Generator tool (UML->XMI->XSLT->java code)
      */
      <xsl:apply-templates select="." mode="JAXBAnnotation"/>
      public enum <xsl:value-of select="name"/>&bl;{
      
        <xsl:apply-templates select="literal"  />

        /** string representation */
        private final String value;

        /**
         * Creates a new <xsl:value-of select="name"/> Enumeration Literal
         *
         * @param v string representation
         */
        <xsl:value-of select="name"/>(final String v) {
            value = v;
        }

        /**
         * Returns string representation
         * @return string representation
         */
        public final String value() {
            return this.value;
        }

        /**
         * Returns <xsl:value-of select="name"/> Enumeration Literal corresponding to the given string representation
         *
         * @param v string representation
         *
         * @return value <xsl:value-of select="name"/> Enumeration Literal
         *
         * @throws IllegalArgumentException if string representation not found
         */
        public final static <xsl:value-of select="name"/> fromValue(final String v) {
          for (<xsl:value-of select="name"/> c : <xsl:value-of select="name"/>.values()) {
              if (c.value.equals(v)) {
                  return c;
              }
          }
          throw new IllegalArgumentException("<xsl:value-of select="name"/>.fromValue : unknown value : " + v);
        }
      
      }
    </xsl:result-document>
  </xsl:template>
  
  
  

  
  
  
  
  <xsl:template match="attribute" mode="declare">
    <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="datatype/@xmiidref"/></xsl:call-template></xsl:variable>
    /** 
    * Attribute <xsl:value-of select="name"/> :
    * <xsl:apply-templates select="." mode="desc" />
    * (
    * Multiplicity : <xsl:value-of select="multiplicity"/>
    <xsl:if test="constraints/maxLength">
    * , MaxLength : <xsl:value-of select="constraints/maxLength"/>
    </xsl:if>
    * )
    */
    <xsl:apply-templates select="." mode="JPAAnnotation"/>
    <xsl:apply-templates select="." mode="JAXBAnnotation"/>
    private <xsl:value-of select="$type"/>&bl;<xsl:value-of select="name"/>;
  </xsl:template>
  
  
  
  
  <xsl:template match="attribute" mode="getset">
    <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="datatype/@xmiidref"/></xsl:call-template></xsl:variable>
    <xsl:variable name="name">
      <xsl:call-template name="upperFirst">
        <xsl:with-param name="val" select="name"/>
      </xsl:call-template>
    </xsl:variable>
    /**
    * Returns <xsl:value-of select="name"/> Attribute
    * @return <xsl:value-of select="name"/> Attribute
    */
    public <xsl:value-of select="$type"/>&bl;get<xsl:value-of select="$name"/>() {
    return this.<xsl:value-of select="name"/>;
    }
    /**
    * Defines <xsl:value-of select="name"/> Attribute
    * @param p<xsl:value-of select="$name"/> value to set
    */
    public void set<xsl:value-of select="$name"/>(final <xsl:value-of select="$type"/> p<xsl:value-of select="$name"/>) {
    this.<xsl:value-of select="name"/> = p<xsl:value-of select="$name"/>;
    }
  </xsl:template>




  <xsl:template match="attribute" mode="setProperty">
    <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="datatype/@xmiidref"/></xsl:call-template></xsl:variable>
    <xsl:variable name="name">
      <xsl:call-template name="upperFirst">
        <xsl:with-param name="val" select="name"/>
      </xsl:call-template>
    </xsl:variable>
    if ("<xsl:value-of select="name"/>".equals(propertyName)) {
      set<xsl:value-of select="$name"/>((<xsl:value-of select="$type"/>)value);
      return true;
    }
  </xsl:template>
  
  
  
  
  <xsl:template match="collection" mode="declare">
    <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="datatype/@xmiidref"/></xsl:call-template></xsl:variable>
    /** 
    * Collection <xsl:value-of select="name"/> :
    * <xsl:apply-templates select="." mode="desc" />
    * (
    * Multiplicity : <xsl:value-of select="multiplicity"/>
    * )
    */
    <xsl:apply-templates select="." mode="JPAAnnotation"/>
    <xsl:apply-templates select="." mode="JAXBAnnotation"/>
    private List&lt;<xsl:value-of select="$type"/>&gt;&bl;<xsl:value-of select="name"/> = null;
  </xsl:template>
  
  
  
  
  <xsl:template match="collection" mode="getset">
    <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="datatype/@xmiidref"/></xsl:call-template></xsl:variable>
    <xsl:variable name="name">
      <xsl:call-template name="upperFirst">
        <xsl:with-param name="val" select="name"/>
      </xsl:call-template>
    </xsl:variable>
    /**
    * Returns <xsl:value-of select="name"/> Collection
    * @return <xsl:value-of select="name"/> Collection
    */
    public List&lt;<xsl:value-of select="$type"/>&gt;&bl;get<xsl:value-of select="$name"/>() {
    return this.<xsl:value-of select="name"/>;
    }
    /**
    * Defines <xsl:value-of select="name"/> Collection
    * @param p<xsl:value-of select="$name"/> collection to set
    */
    public void set<xsl:value-of select="$name"/>(final List&lt;<xsl:value-of select="$type"/>&gt; p<xsl:value-of select="$name"/>) {
    this.<xsl:value-of select="name"/> = p<xsl:value-of select="$name"/>;
    }
    /**
    * Add a <xsl:value-of select="$type"/> to the collection
    * @param p<xsl:value-of select="$type"/>&bl;<xsl:value-of select="$type"/> to add
    */
    public void add<xsl:value-of select="$name"/>(final <xsl:value-of select="$type"/> p<xsl:value-of select="$type"/>) {
      if(this.<xsl:value-of select="name"/> == null) {
        this.<xsl:value-of select="name"/> = new ArrayList&lt;<xsl:value-of select="$type"/>&gt;();
      }
      final int rank = this.<xsl:value-of select="name"/>.size();
      p<xsl:value-of select="$type"/>.setRank(rank);
      this.<xsl:value-of select="name"/>.add(p<xsl:value-of select="$type"/>);
    }
  </xsl:template>




  <xsl:template match="reference" mode="declare">
  <xsl:if test="not(subsets)">
    <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="datatype/@xmiidref"/></xsl:call-template></xsl:variable>
    /** 
    * Reference <xsl:value-of select="name"/> :
    * <xsl:apply-templates select="." mode="desc" />
    * (
    * Multiplicity : <xsl:value-of select="multiplicity"/>
    * )
    */
    <xsl:apply-templates select="." mode="JPAAnnotation"/>
    <xsl:apply-templates select="." mode="JAXBAnnotation"/>
    private <xsl:value-of select="$type"/>&bl;<xsl:value-of select="name"/> = null;
    /**
    * "lazy" version of the <xsl:value-of select="name"/> reference.
    * Used by XML (un)marshallers to resolve possibly inderectly referenced resource <xsl:value-of select="$type"/>. 
    */
    <xsl:apply-templates select="." mode="JPAAnnotation_reference"/>
    <xsl:apply-templates select="." mode="JAXBAnnotation_reference"/>
    protected Reference&bl;p_<xsl:value-of select="name"/> = null;
    </xsl:if>
  </xsl:template>
  
  
  
  
  <xsl:template match="reference" mode="getset">
    <xsl:variable name="ctype" select="key('element',datatype/@xmiidref)"/>

    <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="datatype/@xmiidref"/></xsl:call-template></xsl:variable>
    <xsl:variable name="name">
      <xsl:call-template name="upperFirst">
        <xsl:with-param name="val" select="name"/>
      </xsl:call-template>
    </xsl:variable>
    /**
    * Returns <xsl:value-of select="name"/> Reference<br/>
    * If the <xsl:value-of select="name"/> variable is null but its "lazy" version
    * p_<xsl:value-of select="name"/> is not, that lazy reference will be resolved to the actual object.
    * @return <xsl:value-of select="name"/> Reference
    */
    public <xsl:value-of select="$type"/>&bl;get<xsl:value-of select="$name"/>() {
    <xsl:choose>
      <xsl:when test="subsets">
        return (<xsl:value-of select="$type"/>)super.get<xsl:value-of select="$name"/>();
      </xsl:when>
      <xsl:otherwise>
        if(this.<xsl:value-of select="name"/> == null &amp;&amp; this.p_<xsl:value-of select="name"/> != null) {
          this.<xsl:value-of select="name"/> = (<xsl:value-of select="$type"/>)resolve(this.p_<xsl:value-of select="name"/>, <xsl:value-of select="$type"/>.class);
        }
        return this.<xsl:value-of select="name"/>;
      </xsl:otherwise>
    </xsl:choose>
    }
    /**
    * Defines <xsl:value-of select="name"/> Reference
    * @param p<xsl:value-of select="$name"/> reference to set
    */
    public void set<xsl:value-of select="$name"/>(final <xsl:value-of select="$type"/> p<xsl:value-of select="$name"/>) {
    <xsl:choose>
      <xsl:when test="subsets">
        super.set<xsl:value-of select="$name"/>(p<xsl:value-of select="$name"/>);
      </xsl:when>
      <xsl:otherwise>
        this.<xsl:value-of select="name"/> = p<xsl:value-of select="$name"/>;
      </xsl:otherwise>
    </xsl:choose>
    }
  </xsl:template>
  
  
  
  
  <xsl:template match="reference" mode="setProperty">
    <xsl:variable name="ctype" select="key('element',datatype/@xmiidref)"/>

    <xsl:variable name="type"><xsl:call-template name="JavaType"><xsl:with-param name="xmiid" select="datatype/@xmiidref"/></xsl:call-template></xsl:variable>
    <xsl:variable name="name">
      <xsl:call-template name="upperFirst">
        <xsl:with-param name="val" select="name"/>
      </xsl:call-template>
    </xsl:variable>

    if ("<xsl:value-of select="name"/>".equals(propertyName)) {
      set<xsl:value-of select="$name"/>((<xsl:value-of select="$type"/>)value);
      return true;
    }
    if ("<xsl:value-of select="name"/>Ref".equals(propertyName)) {
      // sets JAXB Reference Field
      this.p_<xsl:value-of select="name"/> = newReference((MetadataObject)value);
      return true;
    }
  </xsl:template>




  <xsl:template match="literal" >
    /** 
    * Value <xsl:value-of select="value"/> :
    * 
    * <xsl:apply-templates select="." mode="desc" />
    */
    <xsl:variable name="up">
      <xsl:call-template name="constant">
        <xsl:with-param name="text" select="value"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:apply-templates select="." mode="JAXBAnnotation"/>
    <xsl:value-of select="$up"/>("<xsl:value-of select="value"/>")
    <xsl:choose>
      <xsl:when test="position() != last()"><xsl:text>,</xsl:text></xsl:when>
      <xsl:otherwise><xsl:text>;</xsl:text></xsl:otherwise>
    </xsl:choose>
    &cr;
  </xsl:template>
  
  
  
  
  <xsl:template match="attribute|reference|collection" mode="tostring">
    <xsl:variable name="name">
      <xsl:call-template name="upperFirst">
        <xsl:with-param name="val" select="name"/>
      </xsl:call-template>
    </xsl:variable>
    sb.append("<xsl:value-of select="name"/>=");
    if (get<xsl:value-of select="$name"/>() != null) {
      MetadataElement.deepToString(sb, isDeep, ids, get<xsl:value-of select="$name"/>());
    }
    <xsl:if test="position() != last()">sb.append(", ");</xsl:if>
  </xsl:template>




  <xsl:template match="attribute|reference|collection" mode="getProperty">
    <xsl:variable name="name">
      <xsl:call-template name="upperFirst">
        <xsl:with-param name="val" select="name"/>
      </xsl:call-template>
    </xsl:variable>
    if ("<xsl:value-of select="name"/>".equals(propertyName)) {
      return get<xsl:value-of select="$name"/>();
    }
  </xsl:template>




  <xsl:template match="*" mode="desc">
    <xsl:choose>
      <xsl:when test="count(description) > 0"><xsl:value-of select="description" disable-output-escaping="yes"/></xsl:when>
      <xsl:otherwise>TODO : Missing description : please, update your UML model asap.</xsl:otherwise>
    </xsl:choose>
  </xsl:template>  
  
  
  
  
  <!-- specific documents --> 

  <!-- ModelVersion.java -->
  <xsl:template match="model" mode="modelVersion">
    <xsl:variable name="file" select="concat('src/', $root_package_dir,'/','ModelVersion.java')"/>
    <!-- open file for this class -->
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}">
      package <xsl:value-of select="$root_package"/>;
      
      /**
      * Version class for <xsl:value-of select="name"/> :
      *
      * <xsl:apply-templates select="." mode="desc" />
      *
      * @author generated by UML2 Generator tool (UML->XMI->XSLT->java code)
      */
      public final class ModelVersion { 
      
      /** last modification date of the UML model */
      public final static long LAST_MODIFICATION_DATE = <xsl:value-of select="$lastModified"/>l;
      
      private ModelVersion() {
      // forbidden constructor
      }
      
      }
    </xsl:result-document>
  </xsl:template>
  
  
  
  
  <!-- package.html -->
  <xsl:template match="package" mode="packageDesc">
    <xsl:param name="dir"/>
    <xsl:variable name="file" select="concat('src/',$dir,'/package.html')"/>
    <!-- open file for this class -->
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
    <xsl:result-document href="{$file}" format="packageInfo">
      <html>
        <head>
          <title>Package Information</title>
          <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        </head>
        <body>&cr;
          <xsl:apply-templates select="." mode="desc" />
        </body>
      </html>
    </xsl:result-document>
  </xsl:template>
  
  
  
  
    
  
  
  
  <xsl:template name="QualifiedJavaType">
    <xsl:param name="type"/>
<!--     <xsl:param name="xmiid"/>  
    <xsl:variable name="type" select="key('element',$xmiid)"/>
     <xsl:message>QualifiedJavaType: XMIID = <xsl:value-of select="$xmiid"/></xsl:message>
   --> 
    <xsl:choose>
      <xsl:when test="name($type) = 'primitiveType'">
        <xsl:choose>
          <xsl:when test="$type/name = 'boolean'">Boolean</xsl:when>
          <xsl:when test="$type/name = 'short'">Short</xsl:when>
          <xsl:when test="$type/name = 'integer'">Integer</xsl:when>
          <xsl:when test="$type/name = 'long'">Long</xsl:when>
          <xsl:when test="$type/name = 'float'">Float</xsl:when>
          <xsl:when test="$type/name = 'real'">Double</xsl:when>
          <xsl:when test="$type/name = 'double'">Double</xsl:when>
          <xsl:when test="$type/name = 'datetime'">Date</xsl:when>
          <xsl:when test="$type/name = 'string'">java.lang.String</xsl:when>
          <xsl:otherwise>java.lang.String</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="val">
          <xsl:call-template name="upperFirst">
            <xsl:with-param name="val" select="$type/name"/>
          </xsl:call-template>
        </xsl:variable>
            <xsl:variable name="path">
          <xsl:call-template name="package-path">
             <xsl:with-param name="packageid"><xsl:value-of select="$type/../@xmiid"/></xsl:with-param>
             <xsl:with-param name="delimiter">.</xsl:with-param>
           </xsl:call-template>
        </xsl:variable>
        <xsl:if test="$root_package"><xsl:value-of select="concat($root_package,'.')"/></xsl:if><xsl:value-of select="$path"/>.<xsl:value-of select="$val"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template> 
  


  <xsl:template name="findParentContainer">
    <xsl:param name="xmiid"/>
    <xsl:variable name="class" select="key('element',$xmiid)"/>
    <xsl:choose>
      <xsl:when test="not($class/container)">
        <xsl:if test="$class/extends">
          <xsl:call-template name="findParentContainer">
            <xsl:with-param name="xmiid" select="$class/extends/@xmiidref"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$class/container/@xmiidref"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
</xsl:stylesheet>
