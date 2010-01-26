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
                <td><c:out value="${authority.authority}" /></td>
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


<form:form action="changePassword.htm" id="changePasswordForm" commandName="user" cssClass="dataform">
    <form:errors path="*" cssClass="errorMessages"/>

    <fieldset>
       <legend>Change Password</legend>
        <ol>  
             <li>
                <label for="password">New Password</label>
                <form:password path="password"/>
             </li>
        </ol>

    </fieldset>                             
        <p>
                <input type="submit" value="Change Password" class="controlButton"/>
        </p>
</form:form>

</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
