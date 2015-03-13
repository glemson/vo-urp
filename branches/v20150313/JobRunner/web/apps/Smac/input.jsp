<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.process.ProcessContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>

<%@page import="java.util.Map"%>
<%@page import="org.gavo.hydrosims.Smac"%>
<c:set var="title" scope="request" value="Smac - Input" ></c:set>

<jsp:include page="./header.jsp" flush="false"/>

<!-- <a href="<%= request.getContextPath() %>/SeSAM.do?action=list">Job queue</a>  -->
<p>
This page allows you to visualise hydrodynamical simulations using Smac.
Please see <a href="http://www.mpa-garching.mpg.de/~kdolag/Hutt" target="_blank">here</a> for further information about the simulations.
Please see <a href="http://www.mpa-garching.mpg.de/~kdolag/Smac" target="_blank">here</a> for documentation about the Smac visualisation code.
</p>
<br/>
<form action="<%= request.getContextPath() %>/Smac.do?action=start" method="POST" >
<hr/>
<table>
<c:set var="app" value="${requestScope.LEGACY_APP}" ></c:set>
<c:set var="params" value="${app.parameter}" ></c:set>
<c:forEach var="p" begin="0" items="${params}">
<c:if test="${p.isFixed != 1}"> 
<tr>
<td >${p.name}</td>
<td nowrap> 
<x:parameter p="${p}"/><br/>
</td> 
<td ><x:lineending item="${p.description}"/> </td> 
</tr></c:if>
<c:if test="${p.name == 'SNAP_END' or p.name == 'OUTPUT_SUB' or p.name == 'CENTER_Z' or p.name == 'IMG_SIZE' or p.name == 'MAX_DIST'}"><tr><td colspan='3'><hr/></td></tr></c:if>
</c:forEach>
</table>
<input type="submit" name="Submit" value="Submit"/>
</form>

    
<jsp:include page="../../footer.jsp" flush="false" />