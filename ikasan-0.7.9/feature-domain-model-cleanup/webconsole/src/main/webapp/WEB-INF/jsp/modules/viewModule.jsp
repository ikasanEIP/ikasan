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
<%@ include file="/WEB-INF/jsp/modules/modulesTop.jsp"%>

<div class="middle">

<h2>Module :: <c:out value="${module.name}" /></h2>

<span id="moduleDescription"><c:out value="${module.description}" /></span>

    <h3>Details</h3>
        <table id="schedulerDetails" class="keyValueTable">
            <tr>
                <th>
                    Module Name
                </th>
                <td>
                    <c:out value="${module.name}" />
                </td>
            </tr>
            
            <tr>
                <th>
                    Module Id
                </th>
                <td>
                    <c:out value="${module.id}" />
                </td>
            </tr>
        </table>

<h3>Initiators</h3>
<table id="initiatorsList" class="listTable" >
	<thead>
		<tr>
			<th>Name</th>
			<th>Type</th>
			<th>Flow</th>
			<th>Status</th>
			<th>&nbsp;</th>
		</tr>
	</thead>

	<tbody>
		<c:forEach items="${module.initiators}" var="initiator">
			<tr>
				<td>

                    <c:url var="viewInitiatorLink" value="viewInitiator.htm">
                        <c:param name="moduleName" value="${moduleName}"/>
                        <c:param name="initiatorName" value="${initiator.name}"/>
                    </c:url>
                    <a href="<c:out value="${viewInitiatorLink}" escapeXml="true" />">
                        <c:out value="${initiator.name}" />
                    </a>

				
				
				</td>
				<td><c:out value="${initiator.type}" /></td>
				<td>
				     <c:url var="viewFlowLink" value="viewFlow.htm">
                        <c:param name="moduleName" value="${moduleName}"/>
                        <c:param name="flowName" value="${initiator.flow.name}"/>
                     </c:url>
                    <a href="<c:out value="${viewFlowLink}" escapeXml="true" />">
						<c:out value="${initiator.flow.name}" />
					</a>
				</td>
				<td class="initiatorState-<c:out value="${initiator.state.name}" />">
					<c:choose>
						<c:when test="${initiator.running}">
							<c:choose>
								<c:when test="${initiator.recovering}">
									Recovering
								</c:when>
								<c:otherwise>
									Running
								</c:otherwise>
							</c:choose>
						</c:when>
	
						<c:otherwise>
							<c:choose>
								<c:when test="${initiator.error}">
									Stopped In Error
								</c:when>
								<c:otherwise>
									Stopped
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</td>
				<td>	
					<security:authorize ifAllGranted="ADMIN_${moduleName}">
    	 				<c:choose>
    						<c:when test="${initiator.running}">
    						  <c:url var="initiatorLink" value="initiator.htm">
                                <c:param name="moduleName" value="${moduleName}"/>
                                <c:param name="initiatorName" value="${initiator.name}"/>
                                <c:param name="initiatorAction" value="stop"/>
                              </c:url>
                              <form:form action="${initiatorLink}" method="post">
                                <input type="submit" value="Stop" class="controlButton"/>
                              </form:form>
    						</c:when>
    						<c:otherwise>
                              <c:url var="initiatorLink" value="initiator.htm">
                                <c:param name="moduleName" value="${moduleName}"/>
                                <c:param name="initiatorName" value="${initiator.name}"/>
                                <c:param name="initiatorAction" value="start"/>
                              </c:url>
                                <form:form action="${initiatorLink}" method="post">
                                    <input type="submit" value="Start" class="controlButton" class="controlButton"/>
                                </form:form>

    						</c:otherwise>
    	 				</c:choose>
                    </security:authorize>					
				</td>
			</tr>
		</c:forEach>

	</tbody>


</table>


</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
