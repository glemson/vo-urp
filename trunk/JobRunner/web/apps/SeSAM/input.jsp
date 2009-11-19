<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.process.ProcessContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 


<c:set var="title" scope="request" value="SeSAM - Input" ></c:set>
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
2. should we use an existing (IVOA) application and job definition, XML based maybe?
3. shall we generate pages form a single process description XML file ?
4. ...



 -->

<!-- <a href="<%= request.getContextPath() %>/SeSAM.do?action=list">Job queue</a>  -->
<br/>

<form action="<%= request.getContextPath() %>/SeSAM.do?action=start" method="POST">
<table>
<tr>
<td >f_s</td>
<td> 
<input type="text" name="f_s_0" value="-6.5"/><br/> 
<input type="text" name="f_s_1" value="1.04"/><br/> 
<input type="text" name="f_s_2" value="-0.0394"/><br/> 
<input type="text" name="f_s_3" value="-0.82"/><br/> 
<input type="text" name="f_s_4" value="0"/><br/> 
<input type="text" name="f_s_5" value="0"/>
</td> 
<td >bla bla</td> 
</tr>
<tr>
<td>f_d</td>
<td> 
<input type="text" name="f_d_0" value="3.5"/><br/>
<input type="text" name="f_d_1" value="0"><br/>
<input type="text" name="f_d_2" value="0"><br/>
<input type="text" name="f_d_4" value="0"><br/>
<input type="text" name="f_d_3" value="0"><br/>
<input type="text" name="f_d_5" value="0">
</td> 
<td >bla bla</td> 
</tr>
<tr>
<td>f_c</td>
<td> 
<input type="text" name="f_c_0" value="0"/><br/>
<input type="text" name="f_c_1" value="0"/><br/>
<input type="text" name="f_c_2" value="0"/><br/>
<input type="text" name="f_c_3" value="0"/><br/>
<input type="text" name="f_c_4" value="0"/><br/>
<input type="text" name="f_c_5" value="0"/>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>f_e</td>
<td> 
<input type="text" name="f_e_0" value="0"/><br/>
<input type="text" name="f_e_1" value="0"/><br/>
<input type="text" name="f_e_2" value="0"/><br/>
<input type="text" name="f_e_3" value="0"/><br/>
<input type="text" name="f_e_4" value="0"/><br/>
<input type="text" name="f_e_5" value="0"/>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>f_r</td>
<td> 
<input type="text" name="f_r_0" value="0"/><br/>
<input type="text" name="f_r_1" value="0"/><br/>
<input type="text" name="f_r_2" value="0"/><br/>
<input type="text" name="f_r_3" value="0"/><br/>
<input type="text" name="f_r_4" value="0"/><br/>
<input type="text" name="f_r_5" value="0"/>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>f_ac</td>
<td> 
<input type="text" name="f_ac_0" value="0"/><br/>
<input type="text" name="f_ac_1" value="0"/><br/>
<input type="text" name="f_ac_2" value="0"/>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>f_ah</td>
<td> 
<input type="text" name="f_ah_0" value="0.17"/><br/>
<input type="text" name="f_ah_1" value="0"/><br/>
<input type="text" name="f_ah_2" value="0"/>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>ac_thres</td>
<td> 
<input type="text" name="ac_thres_0" value="0"/><br/>
<input type="text" name="ac_thres_1" value="0"/><br/>
<input type="text" name="ac_thres_2" value="0"/><br/>
<input type="text" name="ac_thres_3" value="0"/><br/>
<input type="text" name="ac_thres_4" value="0"/>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>mcrit_sf</td>
<td> 
<input type="text" name="mcrit_sf_0" value="0"/><br/>
<input type="text" name="mcrit_sf_1" value="0"/><br/>
<input type="text" name="mcrit_sf_2" value="0"/><br/>
<input type="text" name="mcrit_sf_3" value="0"/><br/>
<input type="text" name="mcrit_sf_4" value="0"/>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>halo_thresh</td>
<td> 
<input type="text" name="halo_thresh_0" value="8e15"/><br/>
<input type="text" name="halo_thresh_1" value="1"/>
</td>
<td>bla bla</td> 
</tr>
<tr>
<td>alpha_c</td>
<td> <input type="text" name="alpha_c" value="0"/></td> 
<td>bla bla</td> 
</tr>
<tr>
<td>alpha_h</td>
<td > <input type="text" name="alpha_h" value="0.25"/></td>
<td>bla bla</td> 
</tr>
<tr>
<td>hot2central</td>
<td> 
<select name="hot2central">
<option  value="0"/>0</option>
<option  value="1"/>1</option>
</select>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>negative_mass</td>
<td>
<select name="negative_mass">
<option  value="0"/>0</option>
<option  value="1"/>1</option>
<option  value="2" selected/>2</option>
</select></td>
<td>bla bla</td> 
</tr>
<tr>
<td>merge_coeff</td>
<td> 
<input type="text" name="merge_coeff_0" value="0.56"/><br/>
<input type="text" name="merge_coeff_1" value="0.7"/><br/>
<input type="text" name="merge_coeff_2" value="0"/>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>stam</td>
<td> 
<input type="text" name="stam_0" value="0"/><br/>
<input type="text" name="stam_1" value="0"/><br/>
<input type="text" name="stam_2" value="0"/><br/>
<input type="text" name="stam_3" value="0"/>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>z_reion</td>
<td > <input type="text" name="z_reion" value="7"/></td>
<td>bla bla</td> 
</tr>
<tr>
<td>dynf_t</td>
<td> 
<input type="text" name="dynf_t_0" value="3"/><br/>
<input type="text" name="dynf_t_1" value="0"/>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>dynf_r</td>
<td> 
<select name="dynf_r">
<option  value="0"/>0</option>
<option  value="1"/>1</option>
</select>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>dynf_m</td>
<td> 
<select name="dynf_m">
<option  value="0"/>0</option>
<option  value="1"/>1</option>
<option  value="2" selected/>2</option>
</select>
</td> 
<td>bla bla</td> 
</tr>
<tr>
<td>burst_time</td>
<td> 
<input type="text" name="burst_time_0" value="1e-6"/><br/>
<input type="text" name="burst_time_1" value="1"/>
</td> 
<td>bla bla<td> 
</tr>
<tr>
<td>f_recycle</td>
<td > <input type="text" name="f_recycle" value="0.5"/></td>
<td>bla bla</td> 
</tr>



</table>
<input type="submit" name="GO" valuer="GO"/>
</form>

    
<jsp:include page="../../footer.jsp" flush="false" />