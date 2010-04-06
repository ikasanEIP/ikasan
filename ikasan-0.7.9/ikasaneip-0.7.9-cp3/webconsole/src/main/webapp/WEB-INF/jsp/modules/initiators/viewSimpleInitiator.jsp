<%@ include file="/WEB-INF/jsp/modules/initiators/initiatorTop.jsp"%>






<p>
<c:out value="${initiationResult}"/>
</p>

<form:form action="simpleInitiatorPost.htm"  commandName="payloadCommand" >

    <form:textarea cols="80" rows="10" path="payloadContent"/>
        <input type="submit" value="Execute" class="controlButton"/>
    </form:form>


</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
