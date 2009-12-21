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
<%@ include file="/WEB-INF/jsp/top.jsp" %>


<h2><c:out value="${message.messageId}"/></h2>

<table class="keyValue">
	<tr>
		<th>JMS Correlation Id</th>
		<td>${message.messagingProperties.jmsCorrelationId}</td>
		<th>JMS Delivery Mode</th>
		<td>${message.messagingProperties.jmsDeliveryMode}</td>

		<th>JMS Expiration</th>
		<td>${message.messagingProperties.jmsExpiration}</td>
		<th>JMS Priority</th>
		<td>${message.messagingProperties.jmsPriority}</td>
	</tr>
	<tr>
		<th>JMS Redelivered</th>
		<td>${message.messagingProperties.jmsRedelivered}</td>
		<th>JMS Reply To</th>
		<td>${message.messagingProperties.jmsReplyTo}</td>
		<th>JMS Timestamp</th>
		<td>${message.messagingProperties.jmsTimestamp}</td>
		<th>JMS Type</th>
		<td>${message.messagingProperties.jmsType}</td>
	</tr>
</table>

<table class="keyValue">
	<c:forEach items="${messageProperties}" var="property" >

	<tr>
		<th><c:out value="${property.key}"/></th>
		<td><c:out value="${property.value}"/></td>
		
	</tr>
	</c:forEach>
	
</table>

<p>
	<c:out value="${message.text}"/>	
</p>

	
<%@ include file="/WEB-INF/jsp/bottom.jsp" %>

