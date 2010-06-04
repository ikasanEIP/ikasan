<%@ include file="/WEB-INF/jsp/modules/modulesTop.jsp"%>


<div class="middle">


<h2><c:out value="${initiator.name}" /></h2>


<div id="initiatorControlBar">





<div id="initiatorControl">


<p>Current status is:</p>
<span id="initiatorStateControl" class="initiatorState-<c:out value="${initiator.state.name}" />"><c:out value="${initiator.state.name}" /></span>

    <security:authorize ifAllGranted="ADMIN_${moduleName}">
        <c:choose>
            <c:when test="${initiator.running}">
              <c:url var="initiatorLink" value="initiator.htm">
                <c:param name="moduleName" value="${moduleName}"/>
                <c:param name="initiatorName" value="${initiator.name}"/>
                <c:param name="initiatorAction" value="stop"/>
              </c:url>
              <form:form action="${initiatorLink}" method="post">
                <input type="submit" value="Stop" class="controlButton"/>
              </form:form>
            </c:when>
            <c:otherwise>
              <c:url var="initiatorLink" value="initiator.htm">
                <c:param name="moduleName" value="${moduleName}"/>
                <c:param name="initiatorName" value="${initiator.name}"/>
                <c:param name="initiatorAction" value="start"/>
              </c:url>
                <form:form action="${initiatorLink}" method="post">
                    <input type="submit" value="Start" class="controlButton"/>
                </form:form>

            </c:otherwise>
        </c:choose>
    </security:authorize>  

</div>








<p>

 <security:authorize ifAllGranted="ROLE_ADMIN">
 <c:url var="initiatorLink" value="initiator.htm">
                <c:param name="moduleName" value="${moduleName}"/>
                <c:param name="initiatorName" value="${initiator.name}"/>
              </c:url>
<form action="${initiatorLink}" method="post">
	Startup Type : <select name="startupType">
		<option value="MANUAL" <c:if test="${startupControl.manual}">selected="selected" </c:if>  >Manual</option>
		<option value="AUTOMATIC" <c:if test="${startupControl.automatic}">selected="selected" </c:if>  >Automatic</option>
		<option value="DISABLED" <c:if test="${startupControl.disabled}">selected="selected" </c:if>  >Disabled</option>
	</select>

	Comment : <input name="startupComment" value="${startupControl.comment}" type="text">

	<input type="submit" value="Update" class="controlButton"/>
</security:authorize>

<security:authorize ifNotGranted="ROLE_ADMIN">
Startup Type : ${startupControl.startupType}
</security:authorize>

</p>
</form>
    


</div> 




