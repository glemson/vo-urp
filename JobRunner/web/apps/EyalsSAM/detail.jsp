<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.RootContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@taglib uri="/WEB-INF/dirtag.tld" prefix="dir" %> 


<%@page import="org.ivoa.util.runner.RootContext"%>
<c:set var="title" scope="request" value="Job Runner - Detail" ></c:set>
<%-- <c:set var="noLink" scope="request" value="0" ></c:set> --%>
<jsp:include page="../../header.jsp" flush="false"/>

<a href="EyalsSAM.do?action=list">list jobs</a>

<c:if test="${!empty requestScope.runContext}">

<c:set var="ctx" value="${requestScope.runContext}" ></c:set>
<p>
	Job : ${ctx.id} - ${ctx.state}
	<br/><br/>

    Output : <br/><br/>
    
	<%= ((RootContext) request.getAttribute("runContext")).getRing().getContent("", "<br/>\n") %>    

    Root directory 
    <table><tr><th>File name</th><th>Size (bytes)</th><th>Modified</th></tr>
<dir:Directory path="${requestScope.resultDir}" > 
<tr><td> <a href="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/<%=fileName%>"><%=fileName%></a></td><td>  <%=fileSize%> </td><td><%=fileDate%> </td></tr>
</dir:Directory> 
</table>

    
</p>	

</c:if>
    
<jsp:include page="../../footer.jsp" flush="false" />
