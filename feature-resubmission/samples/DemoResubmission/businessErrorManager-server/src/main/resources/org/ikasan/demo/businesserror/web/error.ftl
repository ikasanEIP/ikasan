<#include "top.ftl">

<div id="messageDetailPage" class="pageBody">
<div id="errorsPage" class="pageBody">
<h1>Error Message</h1>

<p id="externalReference" >
	<span class="keyLabel">External Reference:</span> 
	<a href="${error.externalReference!"none"}">${error.externalReference!"none"}</a>
</p>
<p id="errorSummary" >
	<span class="keyLabel">Summary:</span>
	${error.errorSummary!"none"}
</p>
<p class="keyLabel">Message Detail</p>
<p id="errorMessage">
	${error.errorMessage!"none"}
</p>

<p>
<a href="./errors.htm">Back to errors</a>
</p>


</div> <!--end page body-->


<#include "bottom.ftl">