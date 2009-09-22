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

  <link type="text/css" href="<c:url value='/css/jquery/redmond/jquery-ui-1.7.2.custom.css'/>" rel="stylesheet" />
  <script type="text/javascript" src="<c:url value='/js/jquery-ui-1.7.2.custom.min.js'/>"></script>
  <script type="text/javascript">

      /* jquery date picker assistance */
      $(document).ready(function()
      {
        $("#fromDateString").datepicker({dateFormat: 'dd/mm/yy' });
        $("#untilDateString").datepicker({dateFormat: 'dd/mm/yy' });
        
        $("#fromDateString").change(function() {
         $("#fromTimeString").val('00:00:00');
        });
        
        $("#untilDateString").change(function() {
         $("#untilTimeString").val('00:00:00');
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
            if(theForm[z].type == 'checkbox' && theForm[z].name != '(de)select all' && theForm[z].name != 'orderAsc')
            {
                theForm[z].checked = theElement.checked;
            }
        }
    }

    /* 
     * A function to show/hide the search form section.
     */
    function showHideSearchForm() {
        if (document.getElementById('searchForm').style.display == "")
        {
            document.getElementById('searchForm').style.display = "none";
        }
        else
        {
            document.getElementById('searchForm').style.display = "";
        }
    }

    /* 
     * A function to encode the data going down to the server.
     */
    function encodeSearch() {

        for (var i=0; i < document.wiretapSearchForm.pointToPointFlowProfileNames.length; i++)
        {
            if (document.wiretapSearchForm.pointToPointFlowProfileNames[i].checked)
                {
                    document.wiretapSearchForm.pointToPointFlowProfileNames[i].value = escape(document.wiretapSearchForm.pointToPointFlowProfileNames[i].value); 
                }
            }
        }
        
        document.wiretapSearchForm.submit();
        /*
        alert(document.getElementById("pointToPointFlowProfileNames").value);
        document.getElementById("pointToPointFlowProfileNames").value = escape(document.getElementById("pointToPointFlowProfileNames").value);
        alert(document.getElementById("pointToPointFlowProfileNames").value); 
        document.getElementById('wiretapSearchForm').pointToPointFlowProfileNames.value = escape(document.getElementById('wiretapSearchForm').pointToPointFlowProfileNames.value);
        alert(document.getElementById('wiretapSearchForm').pointToPointFlowProfileNames.value);
        document.getElementById('wiretapSearchForm').submit();
        */
        // window.location = window.location + "?nickname=" + nickname + "&password=" + password;
    }

  </script>

<div class="middle">
    <a href="javascript:showHideSearchForm()">Show/Hide Search Form</a>
    <div id="searchForm">
    <form method="get" id="wiretapSearchForm" name="wiretapSearchForm" action="javascript:encodeSearch()" class="dataform fancydataform">

        <c:if test="${errors != ''}">
            <!-- The list of errors -->
            <c:forEach items="${errors}" var="error">
                <span class="errorMessages"><c:out value="${error}" /></span><br>
            </c:forEach>
        </c:if>

        <!-- The Search criteria for the user to search with -->
        <fieldset>
            <legend><fmt:message key="wiretap_events_search"/></legend>
            <ol>
                <li>
                    <label for="pointToPointFlowProfiles"><fmt:message key="wiretap_events_pointToPointFlowProfile"/></label>
                    
                    <input type="checkbox" name="selectAll" <c:if test="${selectAll == 'true'}">checked="checked"</c:if> onclick="checkUncheckAll(this);"/> (de)select all

                    <!-- PointToPointProfile Checkboxes -->
                    <div id="eventSearchPointToPointFlowProfileCheckboxes" class="multiSelectCheckboxes">            
                        <table id="wiretapSearch" class="searchTable">
                            <thead>
                                <tr>
                                    <td><fmt:message key="wiretap_event_pointToPointFlowProfile_name"/></td>
                                </tr>
                            <thead>
                            <tbody>
                                <c:forEach items="${pointToPointFlowProfiles}" var="pointToPointFlowProfile">
                                    <tr>
                                        <td class="border" valign="top">
                                            <c:out value="${pointToPointFlowProfile.name}" /> :: 
                                            <c:forEach items="${searchParams['pointToPointFlowProfileNames']}" var="pointToPointFlowProfileName">
                                                <c:out value="${pointToPointFlowProfileName}" />
                                            </c:forEach>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="border" valign="top"><input id="pointToPointFlowProfileNames" name="pointToPointFlowProfileNames" type="checkbox" value="${pointToPointFlowProfile.name}" <c:forEach items="${searchParams['pointToPointFlowProfileNames']}" var="pointToPointFlowProfileName"><c:if test="${pointToPointFlowProfile.name == pointToPointFlowProfileName}">checked="checked"</c:if></c:forEach> /> <c:out value="${pointToPointFlowProfile.name}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="border" valign="top" style="font-size : 8px;">
                                            <c:forEach items="${pointToPointFlowProfile.pointToPointFlows}" var="pointToPointFlow">                                    
                                                <c:out value="${pointToPointFlow.fromModule.name}" /> --&gt; <c:out value="${pointToPointFlow.toModule.name}" />
                                            </c:forEach>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </li>

                <!--
                <li>
                    <label for="modules"><fmt:message key="wiretap_events_module"/></label>
                    <input type="checkbox" name="selectAll" <c:if test="${selectAll == 'true'}">checked="checked"</c:if> onclick="checkUncheckAll(this);"/> (de)select all
                    <div id="eventSearchModuleCheckboxes" class="multiSelectCheckboxes">            
                    <table id="wiretapSearch" class="searchTable">
                        <thead>
                            <tr>
                                <td><fmt:message key="wiretap_event_module_name"/></td>
                                <td><fmt:message key="wiretap_event_module_position_in_flows"/></td>
                            </tr>
                        <thead>
                        <tbody>
                            <c:forEach items="${modules}" var="module">
                                <tr>
                                    <td class="border" valign="top"><input name="moduleNames" type="checkbox" value="${module.name}" <c:forEach items="${searchParams['moduleNames']}" var="moduleName"><c:if test="${module.name == moduleName}">checked="checked"</c:if></c:forEach> /> <c:out value="${module.name}"/></td>
                                    <td class="border" valign="top"><c:out value="${module.description}" escapeXml="false" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    </div>
                </li>
                -->
                <li>
                    <label for="componentName"><fmt:message key="wiretap_events_component"/></label>
                    <input id="componentName" type="text" name="componentName" value="${searchParams["componentName"]}"/>
                </li>
                <li>
                    <label for="eventId"><fmt:message key="wiretap_events_event_id"/></label>
                    <input id="eventId" type="text" name="eventId" value="${searchParams["eventId"]}"/>
                </li>
                <li>
                    <label for="payloadId"><fmt:message key="wiretap_events_payload_id"/></label>
                    <input id="payloadId" type="text" name="payloadId" value="${searchParams["payloadId"]}"/>
                </li>
                <li>
                    <label for="fromDate"><fmt:message key="wiretap_events_from"/></label>
                    <input id="fromDateString" type="text" name="fromDateString" size="10" value="${searchParams["fromDateString"]}"/>
                    <input id="fromTimeString" type="text" name="fromTimeString" size="8" value="${searchParams["fromTimeString"]}"/>
                </li>
                <li>
                    <label for="untilDate"><fmt:message key="wiretap_events_until"/></label>
                    <input id="untilDateString" type="text" name="untilDateString" size="10" value="${searchParams["untilDateString"]}"/>
                    <input id="untilTimeString" type="text" name="untilTimeString" size="8" value="${searchParams["untilTimeString"]}"/>
                </li>
                <li>
                    <label for="payloadContent"><fmt:message key="wiretap_events_payload_content"/></label>
                    <input id="payloadContent" type="text" name="payloadContent" value="${searchParams["payloadContent"]}" />
                </li>
                <li>
                    <label for="orderBy">Order By</label>
                    <select id="orderBy" name="orderBy">
                        <option value="id" <c:if test="${orderBy=='id'}">selected="selected"</c:if>>Id</option>
                        <option value="moduleName" <c:if test="${orderBy=='moduleName'}">selected="selected"</c:if>>Module</option>                 
                        <option value="flowName" <c:if test="${orderBy=='flowName'}">selected="selected"</c:if>>Flow</option>
                        <option value="componentName" <c:if test="${orderBy=='componentName'}">selected="selected"</c:if>>Component</option>
                        <option value="eventId" <c:if test="${orderBy=='eventId'}">selected="selected"</c:if>>Event Id</option>
                        <option value="payloadId" <c:if test="${orderBy=='payloadId'}">selected="selected"</c:if>>Payload Id</option>
                        <option value="created" <c:if test="${orderBy=='created'}">selected="selected"</c:if>>Created Date/Time</option>
                    </select>
                    <label for="orderAsc">Ascending</label>
                    <input id="orderAsc" type="checkbox" name="orderAsc" <c:if test="${orderAsc=='true'}">checked="checked"</c:if>/>
                </li>
            </ol>
        </fieldset>
        <p>
            <input type="submit" value="Search for Events" class="controlButton"/>
        </p>
    </form>
    </div>

    <%@ include file="/WEB-INF/jsp/pagedResultsHeader.jsp"%>
    
    <c:url var="idLink" value="list.htm">
        <c:forEach var="entry" items="${searchParams}">
            <c:forEach var="entryValue" items="${entry.value}">
               <c:param name="${entry.key}" value="${entryValue}"/>
            </c:forEach>
        </c:forEach>
       	<c:param name="orderBy" value="id"/>
       	<c:param name="orderAsc" value="${!orderAsc}"/>
        <c:param name="selectAll" value="${selectAll}"/>       	
        <c:param name="pageSize" value="${pageSize}"/>
    </c:url>

    <c:url var="moduleLink" value="list.htm">
        <c:forEach var="entry" items="${searchParams}">
            <c:forEach var="entryValue" items="${entry.value}">
               <c:param name="${entry.key}" value="${entryValue}"/>
            </c:forEach>
        </c:forEach>
       	<c:param name="orderBy" value="moduleName"/>
       	<c:param name="orderAsc" value="${!orderAsc}"/>
        <c:param name="selectAll" value="${selectAll}"/>       	
        <c:param name="pageSize" value="${pageSize}"/>
    </c:url>

    <c:url var="flowLink" value="list.htm">
        <c:forEach var="entry" items="${searchParams}">
            <c:forEach var="entryValue" items="${entry.value}">
               <c:param name="${entry.key}" value="${entryValue}"/>
            </c:forEach>
        </c:forEach>
       	<c:param name="orderBy" value="flowName"/>
       	<c:param name="orderAsc" value="${!orderAsc}"/>
        <c:param name="selectAll" value="${selectAll}"/>       	
        <c:param name="pageSize" value="${pageSize}"/>
    </c:url>

    <c:url var="componentLink" value="list.htm">
        <c:forEach var="entry" items="${searchParams}">
            <c:forEach var="entryValue" items="${entry.value}">
               <c:param name="${entry.key}" value="${entryValue}"/>
            </c:forEach>
        </c:forEach>
        <c:param name="orderBy" value="componentName"/>
        <c:param name="orderAsc" value="${!orderAsc}"/>
        <c:param name="selectAll" value="${selectAll}"/>
        <c:param name="pageSize" value="${pageSize}"/>
    </c:url>

    <c:url var="createdDateTimeLink" value="list.htm">
        <c:forEach var="entry" items="${searchParams}">
            <c:forEach var="entryValue" items="${entry.value}">
               <c:param name="${entry.key}" value="${entryValue}"/>
            </c:forEach>
        </c:forEach>
        <c:param name="orderBy" value="created"/>
        <c:param name="orderAsc" value="${!orderAsc}"/>
        <c:param name="selectAll" value="${selectAll}"/>        
        <c:param name="pageSize" value="${pageSize}"/>
    </c:url>

    <c:if test="${resultSize > 0}">
        <table id="wiretapSearchResults" class="listTable">
            <thead>
                <tr>
                    <th><a href="<c:out value="${idLink}#results" escapeXml="true" />"><fmt:message key="wiretap_event_results_id"/></a></th>
                    <th><a href="<c:out value="${moduleLink}#results" escapeXml="true" />"><fmt:message key="wiretap_event_results_module"/></th>
                    <th><a href="<c:out value="${flowLink}#results" escapeXml="true" />"><fmt:message key="wiretap_event_results_flow"/></th>
                    <th><a href="<c:out value="${componentLink}#results" escapeXml="true" />"><fmt:message key="wiretap_event_results_component"/></th>
                    <th><a href="<c:out value="${createdDateTimeLink}#results" escapeXml="true" />"><fmt:message key="wiretap_event_results_created_date_time"/></a></th>
                </tr>
            </thead>

            <tbody>
                <c:forEach items="${results}" var="event">
                    <c:url var="viewEventLink" value="viewEvent.htm">
                        <c:param name="eventId" value="${event.id}"/>
                        <c:param name="searchResultsUrl" value="${searchResultsUrl}"/>
                    </c:url>
                    <tr>
                        <td><a href="<c:out value="${viewEventLink}" escapeXml="true" />"><c:out value="${event.id}" /></a></td>           
                        <td><c:out value="${event.moduleName}" /></td>
                        <td><c:out value="${event.flowName}" /></td>
                        <td><c:out value="${event.componentName}" /></td>
                        <td><c:out value="${event.created}" /></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

    </c:if>

</div>

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
