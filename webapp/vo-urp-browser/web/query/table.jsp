<%@ page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
 
<jsp:include page="../header.jsp" flush="false" />

<c:set var="table" value="${requestScope.table}"></c:set> <!-- a Tables object -->

  <table border="1" cellspacing="0" cellpadding="4" width="100%">
    <tr class="main">
      <th>${h}</th>
    </tr>
  <c:forEach var="c" begin="0" items="${table.columns}">
    <tr>
      <td>
        ${c.column_name}
      </td>
      <td>
        ${c.datatype}
      </td>
    </tr>
  </c:forEach>
</table>  
<jsp:include page="../footer.jsp" flush="false" />
