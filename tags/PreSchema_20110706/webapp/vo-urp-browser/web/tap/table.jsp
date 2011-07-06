<%@ page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
 
<jsp:include page="../header.jsp" flush="false" />

<c:set var="query" value="${requestScope.query}"></c:set> <!-- a Query object -->
<c:if test="${empty query.results}">
The query:<br/>
<pre>
${query.sql}
</pre><br/>
did not produce a result.
</c:if>

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

<jsp:include page="../footer.jsp" flush="false" />
