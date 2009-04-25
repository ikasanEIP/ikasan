<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!-- 
# //
# //
# // $Id: top.jsp 16798 2009-04-24 14:12:09Z mitcje $
# // $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/main/webapp/WEB-INF/jsp/top.jsp $
# // 
# // ====================================================================
# // Ikasan Enterprise Integration Platform
# // Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
# // by the @authors tag. See the copyright.txt in the distribution for a
# // full listing of individual contributors.
# //
# // This is free software; you can redistribute it and/or modify it
# // under the terms of the GNU Lesser General Public License as
# // published by the Free Software Foundation; either version 2.1 of
# // the License, or (at your option) any later version.
# //
# // This software is distributed in the hope that it will be useful,
# // but WITHOUT ANY WARRANTY; without even the implied warranty of
# // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# // Lesser General Public License for more details.
# //
# // You should have received a copy of the GNU Lesser General Public
# // License along with this software; if not, write to the 
# // Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
# // or see the FSF site: http://www.fsfeurope.org/.
# // ====================================================================
# //
# // Author:  Ikasan Development Team
# // 
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ikasan Management Console</title>
<meta http-equiv="Content-Language" content="English" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate" />
<link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>" media="screen" />
</head>
<body>
<div id="wrap">

<div id="top"></div>

<div id="content">  

<div class="header">
    <h1><a href="<c:url value='/'/>">Ikasan</a></h1>
    <h2>Management Console</h2>
</div>



<div id="subheader">
	<ul id="mainNavigation">
		<li><a href="<c:url value='/home.htm'/>">Home</a></li>
		<li><a href="<c:url value='/modules/list.htm'/>">Modules</a></li>
		<li><a href="<c:url value='/events/search.htm'/>">Events</a></li>
		<security:authorize ifAllGranted="ROLE_ADMIN">
		  <li><a href="<c:url value='/admin/admin.htm'/>">Admin</a></li>
		</security:authorize>
	</ul>
	<span id="sessioninfo">
		logged in as <security:authentication property="principal.username"/> - <a href="<c:url value="/j_spring_security_logout"/>">Logout</a>
	</span>
</div>



