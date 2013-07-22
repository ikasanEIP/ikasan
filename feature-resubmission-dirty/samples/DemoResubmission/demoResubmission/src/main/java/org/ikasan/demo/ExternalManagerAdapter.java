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
		
		ExcludedEvent excludedEvent = excludedEventService.getExcludedEvent(eventId);
		if (excludedEvent==null){
			logger.warn("could not find exlcudedEvent for eventId ["+eventId+"]");
		}
		
		excludedEventService.resubmit(excludedEvent.getId());
		
		
	}

	public void report(String errorReport, String errorId, String resubmissionReference) {
		logger.info("reporting error ["+errorReport+"]");
		
		eaiAdapter.postBusinessError(new BusinessError(null, errorId, errorReport));
		
	
		
	}

	





}
