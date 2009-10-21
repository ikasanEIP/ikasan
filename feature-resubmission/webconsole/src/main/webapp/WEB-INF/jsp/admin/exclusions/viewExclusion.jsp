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
<%@ include file="/WEB-INF/jsp/admin/exclusions/exclusionsTop.jsp"%>

<div class="middle">

<c:if test="${resubmissionError!=null}">
	<p class="errorMessages">
		Sorry, resubmission was not successful. See latest Error Occurrence for details
	</p>
</c:if>



<h2>Excluded Event :: <c:out value="${excludedEvent.id}" /></h2>

<table id="excludedEventHeaders" class="keyValueTable">
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
	<tr>
		<th>
			Error Occurrences
		</th>
		<td>
		<ol id="excludedEventErrorOccurrences" >
	<c:forEach items="${excludedEvent.errorOccurrences}" var="errorOccurrence" >
	    <c:url var="errorLink" value="../errors/viewError.htm">
            <c:param name="errorId" value="${errorOccurrence.id}"/>
        </c:url>
		<li>
			<a href="<c:out value="${errorLink}"/>"><c:out value="${errorOccurrence.logTime}"/></a> : 
			<c:out value="${errorOccurrence.moduleName}"/>-<c:out value="${errorOccurrence.flowElementName}"/>
		</li>
	</c:forEach>
	</ol>
		</td>
	</tr>
	
	<c:if test="${excludedEvent.resubmitted}">
	<tr>
		<th>
			Resubmitted
		</th>
		<td>
			 at <fmt:formatDate value="${excludedEvent.resubmissionTime}" pattern="dd/MM/yyyy h:mma"/> by <c:out value="${excludedEvent.resubmitter}" />
		</td>
	</tr>
	</c:if>
	
	
</table>

<%-- Only show the resubmit button if ExcludedEvent has not already been resubmitted --%>
	<c:if test="${!excludedEvent.resubmitted}">
			<c:url var="resubmitLink" value="exclusion.htm">
                <c:param name="eventId" value="${excludedEvent.event.id}"/>
                <c:param name="action" value="resubmit"/>
              </c:url>
	
    	<form:form action="${resubmitLink}" method="post" cssClass="controls">
        	<input type="submit" value="Resubmit" class="controlButton"/>
        </form:form>
    </c:if>


<h2>Event</h2>
<div class="event">

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
	<div id="payload">
		<jsp:useBean id="payload" type="org.ikasan.common.Payload" />  
		<h3 id="enumerator">Payload(<c:out value="${status.count}" />)</h3>
		
		<table id="excludedEventPayload" class="keyValueTable">
		
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
		</table>
	
		<h4>Content</h4>
		<p id="payloadContent" class="unformattable">
		<% pageContext.setAttribute("displayableContent", new String(payload.getContent())); %>
				<c:out value="${displayableContent}" />
		</p>
			
	</div>
	</c:forEach>

	
</div> <!-- end div event -->
	
	
	


</div> 



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
