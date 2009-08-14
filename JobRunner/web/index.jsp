<%@page contentType="text/html" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>

<c:set var="title" scope="request" value="Job Runner - Home" ></c:set>
<c:set var="noLink" scope="request" value="1" ></c:set>

<jsp:include page="header.jsp" flush="false"/>

<h3>Actions</h3>

<p>
    <a href="./EyalsSAM/input.jsp">Start a job running Eyals SAM</a>
  </p>


<jsp:include page="footer.jsp" flush="false" />
