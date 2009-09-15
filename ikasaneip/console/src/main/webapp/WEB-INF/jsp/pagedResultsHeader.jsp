<%-- 
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

    <c:url var="firstPageLink" value="list.htm">
        <c:forEach var="entry" items="${searchParams}">
            <c:forEach var="entryValue" items="${entry.value}">
               <c:param name="${entry.key}" value="${entryValue}"/>
            </c:forEach>
        </c:forEach>
        <c:param name="page" value="0"/>
        <c:param name="orderBy" value="${orderBy}"/>
        <c:param name="orderAsc" value="${orderAsc}"/>
        <c:param name="selectAll" value="${selectAll}"/>
        <c:param name="pageSize" value="${pageSize}"/>
    </c:url>
    
    <c:url var="nextPageLink" value="list.htm">
		<c:forEach var="entry" items="${searchParams}">
            <c:forEach var="entryValue" items="${entry.value}">
               <c:param name="${entry.key}" value="${entryValue}"/>
            </c:forEach>
        </c:forEach>
    	<c:param name="page" value="${page+1}"/>
    	<c:param name="orderBy" value="${orderBy}"/>
    	<c:param name="orderAsc" value="${orderAsc}"/>
    	<c:param name="selectAll" value="${selectAll}"/>
        <c:param name="pageSize" value="${pageSize}"/>
    </c:url>
    
    <c:url var="previousPageLink" value="list.htm">
		<c:forEach var="entry" items="${searchParams}">
            <c:forEach var="entryValue" items="${entry.value}">
                <c:param name="${entry.key}" value="${entryValue}"/>
            </c:forEach>
		</c:forEach>    	
    	<c:param name="page" value="${page-1}"/>
    	<c:param name="orderBy" value="${orderBy}"/>
    	<c:param name="orderAsc" value="${orderAsc}"/>
        <c:param name="selectAll" value="${selectAll}"/>
        <c:param name="pageSize" value="${pageSize}"/>
    </c:url>

    <c:url var="lastPageLink" value="list.htm">
        <c:forEach var="entry" items="${searchParams}">
            <c:forEach var="entryValue" items="${entry.value}">
               <c:param name="${entry.key}" value="${entryValue}"/>
            </c:forEach>
        </c:forEach>
        <c:param name="page" value="${lastPage}"/>
        <c:param name="orderBy" value="${orderBy}"/>
        <c:param name="orderAsc" value="${orderAsc}"/>
        <c:param name="selectAll" value="${selectAll}"/>
        <c:param name="pageSize" value="${pageSize}"/>
    </c:url>

    <c:url var="searchLink" value="list.htm">
        <c:forEach var="entry" items="${searchParams}">
            <c:forEach var="entryValue" items="${entry.value}">
               <c:param name="${entry.key}" value="${entryValue}"/>
            </c:forEach>
        </c:forEach>
        <c:param name="page" value="${page}"/>
        <c:param name="orderBy" value="${orderBy}"/>
        <c:param name="orderAsc" value="${orderAsc}"/>
        <c:param name="selectAll" value="${selectAll}"/>
        <c:param name="pageSize" value="${pageSize}"/>
    </c:url>

  <script type="text/javascript">

    /* 
     * A function to fire the search again (triggered by a change in pageSize).
     */
    function executeSearch()
    {
        var url = '<c:out value="${searchLink}#results" escapeXml="false" />';
        var newPageSize = document.getElementById('pageSize').value;
        var newURL = "pageSize=" + newPageSize;
        url = url.replace(/pageSize=${pageSize}/, newURL);
        document.location.href = url;
    }

  </script>

    <c:choose>
    	<c:when test="${resultSize==0}">
    		<p><fmt:message key="paged_results_header_no_results"/></p>
		</c:when>
		<c:otherwise>
    		<span id="currentlyShowing"><fmt:message key="paged_results_header_showing"/> <c:out value="${firstResultIndex+1}"/> <fmt:message key="paged_results_header_to"/> <c:out value="${firstResultIndex + size}"/> <fmt:message key="paged_results_header_of"/> <c:out value="${resultSize}"/> <fmt:message key="paged_results_header_results"/>&nbsp;&nbsp;<a href="#top"><fmt:message key="paged_results_header_back_to_top"/></a></span>
    		<span id="navigationControls">
    		    <a name="results" />
            	<c:if test="${page gt 0}"><a href="<c:out value="${firstPageLink}#results" escapeXml="true" />"><fmt:message key="paged_results_header_first"/></a>&nbsp;&nbsp;<a href="<c:out value="${previousPageLink}#results" escapeXml="true" />"><fmt:message key="paged_results_header_previous"/></a>&nbsp;</c:if>
            	<c:if test="${!isLastPage}"><a href="<c:out value="${nextPageLink}#results" escapeXml="true" />"><fmt:message key="paged_results_header_next"/></a>&nbsp;&nbsp;<a href="<c:out value="${lastPageLink}#results" escapeXml="true" />"><fmt:message key="paged_results_header_last"/></a></c:if>
                <select id="pageSize" name="pageSize" onchange="javascript:executeSearch()">
                    <option value="10" <c:if test="${pageSize=='10'}">selected="selected"</c:if>>10</option>
                    <option value="25" <c:if test="${pageSize=='25'}">selected="selected"</c:if>>25</option>
                    <option value="50" <c:if test="${pageSize=='50'}">selected="selected"</c:if>>50</option>
                    <option value="100" <c:if test="${pageSize=='100'}">selected="selected"</c:if>>100</option>
                </select>
    		</span>
    	</c:otherwise>
    </c:choose>

</div> <!--end searchResultsHeader -->
