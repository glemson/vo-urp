<%@page contentType="text/html" session="false" pageEncoding="UTF-8" 
import="org.ivoa.util.runner.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title" scope="request" value="Job Runner - History" ></c:set>
<%-- <c:set var="noLink" scope="request" value="1" ></c:set> --%>
<jsp:include page="./header.jsp" flush="false"/>

<p> 
<c:if test="${!empty requestScope.history}">

<table>
<tr><th>Job</th><th>id</th><th>state</th><th>Date</th><th></th></tr>
	<c:forEach var="ctx" items="${requestScope.history}">
<tr><td>${ctx.name}</td><td>${ctx.id}</td><td>${ctx.state}</td><td>${ctx.creationDateFormatted}</td>
<td>
<a href="${ctx.name}.do?action=detail&id=${ctx.id}">job detail</a></td></tr>
	</c:forEach>
</table>
</c:if>
</p>	
    
<jsp:include page="./footer.jsp" flush="false" />
