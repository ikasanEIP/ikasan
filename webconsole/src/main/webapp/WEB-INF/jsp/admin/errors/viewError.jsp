<%@ include file="/WEB-INF/jsp/admin/adminTop.jsp"%>
<%-- 
# //
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
<div class="middle">

<h2>Error Occurrence :: <c:out value="${error.id}" /></h2>

<table id="errorOccurrence" class="keyValueTable">
	<tr>
		<th>
			Module Name
		</th>
		<td>
			<c:out value="${error.moduleName}" />
		</td>
	</tr>
	
	<c:if test="${!empty error.flowName}">
	<tr>
		<th>
			Flow Name
		</th>
		<td>
			<c:out value="${error.flowName}" />
		</td>
	</tr>
	</c:if>
	
	<c:if test="${!empty error.flowElementName}">
	<tr>
		<th>
			Flow Element Name
		</th>
		<td>
			<c:out value="${error.flowElementName}" />
		</td>
	</tr>
	</c:if>
	
	<c:if test="${!empty error.initiatorName}">
	<tr>
		<th>
			Initiator Name
		</th>
		<td>
			<c:out value="${error.initiatorName}" />
		</td>
	</tr>
	</c:if>
	
	<tr>
		<th>
			Error Detail
		</th>
		<td>
			<c:out value="${error.errorDetail}" />
		</td>
	</tr>
	
	<c:if test="${!empty error.currentEvent}">
	<tr>
		<th>
			Current Event
		</th>
		<td>
			<c:out value="${error.currentEvent}" />
		</td>
	</tr>
	</c:if>
	
	<c:if test="${!empty error.eventId}">
	<tr>
		<th>
			Original Event
		</th>
		<td>
			<c:out value="${error.eventId}" />
		</td>
	</tr>
	</c:if>
	
	<tr>
		<th>
			Time Logged
		</th>
		<td>
			<fmt:formatDate value="${error.logTime}"
                                pattern="dd/MM/yyyy h:mma"/>
		</td>
	</tr>
	<tr>
		<th>
			Expiry
		</th>
		<td>
			<fmt:formatDate value="${error.expiry}"
                                pattern="dd/MM/yyyy h:mma"/>
		</td>
	</tr>
</table>


</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
