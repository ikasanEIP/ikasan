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

        <script type="text/javascript" src="<c:url value='/js/maintainUser.js'/>"></script>

        <div id="content">
            <h1><fmt:message key="maintain_user_heading"/></h1>
            
            <!-- Maintain Users section TODO change CSS names -->
            <div class="createUserBox">
                <div class="searchFormHeading">
                    &nbsp;<fmt:message key="maintain_user_user"/> <c:out value="${user.username}" />
                </div>
                <div class="hr"><hr /></div>

                <!-- Maintain User fields -->
                <table id="searchFields">
                    <tr>
                        <td class="searchCell formLabel">
                             <fmt:message key="maintain_user_username" />
                        </td>
                        <td class="searchCell">
                             <c:out value="${user.username}" />
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                             <fmt:message key="maintain_user_email" />
                        </td>
                        <td class="searchCell">
                             <c:out value="${user.email}" />
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="maintain_user_enabled" />
                        </td>
                        <td class="searchCell">
                            <c:if test="${user.enabled == 'true'}">
                            <img class="smallIcon" src="/console/images/Enabled.png" alt="Yes" />
                            </c:if>
                            <c:if test="${user.enabled != 'true'}">
                            <img class="smallIcon" src="/console/images/Disabled.png" alt="No" />
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td>
                            <c:url var="deleteLink" value="delete.htm">
                                <c:param name="username" value="${user.username}" />
                            </c:url>
                            <div class="miniForm">
                                <form action="${deleteLink}" method="post">
                                    <input type="submit" value="<fmt:message key="maintain_user_delete"/>" class="button" />&nbsp;
                                </form>
                            </div>
                            <c:if test="${user.enabled}">
                            <c:url var="disableLink" value="disable.htm">
                                <c:param name="username" value="${user.username}" />
                            </c:url>
                            <div class="miniForm">
                                <form action="${disableLink}" method="post">
                                    <input type="submit" value="<fmt:message key="maintain_user_disable"/>" class="button" />
                                </form>
                            </div>
                            </c:if>
                            <c:if test="${!user.enabled}">
                            <c:url var="enableLink" value="enable.htm">
                                <c:param name="username" value="${user.username}" />
                            </c:url>
                            <div class="miniForm">
                                <form action="${enableLink}" method="post">
                                    <input type="submit" value="<fmt:message key="maintain_user_enable"/>" class="button" />
                                </form>
                            </div>
                            </c:if>
                        </td>
                    </tr>
                </table>
            </div>

            <div><p>&nbsp;</p></div>

            <!-- Change Email section TODO change CSS names -->
            <div class="createUserBox">
                <div class="searchFormHeading">
                    &nbsp;<fmt:message key="maintain_user_change_email"/>
                </div>
                <div class="hr"><hr /></div>

                <%-- TODO Can also JSP template this? --%>
                <c:if test="${errors != ''}">
                <c:forEach items="${errors}" var="error">
                <span class="important"><c:out value="${error}" /></span><br />
                </c:forEach>
                </c:if>

                <!-- Change Password fields -->
                <form action="changeEmail.htm" method="post">
                    <table id="searchFields">
                        <tr>
                            <td class="searchCell formLabel">
                                 <fmt:message key="maintain_user_new_email" />
                            </td>
                            <td class="searchCell">
                                 <input class="inputTextLong" id="email" type="text" name="email" value="" />
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td>
                                <input type="submit" value="<fmt:message key="maintain_user_change_email_button" />" class="largeButton" />
                            </td>
                        </tr>
                    </table>
                </form>
            </div>

            <div><p>&nbsp;</p></div>

            <!-- Change Password section TODO change CSS names -->
            <div class="createUserBox">
                <div class="searchFormHeading">
                    &nbsp;<fmt:message key="maintain_user_change_password"/>
                </div>
                <div class="hr"><hr /></div>

                <%-- TODO Can also JSP template this? --%>
                <c:if test="${errors != ''}">
                <c:forEach items="${errors}" var="error">
                <span class="important"><c:out value="${error}" /></span><br />
                </c:forEach>
                </c:if>

                <!-- Change Password fields -->
                <form action="changePassword.htm" method="post">
                    <table id="searchFields">
                        <tr>
                            <td class="searchCell formLabel">
                                 <fmt:message key="maintain_user_new_password" />
                            </td>
                            <td class="searchCell">
                                 <input class="inputText" id="password" type="password" name="password" value="" />
                            </td>
                        </tr>
                        <tr>
                            <td class="searchCell formLabel">
                                 <fmt:message key="maintain_user_confirm_new_password" />
                            </td>
                            <td class="searchCell">
                                 <input class="inputText" id="confirm_password" type="password" name="confirm_password" value="" />
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td>
                                <input type="submit" value="<fmt:message key="maintain_user_change_password_button" />" class="largeButton" />
                            </td>
                        </tr>
                    </table>
                </form>
            </div>

            <div><p>&nbsp;</p></div>

            <!-- Authorities section TODO change CSS names -->
            <div>
                <div class="searchFormHeading">
                    &nbsp;<fmt:message key="maintain_user_authorities"/>
                </div>
                <div class="hr"><hr class="searchFormHR" /></div>

                <!-- TODO:  Tidyup CSS names -->
                <table id="authorities">
                    <thead>
                        <tr>
                            <th class="resultsHeaderCell"><fmt:message key="maintain_user_authority_name" /></th>
                            <th class="resultsHeaderCell"><fmt:message key="maintain_user_authority_description" /></th>
                            <th class="resultsHeaderCell"><fmt:message key="maintain_user_enabled" /></th>
                            <th class="">&nbsp;</th>
                        </tr>
                     </thead>
                     <tbody>
                         <c:forEach items="${allAuthorities}" var="authority">
                         <tr>
                            <td class="resultsCell"><c:out value="${authority.authority}" /></td>
                            <td class="resultsCell"><c:out value="${authority.description}" /></td>
                            <td class="listCellLast">
                                <!-- TODO Not the most efficient way to process this, can be improved later if there's a performance problem -->
                                <c:forEach items="${user.authorities}" var="grantedAuthority">
                                <c:if test="${grantedAuthority.authority == authority.authority}">
                                <img class="smallIcon" src="/console/images/Enabled.png" alt="Yes" />
                                </c:if>
                                </c:forEach>
                                <c:forEach items="${nonGrantedAuthorities}" var="nonGrantedAuthority">
                                <c:if test="${nonGrantedAuthority.authority == authority.authority}">
                                <img class="smallIcon" src="/console/images/Disabled.png" alt="No" />
                                </c:if>
                                </c:forEach>
                            </td>
                            <td class="">
                                <c:forEach items="${user.authorities}" var="grantedAuthority">
                                <c:if test="${grantedAuthority.authority == authority.authority}">
                                <c:url var="revokeLink" value="revokeAuthority.htm">
                                    <c:param name="username" value="${user.username}"/>
                                    <c:param name="authority" value="${authority.authority}"/>
                                </c:url>
                                <form action="${revokeLink}" method="post">
                                    <input type="submit" value="<fmt:message key="maintain_user_revoke" />" class="button" />
                                </form>
                                </c:if>
                                </c:forEach>
                                <c:forEach items="${nonGrantedAuthorities}" var="nonGrantedAuthority">
                                <c:if test="${nonGrantedAuthority.authority == authority.authority}">
                                <c:url var="grantLink" value="grantAuthority.htm">
                                    <c:param name="username" value="${user.username}"/>
                                    <c:param name="authority" value="${authority.authority}"/>
                                </c:url>
                                <form action="${grantLink}" method="post">
                                    <input type="submit" value="<fmt:message key="maintain_user_grant" />" class="button" />
                                </form>
                                </c:if>
                                </c:forEach>
                            </td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

        </div> <!-- End Content -->

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
