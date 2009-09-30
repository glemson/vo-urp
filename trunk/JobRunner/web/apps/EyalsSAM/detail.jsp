<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.RootContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@taglib uri="/WEB-INF/dirtag.tld" prefix="dir" %> 


<%@page import="org.ivoa.util.runner.RootContext"%>
<%@page import="org.ivoa.util.runner.RunState"%>
<c:set var="title" scope="request" value="Job Runner - Detail" ></c:set>
<%-- <c:set var="noLink" scope="request" value="0" ></c:set> --%>
<jsp:include page="../../header.jsp" flush="false"/>

<c:if test="${!empty requestScope.runContext}">

<c:set var="ctx" value="${requestScope.runContext}" ></c:set>
<%
  RunState state = ((RootContext) request.getAttribute("runContext")).getState();
  boolean ok = (state==RunState.STATE_FINISHED_OK | state == RunState.STATE_FINISHED_ERROR);
%>
<p>
	Job : ${ctx.id} - ${ctx.state}
	<br/><br/>

    Output : <br/><br/>
    
	<%= ((RootContext) request.getAttribute("runContext")).getRing().getContent("", "<br/>\n") %>    
<% if(state==RunState.STATE_FINISHED_OK) { %>
  <img src="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/madau_diagram.png"/>
  <img src="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/cond_mass_fun.png"/>
<% } %>
<br/><br/>
    Root directory : <% if(ok){ %><a href="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}">download zip</a><% } %>
    
    <table><tr><th>File name</th><th>Size (bytes)</th><th>Modified</th></tr>
<c:if test="${!empty requestScope.resultDir}">
<dir:Directory path="${requestScope.resultDir}" > 
<tr><td> 
<% if(ok){ %>
<a href="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/<%=fileName%>">
<% } %>
 <%= fileName %>
<% if(ok){ %>
 </a>
 <%} %>
 </td><td>  <%=fileSize%> </td><td><%=fileDate%> </td></tr>
</dir:Directory> 
</c:if>
</table>

    
</p>	

</c:if>
    
<jsp:include page="../../footer.jsp" flush="false" />
