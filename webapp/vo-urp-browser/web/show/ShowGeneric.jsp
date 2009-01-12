<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>

<jsp:include page="../header.jsp" flush="false" />

<c:set var="item" value="${requestScope.item}" ></c:set>
<c:set var="meta" value="${requestScope.item.classMetaData}" ></c:set>

<c:if test="${meta.root}">
  <p>
    <b>Data Model serialization : </b>
    <a href="Show.do?entity=${requestScope.entity.name}&id=${item.id}&view=xml" title="view as XML"><img src="static/xml_small.png"/></a>
  </p>
</c:if>

<c:if test="${meta.contained}">
  <p>
    <a href="Show.do?entity=${item.container.className}&id=${item.container.id}" title="view the parent entity">Parent ${item.container.className}</a>
  </p>
</c:if>

<c:if test="${meta.hasReferrers}">
  <p>
    <b>Referrers</b>
    <ul>
<c:forEach var="entry" begin="0" items="${meta.referrers}">
  <li>
    <a href="List.do?entity=${entry.value}&queryClause=where%20item.${entry.key}.id=${item.id}&" title="view the referrer list">${entry.value}.${entry.key}</a>
  </li>
</c:forEach>
</ul>
  </p>
</c:if>

<table border="1" cellspacing="0" cellpadding="4" width="100%">
  <tr class="main">
    <th>Property</th>
    <th>Value</th>
  </tr>
<c:forEach var="entry" begin="0" items="${meta.attributeList}">
  <tr>
    <th><span class="tooltipTrigger" title="&lt;h4&gt;${entry.name}&lt;/h4&gt;${entry.description}">${entry.name}</span></th>
    <td><x:getProperty item="${item}" name="${entry.name}" var="value">${value}</x:getProperty></td>
  </tr>
</c:forEach>

<c:if test="${meta.hasReferences}">
  <tr class="main">
    <th>Reference</th>
    <th>Value</th>
  </tr>
<c:forEach var="entry" begin="0" items="${meta.referenceList}">
  <tr>
    <th><span class="tooltipTrigger" title="&lt;h4&gt;${entry.name}&lt;/h4&gt;${entry.description}">${entry.name}</span></th>
    <td>
      <x:getProperty item="${item}" name="${entry.name}" var="ref">
        <a href="Show.do?entity=${ref.className}&id=${ref.id}" title="show ${ref.className}">
        <x:showRef ref="${ref}"/>
        </a>
      </x:getProperty>
      </td>
  </tr>
</c:forEach>
</c:if>


<c:if test="${meta.hasCollections}">
  <tr class="main">
    <th>Collection</th>
    <th>Value</th>
  </tr>

<c:forEach var="entry" begin="0" items="${meta.collectionList}">
  <tr>
    <th><span class="tooltipTrigger" title="&lt;h4&gt;${entry.name}&lt;/h4&gt;${entry.description}">${entry.name}</span></th>
    <td>
      <x:getProperty item="${item}" name="${entry.name}" var="col">
        <x:showCollection col="${col}"/>
      </x:getProperty>
      </td>
  </tr>
</c:forEach>
</c:if>

</table>

<jsp:include page="../footer.jsp" flush="false" />
