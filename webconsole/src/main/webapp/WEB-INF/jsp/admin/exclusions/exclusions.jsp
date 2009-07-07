<%@ include file="/WEB-INF/jsp/admin/exclusions/exclusionsTop.jsp"%>
<%-- 
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

<h2>Excluded Events</h2>

<div id="searchResultsHeader">
    
    <c:url var="nextPageLink" value="list.htm">
		<c:forEach var="entry" items="${searchParams}">
			<c:param name="${entry.key}" value="${entry.value}"/>
		</c:forEach>    	
    	<c:param name="page" value="${page+1}"/>
    	<c:param name="orderBy" value="${orderBy}"/>
    	<c:param name="orderAsc" value="${orderAsc}"/>
    </c:url>
    
    <c:url var="previousPageLink" value="list.htm">
		<c:forEach var="entry" items="${searchParams}">
			<c:param name="${entry.key}" value="${entry.value}"/>
		</c:forEach>    	
    	<c:param name="page" value="${page-1}"/>
    	<c:param name="orderBy" value="${orderBy}"/>
    	<c:param name="orderAsc" value="${orderAsc}"/>
    </c:url>
    

    
    <span id="currentlyShowing">Showing <c:out value="${firstResultIndex+1}"/> to <c:out value="${firstResultIndex + size}"/> of <c:out value="${resultSize}"/> results</span>
    <span id="navigationControls" >
            <c:if test="${page gt 0}">
                <a href="<c:out value="${previousPageLink}" escapeXml="true" />">Previous</a>&nbsp;
            </c:if>
            <c:if test="${!lastPage}">
                <a href="<c:out value="${nextPageLink}" escapeXml="true" />">Next</a>
            </c:if>
    </span>
    

</div> <!--end searchResultsHeader -->
    
    
    
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
    			<c:param name="excludedEventId" value="${exclusion.id}"/>
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
