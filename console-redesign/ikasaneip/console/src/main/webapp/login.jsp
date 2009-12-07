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
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/global.css'/>" media="screen" />    
    <script type="text/javascript" src="<c:url value='/js/jquery-1.3.2.min.js'/>"></script>
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
           <!-- <a href="<c:url value='/'/>"> -->
           <img id="logo" src="images/Ikasan_Logo_Transp.png" alt="logo" />
           <!-- </a> -->
           <h2>Enterprise Integration Platform - Console</h2>
        </div>

        <div class="content">
            <%-- 
                This form-login-page form is also used as the
                form-error-page to ask for a login again.
             --%>
            <c:if test="${not empty param.login_error}">
                <span class="errorMessages">
                    Your login attempt was not successful, try again<br>
                    <br />
                    Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>
                </span>
            </c:if>

            <form id="loginForm" action="<c:url value='j_spring_security_check'/>" method="post" class="dataform fancydataform">
                <fieldset>
                    <legend>Login</legend>
                    <ol>
                        <li>
                            <label for="j_username">User</label>
                            <input type='text' id='j_username' name='j_username' <c:if test="${not empty param.login_error}">value='<%= session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) %>'</c:if>/>
                        </li>
                        <li>
                            <label for="j_password">Password</label>
                            <input type='password' id='j_password' name='j_password' />
                        </li>
                        <li>
                            <label for="_spring_security_remember_me">Save password for 2 weeks</label>
                            <input type="checkbox" id="_spring_security_remember_me" name="_spring_security_remember_me"/>
                        </li>	
                    </ol>
                </fieldset>
                <p>
                    <input name="submit" type="submit" value="Login" class="controlButton"/>
                    <input name="reset" type="reset" value="Reset" class="controlButton"/>
                </p>
            </form>
        </div>
    </div>
</body>
</html>

