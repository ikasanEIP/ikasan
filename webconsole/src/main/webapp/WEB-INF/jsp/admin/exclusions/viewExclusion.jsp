<%@ include file="/WEB-INF/jsp/admin/exclusions/exclusionsTop.jsp"%>
<%-- 

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




<h2>Excluded Event :: <c:out value="${excludedEvent.id}" /></h2>

<table id="excludedEvent" class="keyValueTable">
	<tr>
		<th>
			Module Name
		</th>
		<td>
			<c:out value="${excludedEvent.moduleName}" />
		</td>
	</tr>
	

	<tr>
		<th>
			Flow Name
		</th>
		<td>
			<c:out value="${excludedEvent.flowName}" />
		</td>
	</tr>

	
	<tr>
		<th>
			Exclusion Time
		</th>
		<td>
			<fmt:formatDate value="${excludedEvent.exclusionTime}"
                                pattern="dd/MM/yyyy h:mma"/>
		</td>
	</tr>
</table>
	
	<table id="excludedEventEvent" class="keyValueTable">
	
	<tr>
		<th>
			Event Id
		</th>
		<td>
			<c:out value="${excludedEvent.event.id}" />
		</td>
	</tr>
	<tr>
		<th>
			Priority
		</th>
		<td>
			<c:out value="${excludedEvent.event.priority}" />
		</td>
	</tr>
	<tr>
		<th>
			Timestamp
		</th>
		<td>
			<c:out value="${excludedEvent.event.timestamp}" />
		</td>
	</tr>
	
	</table>
	
	<c:forEach items="${excludedEvent.event.payloads}" var="payload" varStatus="status">
	<h2>Payload(<c:out value="${status.count}" />)</h2>
	
		<table id="excludedEventEvent" class="keyValueTable">
	
	<tr>
		<th>
			Payload Id
		</th>
		<td>
			<c:out value="${payload.id}" />
		</td>
	</tr>
	<tr>
		<th>
			Priority
		</th>
		<td>
			<c:out value="${payload.priority}" />
		</td>
	</tr>
	<tr>
		<th>
			Timestamp
		</th>
		<td>
			<c:out value="${payload.timestamp}" />
		</td>
	</tr>
	<tr>
		<th>
			Name
		</th>
		<td>
			<c:out value="${payload.name}" />
		</td>
	</tr>
	<tr>
		<th>
			Spec
		</th>
		<td>
			<c:out value="${payload.spec}" />
		</td>
	</tr>
	<tr>
		<th>
			SrcSystem
		</th>
		<td>
			<c:out value="${payload.srcSystem}" />
		</td>
	</tr>

	<tr>
		<th>
			Content
		</th>
		<td>
			<c:out value="${payload.displayableContent}" />
		</td>
	</tr>	
	</table>
	</c:forEach>

	<tr>
		<td colspan="2">
		  <c:url var="resubmitLink" value="exclusion.htm">
                <c:param name="excludedEventId" value="${excludedEvent.id}"/>
                <c:param name="action" value="resubmit"/>
              </c:url>
              <form:form action="${resubmitLink}" method="post">
                <input type="submit" value="Resubmit" class="controlButton"/>
              </form:form>
		</td>
	</tr>

</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
