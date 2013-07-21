<%-- 

 $Id: home.jsp 2725 2009-12-09 19:30:08Z karianna $
 $URL: https://open.jira.com/svn/IKASAN/branches/console-redesign/ikasaneip/console/src/main/webapp/WEB-INF/jsp/home.jsp $ 

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
            <h1><fmt:message key="support_heading"/></h1>
            <p class="text"><fmt:message key="support_heading2"/></p>
            <table id="supportLinksTable">
                <tr>
                    <td class="supportCell"><a href="http://www.ikasan.org"><img class="supportImage" src="/console/images/Icon_ProjectSite.png" alt="Site Link"/></a></td>
                    <td class="supportCell2">
                        <a class="supportText" href="http://www.ikasan.org"><fmt:message key="support_website_description"/></a><br/>
                        <a href="http://www.ikasan.org"><fmt:message key="support_website_description2"/></a>
                    </td>
                </tr>
                <tr>
                    <td class="supportCell"><a href="http://sourceforge.net/apps/mediawiki/ikasaneip/index.php?title=IRC"><img class="supportImage" src="/console/images/Icon_RealTimeSupport.png" alt="IRC Link"/></a></td>
                    <td class="supportCell2">
                        <a class="supportText" href="http://sourceforge.net/apps/mediawiki/ikasaneip/index.php?title=IRC"><fmt:message key="support_irc_description"/></a><br/>
                        <a href="http://sourceforge.net/apps/mediawiki/ikasaneip/index.php?title=IRC"><fmt:message key="support_irc_description2"/></a>
                    </td>
                </tr>
                <tr>
                    <td class="supportCell"><a href="mailto:ikasan-user@lists.sourceforge.net"><img class="supportImage" src="/console/images/Icon_UserMailingList.png" alt="Mailing List Link"/></a></td>
                    <td class="supportCell2">
                        <a class="supportText" href="mailto:ikasan-user@lists.sourceforge.net"><fmt:message key="support_mailing_list_description"/></a><br/>
                        <a href="http://lists.sourceforge.net/lists/listinfo/ikasaneip-user"><fmt:message key="support_mailing_list_description2"/></a>
                    </td>
                </tr>
                <tr>
                    <td class="supportCell"><a href="http://sourceforge.net/apps/mediawiki/ikasaneip/"><img class="supportImage" src="/console/images/Icon_Wiki.png" alt="Wiki Link"/></a></td>
                    <td class="supportCell2">
                        <a class="supportText" href="http://sourceforge.net/apps/mediawiki/ikasaneip/"><fmt:message key="support_wiki_description"/></a><br/>
                        <a href="http://sourceforge.net/apps/mediawiki/ikasaneip/"><fmt:message key="support_wiki_description2"/></a>
                    </td>
                </tr>
                <tr>
                    <td class="supportCell"><a href="http://open.jira.com/browse/IKASAN"><img class="supportImage" src="/console/images/Icon_Bugs.png" alt="JIRA Link"/></a></td>
                    <td class="supportCell2"><a class="supportText" href="http://open.jira.com/browse/IKASAN"><fmt:message key="support_jira_description"/></a><br/><a href="http://open.jira.com/browse/IKASAN"><fmt:message key="support_jira_description2"/></a></td>
                </tr>
            </table>
        </div>
<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
