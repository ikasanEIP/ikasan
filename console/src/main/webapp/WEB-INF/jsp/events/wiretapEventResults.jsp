<%-- 
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
<%@ include file="/WEB-INF/jsp/events/eventsTop.jsp"%>

<div class="middle">

<h3><fmt:message key="wiretap_event_results_heading2"/></h3>

<a href="search.htm"><fmt:message key="wiretap_event_results_new_search"/></a>

<div id="searchResultsHeader">
    
    <span id="currentlyShowing"><fmt:message key="wiretap_event_results_showing"/> <c:out value="${searchResults.firstIndex}"/> <fmt:message key="wiretap_event_results_to"/> <c:out value="${searchResults.lastIndex}"/> <fmt:message key="wiretap_event_results_of"/> <c:out value="${searchResults.resultSize}"/> <fmt:message key="wiretap_event_results_results"/></span>
    <span id="navigationControls" >
        <c:if test="${searchResults.firstIndex gt 1}">
            <a href="previous.htm"><fmt:message key="wiretap_event_results_previous"/></a>&nbsp;
        </c:if>
        <c:if test="${searchResults.resultSize gt searchResults.lastIndex}">
            <a href="next.htm"><fmt:message key="wiretap_event_results_next"/></a>
        </c:if>
    </span>
    
</div> <!--end searchResultsHeader -->

<table id="wiretapSearchResults" class="listTable">
    <thead>
        <tr>
            <th><fmt:message key="wiretap_event_results_id"/></th>
            <th><fmt:message key="wiretap_event_results_module"/></th>
            <th><fmt:message key="wiretap_event_results_flow"/></th>
            <th><fmt:message key="wiretap_event_results_component"/></th>
            <th><fmt:message key="wiretap_event_results_event_id"/> / <fmt:message key="wiretap_event_results_payload_id"/></th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${searchResults.wiretapEventHeaders}" var="event">
            <tr>
                <td>
                    <a href="viewEvent.htm?eventId=<c:out value="${event.id}" />">
                        <c:out value="${event.id}" />
                    </a>
                </td>           
                <td>
                    <c:out value="${event.moduleName}" />
                </td>
                <td>
                    <c:out value="${event.flowName}" />
                </td>
                <td>
                    <c:out value="${event.componentName}" />
                </td>
                <td>
                    <c:out value="${event.eventId}" /><br><c:out value="${event.payloadId}" />
                </td>
            </tr>
        </c:forEach>

    </tbody>

</table>

</div>

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
