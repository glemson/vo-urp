<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page contentType="text/html" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="x" %>
<%@page import="org.ivoa.conf.RuntimeConfiguration"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>${requestScope.title}</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <link href="${pageContext.request.contextPath}/static/simple.css" rel="stylesheet" type="text/css"/>

<c:set var="app" value="${requestScope.LEGACY_APP}" ></c:set>
<script type="text/javascript">
<x:javascript app="${app}"/>
</script>
</head>

<body onload="initialize();">

<div id="header">
  <a name="topPage"></a><h1 class="banner">Job Runner@GAVO</h1>
</div>

<div id="layout">
<div id="content">

  <h1>
    ${requestScope.title} :
  </h1>

  <p>Go to: <a href="${pageContext.request.contextPath}/index.jsp" title="go to Main"><b>Main</b></a> 
  - <a href="javascript:history.go(-1)" title="go back"><b>Previous Page</b></a>
  - <a href="Jobs.do?action=list"><b>Job Queue</b></a>
  - <a href="Jobs.do?action=history"><b>History</b></a>
  </p>
