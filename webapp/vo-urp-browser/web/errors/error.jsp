<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title" scope="request" value="Error" ></c:set>
<c:set var="noLink" scope="request" value="1"></c:set>
<jsp:include page="../header.jsp" flush="false" />

<p class="error">
  Maybe the request caused an application error or the requested resource is not available.
</p>

<strong>Here is information about the error:</strong> <br/><br/>
  
<c:set var="e" value="${requestScope['javax.servlet.error.exception']}"></c:set>

The exception message:
<pre>
<c:out value="${e.message}" />
</pre>

<br/><br/>

The type of exception: 
<c:out value="${e.class.name}" />
<br><br>

The stack trace:
<pre>
<%
final Exception exception = (Exception)request.getAttribute("javax.servlet.error.exception");
// if there is an exception
if (exception != null) {
  // print the stack trace hidden in the HTML source code for debug
  exception.printStackTrace(new java.io.PrintWriter(out));
}
%>
</pre>

<jsp:include page="../footer.jsp" flush="false" />
