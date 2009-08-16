<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.RootContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@taglib uri="/WEB-INF/dirtag.tld" prefix="dir" %> 

<c:set var="title" scope="request" value="Job Runner - Detail" ></c:set>

<jsp:include page="../../header.jsp" flush="false"/>

<c:if test="${!empty requestScope.runContext}">

<c:set var="ctx" value="${requestScope.runContext}" ></c:set>
<p>
	Job : ${ctx.name} [${ctx.id}] - ${ctx.state}
	<br/><br/>
<hr/>
    Log : <br/><br/>
    
	<%= ((RootContext) request.getAttribute("runContext")).getRing().getContent("", "<br/>\n") %>    
<hr/>
    Results :
    <table><tr><th>File name</th><th>Size (bytes)</th><th>Modified</th></tr>
<dir:Directory path="${requestScope.resultDir}" > 
<tr><td> <a href="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/<%=fileName%>"><%=fileName%></a></td><td>  <%=fileSize%> </td><td><%=fileDate%> </td></tr>
</dir:Directory> 
<img src="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/b_result.png"/>
</table>

    
</p>	

</c:if>
    
<jsp:include page="../../footer.jsp" flush="false" />
