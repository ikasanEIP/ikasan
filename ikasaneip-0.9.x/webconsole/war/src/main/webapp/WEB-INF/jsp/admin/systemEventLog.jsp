<%--

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
    =============================================================================

--%>

<%@ include file="/WEB-INF/jsp/admin/adminTop.jsp"%>
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
<link type="text/css"
	href="<c:url value='/css/jquery/redmond/jquery-ui-1.7.2.custom.css'/>"
	rel="stylesheet" />
<script type="text/javascript"
	src="<c:url value='/js/jquery-ui-1.7.2.custom.min.js'/>"></script>
<script type="text/javascript">
  $(document).ready(function(){
    $("#timestampFromDate").datepicker({dateFormat: 'dd/mm/yy' });
    $("#timestampToDate").datepicker({dateFormat: 'dd/mm/yy' });
    
    $("#timestampFromDate").change(function() {
     $("#timestampFromTime").val('00:00:00');
    });
    
    $("#timestampToDate").change(function() {
     $("#timestampToTime").val('00:00:00');
    });
    
  });
  </script>
<div class="middle">

<h3>System Event Log</h3>

<form method="get" id="systemEventSearchForm"
	class="dataform fancydataform">
<fieldset><legend>Search</legend>

<ol>
	<li><label for="modules">Subject</label> <input name="subject"
		value="${searchParams.subject}" /></li>
	<li><label for="action">Action</label> <input name="action"
		value="${searchParams.action}" /></li>
	<li><label for="timestampFrom">Timestamp From</label> <input
		id="timestampFromDate" name="timestampFromDate"
		value="${searchParams.timestampFromDate}" size="10"> <input
		id="timestampFromTime" name="timestampFromTime"
		value="${searchParams.timestampFromTime}" size="8"></li>
	<li><label for="timestampTo">Timestamp To</label> <input
		id="timestampToDate" name="timestampToDate"
		value="${searchParams.timestampToDate}" size="10"> <input
		id="timestampToTime" name="timestampToTime"
		value="${searchParams.timestampToTime}" size="8"></li>
	<li><label for="actor">Actor</label> <input name="actor"
		value="${searchParams.actor}" /></li>
</ol>
</fieldset>
<p><input type="submit" value="Search" class="controlButton" /></p>
</form>


<div id="searchResultsHeader"><c:url var="nextPageLink"
	value="search.htm">
	<c:forEach var="entry" items="${searchParams}">
		<c:forEach var="entryValue" items="${entry.value}">
			<c:param name="${entry.key}" value="${entryValue}" />
		</c:forEach>
	</c:forEach>
	<c:param name="page" value="${page+1}" />
	<c:param name="orderBy" value="${orderBy}" />
	<c:param name="orderAsc" value="${orderAsc}" />
</c:url> <c:url var="previousPageLink" value="search.htm">
	<c:forEach var="entry" items="${searchParams}">
		<c:forEach var="entryValue" items="${entry.value}">
			<c:param name="${entry.key}" value="${entryValue}" />
		</c:forEach>
	</c:forEach>
	<c:param name="page" value="${page-1}" />
	<c:param name="orderBy" value="${orderBy}" />
	<c:param name="orderAsc" value="${orderAsc}" />
</c:url> <c:choose>
	<c:when test="${resultSize==0}">
		<p>No results</p>
	</c:when>
	<c:otherwise>
		<span id="currentlyShowing">Currently showing: <c:out
			value="${firstResultIndex+1}" /> to <c:out
			value="${firstResultIndex + size}" /> of <c:out value="${resultSize}" />
		results </span>
		<span id="navigationControls"> <a name="results" /> <c:if
			test="${page gt 0}">
			<a href="<c:out value="${previousPageLink}" escapeXml="true" />">previous</a>&nbsp;
            	</c:if> <c:if test="${!lastPage}">
			<a href="<c:out value="${nextPageLink}" escapeXml="true" />">next</a>
		</c:if></span>
	</c:otherwise>
</c:choose></div>
<!--end searchResultsHeader -->


<c:if  test="${resultSize>0}">
	<table id="systemEventSearchResults" class="listTable">
		<thead>
			<tr>
				<th>Id</th>
				<th>Subject</th>
				<th>Action</th>
				<th>Timestamp</th>
				<th>Actor</th>
	
			</tr>
		</thead>
	
		<tbody>
			<c:forEach items="${results}" var="event">
				<tr>
					<td><c:out value="${event.id}" /></td>
					<td><c:out value="${event.subject}" /></td>
					<td><c:out value="${event.action}" /></td>
					<td><c:out value="${event.timestamp}" /></td>
					<td><c:out value="${event.actor}" /></td>
				</tr>
			</c:forEach>
	
		</tbody>
	
	
	</table>
</c:if>
	<div>
	<form method="post" id="housekeepingForm" action="housekeeping.htm">
	<input type="submit" value="Housekeep System Events"
		class="controlButton" /></form>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
