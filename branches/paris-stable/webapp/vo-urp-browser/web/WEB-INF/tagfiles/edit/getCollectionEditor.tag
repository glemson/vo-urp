<%@ tag isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ attribute name="collection" type="org.ivoa.metamodel.Collection" required="true" %>
<!-- TODO implement ... -->
<%
  String type = collection.getDatatype().getName();
  String id = collection.getXmiid();

%>

<jsp:doBody/>
