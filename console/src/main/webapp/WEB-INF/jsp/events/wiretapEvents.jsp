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
<%@ include file="/WEB-INF/jsp/events/eventsTop.jsp"%>

  <link type="text/css" href="<c:url value='/css/jquery/redmond/jquery-ui-1.7.2.custom.css'/>" rel="stylesheet"></link>
  <script type="text/javascript" src="<c:url value='/js/jquery-ui-1.7.2.custom.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/js/wiretapevents.js'/>"></script>

<div class="middle">
    <a id="toggleSearchMode" href="">Toggle Flows vs Modules Search</a>

    <form method="get" id="wiretapSearchForm" action="" class="dataform fancydataform">

        <c:if test="${errors != ''}">
            <c:forEach items="${errors}" var="error">
                <span class="errorMessages"><c:out value="${error}" /></span><br />
            </c:forEach>
        </c:if>

        <!-- The Search criteria for the user to search with -->
        <fieldset>
            <legend><a id="showHideSearchForm" href="">[-]</a> <fmt:message key="wiretap_events_search"/></legend>
            <div id="searchFields">
            <input name="pointToPointFlowProfileSearch" id="pointToPointFlowProfileSearch" type="hidden" value="<c:out value="${pointToPointFlowProfileSearch}"/>" />
            <ol>
                <li id="pointToPointFlowProfileCheckboxes">
                    <label for="pointToPointFlowProfileIds"><fmt:message key="wiretap_events_pointToPointFlowProfile"/></label>
                    
                    <!-- PointToPointProfile Checkboxes -->
                    <div id="eventSearchPointToPointFlowProfileCheckboxes" class="multiSelectCheckboxes">
                        <input type="checkbox" id="pointToPointFlowProfileSelectAll" name="pointToPointFlowProfileSelectAll" <c:if test="${pointToPointFlowProfileSelectAll == 'true'}">checked="checked"</c:if> onclick="checkUncheckAll(this);"/> (de)select all
                        <table class="searchTable">
                            <thead>
                                <tr>
                                    <td><fmt:message key="wiretap_event_pointToPointFlowProfile_name"/></td>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${pointToPointFlowProfiles}" var="pointToPointFlowProfile">
                                    <tr>
                                        <td class="border" valign="top">
                                            <input id="pointToPointFlowProfileIds" name="pointToPointFlowProfileIds" type="checkbox" value="${pointToPointFlowProfile.id}" <c:forEach items="${searchParams['pointToPointFlowProfileIds']}" var="pointToPointFlowProfileId"><c:if test="${pointToPointFlowProfile.id == pointToPointFlowProfileId}">checked="checked"</c:if></c:forEach> /> <c:out value="${pointToPointFlowProfile.name}"/>
                                        </td>
                                    </tr>
                                    <tr class="toggle">
                                        <td class="border" valign="top">
                                            <c:forEach items="${pointToPointFlowProfile.pointToPointFlows}" var="pointToPointFlow">
                                                <%-- If its the first element is null then list the 2nd item --%>
                                                <c:if test="${pointToPointFlow.fromModule == null}">
                                                    <c:if test="${pointToPointFlow.toModule != null}">
                                                        <c:out value="${pointToPointFlow.toModule.name}" />
                                                    </c:if>
                                                </c:if>
                                                <%-- If the first element is not null and the 2nd element is not null then list the 2nd item (prefixed with an arrow) --%>
                                                <c:if test="${pointToPointFlow.fromModule != null}">
                                                    <%-- If its the middle item then list its 2nd element --%>
                                                    <c:if test="${pointToPointFlow.toModule != null}">
                                                        --&gt; <c:out value="${pointToPointFlow.toModule.name}" />
                                                    </c:if>
                                                </c:if>
                                                <%-- We don't care about other cases for display purposes --%>
                                            </c:forEach>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </li>

                <!-- Module Checkboxes -->
                <li id="moduleCheckboxes">
                    <label for="moduleIds"><fmt:message key="wiretap_events_module"/></label>
                    <div id="eventSearchModuleCheckboxes" class="multiSelectCheckboxes">            
                        <input type="checkbox" id="moduleSelectAll" name="moduleSelectAll" <c:if test="${moduleSelectAll == 'true'}">checked="checked"</c:if> onclick="checkUncheckAll(this);"/> (de)select all
                        <table class="searchTable">
                            <thead>
                                <tr>
                                    <td><fmt:message key="wiretap_event_module_name"/></td>
                                    <td><fmt:message key="wiretap_event_module_description"/></td>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${modules}" var="module">
                                    <tr>
                                        <td class="border" valign="top"><input name="moduleIds" type="checkbox" value="${module.id}" <c:forEach items="${searchParams['moduleIds']}" var="moduleId"><c:if test="${module.id == moduleId}">checked="checked"</c:if></c:forEach> /> <c:out value="${module.name}"/></td>
                                        <td class="border" valign="top"><c:out value="${module.description}" escapeXml="false" /></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </li>

                <li>
                    <label for="componentName"><fmt:message key="wiretap_events_component"/></label>
                    <input id="componentName" type="text" name="componentName" value="${searchParams["componentName"]}"/>
                </li>
                <li>
                    <label for="eventId"><fmt:message key="wiretap_events_event_id"/></label>
                    <input id="eventId" type="text" name="eventId" value="${searchParams["eventId"]}"/>
                </li>
                <li>
                    <label for="payloadId"><fmt:message key="wiretap_events_payload_id"/></label>
                    <input id="payloadId" type="text" name="payloadId" value="${searchParams["payloadId"]}"/>
                </li>
                <li>
                    <label for="fromDateString"><fmt:message key="wiretap_events_from"/></label>
                    <input id="fromDateString" type="text" name="fromDateString" size="10" value="${searchParams["fromDateString"]}"/>
                    <input id="fromTimeString" type="text" name="fromTimeString" size="8" value="${searchParams["fromTimeString"]}"/>
                </li>
                <li>
                    <label for="untilDateString"><fmt:message key="wiretap_events_until"/></label>
                    <input id="untilDateString" type="text" name="untilDateString" size="10" value="${searchParams["untilDateString"]}"/>
                    <input id="untilTimeString" type="text" name="untilTimeString" size="8" value="${searchParams["untilTimeString"]}"/>
                </li>
                <li>
                    <label for="payloadContent"><fmt:message key="wiretap_events_payload_content"/></label>
                    <input id="payloadContent" type="text" name="payloadContent" value="${searchParams["payloadContent"]}" />
                </li>
                <li>
                    <label for="orderBy">Order By</label>
                    <select id="orderBy" name="orderBy">
                        <option value="id" <c:if test="${orderBy=='id'}">selected="selected"</c:if>>Id</option>
                        <option value="moduleName" <c:if test="${orderBy=='moduleName'}">selected="selected"</c:if>>Module</option>                 
                        <option value="flowName" <c:if test="${orderBy=='flowName'}">selected="selected"</c:if>>Flow</option>
                        <option value="componentName" <c:if test="${orderBy=='componentName'}">selected="selected"</c:if>>Component</option>
                        <option value="eventId" <c:if test="${orderBy=='eventId'}">selected="selected"</c:if>>Event Id</option>
                        <option value="payloadId" <c:if test="${orderBy=='payloadId'}">selected="selected"</c:if>>Payload Id</option>
                        <option value="created" <c:if test="${orderBy=='created'}">selected="selected"</c:if>>Created Date/Time</option>
                    </select>
                    <label for="orderAsc">Ascending</label>
                    <input id="orderAsc" type="checkbox" name="orderAsc" <c:if test="${orderAsc=='true'}">checked="checked"</c:if>/>
                </li>
            </ol>
            </div>
        </fieldset>
        <p>
            <input type="submit" value="Search for Events" class="controlButton"/>
        </p>

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
        <table id="wiretapSearchResults" class="listTable">
            <thead>
                <tr>
                    <th><a href="<c:out value="${idLink}#results" escapeXml="true" />"><fmt:message key="wiretap_event_results_id"/></a></th>
                    <th><a href="<c:out value="${moduleLink}#results" escapeXml="true" />"><fmt:message key="wiretap_event_results_module"/></a></th>
                    <th><a href="<c:out value="${flowLink}#results" escapeXml="true" />"><fmt:message key="wiretap_event_results_flow"/></a></th>
                    <th><a href="<c:out value="${componentLink}#results" escapeXml="true" />"><fmt:message key="wiretap_event_results_component"/></a></th>
                    <th><a href="<c:out value="${createdDateTimeLink}#results" escapeXml="true" />"><fmt:message key="wiretap_event_results_created_date_time"/></a></th>
                </tr>
            </thead>

            <tbody>
                <c:forEach items="${results}" var="event">
                    <c:url var="viewEventLink" value="viewEvent.htm">
                        <c:param name="eventId" value="${event.id}"/>
                        <c:param name="searchResultsUrl" value="${searchResultsUrl}"/>
                    </c:url>
                    <tr>
                        <td><a href="<c:out value="${viewEventLink}" escapeXml="true" />"><c:out value="${event.id}" /></a></td>           
                        <td><c:out value="${event.moduleName}" /></td>
                        <td><c:out value="${event.flowName}" /></td>
                        <td><c:out value="${event.componentName}" /></td>
                        <td><c:out value="${event.created}" /></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

    </c:if>

</div>

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
