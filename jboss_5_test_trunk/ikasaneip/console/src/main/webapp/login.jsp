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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Ikasan Console</title>
    <meta http-equiv="Content-Language" content="English" />
    <meta http-equiv="Content-Type" content="text/xhtml; charset=UTF-8" />
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate" />
    <link type="text/css" href="<c:url value='/css/global.css'/>" media="screen" rel="stylesheet" />
    <link type="text/css" href="<c:url value='/css/jquery.hovertip-1.0.css'/>" rel="stylesheet" />
    <script type="text/javascript" src="<c:url value='/js/jquery-1.3.2.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery.hovertip-1.0.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery.simpleautogrow.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/ikasan.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/login.js'/>"></script>

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
           <img id="logo" src="/console/images/Ikasan_Logo_Transp.png" alt="logo" />
           <h2>Enterprise Integration Platform - Console</h2>
        </div>

        <!-- The navigation bar -->
        <div id="navigation">&nbsp;</div>

        <!--  Main Content -->
        <div id="content">
            <div id="leftContent">
                <h1>Welcome...</h1>
                <p class="text">
                    Welcome to the console for Ikasan EIP, your gateway to many Ikasan EIP services.
                </p>
                <h4>What does the Ikasan EIP console do?</h4>
                <p class="text">
                    This browser based console allows end users and administrators to execute Ikasan EIP services.  
                    As of version 0.7.7 this includes wiretapped event search and user administration.
                </p>
                <h4>What next?</h4>
                <p class="text">
                    Please Login or if you are a new user then please follow the instructions by hovering over the help icon next to "I'm a new user" text.
                </p>
            </div>
            <div id="rightContent">
                <div id="arrow"><img class="arrowImage" src="/console/images/Icon_Arrow.png" alt="Arrow"/></div>
                <div id="loginBox">
                    <h1>Login</h1>
                    <form id="loginForm" action="<c:url value='j_spring_security_check'/>" method="post">
                        <table id="loginTable">
                            <tr>
                                <td class="loginCell formLabel">Username</td>
                                <td class="loginCell"><input class="inputText" type='text' id='j_username' name='j_username' <c:if test="${not empty param.login_error}">value='<%= session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) %>'</c:if>/></td>
                            </tr>
                            <tr>
                                <td class="loginCell formLabel">Password</td>
                                <td class="loginCell"><input class="inputText" type='password' id='j_password' name='j_password' /></td>
                            </tr>
                            <tr>
                                <td class="loginCell formLabel">I'm a new user <span title="Please contact your System Administrator" id="loginHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span></td>
                                <td class="loginCell"><input class="button" name="submit" type="submit" value="Login"/></td>
                            </tr>
                        </table>
                    </form>
                    <%-- 
                        This form-login-page form is also used as the
                        form-error-page to ask for a login again.
                     --%>
                    <c:if test="${not empty param.login_error}">
                    <span class="important">
                        Your login attempt was not successful, try again<br />
                        <br />
                        Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>
                    </span>
                    </c:if>
                </div>
            </div> <!-- End RHContent -->
        </div> <!-- End content -->
    </div> <!-- End container -->
</body>
</html>
