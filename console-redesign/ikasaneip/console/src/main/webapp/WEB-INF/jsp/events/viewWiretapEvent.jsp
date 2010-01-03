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
<%@ include file="/WEB-INF/jsp/events/eventsTop.jsp"%>

        <script type="text/javascript" src="<c:url value='/js/wiretapevent.js'/>"></script>

        <div id="content">
            <h1><fmt:message key="wiretap_heading"/></h1>
            
            <!-- Navigation section -->
            <div id="wiretapNavigation">
                <c:choose>
                <c:when test="${wiretapEvent.previousByPayload != null}">
                <a class="active" href="viewEvent.htm?wiretapEventId=<c:out value="${wiretapEvent.previousByPayload}" />"><img class="smallIcon" src="/console/images/Previous_On.png" alt="&lt;" /><fmt:message key="wiretap_event_previous"/></a>
                </c:when>
                <c:otherwise>
                <img class="smallIcon" src="/console/images/Previous.png" alt="&lt;" /><fmt:message key="wiretap_event_previous"/>
                </c:otherwise>
                </c:choose>
                &nbsp;
                <c:choose>
                <c:when test="${wiretapEvent.nextByPayload != null}">
                <a class="active" href="viewEvent.htm?wiretapEventId=<c:out value="${wiretapEvent.nextByPayload}" />"><fmt:message key="wiretap_event_next"/><img class="smallIcon" src="/console/images/Next_On.png" alt="&gt;" /></a>
                </c:when>
                <c:otherwise>
                <fmt:message key="wiretap_event_next"/><img class="smallIcon" src="/console/images/Next.png" alt="&gt;" />
                </c:otherwise>
                </c:choose>
                <span title="<fmt:message key="wiretap_navigation_help"/>" id="wiretapNavigationHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
            </div>

            <div><p>&nbsp;</p></div>

            <!-- Wiretap Details section TODO change CSS names -->
            <div class="createUserBox" style="clear: both">
                <div class="searchFormHeading">
                    &nbsp;<fmt:message key="wiretap_details"/>
                </div>
                <div class="hr"><hr /></div>

                <!-- Wiretap Detail fields -->
                <table id="searchFields">
                    <tr>
                        <td class="searchCell formLabel">
                             <fmt:message key="wiretap_id" />
                        </td>
                        <td class="searchCell">
                             <c:out value="${wiretapEvent.id}" />
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_module_name" />
                        </td>
                        <td class="searchCell">
                            <c:out value="${wiretapEvent.moduleName}" />
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_module_flow_name" />
                        </td>
                        <td class="searchCell">
                            <c:out value="${wiretapEvent.flowName}" />
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_component_name" />
                        </td>
                        <td class="searchCell">
                            <c:out value="${wiretapEvent.componentName}" />
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_created_date_time" />
                        </td>
                        <td class="searchCell">
                            <fmt:formatDate value="${wiretapEvent.created}" pattern="dd/MM/yyyy h:mma" />
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_expiry_date_time" />
                        </td>
                        <td class="searchCell">
                            <fmt:formatDate value="${wiretapEvent.expiry}" pattern="dd/MM/yyyy h:mma" />
                        </td>
                    </tr>
                </table>
            </div>

            <div><p>&nbsp;</p></div>

            <!-- Wiretap Event Details section TODO change CSS names -->
            <div class="createUserBox">
                <div class="searchFormHeading">
                    &nbsp;<fmt:message key="wiretap_event_details"/>
                </div>
                <div class="hr"><hr /></div>

                <!-- Wiretap Event Detail fields -->
                <table id="searchFields">
                    <tr>
                        <td class="searchCell formLabel">
                             <fmt:message key="wiretap_event_event_id" />
                        </td>
                        <td class="searchCell">
                             <c:out value="${wiretapEvent.eventId}" />
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_event_payload_id" />
                        </td>
                        <td class="searchCell">
                            <c:out value="${wiretapEvent.payloadId}" />
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_event_payload_content_native" />
                            <span title="<fmt:message key="wiretap_event_native_format_help"/>" id="wiretapEventNativeFormatHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                        <td class="searchCell">
                        <!-- TODO:  To be enhanced with not just a XML check -->
                        <c:choose>
                        <c:when test='${fn:startsWith(wiretapEvent.payloadContent, "<?xml")}'>
                            <a href="viewPrettyPayloadContent.htm?wiretapEventId=<c:out value="${wiretapEvent.id}" />" class="new-window">
                                <fmt:message key="wiretap_event_formatted_content_native"/>
                            </a>
                            </c:when>
                            <c:otherwise>
                            <fmt:message key="wiretap_event_payload_content_not_native"/>
                            </c:otherwise>
                        </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_event_download" />
                            <span title="<fmt:message key="wiretap_event_download_help"/>" id="wiretapEventDownloadHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                        <td class="searchCell">
                            <a href="downloadPayloadContent.htm?wiretapEventId=<c:out value="${wiretapEvent.id}" />">
                                <img src="/console/images/Btn_Download.png" alt="download" id="downloadButton" />
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_event_payload_content" />
                        </td>
                        <td class="searchCell">
                            <textarea id="payloadContent" readonly="readonly"><c:out value="${wiretapEvent.payloadContent}" /></textarea>
                        </td>
                    </tr>
                </table>
            </div>
            
            <div id="backToSearch">
                <c:choose>
                <c:when test="${searchResultsUrl != null}">
                    <a href="<c:out value="${searchResultsUrl}" />"><fmt:message key="wiretap_back_to_search_results" /></a>
                </c:when>
                <c:otherwise>
                    <a href="<c:url value="newSearch.htm"/>"><fmt:message key="wiretap_new_search" /></a>
                </c:otherwise>
                </c:choose>
            </div>
        </div> <!-- End Content -->

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
