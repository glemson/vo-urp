<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY cr "<xsl:text>
</xsl:text>">
]>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:uml="http://schema.omg.org/spec/UML/2.0"
                xmlns:xmi="http://schema.omg.org/spec/XMI/2.1">
  
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
  <xsl:strip-space elements="*" />
  
  <!-- xml index on xml:id -->
  <xsl:key name="classid" match="*" use="@xmi:id"/>
  
  
  
  <!-- main -->
  <xsl:template match="/">
    <check>
      <xsl:choose>
        <xsl:when test="namespace-uri(/*) != 'http://schema.omg.org/spec/XMI/2.1'">
          <ERROR>Wrong namespace: this script can convert only XMI v2.1</ERROR>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="xmi:XMI"/>
        </xsl:otherwise>
      </xsl:choose>
    </check>
  </xsl:template>
  
  
  
  
  <!-- check xmi:XMI : process children -->
  <xsl:template match="xmi:XMI">
    <xsl:apply-templates select="uml:Model"/>
  </xsl:template>
  
  
  
  
  <!-- check uml:Model : process only uml:Package nodes -->
  <xsl:template match="uml:Model">
    &cr;&cr;
    <model name="{@name}">
      <xsl:call-template name="check-name">
        <xsl:with-param name="text" select="@name"/>
      </xsl:call-template>
      <xsl:call-template name="check-comment">
        <xsl:with-param name="text" select="ownedComment/@body"/>
      </xsl:call-template>
      
      <xsl:apply-templates select="*[@xmi:type='uml:Package']"/>
    </model>
    &cr;&cr;
  </xsl:template>
  
  
  
  
  <!-- check uml:Package : process uml:DataType, uml:Enumeration, uml:Class nodes -->
  <xsl:template match="*[@xmi:type='uml:Package']">
    <!-- check if a name is defined to avoid bad nodes -->
    <xsl:if test="count(@name) > 0">
      &cr;&cr;
      <package name="{@name}">
        
        <xsl:call-template name="check-name">
          <xsl:with-param name="text" select="@name"/>
        </xsl:call-template>
        <xsl:call-template name="check-comment">
          <xsl:with-param name="text" select="ownedComment/@body"/>
        </xsl:call-template>
        
        <xsl:apply-templates select=".//*[@xmi:type='uml:DataType']" />
        
        <xsl:apply-templates select=".//*[@xmi:type='uml:Enumeration']" />
        
        <xsl:apply-templates select=".//*[@xmi:type='uml:Class']" />
        
        <xsl:apply-templates select="./*[@xmi:type='uml:Package']"/>
        
      </package>
      &cr;&cr;
    </xsl:if>
  </xsl:template>
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:DataType']">
    &cr;
    <datatype name="{@name}">
      <xsl:call-template name="check-name">
        <xsl:with-param name="text" select="@name"/>
      </xsl:call-template>
      <xsl:call-template name="check-comment">
        <xsl:with-param name="text" select="ownedComment/@body"/>
      </xsl:call-template>
      
      <xsl:if test="*[@xmi:type='uml:Generalization']">
        <xsl:apply-templates select="*[@xmi:type='uml:Generalization']"/>
      </xsl:if>
      
      <xsl:apply-templates select=".//*[@xmi:type='uml:Property']"/>
      
    </datatype>
    &cr;&cr;
  </xsl:template>
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:Enumeration']">
    &cr;
    <enumeration name="{@name}">
      <xsl:call-template name="check-name">
        <xsl:with-param name="text" select="@name"/>
      </xsl:call-template>
      <xsl:call-template name="check-comment">
        <xsl:with-param name="text" select="ownedComment/@body"/>
      </xsl:call-template>
      
      <xsl:apply-templates select="*[@xmi:type='uml:EnumerationLiteral']"/>
    </enumeration>
    &cr;&cr;
  </xsl:template>
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:EnumerationLiteral']">
    <literal name="{@name}">
      <xsl:call-template name="check-name">
        <xsl:with-param name="text" select="@name"/>
      </xsl:call-template>
      <xsl:call-template name="check-comment">
        <xsl:with-param name="text" select="ownedComment/@body"/>
      </xsl:call-template>
    </literal>
  </xsl:template>
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:Class']">
    &cr;
    <class name="{@name}">
      <xsl:call-template name="check-name">
        <xsl:with-param name="text" select="@name"/>
      </xsl:call-template>
      <xsl:call-template name="check-comment">
        <xsl:with-param name="text" select="ownedComment/@body"/>
      </xsl:call-template>
      
      <xsl:if test="*[@xmi:type='uml:Generalization']">
        <xsl:apply-templates select="*[@xmi:type='uml:Generalization']"/>
      </xsl:if>
      
      <xsl:apply-templates select=".//*[@xmi:type='uml:Property']"/>
      
    </class>
    &cr;
    
  </xsl:template>
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:Generalization']">
    <xsl:variable name="classe">
      <xsl:call-template name="class-from-id">
        <xsl:with-param name="id" select="@general"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:call-template name="check-class">
      <xsl:with-param name="text" select="$classe"/>
    </xsl:call-template>
  </xsl:template>
  
  
  
  
  <xsl:template match="*[@xmi:type='uml:Property']">
    &cr;
    <property name="{@name}">
      <xsl:call-template name="check-name">
        <xsl:with-param name="text" select="@name"/>
      </xsl:call-template>
      <xsl:call-template name="check-comment">
        <xsl:with-param name="text" select="ownedComment/@body"/>
      </xsl:call-template>
      
      <xsl:variable name="type">
        <xsl:call-template name="class-from-id">
          <xsl:with-param name="id" select="@type"/>
        </xsl:call-template>
      </xsl:variable>

      <xsl:call-template name="check-class">
        <xsl:with-param name="text" select="$type"/>
      </xsl:call-template>
      
    </property>
    &cr;
  </xsl:template>
  
  
  
  
  <xsl:template name="class-from-id">
    <xsl:param name="id"/>
    <xsl:variable name="c" select="key('classid',$id)"/>
    <xsl:choose>
      <xsl:when test="$c/@xmi:type = 'uml:PrimitiveType'">
        <xsl:value-of select="$c/@name"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$c/@name"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  
  <!-- validation -->
  
  <xsl:template name="check-name">
    <xsl:param name="text"/>
    
    <xsl:if test="contains($text, '.') or contains($text, ':') or  contains($text, ',') or contains($text, ';') or contains($text, '?') or contains($text, '*')">
      <ERROR>Element [<xsl:value-of select="@name"/>] has a name attribute with BAD CHARACTERS ['.:,;?*']</ERROR>
    </xsl:if>
    
    <xsl:if test="contains($text, '-') or contains($text, '#') or contains($text, '&amp;') or contains($text, '@')">
      <ERROR>Element [<xsl:value-of select="@name"/>] has a name attribute with BAD CHARACTERS ['-#&amp;@']</ERROR>
    </xsl:if>
    
    <xsl:if test="contains($text, '(') or contains($text, '{') or contains($text, '[') or contains($text, ')') or contains($text, '}') or contains($text, ']')">
      <ERROR>Element [<xsl:value-of select="@name"/>] has a name attribute with BAD CHARACTERS ['(){}[]']</ERROR>
    </xsl:if>
    <!--
    <xsl:if test="contains($text, '&apos;') or contains($text, '&quot;')">
      <ERROR>Class [<xsl:value-of select="@name"/>] has a name attribute with bad characters ['"]</ERROR>
    </xsl:if>
-->
    <xsl:if test="string-length($text) = 0">
      <ERROR>Element [<xsl:value-of select="@name"/>] has an EMPTY name attribute</ERROR>
    </xsl:if>
    
    <xsl:if test="string-length($text) > 30">
      <ERROR>Element [<xsl:value-of select="@name"/>] has a name attribute too LONG [30]</ERROR>
    </xsl:if>
    
  </xsl:template>
  
  
  
  
  <xsl:template name="check-comment">
    <xsl:param name="text"/>
    
    <xsl:if test="string-length($text) = 0">
      <WARN>Element [<xsl:value-of select="@name"/>] has an EMPTY description</WARN>
    </xsl:if>
    
  </xsl:template>
  
  
  
  
  <xsl:template name="check-class">
    <xsl:param name="text"/>
    
    <xsl:if test="string-length($text) = 0">
      <ERROR>Element [<xsl:value-of select="@name"/>] has an EMPTY or INVALID reference</ERROR>
    </xsl:if>
    
  </xsl:template>
  
  
</xsl:stylesheet>
