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
<%@ include file="/WEB-INF/jsp/admin/adminTop.jsp"%>

<div class="middle">

<h2>User :: <c:out value="${user.username}" /></h2>


    <h3>Details</h3>
        <table id="userDetails" class="keyValueTable">
            <tr>
                <th>
                    Username
                </th>
                <td>
                    <c:out value="${user.username}" />
                </td>
            </tr>
            <tr>
                <th>
                    Email Address
                </th>
                <td>
                    <c:out value="${user.email}" />
                </td>
            </tr>
            <tr>
                <th>
                    Enabled
                </th>
                <td>
                    <c:out value="${user.enabled}" />
                </td>
            </tr>
        </table>
        
        <p>
          <c:url var="deleteLink" value="delete.htm">
            <c:param name="username" value="${user.username}"/>
          </c:url>
              <form:form action="${deleteLink}" method="post">
                <input type="submit" value="Delete" class="controlButton"/>
              </form:form>
            Delete
          </a>
        </p>
        
        <p>
          <c:if test="${user.enabled}">
              <c:url var="disableLink" value="disable.htm">
                <c:param name="username" value="${user.username}"/>
              </c:url>
              <form:form action="${disableLink}" method="post">
                <input type="submit" value="Disable" class="controlButton"/>
              </form:form>
          </c:if>
          <c:if test="${!user.enabled}">
              <c:url var="enableLink" value="enable.htm">
                <c:param name="username" value="${user.username}"/>
              </c:url>
              <form:form action="${enableLink}" method="post">
                <input type="submit" value="Enable" class="controlButton"/>
              </form:form>
          </c:if>
        </p>

<h3>Granted Authorities</h3>
<table id="grantedAuthoritiesList" class="listTable" >
	<thead>
		<tr>
			<th>Authority</th>
			<th>Description</th>
			<th>&nbsp;</th>
		</tr>
	</thead>

	<tbody>
		<c:forEach items="${user.authorities}" var="authority">
		  <c:url var="revokeLink" value="revokeAuthority.htm">
            <c:param name="username" value="${user.username}"/>
            <c:param name="authority" value="${authority.authority}"/>
          </c:url>
          
			<tr>
				<td><c:out value="${authority.authority}" /></td>
				<td><c:out value="${authority.description}" /></td>
				<td>
                  <form:form action="${revokeLink}" method="post">
                    <input type="submit" value="Revoke" class="controlButton"/>
                  </form:form>
				</td>
			</tr>
		</c:forEach>

	</tbody>


</table>

<h3>Non Granted Authorities</h3>
<table id="nonGrantedAuthoritiesList" class="listTable" >
    <thead>
        <tr>
            <th>Authority</th>
            <th>Description</th>
            <th>&nbsp;</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${nonGrantedAuthorities}" var="authority">
          <c:url var="grantLink" value="grantAuthority.htm">
            <c:param name="username" value="${user.username}"/>
            <c:param name="authority" value="${authority.authority}"/>
          </c:url>
          
            <tr>
                <td><c:out value="${authority.name}" /></td>
                <td><c:out value="${authority.description}" /></td>
                <td>
                  <form:form action="${grantLink}" method="post">
                    <input type="submit" value="Grant" class="controlButton"/>
                  </form:form>
                </td>
            </tr>
        </c:forEach>

    </tbody>


</table>

<%--
    IKASAN-457, will need to overhaul this in 0.8.1 or later

<form:form action="changePassword.htm" id="changePasswordForm" commandName="user" cssClass="dataform">
    <form:errors path="*" cssClass="errorMessages"/>

    <fieldset>
       <legend>Change Password</legend>
        <ol>  
             <li>
                <label for="password">New Password</label>
                <form:password path="password"/>
             </li>
             <li>
                <label for="confirm_password">Confirm New Password</label>
                <form:password path="confirm_password"/>
             </li>
        </ol>

    </fieldset>                             
        <p>
                <input type="submit" value="Change Password" class="controlButton"/>
        </p>
</form:form>
--%>

</div>

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
