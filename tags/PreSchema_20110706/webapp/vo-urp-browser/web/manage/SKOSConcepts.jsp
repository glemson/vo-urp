<%@page contentType="text/html" session="false" pageEncoding="UTF-8" import="org.ivoa.dm.MetaModelFactory"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@page import="org.ivoa.web.servlet.SKOSServlet"%>
<%@page import="org.ivoa.conf.RuntimeConfiguration"%>

<jsp:include page="../header.jsp" flush="false" />

<c:set var="error" value="${requestScope.errorMessage}" ></c:set>
<c:set var="result" value="${requestScope.validation}" ></c:set>

<% pageContext.setAttribute("skosconcepts", MetaModelFactory.getInstance().getSkosConcepts()); %>

<c:forEach var="sc" items="${skosconcepts}">
<p>
${sc.value.utype}.<a href="${sc.key.skosconcept.broadestSKOSConcept[0]}" target="_blank">${sc.key.name}</a> 
</p>
</c:forEach>
<!-- 
<form method="POST" action="<%= request.getContextPath() %>/SKOS.do" enctype="multipart/form-data">
<input type="hidden" name="action" value="<%= SKOSServlet.INPUT_ACTION_query %>"/>
<input type="text" name="<%= SKOSServlet.INPUT_TERM %>" size="100"/>
<br/>
<input type="submit" value="Query">
</form>
<hr/>
-->

<jsp:include page="../footer.jsp" flush="false" />
