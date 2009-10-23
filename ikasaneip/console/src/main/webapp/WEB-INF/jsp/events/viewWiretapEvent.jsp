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

<h2><fmt:message key="wiretap_event_event_id"/> :: <c:out value="${wiretapEvent.eventId}" /></h2>

<table id="wiretapContent" class="keyValueTable">
	<tr>
		<th><fmt:message key="wiretap_event_event_id"/></th>
		<td colspan="2"><c:out value="${wiretapEvent.eventId}" /></td>
	</tr>	
	
	<tr>
		<th><fmt:message key="wiretap_event_payload_id"/></th>
		<td colspan="2">
		  <c:out value="${wiretapEvent.payloadId}" />
		  &nbsp;
          <c:choose>
              <c:when test="${wiretapEvent.previousByPayload != null}">
                  <a href="viewEvent.htm?eventId=<c:out value="${wiretapEvent.previousByPayload}" />"><fmt:message key="wiretap_event_previous"/></a>
              </c:when>
              <c:otherwise>
                  <span style="color : #A9A9A9;">(<fmt:message key="wiretap_event_previous"/>)</span>             
              </c:otherwise>
          </c:choose>
          &nbsp;
          <c:choose>
              <c:when test="${wiretapEvent.nextByPayload != null}">
                  <a href="viewEvent.htm?eventId=<c:out value="${wiretapEvent.nextByPayload}" />"><fmt:message key="wiretap_event_next"/></a>
              </c:when>
              <c:otherwise>
                  <span style="color : #A9A9A9;">(<fmt:message key="wiretap_event_next"/>)</span>             
              </c:otherwise>
          </c:choose>                 
		</td>
	</tr>

    <tr>
        <th><fmt:message key="wiretap_event_payload_content_xml"/></th>
        <c:choose>
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
        <th><fmt:message key="wiretap_event_download_payload"/></th>
        <td colspan="2">
            <!-- This link will download the payload content -->
            <a href="downloadPayloadContent.htm?eventId=<c:out value="${wiretapEvent.id}" />">
                <fmt:message key="wiretap_event_download_payload"/>
            </a>
        </td>
    </tr>

	<tr>
		<th class="top"><fmt:message key="wiretap_event_payload_content"/></th>
		<td class="payloadContent" colspan="2"><c:out value="${payloadContent}" escapeXml="false" /></td>
	</tr>	

</table>

</div>

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
