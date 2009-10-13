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

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.error.service.ErrorOccurrenceListener;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.event.exclusion.service.ExcludedEventListener;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;

public class DemoErrorReportingService implements ErrorOccurrenceListener,
		ExcludedEventListener {

	private Logger logger = Logger.getLogger(DemoErrorReportingService.class);
	
	private ExternalManagerAdapter externalManagerAdapter;
	


	public DemoErrorReportingService(ErrorLoggingService errorLoggingService,
			ExcludedEventService excludedEventService,
			ExternalManagerAdapter externalManagerAdapter) {
		super();
		this.externalManagerAdapter = externalManagerAdapter;

		errorLoggingService.addErrorOccurrenceListener(this);
	}

	public void notifyErrorOccurrence(ErrorOccurrence errorOccurrence) {
		logger.info("notified with ["+errorOccurrence+"]");
		String errorReport = generateErrorReport(errorOccurrence, null);

		externalManagerAdapter.report(errorReport, errorOccurrence.getId()
				.toString(), null);
	}

	public void notifyExcludedEvent(Event excludedEvent) {
		logger.info("notified with ["+excludedEvent+"]");
//		errorL
//		
//		String errorReport = generateErrorReport(errorOccurrence, excludedEvent);
	}
	
	
	private String generateErrorReport(ErrorOccurrence errorOccurrence,
			ExcludedEvent excludedEvent) {
		StringBuffer stringBuffer = new StringBuffer();
		
		stringBuffer.append("An error occurred in the following Ikasan location:");
		stringBuffer.append(errorOccurrence.getModuleName());
		stringBuffer.append(".");
		stringBuffer.append(errorOccurrence.getFlowName());
		stringBuffer.append(".");
		stringBuffer.append(errorOccurrence.getFlowElementName());
		stringBuffer.append(", when invoked by Initiator:"+errorOccurrence.getInitiatorName());

		
		stringBuffer.append("\n\n");
		stringBuffer.append("Detailed error message follows:");
		stringBuffer.append(errorOccurrence.getErrorDetail());
		
		if (excludedEvent!=null){
			stringBuffer.append("\n\n");
			stringBuffer.append("The following event was excluded by the system, and is available  for resubmission (retry) [");
			stringBuffer.append(excludedEvent.getEvent());
			stringBuffer.append("]");
		}
		

		return stringBuffer.toString();
	}





}
