<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<%@page import="org.ivoa.web.servlet.ValidationServlet"%>
<%@page import="org.ivoa.web.servlet.secure.ManagerServlet"%>
<%@page import="org.ivoa.conf.RuntimeConfiguration"%>
<jsp:include page="../header.jsp" flush="false" />

<c:set var="error" value="${requestScope.errorMessage}" ></c:set>
<c:set var="result" value="${requestScope.validation}" ></c:set>

<p>
  This page checks that an uploaded document is valid against the <%=RuntimeConfiguration.getInstance().getProjectName()%> XML Schema under<br/>
  <a href="<%= org.ivoa.dm.DataModelManager.SCHEMA_URL %>" target="_blank"><%=org.ivoa.dm.DataModelManager.SCHEMA_URL%></a>.
</p>

<form method="POST" action="<%= request.getContextPath() %>/Validate.do" enctype="multipart/form-data">
<input type="hidden" name="action" value="validate"/>
<input type="file" name="<%= ValidationServlet.INPUT_DOC %>" size="100"/>
<br/>
<input type="submit" value="Validate">
</form>
<hr/>
<c:if test="${error != null}">
Your uploaded document was not correct : <br/><br/>
${error}
</c:if>
<c:if test="${result != null}">
Your uploaded document was ${result.valid ? 'valid' : 'not valid'}.<br/><br/>
<c:if test="${! result.valid}">
<table border="1" cellspacing="0" cellpadding="4" width="100%">
  <tr class="main">
    <th>Severity</th>
    <th>line:column</th>
    <th>message</th>
  </tr>
<c:forEach var="entry" begin="0" items="${result.messages}">
  <tr>
    <td>${entry.severity}</td>
    <td>${entry.lineNumber} : ${entry.columnNumber}</td>
    <td>${entry.message}</td>
  </tr>
</c:forEach>
</table>
</c:if>
</c:if>
<pre>

</pre>


<jsp:include page="../footer.jsp" flush="false" />
