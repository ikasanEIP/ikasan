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
