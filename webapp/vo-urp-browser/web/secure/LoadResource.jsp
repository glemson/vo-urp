<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<%@page import="org.ivoa.web.servlet.ValidationServlet"%>
<%@page import="org.ivoa.web.servlet.secure.ManagerServlet"%>
<%@page import="org.ivoa.conf.RuntimeConfiguration"%>
<jsp:include page="../header.jsp" flush="false" />

<c:set var="error" value="${requestScope.errorMessage}" ></c:set>
<c:set var="entity" value="${requestScope.newObject}" ></c:set>

<p>
  This page loads a document.<br/>
</p>

<form method="POST" action="<%= request.getContextPath() %>/secure/Manager.do" enctype="multipart/form-data">
<input type="hidden" name="<%= ManagerServlet.INPUT_ACTION %>" value="<%= ManagerServlet.INPUT_ACTION_insert %>"/>
<input type="file" name="<%= ManagerServlet.INPUT_DOC %>" size="100"/>
<br/>
<input type="submit" value="Insert">
</form>
<hr/>
<c:if test="${error != null}">
Your uploaded document was not correct : <br/><br/>
${error}
</c:if>
<c:if test="${entity != null}">
Your uploaded document was valid and was loaded into the database.
It can be downloaded via the following 
<a href="Show.do?entity=${entity.name}&id=${entity.id}&view=xml" title="view as XML">link</a>
</c:if>
<jsp:include page="../footer.jsp" flush="false" />
