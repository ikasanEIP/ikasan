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
<%@ include file="/WEB-INF/jsp/admin/exclusions/exclusionsTop.jsp"%>

<div class="middle">

<h2>Excluded Events</h2>

<form method="get" action="${resultsLink}" class="dataform fancydataform" >
	<fieldset>
    	<legend>Search</legend>
    	<ol>
  			<li>
		    	<label for="moduleName">Module Name</label>
				<input id="moduleName" type="text" name="moduleName" size="30" value="${searchParams['moduleName']}"/>
			</li>
  			<li>
		    	<label for="flowName">Flow Name</label>
				<input id="flowName" type="text" name="flowName" size="30" value="${searchParams['flowName']}"/>
			</li>
  			<li>
		    	<label for="orderBy">Order By</label>
				<select id="orderBy" name="orderBy"    >
					<option value="id"  <c:if test="${orderBy=='id'}">selected="selected"</c:if>  >Id</option>
					<option value="moduleName" <c:if test="${orderBy=='moduleName'}">selected="selected"</c:if>>Module</option>					
					<option value="flowName" <c:if test="${orderBy=='flowName'}">selected="selected"</c:if>>Flow</option>
					<option value="exclusionTime" <c:if test="${orderBy=='exclusionTime'}">selected="selected"</c:if>>Exclusion Time</option>
				</select>
				<label for="orderBy">Ascending</label>
				<input id="orderAsc" type="checkbox" name="orderAsc" <c:if test="${orderAsc=='true'}">checked="checked"</c:if>/>
			</li>
		</ol>
	</fieldset>
	<p>
		<input type="submit" value="Search for Exclusions" class="controlButton"/>
	</p>
</form>

<%@ include file="/WEB-INF/jsp/pagedResultsHeader.jsp"%>
    
    <c:url var="idLink" value="list.htm">
		<c:forEach var="entry" items="${searchParams}">
			<c:param name="${entry.key}" value="${entry.value}"/>
		</c:forEach>    	
    	<c:param name="orderBy" value="id"/>
    	<c:param name="orderAsc" value="${!orderAsc}"/>
    </c:url>
    
    <c:url var="moduleLink" value="list.htm">
		<c:forEach var="entry" items="${searchParams}">
			<c:param name="${entry.key}" value="${entry.value}"/>
		</c:forEach>    	
    	<c:param name="orderBy" value="moduleName"/>
    	<c:param name="orderAsc" value="${!orderAsc}"/>
    </c:url>
    
    <c:url var="flowLink" value="list.htm">
		<c:forEach var="entry" items="${searchParams}">
			<c:param name="${entry.key}" value="${entry.value}"/>
		</c:forEach>    	
    	<c:param name="orderBy" value="flowName"/>
    	<c:param name="orderAsc" value="${!orderAsc}"/>
    </c:url>
    
    <c:url var="exclusionTimeLink" value="list.htm">
		<c:forEach var="entry" items="${searchParams}">
			<c:param name="${entry.key}" value="${entry.value}"/>
		</c:forEach>    	
    	<c:param name="orderBy" value="exclusionTime"/>
    	<c:param name="orderAsc" value="${!orderAsc}"/>
    </c:url>


<table id="exclusionsSearchResults" class="listTable">
    <thead>
        <tr>
            <th><a href="<c:out value="${idLink}" escapeXml="true" />">Id</a></th>
            <th><a href="<c:out value="${moduleLink}" escapeXml="true" />">Module</a></th>
            <th><a href="<c:out value="${flowLink}" escapeXml="true" />">Flow</a></th>
            <th><a href="<c:out value="${exclusionTimeLink}" escapeXml="true" />">Exclusion Time</a></th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${results}" var="exclusion">
            <c:url var="viewExclusionLink" value="exclusion.htm">   	
    			<c:param name="eventId" value="${exclusion.event.id}"/>
    			<c:param name="searchResultsUrl" value="${searchResultsUrl}"/>
    		</c:url>
            <tr>
                <td>
                	<a href="<c:out value="${viewExclusionLink}" escapeXml="true" />"><c:out value="${exclusion.id}" /></a>
                </td>           
                <td>
                    <c:out value="${exclusion.moduleName}" />
                </td>
                <td>
                    <c:out value="${exclusion.flowName}" />
                </td>
                <td>
                    <fmt:formatDate value="${exclusion.exclusionTime}"
                                pattern="dd/MM/yyyy h:mma"/>
                </td>
            </tr>
        </c:forEach>

    </tbody>


</table>

</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
