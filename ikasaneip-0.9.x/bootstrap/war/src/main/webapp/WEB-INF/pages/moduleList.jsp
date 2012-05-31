<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<body>
	<h1>Modules</h1>
	
    <c:forEach items="${moduleMap}" var="module">
        <b>Module</b> [${module.key}] <br/>
        <c:forEach items="${module.value}" var="flow">
            <&nbsp/><&nbsp/><&nbsp/><i>Flow<i/> [${flow.key}] [$flow.value}] <br/>
        </c:forEach>
    </c:forEach>
</body>
</html>