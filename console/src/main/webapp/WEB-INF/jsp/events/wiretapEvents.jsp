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
<%@ include file="/WEB-INF/jsp/events/eventsTop.jsp"%>

  <link type="text/css" href="<c:url value='/css/jquery/redmond/jquery-ui-1.7.1.custom.css'/>" rel="stylesheet" />
  <script type="text/javascript" src="<c:url value='/js/jquery-ui-1.7.1.custom.min.js'/>"></script>
  <script type="text/javascript">
      $(document).ready(function()
      {
        $("#fromDate").datepicker({dateFormat: 'dd/mm/yy' });
        $("#untilDate").datepicker({dateFormat: 'dd/mm/yy' });
        
        $("#fromDate").change(function() {
         $("#fromTime").val('00:00:00');
        });
        
        $("#untilDate").change(function() {
         $("#untilTime").val('00:00:00');
        });
      });

    /* 
     * A function to check/uncheck all checkboxes in a form.
     * 
     * Credit to Shawn Olson & http://www.shawnolson.net
     */
    function checkUncheckAll(theElement) {
        var theForm = theElement.form
        var z = 0;
        for(z = 0; z < theForm.length; z++)
        {
            if(theForm[z].type == 'checkbox' && theForm[z].name != '(de)select all')
            {
                theForm[z].checked = theElement.checked;
            }
        }
    }

  </script>

<div class="middle">

<form:form id="wiretapSearchForm" commandName="searchCriteria" cssClass="dataform fancydataform">

<form:errors path="*" cssClass="errorMessages"/>

    <fieldset>
        <legend><fmt:message key="wiretap_events_search"/></legend>
        <ol>
            <li>
                <label for="modules"><fmt:message key="wiretap_events_module"/></label>
                <form:checkbox path="modules" value="(de)select all" onclick="checkUncheckAll(this);"/> (de)select all
                <div id="eventSearchModuleCheckboxes" class="multiSelectCheckboxes">            
                <table>
                    <tr>
                        <td><strong>Module Name</strong></td>
                        <td><strong>Position in Flow(s)</strong></td>
                    </tr>
                    <c:forEach items="${modules}" var="module">
                    <tr>
                        <td valign="top"><form:checkbox path="modules" value="${module.name}" /> <c:out value="${module.name}"/></td>
                        <td valign="top"><c:out value="${module.description}" escapeXml="false" /></td>
                    </tr>
                    </c:forEach>
                </table>
                </div>
            </li>
          
		    <li>
                <label for="componentName"><fmt:message key="wiretap_events_component"/></label>
		        <form:input path="componentName"/>
            </li>
            <li>
                <label for="eventId"><fmt:message key="wiretap_events_event_id"/></label>
                <form:input path="eventId"/>
            </li>
            <li>
                <label for="payloadId"><fmt:message key="wiretap_events_payload_id"/></label>
			    <form:input path="payloadId"/>
            </li>
            <li>
                <label for="fromDate"><fmt:message key="wiretap_events_from"/></label>
                <form:input path="fromDate" size="10"/>
                <form:input path="fromTime" size="8"/>
            </li>
            <li>
                <label for="untilDate"><fmt:message key="wiretap_events_until"/></label>
                <form:input path="untilDate" size="10"/>
                <form:input path="untilTime" size="8"/>
            </li>
            <li>
                <label for="payloadContent"><fmt:message key="wiretap_events_payload_content"/></label>
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
