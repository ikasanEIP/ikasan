<%@ include file="/WEB-INF/jsp/events/eventsTop.jsp"%>
<!-- 
# //
# //
# // $Id: viewWiretapEvent.jsp 16798 2009-04-24 14:12:09Z mitcje $
# // $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/main/webapp/WEB-INF/jsp/events/viewWiretapEvent.jsp $
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
-->

<div class="middle">

<h2>Wiretap Event :: <c:out value="${wiretapEvent.id}" /></h2>

<table id="wiretapDetails" class="keyValueTable">
	<tr>
		<th>
			Module Name
		</th>
		<td>
			<c:out value="${wiretapEvent.moduleName}" />
		</td>
	</tr>
	
	<tr>
		<th>
			Flow Name
		</th>
		<td>
			<c:out value="${wiretapEvent.flowName}" />
		</td>
	</tr>
	
	<tr>
		<th>
			Component Name
		</th>
		<td>
			<c:out value="${wiretapEvent.componentName}" />
		</td>
	</tr>
	<tr>
		<th>
			Created Date
		</th>
		<td>
			<fmt:formatDate value="${wiretapEvent.created}"
                                pattern="dd/MM/yyyy h:mma"/>
		</td>
	</tr>
	<tr>
		<th>
			Expiry
		</th>
		<td>
			<fmt:formatDate value="${wiretapEvent.expiry}"
                                pattern="dd/MM/yyyy h:mma"/>
		</td>
	</tr>
</table>

<h2>Event :: <c:out value="${wiretapEvent.eventId}" /></h2>
<table id="wiretapContent" class="keyValueTable">
	<tr>
		<th>
			Event Id
		</th>
		<td colspan="2">
			<c:out value="${wiretapEvent.eventId}" />
		</td>
	</tr>	
	
	<tr>
		<th>
			Payload Id
		</th>
		<td>
			<c:out value="${wiretapEvent.payloadId}" />
		</td>
		<td>
	 		<c:choose>
						<c:when test="${wiretapEvent.previousByPayload != null}">
							<a href="viewEvent.htm?eventId=<c:out value="${wiretapEvent.previousByPayload}" />">Previous</a>
						</c:when>
						<c:otherwise>
							Previous
						</c:otherwise>
			</c:choose>
			&nbsp;
			<c:choose>
						<c:when test="${wiretapEvent.nextByPayload != null}">
							<a href="viewEvent.htm?eventId=<c:out value="${wiretapEvent.nextByPayload}" />">Next</a>
						</c:when>
						<c:otherwise>
							Next
						</c:otherwise>
			</c:choose>					
		</td>
	</tr>

    <tr>
        <th>
            Payload Content (XML)
        </th>
        <c:choose>
        <c:when test='${fn:startsWith(wiretapEvent.payloadContent, "<?xml")}'>
        <td colspan="2">
            <a href="viewPrettyPayloadContent.htm?eventId=<c:out value="${wiretapEvent.id}" />">
                Formatted Content (XML)
            </a>
        </td>
        </c:when>
        <c:otherwise>
        <td colspan="2">
            Payload Content is not XML, no 'pretty' view available
        </td>
        </c:otherwise>
        </c:choose>
    </tr>

	<tr>
		<th>
			Payload Content
		</th>
		<td colspan="2">
			<c:out value="${wiretapEvent.payloadContent}" />
		</td>
	</tr>	
    
</table>

</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
