<%@page contentType="text/html" session="false" pageEncoding="UTF-8"
 import="org.ivoa.util.runner.process.ProcessContext" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 


<c:set var="title" scope="request" value="Millimil - Input" ></c:set>
<%-- <c:set var="noLink" scope="request" value="0" ></c:set> --%>

<jsp:include page="../../header.jsp" flush="false"/>

<form action="<%= request.getContextPath() %>/Millimil.do?action=start" method="POST">

<textarea name="SQL" rows="20" cols="80"></textarea><br/>

<input type="submit" name="GO" valuer="GO"/>
</form>

<jsp:include page="../../footer.jsp" flush="false" />