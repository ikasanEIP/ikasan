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


<h2><c:out value="${destination.destinationPath}"/></h2>



<div id="subscribers" class="destinationAspect">
	<h3>Subscriptions</h3>
	
	
	<c:forEach items="${destination.subscriptions}" var="subscriptionEntry" >
	<div class="destinationAspect subscription">
		<c:url var="stopSubscriptionLink" value="stopSubscription.htm">
		    <c:param name="destinationPath" value="${destination.destinationPath}"/>
		    <c:param name="subscriptionName" value="${subscriptionEntry.key}"/>
	    </c:url>
	    <h3>${subscriptionEntry.key}</h3>
	    
	    <p class="message">Handled ${subscriptionEntry.value.messageCount} messages since: ${subscriptionEntry.value.subscribingSince}</p>
	    	<table>
				<tr>
					<th>&nbsp;</th>
					<th>Type</th>
					<th>Timestamp</th>
					<th>Export</th>
				</tr>
			<c:forEach items="${subscriptionEntry.value.messages}" var="messageId" >	
				<tr>
					<c:url var="messageLink" value="message.htm">
						<c:param name="destinationPath" value="${destination.destinationPath}"/>
						<c:param name="subscriptionName" value="${subscriptionEntry.key}"/>
		            	<c:param name="messageId" value="${messageId}"/>
		            </c:url>
					<td><a href="${messageLink}"><c:out value="${messageId}"/></a></td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<c:url var="downloadLink" value="export.htm">
						<c:param name="destinationPath" value="${destination.destinationPath}"/>
						<c:param name="subscriptionName" value="${subscriptionEntry.key}"/>
		            	<c:param name="messageId" value="${messageId}"/>
		            </c:url>
					<td><a href="${downloadLink}">Download</a></td>

				</tr>
			</c:forEach>
		</table>
    <form method="post" action="${stopSubscriptionLink}">
		<input type="submit" value="Stop Subscription"/>
	</form>
	    
	</div> <!-- end subscription -->
	</c:forEach>
	
	
	
	
	<c:url var="startSubscriptionLink" value="startSubscription.htm">
		<c:param name="destinationPath" value="${destination.destinationPath}"/>
	</c:url>	


	
	<table>
	<form method="post" action="${startSubscriptionLink}">
	    <tr><td>Subscription Name:</td><td><input name="subscriptionName" type="text"/></td></tr>
		<tr><td>Repository:</td><td> <select name="repositoryName">
			<c:forEach items="${repositoryNames}" var="repositoryName" >
				<option value="${repositoryName}">${repositoryName}</option>
			</c:forEach>
		</select></td></tr>
		<tr><td>Simple:</td><td> <input type="checkbox" name="simpleSubscription">
		<tr><td colspan="2"><input type="submit" value="Start Subscription"/></td></tr>
	</form>
	</table>
	
	
</div>














<c:url var="textMessagePublicationLink" value="publishTextMessage.htm">
	<c:param name="destinationPath" value="${destination.destinationPath}"/>
</c:url>

<div id="textMessagePublisher" class="destinationAspect">
	<h3>Text Message Publisher</h3>
	<form method="post" action="${textMessagePublicationLink}">
		<table>
			
			<tr>
				<td>Message Text:</td>
				<td><textarea rows="20" cols="80" name="messageText"></textarea></td>
			</tr>
			<tr>
				<td>Message Priority:</td>
				<td>
					<select name="priority">
	                	<option value="4">4 - default normal</option>
						<option value="0">0 - lowest</option>
	                	<option value="9">9 - highest</option>
						<option value="1">1</option>
						<option value="2">2</option>
	                	<option value="3">3</option>
	                	<option value="5">5</option>
	                	<option value="6">6</option>
	                	<option value="7">7</option>
	                	<option value="8">8</option>
			    	</select>
				</td>
			</tr>		
			<tr>
				<td colspan="2"><input type="submit" value="Publish"/></td>
			</tr>
		</table>
	</form>
</div>	


<c:url var="mapMessagePublicationLink" value="publishMapMessage.htm">
	<c:param name="destinationPath" value="${destination.destinationPath}"/>
</c:url>
<div id="mapMessagePublisher" class="destinationAspect">
	<h3>Map Message Publisher</h3>
	<form method="post" action="${mapMessagePublicationLink}" enctype="multipart/form-data">
		<table>
			
			<tr>
				<td>Message File:</td>
				<td><input type="file"  name="file" /></td>
			</tr>
			<tr>
				<td>Message Priority:</td>
				<td>
					<select name="priority">
	                	<option value="4">4 - default normal</option>
						<option value="0">0 - lowest</option>
	                	<option value="9">9 - highest</option>
						<option value="1">1</option>
						<option value="2">2</option>
	                	<option value="3">3</option>
	                	<option value="5">5</option>
	                	<option value="6">6</option>
	                	<option value="7">7</option>
	                	<option value="8">8</option>
			    	</select>
				</td>
			</tr>		
			<tr>
				<td colspan="2"><input type="submit" value="Publish"/></td>
			</tr>
		</table>
	</form>
</div>	
<%@ include file="/WEB-INF/jsp/bottom.jsp" %>

