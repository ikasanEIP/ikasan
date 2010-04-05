<%@ include file="/WEB-INF/jsp/modules/initiators/initiatorTop.jsp"%>







<p>RetryCount: <c:out value="${initiator.retryCount}" /></p>

<c:if test="${initiator.state.recovering}">
</c:if>

<h3>Scheduler</h3>
<p><c:out value="${initiator.scheduler.metaData.summary}" /></p>
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
                    Paused
                </th>
                <td>
                    <c:out value="${initiator.scheduler.paused}" />
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



</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
