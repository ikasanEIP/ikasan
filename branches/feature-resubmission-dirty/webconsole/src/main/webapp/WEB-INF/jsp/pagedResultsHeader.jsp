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
    
    <c:choose>
    	<c:when test="${resultSize==0}">
    		<p>Your search did not return any results</p>
		</c:when>
		<c:otherwise>
    		<span id="currentlyShowing">Showing <c:out value="${firstResultIndex+1}"/> to <c:out value="${firstResultIndex + size}"/> of <c:out value="${resultSize}"/> results</span>
    		<span id="navigationControls" >
            	<c:if test="${page gt 0}">
                	<a href="<c:out value="${previousPageLink}" escapeXml="true" />">Previous</a>&nbsp;
            	</c:if>
            	<c:if test="${!lastPage}">
                	<a href="<c:out value="${nextPageLink}" escapeXml="true" />">Next</a>
            	</c:if>
    		</span>
    	</c:otherwise>
    </c:choose>
    

</div> <!--end searchResultsHeader -->
