/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.initiator;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.service.EventProvider;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.monitor.MonitorSubject;

/**
 * Ikasan Abstract Initiator implementation.
 * 
 * @author Ikasan Development Team
 */
public abstract class AbstractInvocationDrivenInitiator extends AbstractInitiator implements InvocationDrivenInitiator, MonitorSubject
{

    
    /** Logger */
    private static Logger logger = Logger.getLogger(AbstractInvocationDrivenInitiator.class);

    /**
     * TODO - use the exception handler in the flow rather than having specific reference in the initiator
     */
    private IkasanExceptionHandler exceptionHandler;
    
    /**
     * Used for sourcing the Events that will be played
     */
    private EventProvider eventProvider;

    

    /**
     * Constructor
     * 
     * @param name Name of the initiator
     * @param name of the Module
     * @param flow The flow leading off from the initiator
     * @param exceptionHandler The exceptionHandler associated with the initiator
     * @param eventProvider used for sourcing the Events that will be played
     */
    public AbstractInvocationDrivenInitiator(String name, String moduleName, Flow flow, IkasanExceptionHandler exceptionHandler, EventProvider eventProvider)
    {
        super(moduleName, name, flow);
        this.exceptionHandler = exceptionHandler;
        this.eventProvider = eventProvider;
        notifyMonitorListeners();
    }


    /**
     * Return the exception handler for this initiator
     * 
     * @return exceptionHandler
     */
    protected IkasanExceptionHandler getExceptionHandler()
    {
        return this.exceptionHandler;
    }


    /**
     * Standard invocation of an initiator.
     */
    public void invoke()
    {
        if (stopping)
        {
            logger.warn("Attempt to invoke an initiator in a stopped state.");
            return;
        }
        
    	List<Event> events = null;
        try
        {
            events = eventProvider.getEvents();
            
        }
        catch (Throwable eventSourcingThrowable)
        {
        	handleEventSourcingThrowable(eventSourcingThrowable);
        }
        
        // invoke flow all the time we have event activity
        invokeFlow(events);
    }


	/**
	 * Handles Errors that occur whilst attempting to source the Event(s) to play
	 * 
	 * @param eventSourcingThrowable
	 */
	protected void handleEventSourcingThrowable(Throwable eventSourcingThrowable) {
		if (errorLoggingService!=null){
			errorLoggingService.logError(eventSourcingThrowable, moduleName, name);
		}
		handleAction(exceptionHandler.invoke(name, eventSourcingThrowable));
	}



    /**
     * Invoke the flow with each of the sourced Events
     */
    protected abstract void invokeFlow(List<Event> events);



    

}
