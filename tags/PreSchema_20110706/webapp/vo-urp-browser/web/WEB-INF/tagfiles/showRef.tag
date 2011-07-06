<%@ tag isELIgnored="false" import="java.util.*, org.ivoa.dm.model.MetadataObject"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ attribute name="ref" type="org.ivoa.dm.model.MetadataObject" required="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>
<%
  Collection col = Arrays.asList(new MetadataObject[]{ ref });
  jspContext.setAttribute("col", col);
%>
<x:showCollection col="${col}"/>
