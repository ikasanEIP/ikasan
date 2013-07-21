<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%-- 
# //
# //
# // $Id$
# // $URL$
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
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Ikasan Console</title>
    <meta http-equiv="Content-Language" content="English" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate" />
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>" media="screen" />
    <script type="text/javascript" src="<c:url value='/js/jquery-1.3.2.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/ikasan.js'/>"></script>
</head>

<body>
<div id="wrap">

    <div id="top"></div>
    
    <div id="content">  
    
        <div class="header">
            <h1><a class="white" href="<c:url value='/'/>"><fmt:message key="top_heading"/></a></h1>
            <h2><fmt:message key="top_heading2"/></h2>
        </div>
        
        <div id="subheader">
        	<ul id="mainNavigation">
        		<li><a href="<c:url value='/home.htm'/>"><fmt:message key="menu_home_link"/></a></li>
        		 | <li><a href="<c:url value='/events/list.htm?newSearch=true'/>"><fmt:message key="menu_events_link"/></a></li>
        		<!-- Security around the admin pages -->
                <security:authorize ifAllGranted="ROLE_ADMIN">
                   | <li><a href="<c:url value='/admin/admin.htm'/>"><fmt:message key="menu_admin_link"/></a></li>
                </security:authorize>
        	</ul>
        	<!-- Logout -->
            <span id="sessioninfo">
                <fmt:message key="menu_admin_logged_in_as"/> <security:authentication property="principal.username"/> - <a href="<c:url value="/j_spring_security_logout"/>"><fmt:message key="menu_admin_logout"/></a>
            </span>
        </div>
