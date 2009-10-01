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
<%@ include file="/WEB-INF/jsp/top.jsp"%>

<div class="middle">
    <h2><fmt:message key="home_heading"/></h2> - <fmt:message key="home_heading2"/>
    <h2><fmt:message key="home_ikasan_support_title"/></h2>
    <p>
        <fmt:message key="home_ikasan_support_text"/>
    </p>
    <p>&nbsp;</p>
    <p>
        <ul class="homeContent">
            <li><fmt:message key="home_ikasan_home_page_text"/> <a href="http://www.ikasan.org"><fmt:message key="home_ikasan_home_page_link"/></a></li>
            <li><fmt:message key="home_ikasan_irc_text"/> <a href="http://sourceforge.net/apps/mediawiki/ikasaneip/index.php?title=IRC"/><fmt:message key="home_ikasan_irc_link"/></a></li>
            <li><fmt:message key="home_ikasan_user_mailing_list_text"/> <a href="mailto://ikasaneip-user@lists.sourceforge.net"><fmt:message key="home_ikasan_user_mailing_list_link"/></a> - (<a href="http://lists.sourceforge.net/lists/listinfo/ikasaneip-user"><fmt:message key="home_ikasan_user_mailing_list_subscribe_link"/></a>)</li>        
            <li><fmt:message key="home_ikasan_wiki_text"/> <a href="http://sourceforge.net/apps/mediawiki/ikasaneip/"><fmt:message key="home_ikasan_wiki_link"/></a></li>
            <li><fmt:message key="home_ikasan_jira_text"/> <a href="http://open.jira.com/browse/IKASAN"><fmt:message key="home_ikasan_jira_link"/></a></li>
        </ul>
    </p>
</div>

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
