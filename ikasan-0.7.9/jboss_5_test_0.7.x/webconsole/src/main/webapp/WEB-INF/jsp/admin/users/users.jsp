<%@ include file="/WEB-INF/jsp/admin/adminTop.jsp"%>
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
<div class="middle">

<h2>Users</h2>

        <h3>Configured Users</h3>
        <table id="users" class="listTable">
            <thead>
                <tr>
                    <th>Username</th>
                    <th>Enabled</th>
                </tr>
            </thead>
        
            <tbody>
                <c:forEach items="${users}" var="user">
                    <c:url var="viewLink" value="view.htm">
                        <c:param name="username" value="${user.username}"/>
                    </c:url>
                    <tr>
                        <td>
                            <a href="<c:out value="${viewLink}" escapeXml="true" />">
                                <c:out value="${user.username}" />
                            </a>
                        </td>
                        <td>
                            <c:out value="${user.enabled}" />
                        </td>
                    </tr>
                </c:forEach>
        
            </tbody>
        
        
        </table>


<form:form action="createUser.htm" id="changePasswordForm" commandName="user" cssClass="dataform">
    <form:errors path="*" cssClass="errorMessages"/>

    <fieldset>
       <legend>Create User</legend>
        <ol>  
             <li>
                <label for="username">Username</label>
                <form:input path="username"/>
             </li>
             <li>
                <label for="password">Password</label>
                <form:password path="password"/>
             </li>
             <li>
             <label for="enabled">Enabled</label>
                <form:checkbox path="enabled"/>
             </li>
        </ol>

    </fieldset>                             
        <p>
                <input type="submit" value="Create User" class="controlButton"/>
        </p>
</form:form>



		

</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
