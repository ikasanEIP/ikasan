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
<%@ include file="/WEB-INF/jsp/top.jsp"%>

        <div id="content">
            <h1><fmt:message key="change_password_heading"/></h1>
            
            <!-- Change Password section TODO change CSS names -->
            <div class="createUserBox">
                <div class="searchFormHeading">
                    &nbsp;<fmt:message key="change_password_change_password"/>
                </div>
                <div class="hr"><hr /></div>

                <%-- TODO Can also JSP template this? --%>
                <c:if test="${errors != ''}">
                <c:forEach items="${errors}" var="error">
                <span class="important"><c:out value="${error}" /></span><br />
                </c:forEach>
                </c:if>

                <!-- Change Password fields -->
                <form action="userChangePassword.htm" method="post">
                    <table id="searchFields">
                        <tr>
                            <td class="searchCell formLabel">
                                 <fmt:message key="change_password_new_password" />
                            </td>
                            <td class="searchCell">
                                 <input class="inputText" id="password" type="password" name="password" value="" />
                            </td>
                        </tr>
                        <tr>
                            <td class="searchCell formLabel">
                                 <fmt:message key="change_password_confirm_new_password" />
                            </td>
                            <td class="searchCell">
                                 <input class="inputText" id="confirmNewPassword" type="password" name="confirmNewPassword" value="" />
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td>
                                <input type="submit" value="<fmt:message key="change_password_change_password_button" />" class="largeButton" />
                            </td>
                        </tr>
                    </table>
                </form>
            </div>

        </div> <!-- End Content -->

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
