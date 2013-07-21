package org.ikasan.demo;

import java.util.Date;



import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.endpoint.Endpoint;
import org.ikasan.framework.component.endpoint.EndpointException;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;



/**
 * Endpoint that checks the time, then logs the event if it is an even minute past the hour
 * otherwise, on odd minutes past the hour throws an UntimelyEventException
 * 
 * @author The Ikasan Development Team
 *
 */
public class TimeDependentExceptionEndpoint implements Endpoint {

	
	private Logger logger = Logger.getLogger(TimeDependentExceptionEndpoint.class);
	

	public void onEvent(Event event) throws EndpointException {
		long currentTimeMillis = System.currentTimeMillis();
		long minsSince1970 = currentTimeMillis / 60000l;
		
		long modValue = minsSince1970 % 2;
		logger.info("currentTimeMillis:"+currentTimeMillis);
		logger.info("minsSince1970:"+minsSince1970);
		logger.info("modValue:"+modValue);
		
		if (modValue > 0){
			logger.info("its not a good time right now:"+event);
			throw new UntimelyEventException("Its not a good time right now, try again later...");
		} else{
			logger.info("dumped event:"+event);
		}

	}

}
