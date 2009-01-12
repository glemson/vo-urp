<%@ tag isELIgnored="false" import="org.ivoa.dm.model.MetadataObject" %>
<%@ attribute name="item" type="org.ivoa.dm.model.MetadataObject" required="true" %>
<%@ attribute name="name" %>
<%@ attribute name="var" required="true" rtexprvalue="false"%>
<%@ variable alias="value" name-from-attribute="var" %>
<%
  Object value = item.getProperty(name);
  jspContext.setAttribute("value", value);
%>
<jsp:doBody/>
