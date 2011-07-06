<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.RootContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title" scope="request" value="Job Runner - Detail" ></c:set>
<c:set var="noLink" scope="request" value="1" ></c:set>
<jsp:include page="../header.jsp" flush="false"/>

<c:if test="${!empty requestScope.runContext}">

<c:set var="ctx" value="${requestScope.runContext}" ></c:set>
<p>
	Job : ${ctx.id} - ${ctx.state}
	<br/><br/>

    Output : <br/><br/>
    
	<%= ((RootContext) request.getAttribute("runContext")).getRing().getContent("", "<br/>\n") %>

</p>	

</c:if>
    
<jsp:include page="../footer.jsp" flush="false" />
