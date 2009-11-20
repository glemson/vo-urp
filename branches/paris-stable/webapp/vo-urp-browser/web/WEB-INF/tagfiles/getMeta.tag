<%@ tag isELIgnored="false" import="org.ivoa.dm.MetaModelFactory" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="var" required="true" rtexprvalue="false"%>
<%@ variable alias="value" name-from-attribute="var" %>
<%
  Object value = MetaModelFactory.getInstance().getObjectClassType(name);
  jspContext.setAttribute("value", value);
%>
<jsp:doBody/>
