<#include "top.ftl">

<div id="messageDetailPage" class="pageBody">
<div id="errorsPage" class="pageBody">
<h1>Error Message</h1>
<p id="errorId">
	Id: ${error.id}
</p>
<p id="externalReference">
	External Reference: ${error.externalReference!"none"}
</p>
<p id="errorSummary">
	${error.errorSummary!"none"}
</p>
<p id="errorMessage">
	${error.errorMessage!"none"}
</p>

<p>
<a href="./errors.htm">Back to errors</a>
</p>


</div> <!--end page body-->


<#include "bottom.ftl">