<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title" scope="request" value="An Error occured" ></c:set>
<jsp:include page="header.jsp" flush="false" />

<h2>
  ${requestScope.errorMessage}
</h2>
    
<jsp:include page="footer.jsp" flush="false" />
