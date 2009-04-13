<%@ tag isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ attribute name="attribute" type="org.ivoa.metamodel.Attribute" required="true" %>
<%
  String type = attribute.getDatatype().getName();
  String id = attribute.getXmiid();
  if("boolean".equals(type))
  {
%>
<select id="${attribute.xmiid}">
  <option selected="true">true</option>
  <option>false</option>
</select>
<%	  
  } else {
%>
<input type="text" id="${attribute.xmiid}"/>
<%	  
  }
%>

<jsp:doBody/>
