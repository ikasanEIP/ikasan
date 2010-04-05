<#include "top.ftl">

<h1>Error Messages</h1>
<table>
	<th>Id</th>
	<th>Summary</th>
	<th>External Reference</th>
	<th>Originating System</th>
	<th>&nbsp;</th>
<#list errors as error>

	<tr>
		<td><a href="error.htm?errorId=${error.id}">${error.id}</a></td>
		<td>${error.errorSummary}</td>
		<td>${error.externalReference}</td>
		<td>${error.originatingSystem}</td>
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

<#include "bottom.ftl">