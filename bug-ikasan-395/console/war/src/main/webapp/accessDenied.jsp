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
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Ikasan Console</title>
    <meta http-equiv="Content-Language" content="English" />
    <meta http-equiv="Content-Type" content="text/xhtml; charset=UTF-8" />
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate" />
    <!-- <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>" media="screen" /> -->
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/global.css'/>" media="screen" />    
    <script type="text/javascript" src="<c:url value='/js/jquery-1.4.2.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/ikasan.js'/>"></script>
</head>

<body>
    <!-- 
        TODO We may not need a container, but it can be easier to manipulate the 
        positioning of various div elements inside a parent div as opposed to inside 
        the body tag 
     -->
    <div id="container">
        <!-- TODO This will need to be altered somewhat -->
        <div id="header">
           <a class="white" href="<c:url value='/'/>"><img id="logo" src="images/logo.png" alt="logo" /></a>
           <h2>Enterprise Integration Platform - Console</h2>
        </div>
        <div id="content">Oops, you don't have authorisation to view this.</div>
    </div>
</body>
</html>

