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

<h2>FlowElement :: <c:out value="${flowElement.componentName}" /></h2>

<span id="flowElementDescription"><c:out value="${flowElement.description}" /></span>

<div class="left">
<h3>Details</h3>
<table id="flowElementDetails" class="keyValueTable">
	<tr>
		<th>
			Name
		</th>
		<td>
			<c:out value="${flowElement.componentName}" />
		</td>
	</tr>
	
	<tr>
		<th>
			Component Type
		</th>
		<td>
			<c:out value="${flowElement.flowComponent.class.simpleName}" />
		</td>
	</tr>
</table>

<h3>Transitions</h3>
<table id="flowElementTransitions" class="listTable">
	<thead>
		<tr>
			<th>Name</th>
			<th>Flow Element</th>
		</tr>
	</thead>

	<tbody>
		<c:if test="${empty flowElement.transitions}">
			<tr>
				<td colspan="2">
					There are no transitions defined for this flow element
				</td>			
			</tr>		
		</c:if>
		
		<c:forEach items="${flowElement.transitions}" var="transitionEntry">
		  <c:url var="transitionLink" value="viewFlowElement.htm">
            <c:param name="moduleName" value="${moduleName}"/>
            <c:param name="flowName" value="${flowName}"/>
            <c:param name="flowElementName" value="${transitionEntry.value.componentName}"/>
          </c:url>
			
			<tr>
				<td>
					<c:out value="${transitionEntry.key}" />
				</td>			
				<td>
                    <a href="<c:out value="${transitionLink}" escapeXml="true" />">
						<c:out value="${transitionEntry.value.componentName}" />
					</a>
					
				</td>
			</tr>
		</c:forEach>
		

	</tbody>
</table>


</div> <!--end left column -->

<div class="left">



<h3>Before Element Triggers</h3>
<table id="beforeFlowElementTriggers" class="listTable">
	<thead>
		<tr>
			<th>Job</th>
			<th>Parameters</th>
			<th>Action</th>
		</tr>
	</thead>

	<tbody>
		<c:if test="${empty beforeElementTriggers}">
			<tr>
				<td colspan="2">
					There are no triggers defined before this flow element
				</td>
				<td>&nbsp;</td>		
			</tr>		
		</c:if>
		
		<c:forEach items="${beforeElementTriggers}" var="trigger">
			<tr>
				<td>
					<c:out value="${trigger.jobName}" />
				</td>			
				<td>
					<c:out value="${trigger.params}" />
				</td>
				<td>
					<c:if test="${trigger.id!=null}">
					   <c:url var="deleteTriggerLink" value="deleteTrigger.htm">
                        <c:param name="moduleName" value="${moduleName}"/>
                        <c:param name="flowName" value="${flowName}"/>
                        <c:param name="flowElementName" value="${flowElementName}"/>
                        <c:param name="triggerId" value="${trigger.id}"/>
                       </c:url>
                        <a href="<c:out value="${deleteTriggerLink}" escapeXml="true" />">
							Delete
						</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		
		<tr>
				<td colspan="2">
					&nbsp;
				</td>			
				<td>
				    <c:url var="createTriggerLink" value="/trigger.htm">
                        <c:param name="moduleName" value="${moduleName}"/>
                        <c:param name="flowName" value="${flowName}"/>
                        <c:param name="flowElementName" value="${flowElementName}"/>
                        <c:param name="relationship" value="before"/>
                     </c:url>
                    <a href="<c:out value="${createTriggerLink}" escapeXml="true" />">
                        Create
                    </a>
				</td>
			</tr>

	</tbody>


</table>


<h3>After Element Triggers</h3>
<table id="afterFlowElementTriggers" class="listTable">
	<thead>
		<tr>
			<th>Job</th>
			<th>Parameters</th>
			<th>Action</th>
		</tr>
	</thead>

	<tbody>
		<c:if test="${empty afterElementTriggers}">
			<tr>
				<td colspan="2">
					There are no triggers defined after this flow element
				</td>
				<td>&nbsp;</td>		
			</tr>		
		</c:if>
		
		<c:forEach items="${afterElementTriggers}" var="trigger">
			<tr>
				<td>
					<c:out value="${trigger.jobName}" />
				</td>			
				<td>
					<c:out value="${trigger.params}" />
				</td>
				<td>
					<c:if test="${trigger.id!=null}">
                       <c:url var="deleteTriggerLink" value="deleteTrigger.htm">
                        <c:param name="moduleName" value="${moduleName}"/>
                        <c:param name="flowName" value="${flowName}"/>
                        <c:param name="flowElementName" value="${flowElementName}"/>
                        <c:param name="triggerId" value="${trigger.id}"/>
                       </c:url>
                        <a href="<c:out value="${deleteTriggerLink}" escapeXml="true" />">
							Delete
						</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		
		<tr>
				<td colspan="2">
					&nbsp;
				</td>			
                <td>
                    <c:url var="createTriggerLink" value="/trigger.htm">
                        <c:param name="moduleName" value="${moduleName}"/>
                        <c:param name="flowName" value="${flowName}"/>
                        <c:param name="flowElementName" value="${flowElementName}"/>
                        <c:param name="relationship" value="after"/>
                     </c:url>
                    <a href="<c:out value="${createTriggerLink}" escapeXml="true" />">
                        Create
                    </a>
                </td>
			</tr>

	</tbody>


</table>

</div> <!-- end right column -->



</div> <!-- end middle -->




<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
