<%@ tag isELIgnored="false" import="org.ivoa.dm.MetaModelFactory" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@taglib tagdir="/WEB-INF/tags" prefix="x" %>
<%@ attribute name="name" required="true" %>

<%
  Object value = MetaModelFactory.getInstance().getObjectClassType(name);
  jspContext.setAttribute("value", value);
%>
<jsp:doBody/>
All <a class="tooltipTrigger" title="&lt;h4&gt;${value.type.name}&lt;/h4&gt;${value.type.description}" href="List.do?entity=${value.type.name}">${value.type.name}s</a>
        <ul>
        <c:forEach var="sub" begin="0" items="${value.subclasses}">
      <li style="list-style-type:disc">
        <x:writeClassHierarchy name="${sub.type.name}"/>
      </li>
      </c:forEach>
    </ul>

