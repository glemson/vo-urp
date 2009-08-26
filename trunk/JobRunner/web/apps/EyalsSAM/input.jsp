<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.process.ProcessContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 


<c:set var="title" scope="request" value="Eyals SAM - Input" ></c:set>
<%-- <c:set var="noLink" scope="request" value="0" ></c:set> --%>

<jsp:include page="../../header.jsp" flush="false"/>
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
2. should we use an existing (IVOA) applicaiton and job definition, XML based maybe?
3. shall we generate pages form a single process description XML file ?
4. ...



 -->

<a href="<%= request.getContextPath() %>/EyalsSAM.do?action=list">Job queue</a>
<br/>

<form action="<%= request.getContextPath() %>/EyalsSAM.do?action=start" method="POST">
<table>
<tr><td>
<a href="<%= request.getContextPath() %>/pages/Help.html#halo_thresh" target="_blank">halo_thresh</a> </td><td> <input type="text" name="halo_thresh" value="1e12"/> 
</td></tr>
<tr><td>
<a href="<%= request.getContextPath() %>/pages/Help.html#alpha_c" target="_blank">alpha_c</a> </td><td> <input type="text" name="alpha_c"  value="0"/>
</td></tr>
<tr><td>
<a href="<%= request.getContextPath() %>/pages/Help.html#alpha_h" target="_blank">alpha_h</a> </td><td> <input type="text" name="alpha_h"  value="0.25"/> 
</td></tr>
<tr><td>
<a href="<%= request.getContextPath() %>/pages/Help.html#z_reion" target="_blank">z_reion</a> </td><td> <input type="text" name="z_reion"  value="7"/> 
</td></tr>
<tr><td>
<a href="<%= request.getContextPath() %>/pages/Help.html#tdyn_f" target="_blank">tdyn_f</a> </td><td> <input type="text" name="tdyn_f"  value="0.2"/> 
</td></tr>
<tr><td>
<a href="<%= request.getContextPath() %>/pages/Help.html#f_recycle" target="_blank">f_recycle</a> </td><td> <input type="text" name="f_recycle"  value="0.5"/>
</td></tr>
<tr><td>
<a href="<%= request.getContextPath() %>/pages/Help.html#halo_type" target="_blank">halo_type</a> </td><td><input type="text" name="halo_type"  value="0"/> 
</td></tr>
</table>

<input type="submit" name="GO" valuer="GO"/>
</form>

    
<jsp:include page="../../footer.jsp" flush="false" />