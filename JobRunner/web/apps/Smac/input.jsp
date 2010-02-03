<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.process.ProcessContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>

<%@page import="java.util.Map"%>
<%@page import="org.gavo.hydrosims.Smac"%>
<c:set var="title" scope="request" value="Smac - Input" ></c:set>

<%
  Map<String, String> parameters = (Map<String,String>)request.getAttribute(Smac.INPUT_PARAMETERS);
  
%>


<jsp:include page="../../header.jsp" flush="false"/>


<c:set var="app" value="${requestScope.LEGACY_APP}" ></c:set>


<script type="text/javascript">
<x:onchange app="${app}"/>
</script>

<!-- 

here java code that checks whether this page should display error messages about 
invalid parameters 

web application should :

1. have a page for setting parameters and submitting job
  1.1 if submission invalid: return to same page with information to fix errors in case the parameters were not valid
  1.2 else: a job is submitted to a job queue and a page is returned with information about the submission.
2. have a page where submitted jobs are listed. 
   this page shows status of jobs, has option to kill a job, has link to a page with details about the job
3. have a job inspection page which shows details: input parameters, status, entry in queue, results if available, error message
   if failed.

Requirements
1. job queue should be persistent.
2. web app may be public or protected by user id. may contain different modes.
3. jobs may time out
4. ...

Questions
1. what is best architecture to accomplish this?
2. should we use an existing (IVOA) application and job definition, XML based maybe?
3. shall we generate pages form a single process description XML file ?
4. ...



 -->

<!-- <a href="<%= request.getContextPath() %>/SeSAM.do?action=list">Job queue</a>  -->
<p>
This page allows you to visualise a hydrodynamical simulation using Smac.
Please see <a href="" target="_blank">here</a> for further documentation about the Smac visualisation code.
</p>
<br/>
<form action="<%= request.getContextPath() %>/Smac.do?action=start" method="POST" enctype="multipart/form-data">
Select a simulation : 
<select name="<%= Smac.SIMULATION %>">
<option selected>Hutt/g676/csf</option>
<option >Hutt/g676/dm</option>
<option >Hutt/g676/ovisc</option>
<option >Hutt/g676/ovisc</option>
</select>&nbsp;
<select name="<%= Smac.SNAPSHOT %>">
<option selected>92</option>
<% for(int i = 91; i >= 0 ; i--){ %><option><%= i %></option><% } %>
</select>
<hr/>
<table>
<c:set var="params" value="${app.parameter}" ></c:set>
<c:forEach var="p" begin="0" items="${params}">
<tr>
<td >${p.name}</td>
<td nowrap> 

<x:parameter p="${p}"/><br/>
</td> 
<td ><x:lineending item="${p.description}"/> </td> 
</tr></c:forEach>
</table>
<input type="submit" name="Submit" value="Submit"/>
</form>







    
<jsp:include page="../../footer.jsp" flush="false" />