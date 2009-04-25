<%@ include file="/WEB-INF/jsp/top.jsp"%>
<!-- 
# //
# //
# // $Id: modulesTop.jsp 16798 2009-04-24 14:12:09Z mitcje $
# // $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/main/webapp/WEB-INF/jsp/modules/modulesTop.jsp $
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
<div class="subnavcontainer">
    <c:if test="${moduleName != null}">
	<ul>
		
		<li>
            <a href="<c:url value="view.htm">
                <c:param name="moduleName" value="${moduleName}"/>
                     </c:url>">
                    <c:out value="${moduleName}" />
            </a>

		</li>
		<c:if test="${flowName != null}">
		  <c:url var="flowLink" value="viewFlow.htm">
            <c:param name="moduleName" value="${moduleName}"/>
            <c:param name="flowName" value="${flowName}"/>
          </c:url>
		
			<li> -> 
                <a href="<c:out value="${flowLink}" escapeXml="true" />">
                        <c:out value="${flowName}" />
                </a>
			</li>
		</c:if>
		<c:if test="${flowElement != null}">
            <c:url var="flowElementLink" value="viewFlowElement.htm">
                <c:param name="moduleName" value="${moduleName}"/>
                <c:param name="flowName" value="${flowName}"/>
                <c:param name="flowElementName" value="${flowElementName}"/>
            </c:url>
			<li> -> 
                <a href="<c:out value="${flowElementLink}" escapeXml="true" />">
                        <c:out value="${flowElementName}" />
                </a>				
			</li>
		</c:if>
	</ul>
	</c:if>
</div>