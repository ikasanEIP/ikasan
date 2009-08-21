<%--
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
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>

<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Ikasan Console</title>
    <meta http-equiv="Content-Language" content="English" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>" media="screen" />
</head>

<body>

<div id="wrap">

    <div id="top"></div>

    <div id="content">

        <div class="header">
            <h1><a class="white" href="./">Ikasan</a></h1>
            <h2>Enterprise Integration Platform - Console</h2>
        </div>
    
        <div class="middle">
            <%-- 
                This form-login-page form is also used as the
                form-error-page to ask for a login again.
             --%>
            <c:if test="${not empty param.login_error}">
                <span class="errorMessages">
                    Your login attempt was not successful, try again<br>
                    <br>
                    Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>
                </span>
            </c:if>
    
            <form id="loginForm" action="<c:url value='j_spring_security_check'/>" method="post" class="dataform fancydataform">
    
                <fieldset>
                    <legend>Login</legend>
                    <ol>
                        <li>
            				<label for="j_username">User</label>
        				    <input type='text' name='j_username' <c:if test="${not empty param.login_error}">value='<%= session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) %>'</c:if>/>
        			    </li>
                        <li>
                            <label for="j_password">Password</label>
                            <input type='password' name='j_password'/>
                        </li>
                        <li>
        				    <label for="_spring_security_remember_me">Save password for 2 weeks</label>
        				    <input type="checkbox" name="_spring_security_remember_me"/>
        				</li>	
                     </ol>   				 
                </fieldset>
    
                <p>
        			<input name="submit" type="submit" value="Login" class="controlButton"/>
            		<input name="reset" type="reset" value="Reset" class="controlButton"/>
                </p>
    
            </form>
        </div>
    
        <div id="clear"></div>

    </div>

    <div id="bottom"></div>

</div>

<div id="footer">Design by <a href="http://www.minimalistic-design.net">Minimalistic Design</a></div>

</body>
</html>

