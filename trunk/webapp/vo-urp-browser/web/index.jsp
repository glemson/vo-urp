<%@page contentType="text/html" session="false" pageEncoding="UTF-8" import="org.ivoa.dm.MetaModelFactory" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>
<%@page import="org.ivoa.conf.RuntimeConfiguration" %>

<c:set var="title" scope="request" value="SimDB Browser - Home" ></c:set>
<c:set var="noLink" scope="request" value="1" ></c:set>

<jsp:include page="header.jsp" flush="false"/>

<% pageContext.setAttribute("types", MetaModelFactory.getInstance().getObjectClassTypeList()); %>

<h3>Browse Model</h3>
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

  <h3><a href="./html/<%= RuntimeConfiguration.get().getProjectName() %>.html"">Documentation</a></h3>
  Browse documentation of the data model.
  <h3><a href="./SKOS.do">SKOS Concepts</a></h3>
  Browse SKOS vocabularies used in the model.
  <h3><a href="./Query.do">SQL Interface</a></h3>
  Query the <%= RuntimeConfiguration.get().getProjectName() %> database using ADQL (well, SQL).
  <h3><a href="./Validate.do">XML Validator</a></h3>
  Validate an XML document against the data model's XML schemas.
  <h3><a href="./secure/Manager.do">XML Loader</a></h3>
  Load an XML document into the database.<br/>
  Requires a login.

  <p>
    <a href="./Log4j.do">Log management (requires a login!)</a>
  </p>


<jsp:include page="footer.jsp" flush="false" />
