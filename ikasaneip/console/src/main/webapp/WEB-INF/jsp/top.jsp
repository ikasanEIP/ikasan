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
    <title>Ikasan Console</title>
    <meta http-equiv="Content-Language" content="English" />
    <meta http-equiv="Content-Type" content="text/xhtml; charset=UTF-8" />
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate" />
    <link type="text/css" href="<c:url value='/css/global.css'/>" media="screen" rel="stylesheet" />
    <link type="text/css" href="<c:url value='/css/jquery/redmond/jquery-ui-1.7.2.custom.css'/>" rel="stylesheet" />
    <link type="text/css" href="<c:url value='/css/jquery.hovertip-1.0.css'/>" rel="stylesheet" />
    <script type="text/javascript" src="<c:url value='/js/jquery-1.3.2.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery.hovertip-1.0.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/ikasan.js'/>"></script>
    
    <!-- This is to fix transparent PNG files in IE6 -->
    <!--[if IE 6]>
    <script src="/console/js/DD_belatedPNG.js"></script>
    <script>
      /* EXAMPLE */
      DD_belatedPNG.fix('img');
      /* string argument can be any CSS selector */
      /* change it to what suits you! */
    </script>
    <![endif]-->
    
</head>

<body>
    <!-- 
        We use a container, as it can be easier to manipulate the positioning of various div elements 
        inside a parent div as opposed to inside a body tag. 
     -->
    <div id="container">

        <!-- The header section -->
        <div id="header">
           <a href="<c:url value='/'/>"><img id="logo" src="/console/images/Ikasan_Logo_Transp.png" alt="logo" /></a>
           <h2><fmt:message key="top_heading"/></h2>
        </div>
        
        <!-- The navigation bar -->
        <div id="navigation">

            <!-- Main navigation -->
            <div id="mainNavigation">
                <ul>
                    <li class="first"><a href="<c:url value='/home.htm'/>"><fmt:message key="menu_home_link"/></a></li>
                    <li><a href="<c:url value='/events/newSearch.htm'/>"><fmt:message key="menu_events_link"/></a></li>
                    <%-- Spring based security around the admin pages --%>
                    <security:authorize ifAllGranted="ROLE_ADMIN">
                    <li class="last"><a href="<c:url value='/admin/admin.htm'/>"><fmt:message key="menu_admin_link"/></a></li>
                    </security:authorize>
                </ul>
            </div>

            <!-- 
            	TODO get the image working in with the word 'Support', unfortunately HTML rears its ugly head 
            	again when trying to get this to work cleanly, it pushes the whole nav bar out by 1px forcing it to wrap
           	-->
            <div id="minorNavigation">
                <ul>
                    <li class="first"><a href="<c:url value='/support.htm'/>"><fmt:message key="menu_support_link"/><!--TODO <img class="helpIcon" src="/console/images/Icon_Help_sml2.png" alt="?" /> --></a></li>
                    <li class="username"><fmt:message key="menu_logged_in_as"/> <security:authentication property="principal.username" /></li>
                    <li class="last"><a href="<c:url value="/j_spring_security_logout"/>"><fmt:message key="menu_logout_link"/></a></li>
                </ul>
            </div>
        </div>