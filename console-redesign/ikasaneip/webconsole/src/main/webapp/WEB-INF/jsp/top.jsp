<%-- 

 $Id$
 $URL$ 

 ====================================================================
 Ikasan Enterprise Integration Platform
 
 Distributed under the Modified BSD License.
 Copyright notice: The copyright for this software and a full listing 
 of individual contributors are as shown in the packaged copyright.txt 
 file. 
 
 All rights reserved.

 Redistribution and use in source and binary forms, with or without 
 modification, are permitted provided that the following conditions are met:

  - Redistributions of source code must retain the above copyright notice, 
    this list of conditions and the following disclaimer.

  - Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.

  - Neither the name of the ORGANIZATION nor the names of its contributors may
    be used to endorse or promote products derived from this software without 
    specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ====================================================================

 Author:  Ikasan Development Team
 
--%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ikasan Management Console</title>
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



