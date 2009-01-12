<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>${requestScope.title}</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <link href="${pageContext.request.contextPath}/static/simple.css" rel="stylesheet" type="text/css"/>

  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/prototype.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/getElementsBySelector.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/scriptaculous.js"></script>

  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/tooltips.js"></script>

  <script type="text/javascript">
  // <![CDATA[
  Tooltips.activateOnLoad();
  // ]]>
  </script>
    
  <link href="${pageContext.request.contextPath}/static/tooltip.css" rel="stylesheet" type="text/css"/>
</head>
<body>

<div id="header">
  <a name="topPage"></a><h1 class="banner">SimDB Browser</h1>
</div>

<div id="layout">
<div id="content">

  <h1>
    ${requestScope.title} :
  </h1>

<c:if test="${empty requestScope.noLink}">
  <p>Back to : <a href="index.jsp" title="go to Index"><b>Index</b></a> - <a href="javascript:history.go(-1)" title="go back"><b>Previous Page</b></a></p>
</c:if>

<%--
<p>This 
<a href="#tooltipOne" class="tooltipTrigger" id="tooltipTriggerOne">tooltip</a>
uses an anchor for finding its popUp hint. &lt;a href="#tooltipOne" ... &gt;</p>
<div class="tooltip" id="tooltipTriggerOnePopUp">
<h4>Tooltip</h4>
<p>A tooltip is an information that pops up on hovering a text or icon, describing the funtion or text.</p>
</div>

<p>This is a text with an inline 
<span class="tooltipTrigger" title="A tooltip is an information that pops up on hovering a text or icon, describing the funtion or text.">span</span>
with class="tooltipTrigger" and an title attribute, which will be converted to a tooltip popup.</p>

<p>This 
<span class="tooltipTrigger" id="tooltipThree">tooltip</span>
has the id="tooltipThree", and will popup a div with the id="tooltipThreePopUp"</p>
<div class="tooltip" id="tooltipThreePopUp">
<h4>Tooltip</h4>
<p>A tooltip is an information that pops up on hovering a text or icon, describing the funtion or text.</p>
</div>
--%>
