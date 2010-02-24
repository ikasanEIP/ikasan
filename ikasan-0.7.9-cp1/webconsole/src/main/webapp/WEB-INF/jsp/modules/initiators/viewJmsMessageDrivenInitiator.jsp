<%@ include file="/WEB-INF/jsp/modules/initiators/initiatorTop.jsp"%>


<p>RetryCount: <c:out value="${initiator.retryCount}" /></p>

<h3>Container</h3>

        <table id="messageListenerContainerDetails" class="keyValueTable">
            <tr>
                <th>
                    Running
                </th>
                <td>
                    <c:out value="${initiator.messageListenerContainer.running}" />
                </td>
            </tr>

            <tr>
                <th>
                    Listener Failing
                </th>
                <td>
                    <c:out value="${initiator.messageListenerContainer.listenerSetupFailure}" />
                </td>
            </tr>
            

        </table>






</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>

