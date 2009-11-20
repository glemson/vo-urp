<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>

<c:set var="list" value="${requestScope.list}" ></c:set>
<c:set var="meta" value="${requestScope.metaData}" ></c:set>

<c:if test="${! empty list}">

  <table border="1" cellspacing="0" cellpadding="4" width="100%">
  <tr>
<%-- get headers (attributes and references with multiplicity = 1 from meta object : --%>
    
<c:forEach var="entry" begin="0" items="${meta.attributeList}">
  <c:if test="${entry.name eq 'identity' or entry.multiplicity eq '1'}">
    <th><span class="tooltipTrigger" title="&lt;h4&gt;${entry.name}&lt;/h4&gt;${entry.description}">${entry.name}</span></th>
  </c:if>
</c:forEach>

<c:if test="${meta.hasReferences}">

<c:forEach var="entry" begin="0" items="${meta.referenceList}">
  <c:if test="${entry.multiplicity eq '1'}">
    <th><span class="tooltipTrigger" title="&lt;h4&gt;${entry.name}&lt;/h4&gt;${entry.description}">${entry.name}</span></th>
  </c:if>
</c:forEach>

</c:if>

  </tr>
<%-- get data from item list : --%>
  <c:forEach var="item" begin="0" items="${list}">
    <tr>
<%-- process an item from the list : --%>

<c:forEach var="entry" begin="0" items="${meta.attributeList}">
  <c:if test="${entry.name eq 'identity' or entry.multiplicity eq '1'}">
    <td>
      <a href="Show.do?entity=${item.className}&id=${item.id}" title="show ${item.className}">
        <x:getProperty item="${item}" name="${entry.name}" var="value">${value}</x:getProperty>
      </a>
    </td>
  </c:if>
</c:forEach>

<c:if test="${meta.hasReferences}">

<c:forEach var="entry" begin="0" items="${meta.referenceList}">
  <c:if test="${entry.multiplicity eq '1'}">
    <td>
      <x:getProperty item="${item}" name="${entry.name}" var="ref">
        <a href="Show.do?entity=${ref.className}&id=${ref.id}" title="show ${ref.className}">
        <x:showRef ref="${ref}"/>
        </a>
      </x:getProperty>
    </td>
  </c:if>
</c:forEach>

</c:if>

    </tr> 
  </c:forEach>
  </table>
</c:if>
