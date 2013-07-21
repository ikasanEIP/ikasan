package org.ikasan.demo;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.error.service.ErrorOccurrenceListener;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.event.exclusion.service.ExcludedEventListener;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;

public class DemoErrorReportingService implements ErrorOccurrenceListener,
		ExcludedEventListener {

	private ExternalManagerAdapter externalManagerAdapter;
	


	public DemoErrorReportingService(ErrorLoggingService errorLoggingService,
			ExcludedEventService excludedEventService,
			ExternalManagerAdapter externalManagerAdapter) {
		super();
		this.externalManagerAdapter = externalManagerAdapter;

		errorLoggingService.addErrorOccurrenceListener(this);
	}

	public void notifyErrorOccurrence(ErrorOccurrence errorOccurrence) {
		String errorReport = generateErrorReport(errorOccurrence, null);

		externalManagerAdapter.report(errorReport, errorOccurrence.getId()
				.toString(), null);
	}

	public void notifyExcludedEvent(Event excludedEvent) {
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
