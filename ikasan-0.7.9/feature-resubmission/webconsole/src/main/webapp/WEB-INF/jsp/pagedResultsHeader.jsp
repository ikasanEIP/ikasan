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