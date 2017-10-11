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
<%@ include file="modulesTop.jsp"%>

<div class="middle">

<h2>Flow :: <c:out value="${flow.name}" /></h2>

 <security:authorize access="hasAnyAuthority('ALL','WriteBlueConsole')">
 <c:url var="initiatorLink" value="flowStartupControl.htm">
                <c:param name="moduleName" value="${moduleName}"/>
                <c:param name="flowName" value="${flow.name}"/>
              </c:url>
<form action="${initiatorLink}" method="post">
    Startup Type : <select name="startupType">
        <option value="MANUAL" <c:if test="${startupControl.manual}">selected="selected" </c:if>  >Manual</option>
        <option value="AUTOMATIC" <c:if test="${startupControl.automatic}">selected="selected" </c:if>  >Automatic</option>
        <option value="DISABLED" <c:if test="${startupControl.disabled}">selected="selected" </c:if>  >Disabled</option>
    </select>

    Comment : <input name="startupComment" value="${startupControl.comment}" type="text">

    <input type="submit" value="Update" class="controlButton"/>
</security:authorize>

	<security:authorize access="hasAnyAuthority('ALL','WriteBlueConsole')">
	Startup Type : ${startupControl.startupType}
	</security:authorize>

</form>


	<h3>Flow Elements</h3>
	
	<table id="flowElementsList" class="listTable">
		<thead>
			<tr>
				<th>Name</th>
				<th>Component Type</th>
			</tr>
		</thead>
	
		<tbody>
			<c:forEach items="${flowElements}" var="flowElement">
				<c:set var="transitions" value="${flowElement.transitions}" />
				<tr>
					<td><a href="viewFlowElement.htm?moduleName=<c:out value="${moduleName}" />&flowName=<c:out value="${flow.name}" />&flowElementName=<c:out value="${flowElement.componentName}" />">
							<c:out value="${flowElement.componentName}" />
						</a>
					</td>
						
						
					
					<td><c:out value="${flowElement.flowComponent.getClass().simpleName}" /></td>
				</tr>
			</c:forEach> 
	
		</tbody>
	
	
	</table>

</div>



<%@ include file="../bottom.jsp"%>
