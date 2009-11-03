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
<%@ include file="/WEB-INF/jsp/top.jsp"%>

<div class="subnavcontainer">
    <c:if test="${moduleName != null}">
	<ul>
		
		<li>
            <a href="<c:url value="view.htm">
                <c:param name="moduleName" value="${moduleName}"/>
                     </c:url>">
                    <c:out value="${moduleName}" />
            </a>

		</li>
        <c:if test="${initiator != null}">
          <c:url var="initiatorLink" value="viewInitiator.htm">
            <c:param name="moduleName" value="${moduleName}"/>
            <c:param name="initiatorName" value="${initiator.name}"/>
          </c:url>
        
            <li> -> 
                <a href="<c:out value="${initiatorLink}" escapeXml="true" />">
                        <c:out value="${initiator.name}" />
                </a>
            </li>
        </c:if>		
		
		
		<c:if test="${flowName != null}">
		  <c:url var="flowLink" value="viewFlow.htm">
            <c:param name="moduleName" value="${moduleName}"/>
            <c:param name="flowName" value="${flowName}"/>
          </c:url>
		
			<li> -> 
                <a href="<c:out value="${flowLink}" escapeXml="true" />">
                        <c:out value="${flowName}" />
                </a>
			</li>
		</c:if>
		<c:if test="${flowElement != null}">
            <c:url var="flowElementLink" value="viewFlowElement.htm">
                <c:param name="moduleName" value="${moduleName}"/>
                <c:param name="flowName" value="${flowName}"/>
                <c:param name="flowElementName" value="${flowElementName}"/>
            </c:url>
			<li> -> 
                <a href="<c:out value="${flowElementLink}" escapeXml="true" />">
                        <c:out value="${flowElementName}" />
                </a>				
			</li>
		</c:if>
	</ul>
	</c:if>
</div>