<%-- 

 $Id:
 $URL: 

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
<%@ include file="/WEB-INF/jsp/admin/adminTop.jsp"%>


<div class="middle">

<h2>Platform Scheduler</h2>

	<h3>Details</h3>
		<table id="schedulerDetails" class="keyValueTable">
			<tr>
				<th>
					Shutdown
				</th>
				<td>
					<c:out value="${platformScheduler.shutdown}" />
				</td>
			</tr>
			
			<tr>
				<th>
					Paused
				</th>
				<td>
					<c:out value="${platformScheduler.paused}" />
				</td>
			</tr>
		</table>

		<h3>Triggers</h3>
		<table id="schedulerTriggersList" class="listTable">
			<thead>
				<tr>
					<th>Name</th>
					<th>Job Name</th>
					<th>Next Fire Time</th>
					<th>Previous Fire Time</th>
				</tr>
			</thead>
		
			<tbody>
				<c:forEach items="${triggers}" var="trigger">
					<tr>
						<td>
							<c:out value="${trigger.name}" />
						</td>			
						<td>
							<c:out value="${trigger.jobName}" />
						</td>
						<td>
							<fmt:formatDate value="${trigger.nextFireTime}"
		                                pattern="dd/MM/yyyy h:mma"/>
						</td>
						<td>
							<fmt:formatDate value="${trigger.previousFireTime}"
		                                pattern="dd/MM/yyyy h:mma"/>
						</td>
					</tr>
				</c:forEach>
		
			</tbody>
		
		
		</table>	

</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
