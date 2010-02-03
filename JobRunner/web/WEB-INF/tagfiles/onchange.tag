<%@ tag isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ attribute name="app" type="org.vourp.runner.model.LegacyApp" required="true" %>

<c:set var="params" value="${app.parameter}" ></c:set>

<c:forEach var="p" items="${params}"><c:if test="${p.class.name == 'org.vourp.runner.model.EnumeratedParameter' && p.numSlaves > 0}">
function ${p.name}_Change() {
  var x = document.getElementById("${p.name}");
  var v= x.options[x.selectedIndex].value;
<c:forEach var="slave" items="${params}"><c:if test="${slave.class.name == 'org.vourp.runner.model.EnumeratedParameter' && not empty slave.dependency && slave.dependency.master.name == p.name}">
  set_${slave.name}(v);
  //alert("clicked "+v);
</c:if></c:forEach>
}
</c:if></c:forEach>

<c:forEach var="p" items="${params}"><c:if test="${p.class.name == 'org.vourp.runner.model.EnumeratedParameter' && not empty p.dependency}">

function set_${p.name}(v) {
  var x = document.getElementById("${p.name}");
  x.options.length = 0;
  //alert("in set_${p.name} with value "+v);
  var count = 0;
    var found = 0;
  <c:forEach var="ch" items="${p.dependency.choice}">
    <c:set var="validvalues" value="${ch.choose}"/>
  if(v == '${ch.if}') {
    found = 1;
    count = 0;
    <c:forEach var="literal" items="${validvalues.literal}">
      x.options[count++] = new Option('${literal.value} - ${literal.title}','${literal.value}');
    </c:forEach>
  }
  if(found == 0) {
    x.options.length = 0;
    x.options[0] = new Option('***',-1);
  }
  
  </c:forEach>
}
</c:if></c:forEach>

