<%@ page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
 
<jsp:include page="../header.jsp" flush="false" />

<h3>Table Metadata</h3><br/>
<form action="<%= request.getContextPath() %>/Tap.do/sync" method="get" target="_blank">
<input type="hidden" name="REQUEST" value="GetTableMetadata"/>
Format : <select name="FORMAT">
<option label="xml" selected="selected">XML</option>
<option label="votable">VOTable</option>
</select><br/>
<input type="submit" name="submit" value="Submit"/>
</form>
<hr/>
<h3>ADQL Query</h3><br/>
<form action="<%= request.getContextPath() %>/Tap.do/sync" method="post">
<input type="hidden" name="REQUEST" value="ADQLQuery"/>
<textarea name="QUERY" cols="80" rows="15">${query.sql}</textarea><br/>
Format:<select name="FORMAT">
  <option value="votable" selected="selected">VOTable</option>
  <option value="html">HTML</option>
  <option value="csv">CSV</option>
</select><br/>
Language : <select name="LANG">
  <option value="ADQL" selected="selected">ADQL</option>
</select><br/>
Version: <select name="VERSION">
  <option value="1.0" selected="selected">1.0</option>
</select><br/>
Maximum # rows : <input type="text" name="MAXREC" value="0"/><br/>
<input type="submit" name="submit" value="Submit"/>

</form>

<jsp:include page="../footer.jsp" flush="false" />
