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

<div class="middle">

<c:out value="${moduleName}" /> -> <c:out value="${flowName}" />
<h2>Configured Component :: <c:out value="${flowElementName}" /></h2>

    <form:errors path="configuration.*" cssClass="errorMessages"/>

    <c:choose>
	    <c:when test="${empty configuration}">
	        <form:form id="configurationForm" commandName="specifyConfiguration" method="post" modelAttribute="configuration" cssClass="dataform fancydataform">
	            <fieldset>
	               <legend>Configuration</legend>
	            
	                    <ol>  
	                        <li>
                               <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
   					               ${message.text}
				               </c:forEach>
	                        </li>
	                     </ol>
	            </fieldset>                          
	            <p>
	                <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
	                <input type="submit" class="button" name="_eventId_cancel" value="Ok" class="controlButton"/>
	            </p>
	        </form:form>
	    </c:when>
	    <c:otherwise>
			<form:form id="configurationForm" commandName="configuration" method="post" modelAttribute="configuration" cssClass="dataform fancydataform">
			    <fieldset>
			       <legend>Configuration</legend>
			    
			            <ol>  
			                <li>
			                    <label for="configurationId">Id</label>
			                    <span class="nonEditable"><c:out value="${configuration.configurationId}" /></span>
			                </li>
			                <li>
			                    <label for="description">Configuration Description</label>
			                    <form:textarea id="${description}" path="description"/>
			                </li>
			                <c:forEach items="${configuration.configurationParameters}" var="current" varStatus="stat">
			                   <li>
			                       <label for"<c:out value="${current.name}" />"/><c:out value="${current.name}" /></label>
			                       <form:textarea id="configurationParameters[${stat.index}].value" path="configurationParameters[${stat.index}].value"/>
			                       <br/>
			                       <label for"<c:out value="${current.description}" />"/>Parameter Description</label>
			                       <form:textarea id="configurationParameters[${stat.index}].description" path="configurationParameters[${stat.index}].description"/>
			                   </li>
			                </c:forEach>
			             </ol>
			
			    </fieldset>                          
			    <p>
			        <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
			        <input type="submit" class="button" name="_eventId_save" value="Save" class="controlButton"/>
			        <input type="submit" class="button" name="_eventId_cancel" value="Cancel" class="controlButton"/>
			    </p>
			</form:form>
	    </c:otherwise>
    </c:choose>
</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>