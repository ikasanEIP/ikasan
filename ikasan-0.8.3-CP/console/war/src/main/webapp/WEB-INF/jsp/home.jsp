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
<%@ include file="/WEB-INF/jsp/top.jsp" %>
        <div id="content">
            <h1><fmt:message key="home_heading"/></h1>
            <p class="text"><fmt:message key="home_heading2"/></p>
            <table id="homeLinksTable">
                <tr>
                    <td class="navigationCell"><img class="navigationArrow" src="/console/images/Icon_ArrowHm.png" alt="Arrow"/></td>
                    <td class="navigationCell"><a href="<c:url value='/events/newSearch.htm'/>"><img class="navigationImage" src="/console/images/Btn_Events.png" alt="Events Link"/></a></td>
                    <%-- Spring based security around the admin pages --%>
                    <security:authorize ifAllGranted="ROLE_ADMIN">
                    <td class="navigationCell"><a href="<c:url value='/admin/admin.htm'/>"><img class="navigationImage" src="/console/images/Btn_Admin.png" alt="Admin Link"/></a></td>
                    </security:authorize>
                    <td rowspan="2" class="navigationCell leftBorder">&nbsp;</td>
                    <td class="navigationCell"><a href="<c:url value='/support.htm'/>"><img class="navigationImage" src="/console/images/Btn_Support.png" alt="Support Link"/></a></td>
                </tr>
                <tr>
                    <td class="navigationCell">&nbsp;</td>
                    <td class="navigationText"><fmt:message key="home_events_description"/></td>
                    <%-- Spring based security around the admin pages --%>
                    <security:authorize ifAllGranted="ROLE_ADMIN">
                    <td class="navigationText"><fmt:message key="home_admin_description"/></td>
                    </security:authorize>
                    <!-- Rowspanned cell extends down here -->
                    <td class="navigationText"><fmt:message key="home_support_description"/></td>
                </tr>
            </table>
        </div>
<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
