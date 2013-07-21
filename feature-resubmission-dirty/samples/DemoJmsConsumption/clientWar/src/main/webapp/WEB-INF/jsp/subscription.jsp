<%@ include file="/WEB-INF/jsp/top.jsp" %>


<h2>Subscription on :: <c:out value="${channelName}"/></h2>


	<c:forEach items="${subscription.messagesReceived}" var="message">
    	<c:url var="messageLink" value="message.htm">
            <c:param name="channelName" value="${channelName}"/>
            <c:param name="messageID" value="${message.messageID}"/>
        </c:url>

	<ul>
		<li>
		<a href="${messageLink}"/><c:out value="${message.messageID}"/></a>
		</li>
	</ul>
	</c:forEach>
	
<%@ include file="/WEB-INF/jsp/bottom.jsp" %>


