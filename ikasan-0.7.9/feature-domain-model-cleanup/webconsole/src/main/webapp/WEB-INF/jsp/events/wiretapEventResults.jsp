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
<%@ include file="/WEB-INF/jsp/events/eventsTop.jsp"%>

<div class="middle">

<h2>Wiretapped Events</h2>

<h3>Results</h3>

<a href="search.htm">New Search</a>

<div id="searchResultsHeader">
    
    <span id="currentlyShowing">Showing <c:out value="${searchResults.firstIndex}"/> to <c:out value="${searchResults.lastIndex}"/> of <c:out value="${searchResults.resultSize}"/> results</span>
    <span id="navigationControls" >
            <c:if test="${searchResults.firstIndex gt 1}">
                <a href="previous.htm">Previous</a>&nbsp;
            </c:if>
            <c:if test="${searchResults.resultSize gt searchResults.lastIndex}">
                <a href="next.htm">Next</a>
            </c:if>
    </span>
    

</div> <!--end searchResultsHeader -->

<table id="wiretapSearchResults" class="listTable">
    <thead>
        <tr>
            <th>Id</th>
            <th>Module</th>
            <th>Flow</th>
            <th>Component</th>
            <th>Event Id / Payload Id</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${searchResults.wiretapEventHeaders}" var="event">
            <tr>
                <td>
                    
                    <a href="viewEvent.htm?eventId=<c:out value="${event.id}" />">
                        <c:out value="${event.id}" />
                    </a>
                </td>           
                <td>
                    <c:out value="${event.moduleName}" />
                </td>
                <td>
                    <c:out value="${event.flowName}" />
                </td>
                <td>
                    <c:out value="${event.componentName}" />
                </td>
                <td>
                    <c:out value="${event.eventId}" /><br><c:out value="${event.payloadId}" />
                </td>
            </tr>
        </c:forEach>

    </tbody>


</table>

</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
