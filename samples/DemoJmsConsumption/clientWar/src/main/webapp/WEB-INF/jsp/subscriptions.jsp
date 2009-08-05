<%@ include file="/WEB-INF/jsp/top.jsp" %>
<h2>Channel Subscriptions</h2>

<form:form>
	<fieldset>
	<c:forEach items="${channelNames}" var="channelName">

	<p>
		
		<c:choose>
			<c:when test="${subscriptions[channelName]==null}">
				<c:out value="${channelName}"/>
				<button name="subscribe" type="submit" value="${channelName}">Subscribe</button>
			</c:when>	
			<c:otherwise>
				<a href="subscription.htm?channel=<c:out value="${channelName}"/>">
					<c:out value="${channelName}"/>
				</a>
				<button name="unsubscribe" type="submit" value="${channelName}">Unsubscribe</button>
			</c:otherwise>		
		</c:choose>
	</p>
	</c:forEach>
	
	
	</fieldset>
</form:form>

<%@ include file="/WEB-INF/jsp/bottom.jsp" %>