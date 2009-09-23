<%-- 

 $Id:
 $URL: 

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
<%@ include file="/WEB-INF/jsp/admin/adminTop.jsp"%>

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
