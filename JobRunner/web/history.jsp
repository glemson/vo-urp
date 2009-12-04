<%@page contentType="text/html" session="false" pageEncoding="UTF-8" 
import="org.ivoa.util.runner.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title" scope="request" value="Job Runner - Queue" ></c:set>
<%-- <c:set var="noLink" scope="request" value="1" ></c:set> --%>
<jsp:include page="./header.jsp" flush="false"/>

<p> History of jobs :
	<br/><br/>

<c:if test="${!empty requestScope.history}">

	<c:forEach var="ctx" items="${requestScope.history}">
	
		Job : ${ctx.name} [${ctx.id}] - ${ctx.state} - [${ctx.creationDateFormatted} ]
		- <a href="${ctx.name}.do?action=detail&id=${ctx.id}">job detail</a>
         <br/>
		
	</c:forEach>

</c:if>
</p>	
    
<jsp:include page="./footer.jsp" flush="false" />
