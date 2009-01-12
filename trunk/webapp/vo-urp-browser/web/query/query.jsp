<%@ page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
 <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
 
<jsp:include page="../header.jsp" flush="false" />

<c:set var="query" value="${requestScope.query}"></c:set>
<c:set var="tap" value="${requestScope.tap}"></c:set>

<table>
<tr>
<td valign="top">
<h3>Tables and Views</h3><br/>
<a href="./tap/sync?REQUEST=getTableMetadata&FORMAT=xml">xml</a>
&nbsp;&nbsp;<a href="./tap/sync?REQUEST=getTableMetadata&FORMAT=votable">votable</a><br/><br/>
<c:forEach var="s" items="${tap}">
<c:if test="${fn:length(s.schema_name) > 0}">
<b>${s.schema_name}</b><br/>
</c:if>

<c:forEach var="t" items="${s.tables}">
&nbsp;&nbsp;<a href="./tap/sync?REQUEST=adqlquery&FORMAT=xml&QUERY=select+*+from+tap_schema.columns+where+table_name='${t.table_name}'&format=html">${t.table_name}</a><br/>
</c:forEach>
</c:forEach>
</td>
<td valign="top">
<table>
<tr><td>
<form action="./Query.do" method="post">

<c:if test="${!empty query.error}">
  <ul class="error">
    <li>${query.error}</li>
  </ul>
</c:if>

<p>
  Please define your ADQL (well, SQL) query and use the submit button :
</p>

<div>
  <input name="action" type="submit" value="submit"/>
</div>

<br/>

<textarea name="sql" cols="80" rows="15">${query.sql}</textarea>

<c:if test="${query.results != null}">
<p>
  Results : (${query.duration} ms)
  <br/>

  <c:set var="headers" value="${query.headers}"></c:set>

  <table border="1" cellspacing="0" cellpadding="4" width="100%">
    <tr class="main">
  <c:forEach var="h" begin="0" items="${headers}">
      <th>${h}</th>
  </c:forEach>
    </tr>
   <c:forEach var="row" begin="0" items="${query.results}">
    <tr>
    <c:forEach var="h" begin="0" items="${row}">
      <td>
        ${h.value}
      </td>
    </c:forEach>
    </tr>
  </c:forEach>
    </table>  

</p>

</c:if>

</form>

</td>
</tr>

<jsp:include page="../footer.jsp" flush="false" />
