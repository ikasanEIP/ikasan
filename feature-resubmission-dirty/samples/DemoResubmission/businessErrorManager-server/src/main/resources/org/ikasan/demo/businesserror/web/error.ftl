<#include "top.ftl">

<h1>Error Message</h1>
<p id="errorId">
	Id: ${error.id}
</p>
<p id="externalReference">
	External Reference: ${error.externalReference}
</p>
<p id="errorSummary">
	${error.errorSummary}
</p>
<p id="errorMessage">
	${error.errorMessage}
</p>

<p>
<a href="./errors.htm">Back to errors</a>
</p>

<#include "bottom.ftl">