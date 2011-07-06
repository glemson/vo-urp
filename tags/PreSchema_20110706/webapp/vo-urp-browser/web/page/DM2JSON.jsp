<%@ page contentType="text/plain" session="false" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ taglib tagdir="/WEB-INF/tags/json" prefix="j" %>
<c:set var="item" value="${requestScope.item}" ></c:set><j:serialize item="${item}"/>
