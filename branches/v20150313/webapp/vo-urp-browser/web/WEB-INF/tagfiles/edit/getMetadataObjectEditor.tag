<%@ tag isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags/edit" prefix="x" %>
<%@ attribute name="object" type="org.ivoa.dm.model.MetadataObject" required="true" %>
<%@ attribute name="meta" type="org.ivoa.dm.ObjectClassType" required="true" %>

<c:if test="${meta.contained}">
  <p>
    <a href="Show.do?entity=${item.container.className}&id=${item.container.id}$view=edit" title="Edit the container">Parent ${item.container.className}</a>
  </p>
</c:if>
<h2>Attributes</h2>
<table>
<c:forEach var="attribute" begin="0" items="${meta.attributeList}">
<tr><th>
<span class="tooltipTrigger" title="&lt;h4&gt;${attribute.name}&lt;/h4&gt;${attribute.description}">${attribute.name}</span>
</th>
<td>
<x:getAttributeEditor attribute="${attribute}" />
</td></tr>
</c:forEach>
</table>
<h2>Collections</h2>
<table>
<c:forEach var="collection" begin="0" items="${meta.collectionList}">
<tr><th>
<span class="tooltipTrigger" title="&lt;h4&gt;${collection.name}&lt;/h4&gt;${collection.description}">${attribute.name}</span>
</th>
<td>
<x:getCollectionEditor collection="${collection}" />
</td></tr>
</c:forEach>
</table>
