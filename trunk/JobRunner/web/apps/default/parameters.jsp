

<%@page import="org.vourp.runner.model.LegacyApp"%>
<%@page import="org.vourp.runner.model.ParameterDeclaration"%>
<%
  LegacyApp legacyApp = (LegacyApp)request.getAttribute("LEGACY_APP");
  if(legacyApp == null) { 
%>
<h2>This application is not properly initialized.</h2>
<% } else { %>
<table>
<% 
  for(ParameterDeclaration p : legacyApp.getParameter()) {
%>
<tr>
<td ><%= p.getName() %></td>
<td nowrap> 
<input type="text" name="<%= p.getName() %>" value="<%= p.getFixedValue() %>" class="number"/>&nbsp;
</td> 
<td ><%= p.getDescription() %> </td> 
</tr>
<% } %>
</table>
<% } %>