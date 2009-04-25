<%@ include file="/WEB-INF/jsp/events/eventsTop.jsp"%>
<!-- 
# //
# //
# // $Id: wiretapEvents.jsp 16798 2009-04-24 14:12:09Z mitcje $
# // $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/main/webapp/WEB-INF/jsp/events/wiretapEvents.jsp $
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
-->
  <link type="text/css" href="<c:url value='/css/jquery/redmond/jquery-ui-1.7.1.custom.css'/>" rel="stylesheet" />
  <script type="text/javascript" src="<c:url value='/js/jquery-1.3.2.min.js'/>"></script>
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
				<input type="submit" value="Search" />
        </p>
</form:form>

</div>


<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
