<%@ include file="/WEB-INF/jsp/top.jsp" %>
<h2>Channel Subscriptions</h2>

<form:form>
	<fieldset>
	<table>
	<c:forEach items="${channelNames}" var="channelName">
		<tr>

		
		<c:choose>
			<c:when test="${subscriptions[channelName]==null}">
				<td><button name="subscribe" type="submit" value="${channelName}">Subscribe</button></td>
				<td><c:out value="${channelName}"/></td>
				<td>&nbsp;</td>
			</c:when>	
			<c:otherwise>
				<td>&nbsp;</td>
				<td><a href="subscription.htm?channel=<c:out value="${channelName}"/>">
					<c:out value="${channelName}"/>
				</a>
				</td>
				<td><button name="unsubscribe" type="submit" value="${channelName}">Unsubscribe</button></td>
			</c:otherwise>		
		</c:choose>

		</tr>
	</c:forEach>
	</table>

	
	</fieldset>
</form:form>

<%@ include file="/WEB-INF/jsp/bottom.jsp" %>