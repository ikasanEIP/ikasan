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

<h2><fmt:message key="wiretap_event_heading"/> :: <c:out value="${wiretapEvent.id}" /></h2>

<table id="wiretapDetails" class="keyValueTable">
	<tr>
		<th><fmt:message key="wiretap_event_module_name"/></th>
		<td><c:out value="${wiretapEvent.moduleName}" /></td>
	</tr>
	
	<tr>
		<th><fmt:message key="wiretap_event_flow_name"/></th>
		<td><c:out value="${wiretapEvent.flowName}" /></td>
	</tr>
	
	<tr>
		<th><fmt:message key="wiretap_event_component_name"/></th>
		<td><c:out value="${wiretapEvent.componentName}" /></td>
	</tr>

	<tr>
		<th><fmt:message key="wiretap_event_created_date"/></th>
		<td><fmt:formatDate value="${wiretapEvent.created}" pattern="dd/MM/yyyy h:mma"/></td>
	</tr>

	<tr>
		<th><fmt:message key="wiretap_event_expiry"/></th>
		<td><fmt:formatDate value="${wiretapEvent.expiry}" pattern="dd/MM/yyyy h:mma"/></td>
	</tr>
</table>

<h2>Event :: <c:out value="${wiretapEvent.eventId}" /></h2>
<table id="wiretapContent" class="keyValueTable">
	<tr>
		<th><fmt:message key="wiretap_event_event_id"/></th>
		<td colspan="2"><c:out value="${wiretapEvent.eventId}" /></td>
	</tr>	
	
	<tr>
		<th><fmt:message key="wiretap_event_payload_id"/></th>
		<td><c:out value="${wiretapEvent.payloadId}" /></td>
		<td>
	 		<c:choose>
				<c:when test="${wiretapEvent.previousByPayload != null}">
			<a href="viewEvent.htm?eventId=<c:out value="${wiretapEvent.previousByPayload}" />"><fmt:message key="wiretap_event_previous"/></a>
				</c:when>
				<c:otherwise>
			<fmt:message key="wiretap_event_previous"/>
				</c:otherwise>
			</c:choose>
			&nbsp;
			<c:choose>
				<c:when test="${wiretapEvent.nextByPayload != null}">
			<a href="viewEvent.htm?eventId=<c:out value="${wiretapEvent.nextByPayload}" />"><fmt:message key="wiretap_event_next"/></a>
				</c:when>
				<c:otherwise>
			<fmt:message key="wiretap_event_next"/>
				</c:otherwise>
			</c:choose>					
		</td>
	</tr>

    <tr>
        <th><fmt:message key="wiretap_event_payload_content_xml"/></th>
        <c:choose>
            <%-- TODO have a proper XML detection method --%>
            <c:when test='${fn:startsWith(wiretapEvent.payloadContent, "<?xml")}'>
                <td colspan="2">
                    <!-- This link will open in a new window, see /js/ikasan.js for details -->
                    <a href="viewPrettyPayloadContent.htm?eventId=<c:out value="${wiretapEvent.id}" />" class="new-window">
                        <fmt:message key="wiretap_event_formatted_content_xml"/>
                    </a>
                </td>
            </c:when>
            <c:otherwise>
                <td colspan="2">
                    <fmt:message key="wiretap_event_payload_content_not_xml"/>
                </td>
            </c:otherwise>
        </c:choose>
    </tr>

	<tr>
		<th class="top"><fmt:message key="wiretap_event_payload_content"/></th>
		<td colspan="2"><c:out value="${payloadContent}" escapeXml="false" /></td>
	</tr>	

</table>

</div>

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
