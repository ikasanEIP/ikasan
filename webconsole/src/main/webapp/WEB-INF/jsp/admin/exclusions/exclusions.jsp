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
    	<c:param name="page" value="${page+1}"/>
    </c:url>
    
    <c:url var="previousPageLink" value="list.htm">
    	<c:param name="page" value="${page-1}"/>
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

<table id="exclusionsSearchResults" class="listTable">
    <thead>
        <tr>
            <th>Id</th>
            <th>Module</th>
            <th>Flow</th>
            <th>Exclusion Time</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${exclusions}" var="exclusion">
            <tr>
                <td>
                    
                    <a href="exclusion.htm?excludedEventId=<c:out value="${exclusion.id}" />">
                        <c:out value="${exclusion.id}" />
                    </a>
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
