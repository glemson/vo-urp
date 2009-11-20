<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags/edit" prefix="x" %>

<jsp:include page="../header.jsp" flush="false" />

<c:set var="item" value="${requestScope.item}" ></c:set>
<c:set var="meta" value="${requestScope.item.classMetaData}" ></c:set>

<c:if test="${meta.root}">
<x:getMetadataObjectEditor object="${item}" meta="${meta}" />
</c:if>

<jsp:include page="../footer.jsp" flush="false" />
