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
            <!-- This link will open in a new window, see /js/ikasan.js for details -->
            <a href="viewPrettyPayloadContent.htm?eventId=<c:out value="${wiretapEvent.id}" />" class="new-window">
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
