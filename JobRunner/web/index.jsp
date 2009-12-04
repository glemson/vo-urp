<%@page contentType="text/html" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>

<c:set var="title" scope="request" value="Job Runner - Home" ></c:set>
<c:set var="noLink" scope="request" value="1" ></c:set>

<jsp:include page="header.jsp" flush="false"/>
<p>
This web site gives access to the applications listed below.
These applications can be run remotely and asynchronously in a common batch queue.
The implementation is based on a version of Laurent Bourges' JobRunner, which can be found online in the
<a href="http://code.google.com/p/vo-urp/" target="_blank">VO-URP</a> project in <a href="http://code.google.com/" target="_blank">GoogleCode</a>. 
For descriptions of the individual applications click on the 'Go' button.
</p>
<p>
To view your job history click <button onclick="window.location='Jobs.do?action=history'">Go</button>
</p>
<p>
To view your jobs in the active queue click <button onclick="window.location='Jobs.do?action=list'">Go</button>
</p>
<h3>    <a href="./apps/SeSAM/input.jsp">SeSAM</a> </h3>
<p>
A semi-analytical galaxy formation code created by Eyal Neistein and Simone Weinmann.<br/>
Ref: <a href="http://xxx.lanl.gov/abs/0911.3147" target="_blank">http://xxx.lanl.gov/abs/0911.3147</a>.
  </p>
<button onclick="window.location='./apps/SeSAM/input.jsp'">Go</button>

<jsp:include page="footer.jsp" flush="false" />
