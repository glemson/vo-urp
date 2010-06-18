<%@ tag isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ attribute name="p" type="org.vourp.runner.model.ParameterDeclaration" required="true" %>

<c:set var="defaultValue" value="${p.defaultValue}"/>
<c:choose> 
  <c:when test="${p.class.name == 'org.vourp.runner.model.EnumeratedParameter' }">
<select id="${p.name}" name="${p.name}" <c:if test="${p.numSlaves > 0 }">onChange='${p.name}_Change();'</c:if> >
    <c:choose>
      <c:when test="${p.dependency == null}">
        <c:set var="vvs" value="${p.validvalues[0]}"/>
        <c:forEach var="vv" items="${vvs.literal}">
<option value="${vv.value}" <c:if test="${defaultValue == vv.value }">selected</c:if>   ><c:choose><c:when test="${empty vv.title}">${vv.value}</c:when><c:otherwise>${vv.value} - ${vv.title}</c:otherwise></c:choose></option>
        </c:forEach>
      </c:when>
      <c:otherwise>
<option>****</option>      
      </c:otherwise>
    </c:choose>
</select>
  </c:when>
  <c:otherwise>
    <c:choose>
      <c:when  test="${ p.datatype == 'BOOLEAN' }">
<select id="${p.name}" name="${p.name}">
  <option <c:if test="${defaultValue == 'true' }">selected</c:if>>true</option>
  <option <c:if test="${defaultValue == 'false' }">selected</c:if>>false</option>
</select>
      </c:when>
      <c:when  test="${ p.datatype == 'INTEGER' || p.datatype == 'FLOAT' ||   p.datatype == 'DOUBLE' ||  p.datatype == 'SHORT'}">
<input type="text" name="${p.name}" class="number" value="${defaultValue}"/>
      </c:when>
      <c:when  test="${ p.datatype == 'FILE'}">
<input type="file" name="${p.name}" size="40" value="${defaultValue}"/>
      </c:when>
      <c:otherwise>
<input type="text" name="${p.name}" size="40"  value="${defaultValue}"/>
      </c:otherwise>
    </c:choose>
  </c:otherwise>
</c:choose>
<jsp:doBody/>
