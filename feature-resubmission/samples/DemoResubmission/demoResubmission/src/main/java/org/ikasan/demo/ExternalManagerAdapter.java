/* 
 * $Id$
 * $URL$ 
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.demo;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.ikasan.demo.businesserror.eai.BusinessErrorListener;
import org.ikasan.demo.businesserror.eai.EaiAdapter;
import org.ikasan.demo.businesserror.model.BusinessError;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;

public class ExternalManagerAdapter implements BusinessErrorListener{

	private ExcludedEventService excludedEventService;
	
	private ErrorLoggingService errorLoggingService;
	
	
	private EaiAdapter eaiAdapter;
	
	private Logger logger = Logger.getLogger(ExternalManagerAdapter.class);

	
	public ExternalManagerAdapter(ExcludedEventService excludedEventService,
			ErrorLoggingService errorLoggingService,
			EaiAdapter eaiAdapter) throws ParserConfigurationException {
		super();
		this.excludedEventService = excludedEventService;
		this.errorLoggingService = errorLoggingService;
		this.eaiAdapter = eaiAdapter;
		eaiAdapter.setBusinessErrorListener(this);
		
		

	}

	


	public void onBusinessError(BusinessError businessError) {
		logger.info("called to resubmit error occurrence ["+businessError.getExternalReference()+"]");
		
		
		
		
		
		ErrorOccurrence errorOccurrence = errorLoggingService.getErrorOccurrence(Long.parseLong(businessError.getExternalReference()));
		
		String eventId = errorOccurrence.getEventId();
		
//		ExcludedEvent excludedEvent = excludedEventService.getExcludedEvent(eventId);
//		if (excludedEvent==null){
//			logger.warn("could not find exlcudedEvent for eventId ["+eventId+"]");
//		}
		
		String moduleName = errorOccurrence.getModuleName();
		GrantedAuthority[] authorities = new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_USER"),new GrantedAuthorityImpl("ADMIN_"+moduleName), new GrantedAuthorityImpl("USER_demoResubmission-demoResubmission")};
		Authentication authentication = new AnonymousAuthenticationToken("key", "principal", authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		
		
		excludedEventService.resubmit(eventId, "dummyUser");
		
		
	}

	public void report(String errorReport, String errorId, String resubmissionReference) {
		logger.info("reporting error ["+errorReport+"]");
		
		eaiAdapter.postBusinessError(new BusinessError(null, errorId, errorReport));
		
	
		
	}

	





}
