<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.process.ProcessContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 


<%@page import="org.gavo.sam.SeSAM"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.Map"%>
<%@page import="org.gavo.sam.SeSAMModel"%>
<%@page import="org.gavo.sam.Model"%>
<%@page import="org.gavo.sam.Datatype"%>
<c:set var="title" scope="request" value="SeSAM - Input" ></c:set>
<%
  SeSAMModel theModel = (SeSAMModel)request.getAttribute(SeSAM.INPUT_MODEL);
  String model = (String)request.getAttribute(SeSAM.INPUT_MODEL_current);
  Map<String, String> parameters = (Map<String,String>)request.getAttribute(SeSAM.INPUT_PARAMETERS);

  Model.Parameter p = null;
  
%>

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
<form action="<%= request.getContextPath() %>/SeSAM.do?action=input" method="POST">
Select a model : 
<select name="<%= SeSAM.INPUT_MODEL_current %>">
<%  for(String key : theModel.defaultModels.keySet()) { %>
<option  value="<%= key %>" <%= (key.equals(model)?"selected":"") %>><%= key  %></option>
<% } %>
</select>&nbsp;&nbsp;<input type="submit" value="Choose"/><br/>
</form>
<hr/>
<form action="<%= request.getContextPath() %>/SeSAM.do?action=start" method="POST">
<table>
<tr>
<td >f_s</td>
<td nowrap> 
<input type="text" name="f_s_0" value="<%= parameters.get("f_s_0") %>" class="number"/>&nbsp;
<input type="text" name="f_s_1" value="<%= parameters.get("f_s_1") %>"  class="number"/>&nbsp;
<input type="hidden" name="f_s_2" value="<%= parameters.get("f_s_2") %>"/><!-- <br/>  --> 
<input type="text" name="f_s_3" value="<%= parameters.get("f_s_3") %>"  class="number"/><!-- <br/>  --> 
<input type="hidden" name="f_s_4" value="<%= parameters.get("f_s_4") %>"/><!-- <br/>  --> 
<input type="hidden" name="f_s_5" value="<%= parameters.get("f_s_5") %>"/>
</td> 
<td >SF efficiency: f_s = 10<sup>f_s[0]+f_s[1]*logM+f_s*(logM)<sup>2</sup></sup>  t<sup>f_s[2]</sup><br/>
M is in units of M<sub>sun</sub>/h <br/>
t is in units of Gyr </td> 
</tr>
<tr>
<td>fs_file</td>
<td> 
<select name="fs_file" class="dropdown">
<% 
p = theModel.parameters.get("fs_file");
if(p != null && p.datatype == Datatype._file && p.possibleValues != null) {
	String selected = (p.possibleValues.contains(model) ? model:"-1");
	for(String v : p.possibleValues) {
 %>
<option  value="<%= v %>" <%= (selected.equals(v)?"selected":"") %>><%= v %></option>
<% }} %>
</select>
<br/>
</td> 
<td>File containing cooling efficiencies. Choosing "-1" will use
the values in f_s.
</td>
</tr>
<tr>
<td>f_d</td>
<td nowrap> 
<input type="text" name="f_d_0" value="<%= parameters.get("f_d_0") %>" class="number"/>&nbsp;
<input type="text" name="f_d_1" value="<%= parameters.get("f_d_1") %>" class="number"/>&nbsp;
<input type="hidden" name="f_d_2" value="<%= parameters.get("f_d_2") %>"><!-- <br/>  -->
<input type="text" name="f_d_3" value="<%= parameters.get("f_d_3") %>" class="number"/><!-- <br/>  -->
<input type="hidden" name="f_d_4" value="<%= parameters.get("f_d_4") %>"/><!-- <br/>  -->
<input type="hidden" name="f_d_5" value="<%= parameters.get("f_d_5") %>"/>
<br/>
</td> 
<td >Feedback efficiency: f_d = f_d[0] (M/10<sup>10</sup>)<sup>f_d[1]</sup>  t<sup>f_d[2]</sup> </td> 
</tr>
<tr>
<td>fd_file</td>
<td> 
<select name="fd_file" class="dropdown">
<% 
p = theModel.parameters.get("fd_file");
if(p != null && p.datatype == Datatype._file && p.possibleValues != null) {
	String selected = (p.possibleValues.contains(model) ? model:"-1");
	for(String v : p.possibleValues) {
 %>
<option  value="<%= v %>" <%= (selected.equals(v)?"selected":"") %>><%= v %></option>
<% }} %>
</select>
<br/>
</td> 
<td>File containing cooling efficiencies. Choosing "-1" will use
the values in f_d.
</td>
</tr>
<tr>
<td>f_c</td>
<td nowrap> 
<input type="text" name="f_c_0" value="<%= parameters.get("f_c_0") %>" class="number"/>&nbsp;
<input type="text" name="f_c_1" value="<%= parameters.get("f_c_1") %>" class="number"/>&nbsp;
<input type="hidden" name="f_c_2" value="<%= parameters.get("f_c_2") %>"/><!-- <br/>  -->
<input type="text" name="f_c_3" value="<%= parameters.get("f_c_3") %>" class="number"/><!-- <br/>  -->
<input type="hidden" name="f_c_4" value="<%= parameters.get("f_c_4") %>"/><!-- <br/>  -->
<input type="hidden" name="f_c_5" value="<%= parameters.get("f_c_5") %>"/>
<br/>
</td> 
<td>Cooling efficiency: f_c = 10<sup>f_c[0]+f_c[1]*logM+f_c*(logM)<sup>2</sup></sup>  t<sup>f_c[2]</sup> </td> 
</tr>
<tr>
<td>fc_file</td>
<td> 
<select name="fc_file" class="dropdown">
<% 
p = theModel.parameters.get("fc_file");
if(p != null && p.datatype == Datatype._file && p.possibleValues != null) {
	String selected = (p.possibleValues.contains(model) ? model:"-1");
	for(String v : p.possibleValues) {
 %>
<option  value="<%= v %>" <%= (selected.equals(v)?"selected":"") %>><%= v %></option>
<% }} %>
</select>
<br/>
</td> 
<td>File containing cooling efficiencies. Choosing "-1" will use
the values in f_c.
</td>
</tr>
<tr>
<td>f_e</td>
<td> 
<input type="text" name="f_e_0" value="<%= parameters.get("f_e_0") %>" class="number"/>&nbsp;
<input type="text" name="f_e_1" value="<%= parameters.get("f_e_1") %>" class="number"/>&nbsp;
<input type="hidden" name="f_e_2" value="<%= parameters.get("f_e_2") %>"/><!-- <br/>  -->
<input type="text" name="f_e_3" value="<%= parameters.get("f_e_3") %>" class="number"/>&nbsp;
<input type="text" name="f_e_4" value="<%= parameters.get("f_e_4") %>" class="number"/><!-- <br/>  -->
<input type="hidden" name="f_e_5" value="<%= parameters.get("f_e_5") %>"/>
<br/>
</td> 
<td>Ejection efficiency: f_e = f_e[0] (M/10<sup>10</sup>)<sup>f_e[1]</sup>  t<sup>f_e[2]</sup> - f_e[3] <br/>
If f_e==-1 Then set f_e=f_d</td> 
</tr>
<tr>
<td>f_r</td>
<td nowrap> 
<input type="text" name="f_r_0" value="<%= parameters.get("f_r_0") %>" class="number"/>&nbsp;
<input type="text" name="f_r_1" value="<%= parameters.get("f_r_1") %>" class="number"/>&nbsp;
<input type="hidden" name="f_r_2" value="<%= parameters.get("f_r_2") %>"/><!-- <br/>  -->
<input type="text" name="f_r_3" value="<%= parameters.get("f_r_3") %>" class="number"/><!-- <br/>  -->
<input type="hidden" name="f_r_4" value="<%= parameters.get("f_r_4") %>"/><!-- <br/>  -->
<input type="hidden" name="f_r_5" value="<%= parameters.get("f_r_5") %>"/>
<br/>
</td> 
<td>Reincorporation efficiency: f_r = f_r[0] (M/10<sup>10</sup>)<sup>f_r[1]</sup>  t<sup>f_r[2]</sup> </td> 
</tr>
<tr>
<td>f_ac</td>
<td nowrap> 
<input type="text" name="f_ac_0" value="<%= parameters.get("f_ac_0") %>" class="number"/>&nbsp;
<input type="text" name="f_ac_1" value="<%= parameters.get("f_ac_1") %>" class="number"/>&nbsp;
<input type="text" name="f_ac_2" value="<%= parameters.get("f_ac_2") %>" class="number"/>
<br/>
</td> 
<td>Cold accretion: f_ac = f_ac[0] t<sup>f_ac[1]</sup> </td> 
</tr>
<tr>
<td>f_ah</td>
<td nowrap> 
<input type="text" name="f_ah_0" value="<%= parameters.get("f_ah_0") %>" class="number"/>&nbsp;
<input type="text" name="f_ah_1" value="<%= parameters.get("f_ah_1") %>" class="number"/><!-- <br/>  -->
<input type="hidden" name="f_ah_2" value="<%= parameters.get("f_ah_2") %>"/>
<br/>
</td> 
<td>Hot accretion: f_ah = f_ah[0] t<sup>f_ah[1]</sup></td> 
</tr>
<tr>
<td>mcrit_sf</td>
<td nowrap> 
<input type="text" name="mcrit_sf_0" value="<%= parameters.get("mcrit_sf_0") %>" class="number"/>&nbsp;
<input type="text" name="mcrit_sf_1" value="<%= parameters.get("mcrit_sf_1") %>" class="number"/>&nbsp;
<input type="text" name="mcrit_sf_2" value="<%= parameters.get("mcrit_sf_2") %>" class="number"/>&nbsp;
<input type="hidden" name="mcrit_sf_3" value="<%= parameters.get("mcrit_sf_3") %>"/><!-- <br/>  -->
<input type="text" name="mcrit_sf_4" value="<%= parameters.get("mcrit_sf_4") %>" class="number"/>
<br/>
</td> 
<td>Ciritical threshold for SF: mcrit_sf = 10<sup>mcrit_sf[0]</sup>  M<sup>mcrit_sf[1]</sup>  t <sup>mcrit_sf[2]</sup><br/>
only works if mcrit_sf[3]==1 </td> 
</tr>
<tr>
<td>halo_thresh</td>
<td> 
<input type="text" name="halo_thresh_0" value="<%= parameters.get("halo_thresh_0") %>" class="number"/>&nbsp
<input type="text" name="halo_thresh_1" value="<%= parameters.get("halo_thresh_1") %>" class="number"/>
<br/>
</td>
<td>If (M>=halo_thresh[0] & t>halo_thresh[1]) Then: NO quiescent SF</td> 
</tr>
<tr>
<td>alpha_c</td>
<td>
<input type="text" name="alpha_c" value="<%= parameters.get("alpha_c") %>" class="number"/>
<br/>
</td> 
<td>Stripping coefficient for cold gas</td> 
</tr>
<tr>
<td>alpha_h</td>
<td ><input type="text" name="alpha_h" value="<%= parameters.get("alpha_c") %>" class="number"/>
<br/>
</td>
<td>Stripping coefficient for hot gas</td> 
</tr>
<tr>
<td>hot2central</td>
<td> 
<select name="hot2central" class="dropdown">
<option  value="0" <%= ("0".equals(parameters.get("hot2central"))?"selected":"") %>/>0</option>
<option  value="1" <%= ("1".equals(parameters.get("hot2central"))?"selected":"") %>/>1</option>
</select>
<br/>
</td> 
<td>1: add stripped gas from satellite into the hot component of the main galaxy inside fof <br/> 0: stripped gas is lost</td> 
</tr>
<tr>
<td>negative_mass</td>
<td> 
<select name="negative_mass" class="dropdown">
<option  value="0" <%= ("0".equals(parameters.get("negative_mass"))?"selected":"") %>/>0</option>
<option  value="1" <%= ("1".equals(parameters.get("negative_mass"))?"selected":"") %>/>1</option>
<option  value="2" <%= ("2".equals(parameters.get("negative_mass"))?"selected":"") %>/>2</option>
</select>
<br/>
</td>
<td>0: do not correct negative masses <br/>
1: always correct negative masses <br/>
2: correct negative masses only at merger induced bursts</td> 
</tr>
<tr>
<td>merge_coeff</td>
<td> 
<input type="text" name="merge_coeff_0" value="<%= parameters.get("merge_coeff_0") %>" class="number"/>&nbsp;
<input type="text" name="merge_coeff_1" value="<%= parameters.get("merge_coeff_1") %>" class="number"/>&nbsp
<input type="text" name="merge_coeff_2" value="<%= parameters.get("merge_coeff_2") %>" class="number"/>
<br/>
</td> 
<td>burst efficiency: merge_coeff[0] (m2/m1)<sup>merge_coeff[1]</sup> <br/>
If (merge_coeff[2]>0 & M>merge_coeff[2]) Then burst efficiency is set to zero ;</td> 
</tr>
<tr>
<td>stam</td>
<td> 
<input type="text" name="stam_0" value="<%= parameters.get("stam_0") %>" class="number"/>&nbsp;
<input type="text" name="stam_1" value="<%= parameters.get("stam_1") %>" class="number"/>&nbsp;
<input type="text" name="stam_2" value="<%= parameters.get("stam_2") %>" class="number"/>&nbsp;
<input type="text" name="stam_3" value="<%= parameters.get("stam_3") %>" class="number"/>
<br/>
</td> 
<td>If [m<sub>star</sub>[sat] / m<sub>cent</sub>[sat] >stam[0] & t>stam[1] & (m<sub>cold</sub>[sat]+m<sub>cold</sub>[cent]) / (m<sub>star</sub>[sat]+m<sub>star</sub>[cent]) > stam[2] ] Then if: <br/>
stam[3]==1 Shut-down all SF modes & cooling <br/>
stam[3]==2 Shut-down cooling </td> 
</tr>
<tr>
<td>z_reion</td>
<td >
<input type="text" name="z_reion" value="<%= parameters.get("z_reion") %>" class="number"/>
<br/>
</td>
<td>If (z>z_reion) Then:  f_s = 0; f_d = 0; f_c = 0; f_r = 0; f_e = 0; </td> 
</tr>
<tr>
<td>dynf_t</td>
<td>
<input type="text" name="dynf_t_0" value="<%= parameters.get("dynf_t_0") %>" class="number"/>&nbsp;
<input type="text" name="dynf_t_1" value="<%= parameters.get("dynf_t_1") %>" class="number"/>
<br/>
</td> 
<td>Dynamical friction time is multiplied by: dynf_t[0] (t/13.6)<sup>dynf_t[1]</sup> </td> 
</tr>
<tr>
<td>dynf_r</td>
<td>
<select name="dynf_r" class="dropdown">
<option  value="0"/>0</option>
<option  value="1"/>1</option>
</select>
<br/>
</td> 
<td>Dynamical friction computation: <br/>
If 1: satellite distance from central is maximum r<sub>vir</sub> </td> 
</tr>
<tr>
<td>dynf_m</td>
<td>
<select name="dynf_m" class="dropdown">
<option  value="0" <%= ("0".equals(parameters.get("dynf_m"))?"selected":"") %>/>0</option>
<option  value="1" <%= ("1".equals(parameters.get("dynf_m"))?"selected":"") %>/>1</option>
<option  value="2" <%= ("2".equals(parameters.get("dynf_m"))?"selected":"") %>/>2</option>
</select>
<br/>
</td> 
<td>Satellite mass for computing dynamical friction: <br/>
0: mass in stars + cold gas <br/>
1: mass of subhalo which was last identified <br/>
2: mass in stars + cold gas + minimum subhalo mass (1.72x10<sup>10</sup> M<sub>sun</sub>/h)</td> 
</tr>
<tr>
<td>burst_time</td>
<td>
<input type="text" name="burst_time_0" value="<%= parameters.get("burst_time_0") %>" class="number"/>&nbsp;
<input type="text" name="burst_time_1" value="<%= parameters.get("burst_time_1") %>" class="number"/>
<br/>
</td> 
<td>Burst dependence on time is gaussian with std of: burst_time[0]  (t/13.6)<sup>burst_time[1]</sup> <td> 
</tr>
<tr>
<td>f_recycle</td> 
<td ><input type="text" name="f_recycle" value="<%= parameters.get("f_recycle") %>" class="number"/>
<br/>
</td>
<td>Recycling factor</td> 
</tr>



</table>
<input type="submit" name="Submit" value="Submit"/>
</form>

    
<jsp:include page="../../footer.jsp" flush="false" />