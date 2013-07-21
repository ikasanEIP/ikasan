<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<body>
	<h1>Modules</h1>
	
    <c:forEach items="${moduleMap}" var="module">
        <b>${module.key}</b><br/>
        <table border="1">
        <tr><th>Flow</th><th>State</th></tr>
        <c:forEach items="${module.value}" var="flow">
            <tr><td>${flow.key}</td><td>${flow.value}</td></tr>
        </c:forEach>
        </table>
    </c:forEach>
</body>
</html>