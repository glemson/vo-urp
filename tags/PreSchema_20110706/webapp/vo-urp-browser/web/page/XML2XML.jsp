<%@ page contentType="text/xml" session="false" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="org.ivoa.xml.XSLTTransformer"%>
<%@page import="java.util.HashMap"%>
<c:set var="xmldoc" value="${requestScope.xmldoc}" ></c:set>
<c:set var="xsltsheet" value="${requestScope.xsltsheet}" ></c:set>
<c:set var="mode" value="${requestScope.mode}" ></c:set>
<c:set var="ns_uri" value="${requestScope.ns_uri}" ></c:set>
<c:set var="ns_suffix" value="${requestScope.ns_suffix}" ></c:set>
<%
    String xsltsheet = request.getParameter("xsltsheet");
	String xmldoc = request.getParameter("xmldoc");
	String mode = request.getParameter("mode");
	String ns_url = request.getParameter("ns_uri");
	String ns_suffix = request.getParameter("ns_suffix");
    HashMap<String,String> p = new HashMap<String, String>();
    p.put("mode",mode);
    p.put("namespaceMappingURL",ns_url);
    if(ns_suffix != null)
    	p.put("ns_suffix",ns_suffix);
	out.write(XSLTTransformer.transform2string(xsltsheet,p,xmldoc));
%>	
