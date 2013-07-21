<%@ include file="/WEB-INF/jsp/top.jsp" %>

<h2>Text Message Publication :: Successful</h2>

	<p>
		<label for="destination" >Destination</label>
		<span id="destination"><c:out value="${destination}"/></span>
	</p>
	<p>
		<label for="messageText" >Message Text</label>
		<span id="messageText"><c:out value="${messageText}"/></span>
	</p>
	<p>
		<label for="priority" >Priority</label>
		<span id="priority"><c:out value="${priority}"/></span>
	</p>

<%@ include file="/WEB-INF/jsp/bottom.jsp" %>