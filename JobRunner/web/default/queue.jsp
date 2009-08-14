<%@page contentType="text/html" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title" scope="request" value="Job Runner - Queue" ></c:set>
<!-- <c:set var="noLink" scope="request" value="1" ></c:set> -->
<jsp:include page="../header.jsp" flush="false"/>

<p> Job List :
	<br/><br/>

<c:if test="${!empty requestScope.queue}">

	<c:forEach var="ctx" items="${requestScope.queue}">
	
		Job : ${ctx.id} - ${ctx.state} 
		- <a href="${ctx.applicationName}.do?action=detail&id=${ctx.id}">job detail</a>
		- <a href="${ctx.applicationName}.do?action=kill&id=${ctx.id}">kill job</a>
		<br/>
		
	</c:forEach>

</c:if>
</p>	
    
<jsp:include page="../footer.jsp" flush="false" />
