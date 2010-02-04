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
This page allows you to visualise a hydrodynamical simulation using Smac.
Please see <a href="" target="_blank">here</a> for further documentation about the Smac visualisation code.
</p>
<br/>
<form action="<%= request.getContextPath() %>/Smac.do?action=start" method="POST" >
Select a simulation : 
<select name="SIMULATION">
<option selected>Hutt/g676/csf</option>
<option >Hutt/g676/dm</option>
<option >Hutt/g676/ovisc</option>
<option >Hutt/g676/ovisc</option>
</select>&nbsp;
<select name="SNAPSHOT">
<option selected>92</option>
<% for(int i = 91; i >= 0 ; i--){ %><option><%= i %></option><% } %>
</select>
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
</tr></c:if></c:forEach>
</table>
<input type="submit" name="Submit" value="Submit"/>
</form>

    
<jsp:include page="../../footer.jsp" flush="false" />