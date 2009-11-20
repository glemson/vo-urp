<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>

<c:set var="cursor" value="${requestScope.cursor}" ></c:set>

<p>
  <b>${cursor.maxPos} [
  <x:getMeta name="${requestScope.entity.name}" var="meta">
    <span class="tooltipTrigger" title="&lt;h4&gt;${meta.type.name}&lt;/h4&gt;${meta.type.description}">${meta.type.name}</span>
  </x:getMeta>
  ] records found for Query :</b>
  
  <br/>
  <code><br/>${cursor.query}<br/></code>
  <c:choose>
    <c:when test="${cursor.doQuery}">
      <form method="get" action="List.do">
        <input type="hidden" name="entity" value="${requestScope.entity.name}"/>
        <input type="hidden" name="start"  value="0"/>
        <textarea name="queryClause" cols="80" rows="5">${cursor.queryClause}</textarea>
        <br/>
        <input type="submit"/>
      </form>
      <br/>
    </c:when>
  </c:choose>
</p>

<c:if test="${cursor.prevPos != -1}">
    <span style="float:left;">
      <a href="List.do?entity=${requestScope.entity.name}&amp;start=${cursor.prevPos}&amp;queryClause=${cursor.queryClause}" title="go to previous page">&lt;&lt; Previous Page</a>
    </span>
</c:if>

<c:if test="${cursor.nextPos != -1}">
    <span style="float:right;">
      <a href="List.do?entity=${requestScope.entity.name}&amp;start=${cursor.nextPos}&amp;queryClause=${cursor.queryClause}" title="go to next page">Next Page &gt;&gt;</a>
    </span>
</c:if>

<br/>
