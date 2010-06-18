<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title" scope="request" value="An Error occured" ></c:set>
<jsp:include page="header.jsp" flush="false" />

<h2>
  Sorry, you already have the maximum number of jobs on the queue.<br/>
  Try again later.
</h2>
    
<jsp:include page="footer.jsp" flush="false" />
