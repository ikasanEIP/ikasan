<?xml version="1.0" encoding="UTF-8"?>
<!-- 


 $Id$
 $URL$
 
 =============================================================================
 Ikasan Enterprise Integration Platform
 
 Distributed under the Modified BSD License.
 Copyright notice: The copyright for this software and a full listing 
 of individual contributors are as shown in the packaged copyright.txt 
 file. 
 
 All rights reserved.

 Redistribution and use in source and binary forms, with or without 
 modification, are permitted provided that the following conditions are met:

  - Redistributions of source code must retain the above copyright notice, 
    this list of conditions and the following disclaimer.

  - Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.

  - Neither the name of the ORGANIZATION nor the names of its contributors may
    be used to endorse or promote products derived from this software without 
    specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 =============================================================================

 Author:  Ikasan Development Team
 
-->

<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">


  <xsl:template match="/">
	<businessError>
		<errorMessage>
			An error has occurred on Ikasan
			
			- at the following location:
			<xsl:value-of select="errorOccurrence/moduleName"/>.<xsl:value-of select="errorOccurrence/flowName"/>.<xsl:value-of select="errorOccurrence/flowElementName"/>
			
			- at the following time:
			<xsl:value-of select="errorOccurrence/logTime"/>
			
			- Ikasan took the following action:
			<xsl:value-of select="errorOccurrence/actionTaken"/>
		
			- error detail follows.....
		<xsl:value-of select="errorOccurrence/errorDetail"/></errorMessage>
		<originatingSystem>ikasan</originatingSystem>
		<externalReference><xsl:value-of select="errorOccurrence/url"/></externalReference>
	</businessError>

  </xsl:template>

  
</xsl:stylesheet>