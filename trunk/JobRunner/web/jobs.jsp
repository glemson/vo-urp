<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title" scope="request" value="Job Runner - Home" ></c:set>
<c:set var="noLink" scope="request" value="1" ></c:set>
<jsp:include page="header.jsp" flush="false"/>
        
    <c:if test="${!empty requestScope.content}">
        <br/>
        <br/>
        Result :
        <p>
            ${requestScope.content}
        </p>
    </c:if>
    
<jsp:include page="footer.jsp" flush="false" />
