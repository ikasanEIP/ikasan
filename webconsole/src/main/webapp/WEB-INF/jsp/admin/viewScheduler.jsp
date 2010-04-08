<%@ include file="/WEB-INF/jsp/admin/adminTop.jsp"%>
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

    <h2>Platform Scheduler</h2>

    <c:choose>
        <c:when test="${platformScheduler.inStandbyMode}">
            <c:url var="schedulerAction" value="schedulerResume.htm" />
        </c:when>
        <c:otherwise>
            <c:url var="schedulerAction" value="schedulerStandby.htm" />
        </c:otherwise>
    </c:choose>
    
    <form method="post" id="schedulerForm" action="${schedulerAction}">

        <h3>Details</h3>
        <table id="schedulerDetails" class="keyValueTable">
            <tr>
                <th>Status</th>
                <td>
                    <c:choose>
                        <c:when test="${platformScheduler.inStandbyMode}">
                            Paused
                        </c:when>
                        <c:otherwise>
                            Running
                        </c:otherwise>
                    </c:choose>                
                </td>
                <td>
                    <c:choose>
                        <c:when test="${platformScheduler.inStandbyMode}">
                            <input type="submit" value="Resume Scheduler" class="controlButton" />
                        </c:when>
                        <c:otherwise>
                            <input type="submit" value="Pause Scheduler" class="controlButton" />
                        </c:otherwise>
                    </c:choose>                
                </td>
            </tr>
        </table>

        <h3>Triggers</h3>
        <table id="schedulerTriggersList" class="listTable">
           <thead>
                <tr>
                    <th>Name</th>
                    <th>Job Name</th>
                    <th>Next Fire Time</th>
                    <th>Previous Fire Time</th>
                </tr>
            </thead>
        
            <tbody>
            <c:forEach items="${triggers}" var="trigger">
                <tr>
                    <td><c:out value="${trigger.name}" /></td>          
                    <td><c:out value="${trigger.jobName}" /></td>
                    <td><fmt:formatDate value="${trigger.nextFireTime}" pattern="dd/MM/yyyy h:mma"/></td>
                    <td><fmt:formatDate value="${trigger.previousFireTime}" pattern="dd/MM/yyyy h:mma"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
