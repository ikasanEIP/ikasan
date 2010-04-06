<%@ include file="/WEB-INF/jsp/admin/errors/errorsTop.jsp"%>
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
<div class="middle">

<h2>Errors</h2>

<form method="get" action="${resultsLink}" class="dataform fancydataform" >
	<fieldset>
    	<legend>Search</legend>
    	<ol>
  			<li>
		    	<label for="moduleName">Module Name</label>
				<input id="moduleName" type="text" name="moduleName" size="30" value="${searchParams["moduleName"]}"/>
			</li>
  			<li>
		    	<label for="flowName">Flow Name</label>
				<input id="flowName" type="text" name="flowName" size="30" value="${searchParams["flowName"]}"/>
			</li>
  			<li>
		    	<label for="orderBy">Order By</label>
				<select id="orderBy" name="orderBy"    >
					<option value="id"  <c:if test="${orderBy=='id'}">selected="selected"</c:if>  >Id</option>
					<option value="moduleName" <c:if test="${orderBy=='moduleName'}">selected="selected"</c:if>>Module</option>					
					<option value="flowName" <c:if test="${orderBy=='flowName'}">selected="selected"</c:if>>Flow</option>
					<option value="logTime" <c:if test="${orderBy=='logTime'}">selected="selected"</c:if>>Time Logged</option>
				</select>
				<label for="orderAsc">Ascending</label>
				<input id="orderAsc" type="checkbox" name="orderAsc" <c:if test="${orderAsc=='true'}">checked="checked"</c:if>/>
			</li>
		</ol>
	</fieldset>
	<p>
		<input type="submit" value="Search for Errors" class="controlButton"/>
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
    
    <c:url var="logTimeLink" value="list.htm">
		<c:forEach var="entry" items="${searchParams}">
			<c:param name="${entry.key}" value="${entry.value}"/>
		</c:forEach>    	
    	<c:param name="orderBy" value="logTime"/>
    	<c:param name="orderAsc" value="${!orderAsc}"/>
    </c:url>

<c:if test="${resultSize>0}">
<table id="loggedErrors" class="listTable">
    <thead>
        <tr>
            <th><a href="<c:out value="${idLink}" escapeXml="true" />">Id</a></th>
            <th><a href="<c:out value="${moduleLink}" escapeXml="true" />">Module</a></th>
            <th><a href="<c:out value="${flowLink}" escapeXml="true" />">Flow</a></th>
            <th>Summary</th>
			<th><a href="<c:out value="${logTimeLink}" escapeXml="true" />">Time Logged</a></th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${results}" var="error">
            <c:url var="viewErrorLink" value="viewError.htm">   	
    			<c:param name="errorId" value="${error.id}"/>
    			<c:param name="searchResultsUrl" value="${searchResultsUrl}"/>
    		</c:url>
            <tr>      
                <td>
                	<a href="<c:out value="${viewErrorLink}" escapeXml="true" />"><c:out value="${error.id}" /></a>
                </td>                 
                <td>
                    <c:out value="${error.moduleName}" />
                </td>
                <td>
                	<c:if test="${!empty error.flowName}">
                    	<c:out value="${error.flowName}" />
                    </c:if>
                	<c:if test="${!empty error.initiatorName}">
                    	<c:out value="${error.initiatorName}" />
                    </c:if>
                </td>
                <td>
                	<a href="<c:out value="${viewErrorLink}" escapeXml="true" />">
                		<c:out value="${error.errorSummary}" />
                	</a>
   
                </td>
                <td>
                    <c:out value="${error.logTime}" />
                </td>
            </tr>
        </c:forEach>

    </tbody>


</table>
</c:if>


</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
