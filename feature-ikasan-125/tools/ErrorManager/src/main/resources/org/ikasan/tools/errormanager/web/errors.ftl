<#include "top.ftl">

<div id="messageListPage" class="pageBody">
<h1>Error Messages</h1>
<table id="messages">
	<th>Id</th>
	<th>Summary</th>
	<th>Originating System</th>
	<th>Time Received</th>
	<th>&nbsp;</th>
<#list errors as error>

	<tr>
		<td><a href="error.htm?errorId=${error.id}">${error.id}</a></td>
		<td>${error.errorSummary!"none"}</td>
		<td>${error.originatingSystem!"none"}</td>
		<td>${error.timeReceived?string("yyyy-MM-dd hh:mm:ss a")!"none"}</td>
		<td>
			<#if error.resubmittable>
				<form action="requestResubmission.htm?errorId=${error.id}" method="post">
					<input type="submit" value="Request Resubmission" />
				</form>
			</#if>
		</td>	
	</tr>
</#list>  
</table>

</div> <!--end page body-->

<#include "bottom.ftl">