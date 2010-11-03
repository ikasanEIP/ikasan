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
<%@ include file="/WEB-INF/jsp/modules/initiators/initiatorTop.jsp"%>









<h3>Scheduler</h3>

        <table id="schedulerDetails" class="keyValueTable">
            <tr>
                <th>
                    Scheduler Name
                </th>
                <td>
                    <c:out value="${initiator.scheduler.schedulerName}" />
                </td>
            </tr>

            <tr>
                <th>
                    Shutdown
                </th>
                <td>
                    <c:out value="${initiator.scheduler.shutdown}" />
                </td>
            </tr>
            
            <tr>
                <th>
                    In Standby mode
                </th>
                <td>
                    <c:out value="${initiator.scheduler.inStandbyMode}" />
                </td>
            </tr>
            
            <tr>
                <th>
                    Thread Pool Size
                </th>
                <td>
                    <c:out value="${initiator.scheduler.metaData.threadPoolSize}" />
                </td>
            </tr>
            <tr>
                <th>
                    Meta Data Summary
                </th>
                <td>
                    <c:out value="${initiator.scheduler.metaData.summary}" />
                </td>
            </tr>
            
            <%--
             
             <!-- 
                 these fields need to be exposed as proper javabean properties
                 see http://jira.opensymphony.com/browse/QUARTZ-745
              -->
             
             <tr>
                <th>
                    Running Since
                </th>
                <td>
                    <c:out value="${initiator.scheduler.metaData.runningSince}" />
                </td>
            </tr>
            
            <tr>
                <th>
                    No of Jobs Executed
                </th>
                <td>
                    <c:out value="${initiator.scheduler.metaData.numJobsExecuted}" />
                </td>
            </tr> --%>
            
            
        </table>


        <h3>Jobs</h3>
        <table id="schedulerJobsList" class="listTable">
            <thead>
                <tr>
                    <th>Name (Group)</th>
                    <th>Stateful</th>
                    <th>Durable</th>
                    <th>Volatile</th>
                </tr>
            </thead>
        
            <tbody>
                <c:forEach items="${jobs}" var="job">
                    <tr>
                        <td>
                            <c:out value="${job.name}" /> <br/>(<i><c:out value="${job.group}" /></i>)
                        </td>           
                        <td>
                            <c:out value="${job.stateful}" />
                        </td>
                        <td>
                             <c:out value="${job.durable}" />
                        </td>
                        <td>
                             <c:out value="${job.volatile}" />
                        </td>
                    </tr>
                </c:forEach>
        
            </tbody>
        
        
        </table>    







        <h3>Triggers</h3>
        <table id="schedulerTriggersList" class="listTable">
            <thead>
                <tr>
                    <th>Name (Group)</th>
                    <th>Job Name</th>
                    <th>Next Fire Time</th>
                    <th>Previous Fire Time</th>
                    <th>State</th>
                </tr>
            </thead>
        
            <tbody>
                <c:forEach items="${triggers}" var="triggerEntry">
                    <tr>
                        <td>
                            <c:out value="${triggerEntry.key.name}" /> <br/>(<i><c:out value="${triggerEntry.key.group}" /></i>)
                        </td>           
                        <td>
                            <c:out value="${triggerEntry.key.jobName}" />
                        </td>
                        <td>
                            <fmt:formatDate value="${triggerEntry.key.nextFireTime}"
                                        pattern="dd/MM/yyyy h:mma"/>
                        </td>
                        <td>
                            <fmt:formatDate value="${triggerEntry.key.previousFireTime}"
                                        pattern="dd/MM/yyyy h:mma"/>
                        </td>
                        <td>
                            <c:out value="${triggerEntry.value}" />
                        </td>
                    </tr>
                </c:forEach>
        
            </tbody>
        
        
        </table>    



<%@ include file="/WEB-INF/jsp/modules/initiators/initiatorBottom.jsp"%>
