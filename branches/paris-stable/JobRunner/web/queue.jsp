<%@page contentType="text/html" session="false" pageEncoding="UTF-8" 
import="org.ivoa.util.runner.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title" scope="request" value="Job Runner - Queue" ></c:set>
<c:set var="user" scope="request" value="${pageContext.request.remoteUser}"/>
<%-- <c:set var="noLink" scope="request" value="1" ></c:set> --%>
<jsp:include page="./header.jsp" flush="false"/>

<% int livecount = LocalLauncher.queryActiveQueuedJobs(); %>
There are currently  <%= livecount %> live jobs on the queue.

<hr/>
<p> Your Jobs:
	<br/><br/>

<% 
int index = 0;
%>
<c:if test="${!empty requestScope.queue}">
<table>
<tr><th>Rank</th><th>Job</th><th>id</th><th>state</th><th>Date</th><th></th><th/><th/></tr>
	<c:forEach var="ctx" items="${requestScope.queue}">
	<c:if test="${ ctx.pending || ctx.running}"><% index++; %>
	<c:if test="${user == ctx.owner}">
<tr><td><%= index %></td><td> ${ctx.name}</td><td>${ctx.id}</td><td>${ctx.state}</td><td>${ctx.creationDateFormatted}</td>
<td>
<a href="${ctx.name}.do?action=detail&id=${ctx.id}">job detail</a></td>
<td>
    <c:if test="${ctx.pending}">
    <a href="${ctx.name}.do?action=cancel&id=${ctx.id}">cancel job</a>
    </c:if>
    </td><td>
    <c:if test="${ctx.running}">
    <a href="${ctx.name}.do?action=kill&id=${ctx.id}">kill job</a>
    </c:if>
</td>
</tr>
</c:if>
</c:if>
	</c:forEach>
</table>
<hr/>
</c:if>
</p>	
<jsp:include page="./footer.jsp" flush="false" />
