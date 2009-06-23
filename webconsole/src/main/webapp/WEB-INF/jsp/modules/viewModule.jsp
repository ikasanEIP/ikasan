<%@ include file="/WEB-INF/jsp/modules/modulesTop.jsp"%>
<%-- 
# //
# //
# // $Id$
# // $URL$
# // 
# // ====================================================================
# // Ikasan Enterprise Integration Platform
# // Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
# // by the @authors tag. See the copyright.txt in the distribution for a
# // full listing of individual contributors.
# //
# // This is free software; you can redistribute it and/or modify it
# // under the terms of the GNU Lesser General Public License as
# // published by the Free Software Foundation; either version 2.1 of
# // the License, or (at your option) any later version.
# //
# // This software is distributed in the hope that it will be useful,
# // but WITHOUT ANY WARRANTY; without even the implied warranty of
# // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# // Lesser General Public License for more details.
# //
# // You should have received a copy of the GNU Lesser General Public
# // License along with this software; if not, write to the 
# // Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
# // or see the FSF site: http://www.fsfeurope.org/.
# // ====================================================================
# //
# // Author:  Ikasan Development Team
# // 
--%>

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
