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

        <script type="text/javascript" src="<c:url value='/js/jquery-ui-1.7.2.custom.min.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/js/jquery.hovertip-1.0.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/js/wiretapevents.js'/>"></script>

        <div id="content">
            <h1><fmt:message key="wiretap_events_search_heading"/></h1>

            <!-- We add an empty action for XHTML compliance -->
            <form method="get" id="wiretapSearchForm" action="">
                
                <%-- TODO Where to put this section --%>
                <c:if test="${errors != ''}">
                <c:forEach items="${errors}" var="error">
                <span class="important"><c:out value="${error}" /></span><br />
                </c:forEach>
                </c:if>
                
                <!-- Point for Back to SEARCH to jump to -->
                <a name="top"></a>
                
                <div class="searchFormHeading"><a id="showHideSearchForm" href="">[-]</a></div><div class="searchFormHeading">&nbsp;<fmt:message key="wiretap_events_search_heading2"/></div>
                <div class="hr"><hr class="searchFormHR" /></div>
                
                <!-- Hidden input filed that holds what type of search mode we're using -->
                <div><input name="pointToPointFlowProfileSearch" id="pointToPointFlowProfileSearch" type="hidden" value="<c:out value="${pointToPointFlowProfileSearch}"/>" /></div>

                <div id="searchMode"><input class="radioButton" name="searchMode" type="radio" value="flows" <c:if test="${pointToPointFlowProfileSearch == 'true'}">checked="checked"</c:if> /> <span id="flowsRadioButton"><fmt:message key="wiretap_events_search_flows"/></span> <input class="radioButton" name="searchMode" type="radio" value="modules" <c:if test="${pointToPointFlowProfileSearch != 'true'}">checked="checked"</c:if> /> <span id="modulesRadioButton"><fmt:message key="wiretap_events_search_modules"/></span> <span title="<fmt:message key="wiretap_events_search_mode_help"/>" id="searchModeHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span></div>
                <div class="hr"><hr /></div>
                
                <!-- The Search criteria for the user to search with -->
                <table id="searchFields">
                    <tbody id="flowCheckboxes">
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_events_search_flows"/>
                        </td>
                        <td class="searchCell">
                            <input class="checkbox" type="checkbox" id="pointToPointFlowProfileSelectAll" name="pointToPointFlowProfileSelectAll" <c:if test="${pointToPointFlowProfileSelectAll == 'true'}">checked="checked"</c:if> onclick="checkUncheckAll(this);"/> <fmt:message key="wiretap_events_search_select_all" /> <span title="<fmt:message key="wiretap_events_search_flow_ids_help"/>" id="pointToPointFlowProfileIdsHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell">&nbsp;</td>
                        <td class="searchCell">
                            <table>
                                <c:forEach items="${pointToPointFlowProfiles}" var="pointToPointFlowProfile">
                                <tr>
                                    <td>
                                        <input class="checkbox" name="pointToPointFlowProfileIds" type="checkbox" value="${pointToPointFlowProfile.id}" <c:forEach items="${searchParams['pointToPointFlowProfileIds']}" var="pointToPointFlowProfileId"><c:if test="${pointToPointFlowProfile.id == pointToPointFlowProfileId}">checked="checked"</c:if></c:forEach> /> <span><c:out value="${pointToPointFlowProfile.name}"/></span>
                                    </td>
                                </tr>
                                <tr class="toggle">
                                    <td class="modulesInFlows">
                                    <c:forEach items="${pointToPointFlowProfile.pointToPointFlows}" var="pointToPointFlow">
                                    <%-- If its the first element is null then list the 2nd item --%>
                                    <c:if test="${pointToPointFlow.fromModule == null}">
                                        <c:if test="${pointToPointFlow.toModule != null}">
                                        <a class="important new-window" href="viewModuleDesign.htm?moduleId=<c:out value="${pointToPointFlow.toModule.id}" />"><c:out value="${pointToPointFlow.toModule.name}" /></a>
                                        </c:if>
                                    </c:if>
                                    <%-- If the first element is not null and the 2nd element is not null then list the 2nd item (prefixed with an arrow) --%>
                                    <c:if test="${pointToPointFlow.fromModule != null}">
                                        <%-- If its the middle item then list its 2nd element --%>
                                        <c:if test="${pointToPointFlow.toModule != null}">
                                        <img class="smallArrowImage" src="/console/images/Icon_Arrow_sml.png" alt="--&gt;" /> <a class="important new-window" href="viewModuleDesign.htm?moduleId=<c:out value="${pointToPointFlow.toModule.id}" />"><c:out value="${pointToPointFlow.toModule.name}" /></a>
                                        </c:if>
                                    </c:if>
                                    <%-- We don't care about other cases for display purposes --%>
                                    </c:forEach>
                                    </td>
                                </tr>
                                </c:forEach>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                    <tbody id="moduleCheckboxes">
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_events_search_modules"/>
                        </td>
                        <td class="searchCell"> 
                            <input class="checkbox" type="checkbox" id="moduleSelectAll" name="moduleSelectAll" <c:if test="${moduleSelectAll == 'true'}">checked="checked"</c:if> onclick="checkUncheckAll(this);"/> <fmt:message key="wiretap_events_search_select_all" /> <span title="<fmt:message key="wiretap_events_search_module_ids_help"/>" id="moduleIdsHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell">&nbsp;</td>
                        <td class="searchCell">
                            <table>
                                <c:forEach items="${modules}" var="module">
                                <tr>
                                    <td class="modulesInFlows"><input class="checkbox" name="moduleIds" type="checkbox" value="${module.id}" <c:forEach items="${searchParams['moduleIds']}" var="moduleId"><c:if test="${module.id == moduleId}">checked="checked"</c:if></c:forEach> /> <a href="viewModuleDesign.htm?moduleId=<c:out value="${module.id}" />" class="new-window"> <c:out value="${module.name}"/></a></td>
                                    <td class="moduleDescription"><c:out value="${module.description}" escapeXml="false" /></td>
                                </tr>
                                </c:forEach>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                    <tbody>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_events_module_flow"/>
                        </td>
                        <td class="searchCell">
                            <input class="inputText" id="moduleFlow" type="text" name="moduleFlow" value="${searchParams['moduleFlow']}"/> <span title="<fmt:message key="wiretap_events_search_module_flow_help"/>" id="moduleFlowHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_events_component"/>
                        </td>
                        <td class="searchCell">
                            <input class="inputText" id="componentName" type="text" name="componentName" value="${searchParams['componentName']}"/> <span title="<fmt:message key="wiretap_events_search_component_name_help"/>" id="componentNameHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_events_event_id"/>
                        </td>
                        <td class="searchCell">
                            <input class="inputText" id="eventId" type="text" name="eventId" value="${searchParams['eventId']}"/> <span title="<fmt:message key="wiretap_events_search_event_id_help"/>" id="eventIdHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_events_payload_id"/>
                        </td>
                        <td class="searchCell">
                            <input class="inputText" id="payloadId" type="text" name="payloadId" value="${searchParams['payloadId']}"/> <span title="<fmt:message key="wiretap_events_search_payload_id_help"/>" id="payloadIdHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_events_from"/>
                        </td>
                        <td class="searchCell">
                            <input class="inputTextDate" id="fromDateString" type="text" name="fromDateString" value="${searchParams['fromDateString']}"/>
                            <input class="inputTextTime" id="fromTimeString" type="text" name="fromTimeString" value="${searchParams['fromTimeString']}"/> <span title="<fmt:message key="wiretap_events_search_from_date_help"/>" id="fromDateStringHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_events_until"/>
                        </td>
                        <td class="searchCell">
                            <input class="inputTextDate" id="untilDateString" type="text" name="untilDateString" value="${searchParams['untilDateString']}"/>
                            <input class="inputTextTime" id="untilTimeString" type="text" name="untilTimeString" value="${searchParams['untilTimeString']}"/> <span title="<fmt:message key="wiretap_events_search_to_date_help"/>" id="toDateStringHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                    </tr>
                    <tr>
                        <td  class="searchCell formLabel">
                            <fmt:message key="wiretap_events_payload_content"/>
                        </td>
                        <td class="searchCell">
                            <input class="inputText" id="payloadContent" type="text" name="payloadContent" value="${searchParams['payloadContent']}" /> <span title="<fmt:message key="wiretap_events_search_payload_content_help"/>" id="payloadContentHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="searchCell formLabel">
                            <fmt:message key="wiretap_events_order_by"/>
                        </td>
                        <td class="searchCell">
                            <select class="inputSelect" id="orderBy" name="orderBy">
                                <option value="id" <c:if test="${orderBy=='id'}">selected="selected"</c:if>>Id</option>
                                <option value="moduleName" <c:if test="${orderBy=='moduleName'}">selected="selected"</c:if>>Module</option>
                                <option value="flowName" <c:if test="${orderBy=='flowName'}">selected="selected"</c:if>>Module Flow</option>
                                <option value="componentName" <c:if test="${orderBy=='componentName'}">selected="selected"</c:if>>Component</option>
                                <option value="eventId" <c:if test="${orderBy=='eventId'}">selected="selected"</c:if>>Event Id</option>
                                <option value="payloadId" <c:if test="${orderBy=='payloadId'}">selected="selected"</c:if>>Payload Id</option>
                                <option value="created" <c:if test="${orderBy=='created'}">selected="selected"</c:if>>Created Date/Time</option>
                            </select>
                            <span title="<fmt:message key="wiretap_events_search_order_by_help"/>" id="orderByHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                            <input id="orderAsc" class="checkbox" type="checkbox" name="orderAsc" <c:if test="${orderAsc=='true'}">checked="checked"</c:if>/> <span class="formLabel"><fmt:message key="wiretap_events_order_ascending"/></span> <span title="<fmt:message key="wiretap_events_search_order_asc_help"/>" id="orderAscHelp"><img class="helpIcon" src="/console/images/Icon_Help_sml.png" alt="?" /></span>
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td>
                            <input type="submit" value="<fmt:message key="wiretap_events_search_submit"/>" class="largeButton" />
                        </td>
                    </tr>
                    </tbody>
                </table>
                 
                <%@ include file="/WEB-INF/jsp/pagedResultsHeader.jsp"%>
            
            </form>

            <%-- Section for re-executing the searches but with their order reversed --%>
            <c:url var="idLink" value="list.htm">
                <c:forEach var="entry" items="${searchParams}">
                    <c:forEach var="entryValue" items="${entry.value}">
                        <c:param name="${entry.key}" value="${entryValue}"/>
                    </c:forEach>
                </c:forEach>
                <c:param name="orderBy" value="id"/>
                <c:param name="orderAsc" value="${!orderAsc}"/>
                <c:param name="pointToPointFlowProfileSearch" value="${pointToPointFlowProfileSearch}"/>
                <c:param name="pointToPointFlowProfileSelectAll" value="${pointToPointFlowProfileSelectAll}"/>
                <c:param name="moduleSelectAll" value="${moduleSelectAll}"/>
                <c:param name="pageSize" value="${pageSize}"/>
            </c:url>
            
            <c:url var="moduleLink" value="list.htm">
                <c:forEach var="entry" items="${searchParams}">
                    <c:forEach var="entryValue" items="${entry.value}">
                        <c:param name="${entry.key}" value="${entryValue}"/>
                    </c:forEach>
                </c:forEach>
                <c:param name="orderBy" value="moduleName"/>
                <c:param name="orderAsc" value="${!orderAsc}"/>
                <c:param name="pointToPointFlowProfileSearch" value="${pointToPointFlowProfileSearch}"/>
                <c:param name="pointToPointFlowProfileSelectAll" value="${pointToPointFlowProfileSelectAll}"/>
                <c:param name="moduleSelectAll" value="${moduleSelectAll}"/>
                <c:param name="pageSize" value="${pageSize}"/>
            </c:url>
            
            <c:url var="flowLink" value="list.htm">
                <c:forEach var="entry" items="${searchParams}">
                    <c:forEach var="entryValue" items="${entry.value}">
                        <c:param name="${entry.key}" value="${entryValue}"/>
                    </c:forEach>
                </c:forEach>
                <c:param name="orderBy" value="flowName"/>
                <c:param name="orderAsc" value="${!orderAsc}"/>
                <c:param name="pointToPointFlowProfileSearch" value="${pointToPointFlowProfileSearch}"/>       	
                <c:param name="pointToPointFlowProfileSelectAll" value="${pointToPointFlowProfileSelectAll}"/>
                <c:param name="moduleSelectAll" value="${moduleSelectAll}"/>        
                <c:param name="pageSize" value="${pageSize}"/>
            </c:url>
            
            <c:url var="componentLink" value="list.htm">
                <c:forEach var="entry" items="${searchParams}">
                    <c:forEach var="entryValue" items="${entry.value}">
                        <c:param name="${entry.key}" value="${entryValue}"/>
                    </c:forEach>
                </c:forEach>
                <c:param name="orderBy" value="componentName"/>
                <c:param name="orderAsc" value="${!orderAsc}"/>
                <c:param name="pointToPointFlowProfileSearch" value="${pointToPointFlowProfileSearch}"/>
                <c:param name="pointToPointFlowProfileSelectAll" value="${pointToPointFlowProfileSelectAll}"/>
                <c:param name="moduleSelectAll" value="${moduleSelectAll}"/>
                <c:param name="pageSize" value="${pageSize}"/>
            </c:url>
            
            <c:url var="createdDateTimeLink" value="list.htm">
                <c:forEach var="entry" items="${searchParams}">
                    <c:forEach var="entryValue" items="${entry.value}">
                       <c:param name="${entry.key}" value="${entryValue}"/>
                    </c:forEach>
                </c:forEach>
                <c:param name="orderBy" value="created"/>
                <c:param name="orderAsc" value="${!orderAsc}"/>
                <c:param name="pointToPointFlowProfileSearch" value="${pointToPointFlowProfileSearch}"/>
                <c:param name="pointToPointFlowProfileSelectAll" value="${pointToPointFlowProfileSelectAll}"/>
                <c:param name="moduleSelectAll" value="${moduleSelectAll}"/>
                <c:param name="pageSize" value="${pageSize}"/>
            </c:url>
        
            <%-- Results section --%>
            <c:if test="${resultSize > 0}">
            <div>
                <table id="wiretapSearchResults">
                    <thead>
                        <tr>
                            <th class="resultsHeaderCell"><a href="<c:out value="${idLink}#results" escapeXml="true" />"><fmt:message key="wiretap_events_search_results_id"/> <img class="smallIcon" src="/console/images/Sort.png" alt="Sort" /></a></th>
                            <th class="resultsHeaderCell"><a href="<c:out value="${moduleLink}#results" escapeXml="true" />"><fmt:message key="wiretap_events_search_results_module"/> <img class="smallIcon" src="/console/images/Sort.png" alt="Sort" /></a></th>
                            <th class="resultsHeaderCell"><a href="<c:out value="${flowLink}#results" escapeXml="true" />"><fmt:message key="wiretap_events_search_results_module_flow"/> <img class="smallIcon" src="/console/images/Sort.png" alt="Sort" /></a></th>
                            <th class="resultsHeaderCell"><a href="<c:out value="${componentLink}#results" escapeXml="true" />"><fmt:message key="wiretap_events_search_results_component"/> <img class="smallIcon" src="/console/images/Sort.png" alt="Sort" /></a></th>
                            <th class="resultsHeaderCellLast"><a href="<c:out value="${createdDateTimeLink}#results" escapeXml="true" />"><fmt:message key="wiretap_events_search_results_created_date_time"/> <img class="smallIcon" src="/console/images/Sort.png" alt="Sort" /></a></th>
                        </tr>
                     </thead>
                     <tbody>
                         <c:forEach items="${results}" var="event">
                         <c:url var="viewEventLink" value="viewEvent.htm">
                         <c:param name="eventId" value="${event.id}"/>
                         <c:param name="searchResultsUrl" value="${searchResultsUrl}"/>
                         </c:url>
                         <tr>
                            <td class="resultsCell"><a href="<c:out value="${viewEventLink}" escapeXml="true" />"><c:out value="${event.id}" /></a></td>
                            <td class="resultsCell"><c:out value="${event.moduleName}" /></td>
                            <td class="resultsCell"><c:out value="${event.flowName}" /></td>
                            <td class="resultsCell"><c:out value="${event.componentName}" /></td>
                            <td class="resultsCellLast"><c:out value="${event.created}" /></td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div> <!-- Close off Search Results -->
            </c:if>
            <div id="backToSearch"><a href="#top"><img class="smallIcon" src="/console/images/Icon_Arrow_Up.png" alt="^" /> <fmt:message key="wiretap_events_search_back_to_top"/></a></div>
        </div> <!-- Close off content -->

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
