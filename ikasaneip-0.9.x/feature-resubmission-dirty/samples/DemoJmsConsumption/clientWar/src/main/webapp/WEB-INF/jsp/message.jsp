<%@ include file="/WEB-INF/jsp/top.jsp" %>


<h2>Message :: <c:out value="${message.messageID}"/> on : <c:out value="${channelName}"/></h2>

<table>
	<tr>
		<td>CorrelationID</td><td><c:out value="${message.correlationID}"/></td>
	</tr>
	<tr>	
		<td>DeliveryMode</td><td><c:out value="${message.deliveryMode}"/></td>
	</tr>
	<tr>
		<td>Destination</td><td><c:out value="${message.destination}"/></td>
	</tr>
	<tr>
		<td>Expiration</td><td><c:out value="${message.expiration}"/></td>
	</tr>
	<tr>
		<td>MappedValues</td><td><c:out value="${message.mappedValues}"/></td>
	</tr>
	<tr>
		<td>MessageID</td><td><c:out value="${message.messageID}"/></td>
	</tr>
	<tr>
		<td>MessageProperties</td><td><c:out value="${message.messageProperties}"/></td>
	</tr>
	<tr>
		<td>Priority</td><td><c:out value="${message.priority}"/></td>
	</tr>
	<tr>
		<td>Redelivered</td><td><c:out value="${message.redelivered}"/></td>
	</tr>
	<tr>
		<td>ReplyTo</td><td><c:out value="${message.replyTo}"/></td>
	</tr>
	<tr>
		<td>TextContent</td><td><c:out value="${message.textContent}"/></td>
	</tr>
	<tr>
		<td>Timestamp</td><td><c:out value="${message.timestamp}"/></td>
	</tr>
	<tr>
		<td>Type</td><td><c:out value="${message.type}"/></td>
	</tr>
</table>


	
<%@ include file="/WEB-INF/jsp/bottom.jsp" %>


