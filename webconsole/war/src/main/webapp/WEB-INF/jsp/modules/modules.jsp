<%@ include file="/WEB-INF/jsp/modules/modulesTop.jsp"%>
<!-- 
# //
# //
# // $Id: modules.jsp 16798 2009-04-24 14:12:09Z mitcje $
# // $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/main/webapp/WEB-INF/jsp/modules/modules.jsp $
# // 
# // ====================================================================
# // Ikasan Enterprise Integration Platform
# // Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
# // by the @authors tag. See the copyright.txt in the distribution for a
# // full listing of individual contributors.
# //
# // This is free software; you can redistribute it and/or modify it
# // under the terms of the GNU Lesser General Public License as
# // published by the Free Software Foundation; either version 2.1 of
# // the License, or (at your option) any later version.
# //
# // This software is distributed in the hope that it will be useful,
# // but WITHOUT ANY WARRANTY; without even the implied warranty of
# // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# // Lesser General Public License for more details.
# //
# // You should have received a copy of the GNU Lesser General Public
# // License along with this software; if not, write to the 
# // Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
# // or see the FSF site: http://www.fsfeurope.org/.
# // ====================================================================
# //
# // Author:  Ikasan Development Team
# // 
-->
<div class="middle">

<h2>Modules</h2>

		<h3>Configured Modules</h3>
		<table id="modulesList" class="listTable">
			<thead>
				<tr>
					<th>Name</th>
					<th>Description</th>
				</tr>
			</thead>
		
			<tbody>
				<c:forEach items="${modules}" var="module">
					<tr>
						<td>
							<a href="view.htm?moduleName=<c:out value="${module.name}" />">
								<c:out value="${module.name}" />
							</a>
						</td>
						<td>
                            <c:out value="${module.description}" />
                        </td>
					</tr>
				</c:forEach>
		
			</tbody>
		
		
		</table>

</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>
