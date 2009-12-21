<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.RootContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@taglib uri="/WEB-INF/dirtag.tld" prefix="dir" %> 


<%@page import="org.ivoa.util.runner.RootContext"%>
<%@page import="org.ivoa.util.runner.RunState"%>
<c:set var="title" scope="request" value="SeSAM - Detail" ></c:set>
<%-- <c:set var="noLink" scope="request" value="0" ></c:set> --%>
<jsp:include page="../../header.jsp" flush="false"/>
<h2>
 <a href="./SeSAM.do?action=input">SeSAM</a>
</h2>
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
    
	<% if( ((RootContext) request.getAttribute("runContext")).getRing() != null) { %>
<%=	((RootContext) request.getAttribute("runContext")).getRing().getContent("","<br/>")   %>
	<%	}%>    
	
<% if(state==RunState.STATE_FINISHED_OK) { %>
  <img src="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/fig1.png"/>
  <img src="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/fig2_1.png"/>
  <img src="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/fig2_2.png"/>
  <img src="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/fig2_3.png"/>
  <img src="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/fig2_4.png"/>
  <img src="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/fig3.png"/>
  <img src="${pageContext.request.contextPath}/Download.do/${ctx.relativePath}/fig4.png"/>
<% } %>
<br/><br/>
    <table><tr><th>File name</th><th>Size (bytes)</th><th>Modified</th></tr>
<c:if test="${!empty requestScope.resultDir}">
<dir:Directory path="${requestScope.resultDir}" sort="1"> 
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
