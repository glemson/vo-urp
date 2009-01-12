<%@ tag isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ attribute name="col" type="java.util.Collection" required="true" %>

<c:if test="${! empty col}">
  <c:set var="itemList.bak" scope="page" value="${requestScope.list}" ></c:set>
  <c:set var="itemMeta.bak" scope="page" value="${requestScope.metaData}" ></c:set>

  <c:set var="list" scope="request" value="${col}" ></c:set>
  <c:set var="metaData" scope="request" value="${col[0].classMetaData}" ></c:set>

  <jsp:include page="/list/ListEmbeddable.jsp" flush="false" />

  <c:set var="list" scope="request" value="${itemList.bak}" ></c:set>
  <c:set var="metaData" scope="request" value="${itemMeta.bak}" ></c:set>
  
</c:if>
