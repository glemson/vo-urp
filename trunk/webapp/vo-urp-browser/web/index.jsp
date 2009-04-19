<%@page contentType="text/html" session="false" pageEncoding="UTF-8" import="org.ivoa.dm.MetaModelFactory" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>

<c:set var="title" scope="request" value="SimDB Browser - Home" ></c:set>
<c:set var="noLink" scope="request" value="1" ></c:set>

<jsp:include page="header.jsp" flush="false"/>

<% pageContext.setAttribute("types", MetaModelFactory.getInstance().getObjectClassTypeList()); %>

<h3>Browse</h3>
<ul>
<c:forEach var="entry" begin="0" items="${types}">
  <c:if test="${entry.root eq true}">
    <c:if test="${entry.baseclass == null}">
  <li style="list-style-type:disc">    <x:writeClassHierarchy name="${entry.type.name}" />  
</li>
    </c:if>
  </c:if>
</c:forEach>
</ul>

  <h3><a href="./Query.do">ADQL (well, SQL) query</a></h3>
  <h3><a href="./Validate.do">XML Validator</a></h3>
  <h3><a href="./secure/Manager.do">XML Loader (requires a login!)</a></h3>

<jsp:include page="footer.jsp" flush="false" />
