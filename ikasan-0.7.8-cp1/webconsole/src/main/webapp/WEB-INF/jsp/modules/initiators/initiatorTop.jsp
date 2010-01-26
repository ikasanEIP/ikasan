<%@ include file="/WEB-INF/jsp/modules/modulesTop.jsp"%>


<div class="middle">

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

<h2><c:out value="${initiator.name}" /></h2>


