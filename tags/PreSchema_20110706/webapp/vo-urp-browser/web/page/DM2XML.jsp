<%@ page contentType="text/xml" session="false" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ taglib tagdir="/WEB-INF/tags/xml" prefix="x" %>
<c:set var="item" value="${requestScope.item}" ></c:set><x:serialize item="${item}"/>
