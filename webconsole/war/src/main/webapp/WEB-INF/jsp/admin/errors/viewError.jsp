<%@ include file="/WEB-INF/jsp/admin/errors/errorsTop.jsp"%>
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
                                pattern="dd/MM/yyyy h:mm:ss a"/>
		</td>
	</tr>
	<tr>
		<th>
			Action Taken
		</th>
		<td>
			<c:out value="${error.actionTaken}"/>
		</td>
	</tr>
	<tr>
		<th>
			Expiry
		</th>
		<td>
			<fmt:formatDate value="${error.expiry}"
                                pattern="dd/MM/yyyy h:mm:ss a"/>
		</td>
	</tr>
	<tr>
		<th>
			URL
		</th>
		<td>
			<a href="${error.url}"><c:out value="${error.url}" /></a>
		</td>
	</tr>
	<c:if test="${!empty error.excludedEvent}">
	<c:url var="viewExclusionLink" value="../exclusions/exclusion.htm">   	
    		<c:param name="eventId" value="${error.excludedEvent.event.id}"/>
    </c:url>
	<tr>
		<th>
			ExcludedEvent
		</th>
		<td>
			<a href="${viewExclusionLink}"><c:out value="${error.excludedEvent.event.id}"/></a>
		</td>
	</tr>
	</c:if>
</table>


		<h3>Error Detail</h3>
		<p id="errorDetail" class="unformattable data"><c:out value="${error.errorDetail}" /></p>

<c:if test="${!empty error.errorEvent}">
		<h3>Event</h3>
<div id="eventDump">
		<table id="currentEventDetails" class="keyValueTable">
		<tr>
			<th>
				Event Id
			</th>
			<td>
				<c:out value="${error.errorEvent.id}" />
			</td>
		</tr>
		<tr>
			<th>
				Priority
			</th>
			<td>
				<c:out value="${error.errorEvent.priority}" />
			</td>
		</tr>
		<tr>
			<th>
				Timestamp
			</th>
			<td>
				<c:out value="${error.errorEvent.timestamp}" />
			</td>
		</tr>
		</table>
		<c:forEach items="${error.errorEvent.payloads}" var="payload" varStatus="status">
		<jsp:useBean id="payload" type="org.ikasan.common.Payload" />
		<h4 id="enumerator">Payload(<c:out value="${status.count}" />)</h4>  
			<table id="currentEventPayloadDetails" class="keyValueTable">
			<tr>
				<th>
					Payload Id
				</th>
				<td>
					<c:out value="${payload.id}" />
				</td>
			</tr>
			<c:forEach items="${payload.attributeMap}" var="attribute" >
			<tr>
				<th>
					Attribute [<c:out value="${attribute.key}" />]
				</th>
				<td>
					<c:out value="${attribute.value}" />
				</td>
			</tr>
			</c:forEach>
			<tr>
				<th>
					Content
				</th>
				<td class="unformattable data">
					<% pageContext.setAttribute("displayableContent", new String(payload.getContent())); %>
					<c:out value="${displayableContent}" />
				</td>
			</tr>
		</table>
		</c:forEach>
	</div><!-- end eventDump -->
</c:if>
 
<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
