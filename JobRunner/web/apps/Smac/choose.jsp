<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.process.ProcessContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>

<%@page import="java.util.Map"%>
<%@page import="org.gavo.hydrosims.Smac"%>
<c:set var="title" scope="request" value="Smac - Input" ></c:set>

<jsp:include page="./header.jsp" flush="false"/>

<!-- <a href="<%= request.getContextPath() %>/SeSAM.do?action=list">Job queue</a>  -->
<h3>UNDER CONSTRUCTION</h3>
<p>
This page allows you to choose a hydrodynamical simulation to be visualised with Smac.
</p>
<br/>
<form action="<%= request.getContextPath() %>/Smac.do?action=start" method="POST" >
<hr/>
<table>
<c:set var="simulation" value="${requestScope.LEGACY_APP}" ></c:set>
<c:set var="params" value="${app.parameter}" ></c:set>
<c:forEach var="p" begin="0" items="${params}">
<c:if test="${p.isFixed != 1}"> 
<tr>
<td >${p.name}</td>
<td nowrap> 
<x:parameter p="${p}"/><br/>
</td> 
<td ><x:lineending item="${p.description}"/> </td> 
</tr></c:if></c:forEach>
</table>
<input type="submit" name="Submit" value="Submit"/>
</form>

    
<jsp:include page="../../footer.jsp" flush="false" />