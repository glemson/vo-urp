<%@page contentType="text/html" session="false" pageEncoding="UTF-8" 
import="org.ivoa.util.runner.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title" scope="request" value="Job Runner - Queue" ></c:set>
<%-- <c:set var="noLink" scope="request" value="1" ></c:set> --%>
<jsp:include page="./header.jsp" flush="false"/>

<p> Job List :
	<br/><br/>

<c:if test="${!empty requestScope.queue}">

	<c:forEach var="ctx" items="${requestScope.queue}">
	
		Job : ${ctx.applicationName} [${ctx.id}] - ${ctx.state} - [${ctx.creationDateFormatted} ]
		- <a href="${ctx.applicationName}.do?action=detail&id=${ctx.id}">job detail</a>
    <c:if test="${ctx.pending}">
    - <a href="${ctx.applicationName}.do?action=cancel&id=${ctx.id}">cancel job</a>
    </c:if>
    <c:if test="${ctx.running}">
    - <a href="${ctx.applicationName}.do?action=kill&id=${ctx.id}">kill job</a>
    </c:if>
         <br/>
		
	</c:forEach>

</c:if>
</p>	
    
<jsp:include page="./footer.jsp" flush="false" />