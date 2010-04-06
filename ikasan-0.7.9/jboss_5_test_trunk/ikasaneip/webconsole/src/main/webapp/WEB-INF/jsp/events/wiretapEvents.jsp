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
<%@ include file="/WEB-INF/jsp/events/eventsTop.jsp"%>

  <link type="text/css" href="<c:url value='/css/jquery/redmond/jquery-ui-1.7.1.custom.css'/>" rel="stylesheet" />
  <script type="text/javascript" src="<c:url value='/js/jquery-ui-1.7.1.custom.min.js'/>"></script>
  <script type="text/javascript">
  $(document).ready(function(){
    $("#fromDate").datepicker({dateFormat: 'dd/mm/yy' });
    $("#untilDate").datepicker({dateFormat: 'dd/mm/yy' });
    
    $("#fromDate").change(function() {
     $("#fromTime").val('00:00:00');
    });
    
    $("#untilDate").change(function() {
     $("#untilTime").val('00:00:00');
    });
    
  });
  </script>

<div class="middle">

<h2>Wiretapped Events</h2>

<h3>Wiretapped Events</h3>
<form method="post" id="housekeepingForm" action="housekeeping.htm">
    <input type="submit" value="Housekeep Wiretap Events" class="controlButton" />
</form>

<h3>Search</h3>
<form:form id="wiretapSearchForm" commandName="searchCriteria" cssClass="dataform fancydataform">

<form:errors path="*" cssClass="errorMessages"/>

    <fieldset>
       <legend>Search</legend>
    
            <ol>  
                 <li>
                    <label for="modules">Modules</label>   
                    <div id="eventSearchModuleCheckboxes" class="multiSelectCheckboxes">            
                    <c:forEach items="${modules}" var="module"> 
                        <form:checkbox path="modules" value="${module.name}"/><c:out value="${module.name}"/>
                        <br>
                    </c:forEach>
                    </div>
                                    
                </li>
		         <li>
		            <label for="componentName">Component</label>
			        <form:input path="componentName"/>
                 </li>
                 <li>
                    <label for="eventId">Event Id</label>
                    <form:input path="eventId"/>
                 </li>
                 <li>
                    <label for="payloadId">Payload Id</label>
				    <form:input path="payloadId"/>
                 </li>
                 <li>
                    <label for="fromDate">From</label>
                    <form:input path="fromDate" size="10"/>
                    <form:input path="fromTime" size="8"/>
                 </li>
                 <li>
                    <label for="untilDate">Until</label>
                    <form:input path="untilDate" size="10"/>
                    <form:input path="untilTime" size="8"/>
                 </li>
                 <li>
                    <label for="payloadContent">Payload Content</label>
				    <form:input path="payloadContent"/>
                 </li>
             </ol>
    </fieldset>                             
        <p>
				<input type="submit" value="Search" class="controlButton"/>
        </p>
</form:form>

</div>


<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
