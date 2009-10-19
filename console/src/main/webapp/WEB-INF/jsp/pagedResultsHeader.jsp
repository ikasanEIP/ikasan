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
        <c:param name="pointToPointFlowProfileSearch" value="${pointToPointFlowProfileSearch}"/>        
        <c:param name="pointToPointFlowProfileSelectAll" value="${pointToPointFlowProfileSelectAll}"/>
        <c:param name="moduleSelectAll" value="${moduleSelectAll}"/>        
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
        <c:param name="pointToPointFlowProfileSearch" value="${pointToPointFlowProfileSearch}"/>    	
        <c:param name="pointToPointFlowProfileSelectAll" value="${pointToPointFlowProfileSelectAll}"/>
        <c:param name="moduleSelectAll" value="${moduleSelectAll}"/>        
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
        <c:param name="pointToPointFlowProfileSearch" value="${pointToPointFlowProfileSearch}"/>    	
        <c:param name="pointToPointFlowProfileSelectAll" value="${pointToPointFlowProfileSelectAll}"/>
        <c:param name="moduleSelectAll" value="${moduleSelectAll}"/>        
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
        <c:param name="pointToPointFlowProfileSearch" value="${pointToPointFlowProfileSearch}"/>        
        <c:param name="pointToPointFlowProfileSelectAll" value="${pointToPointFlowProfileSelectAll}"/>
        <c:param name="moduleSelectAll" value="${moduleSelectAll}"/>        
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
        <c:param name="pointToPointFlowProfileSearch" value="${pointToPointFlowProfileSearch}"/>        
        <c:param name="pointToPointFlowProfileSelectAll" value="${pointToPointFlowProfileSelectAll}"/>
        <c:param name="moduleSelectAll" value="${moduleSelectAll}"/>        
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
    		    <a name="results"></a>
		 		<c:choose>
					<c:when test="${page gt 0}">
				        <a href="<c:out value="${firstPageLink}#results" escapeXml="true" />"><fmt:message key="paged_results_header_first"/></a>&nbsp;&nbsp;<a href="<c:out value="${previousPageLink}#results" escapeXml="true" />"><fmt:message key="paged_results_header_previous"/></a>&nbsp;
					</c:when>
					<c:otherwise>
				        <fmt:message key="paged_results_header_first"/>&nbsp;&nbsp;<fmt:message key="paged_results_header_previous"/>
					</c:otherwise>
				</c:choose>
		 		<c:choose>
					<c:when test="${!isLastPage}">
				        <a href="<c:out value="${nextPageLink}#results" escapeXml="true" />"><fmt:message key="paged_results_header_next"/></a>&nbsp;&nbsp;<a href="<c:out value="${lastPageLink}#results" escapeXml="true" />"><fmt:message key="paged_results_header_last"/></a>
					</c:when>
					<c:otherwise>
						<fmt:message key="paged_results_header_next"/>&nbsp;&nbsp;<fmt:message key="paged_results_header_last"/>
					</c:otherwise>
				</c:choose>
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
