<%@ include file="/WEB-INF/jsp/top.jsp" %>

<h2>Text Message Publication</h2>

<form:form>
	<fieldset>
	<p>
		<label for="destination" >Destination</label>
		<select name="destination">
			<c:forEach items="${channelNames}" var="channelName">
				<option value="${channelName}">
					<c:out value="${channelName}"/>
				</option>
			</c:forEach>
		</select>
	</p>
	<p>
		<label for="messageText" >Message Text</label>
		<textarea name="messageText" cols="80" rows="5" ></textarea>
	</p>
	<p>
		<label for="priority" >Priority</label>
		<select name="priority">
			<option value="0">0</option>
			<option value="1">1</option>
			<option value="2">2</option>
			<option value="3">3</option>
			<option value="4">4</option>
			<option value="5">5</option>
			<option value="6">6</option>
			<option value="7">7</option>
			<option value="8">8</option>
			<option value="9">9</option>
		</select>
	</p>
	<p>
		<input type="submit" value="Send"/>
	</p>
	</fieldset>
</form:form>


<%@ include file="/WEB-INF/jsp/bottom.jsp" %>