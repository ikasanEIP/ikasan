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
    <script type="text/javascript" src="<c:url value='/js/jquery-1.4.2.min.js'/>"></script>
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
                <h1>I forgot my password!</h1>
                <p class="text">
                    Not to worry, these things happen!  Simply enter your Username and click on the "Send New Password" button and the 
                    System will send you a new password to login with.
                </p>
            </div>
            <div id="rightContent">
                <div id="arrow"><img class="arrowImage" src="/console/images/Icon_Arrow.png" alt="Arrow"/></div>
                <div id="loginBox">
                    <h1>Send New Password</h1>

                    <%-- TODO Can also JSP template this? --%>
                    <c:if test="${errors != ''}">
                    <c:forEach items="${errors}" var="error">
                    <span class="important"><c:out value="${error}" /></span><br />
                    </c:forEach>
                    </c:if>
                    
                    <form id="sendPasswordForm" action="/console/users/sendPassword.htm" method="post">
                        <table id="loginTable">
                            <tr>
                                <td class="loginCell formLabel">Username</td>
                                <td class="loginCell"><input class="inputText" type='text' id='username' name='username' <c:if test="${not empty param.login_error}">value='<%= session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) %>'</c:if>/></td>
                            </tr>
                            <tr>
                                <td class="loginCell formLabel">&nbsp;</td>
                                <td class="loginCell"><input class="largeButton" name="submit" type="submit" value="Send New Password"/></td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div> <!-- End RHContent -->
        </div> <!-- End content -->
    </div> <!-- End container -->
</body>
</html>
