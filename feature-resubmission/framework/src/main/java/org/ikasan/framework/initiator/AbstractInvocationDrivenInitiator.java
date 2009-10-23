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
package org.ikasan.framework.initiator;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.service.EventProvider;
import org.ikasan.framework.exception.IkasanExceptionAction;
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
        super(moduleName, name, flow, exceptionHandler);
        this.eventProvider = eventProvider;
        notifyMonitorListeners();
    }



    /**
     * Standard invocation of an initiator.
     */
    public void invoke()
    {
    	logger.info("called");
        if (stopping)
        {
            logger.warn("Attempt to invoke an initiator in a stopped state.");
            return;
        }
        
        List<Event> events = null;
        try{
        	events = eventProvider.getEvents();
        }catch (Throwable eventSourcingThrowable)
        {
        	//tell the error service
    		logError(null, eventSourcingThrowable, name);

        	handleAction(exceptionHandler.handleThrowable(name, eventSourcingThrowable),null);
        }
        
        // invoke flow all the time we have event activity
        invokeFlow(events);
    }


    

}
