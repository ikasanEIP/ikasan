/* 
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.configuration.service.ConfigurationException;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.ikasan.framework.exception.ExcludeEventAction;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.RetryAction;
import org.ikasan.framework.exception.StopAction;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.invoker.FlowInvocationContext;
import org.ikasan.framework.monitor.MonitorListener;

/**
 * Abstract base class for all existing <code>Initiator</code> implementations
 * 
 * Provides implementations for the common <code>Initiator</code> functionality:
 * <ul>
 *     <li>Monitor registration and deregistration</li>
 *     <li>Action handling as a result of flow invocation</li>
 *     <li>Monitor notification</li>
 *     <li>Property access for:
 *         <ol>
 *             <li>ModuleName</li>
 *             <li>(Initiator)Name</li>
 *             <li>Flow</li>
 *             <li>error flag</li>
 *             <li>retry count</li>
 *             <li>stopping flag</li>
 *         </ol>
 *     </li>
 * </ul>
 * 
 * @author Ikasan Development Team
 *
 */
public abstract class AbstractInitiator implements Initiator
{
    
    /** Exception action is an implied rollback message */
    public static final String EXCEPTION_ACTION_IMPLIED_ROLLBACK = "Exception Action implied rollback";
    
    /** Exception action is an implied rollback message */
    public static final String UNSUPPORTED_EXCLUDE_ENCONTERED = "Unsupported EXCLUDE action encountered";

    /** Monitor listeners for the initiator */
    protected List<MonitorListener> monitorListeners = new ArrayList<MonitorListener>();
    
    /** Name of the module */   
    protected String moduleName;
    
    /** Name of the initiator */   
    protected String name;
    
    /** Flow to be invoked by this initiator */   
    protected Flow flow;
    
    /** Flag indicating a stoppage in error */
    protected boolean error = false;
    
    /** Flag indicating that the initiator has received a stop call */
    protected boolean stopping = false;
    
    /** Count of how many times this Initiator has retried */
    protected Integer retryCount = null;
    
    /** Handler for exceptions*/
    protected IkasanExceptionHandler exceptionHandler;
    
    /** Service for logging errors in a heavyweight fashion */
    protected ErrorLoggingService errorLoggingService;
    
    /** Service for excluding events */
    protected ExcludedEventService excludedEventService;
    
    /**
     * Set of ids for Events that will be immediately excluded when next encountered
     */
    protected Set<String> exclusions = new HashSet<String>();
    
    private long handledEventCount = 0;
    
    private Date lastEventTime = null;
    


	/**
     * Constructor
     * 
     * @param moduleName The name of the module
     * @param name The name of this initiator
     * @param flow The name of the flow it starts
     * @param exceptionHandler 
     */
    public AbstractInitiator(String moduleName, String name, Flow flow, IkasanExceptionHandler exceptionHandler)
    {
        this.moduleName = moduleName;
        this.name = name;
        this.flow = flow;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Adds a <code>MonitorListener</code> to this <code>Initiator</code>
     * 
     * @param monitorListener
     */
    public void addListener(MonitorListener monitorListener)
    {
        monitorListeners.add(monitorListener);
    }


    /**
     * Removes a specified <code>MonitorListener</code> from this <code>Initiator</code>
     * 
     * This method has no effect if the specified MonitorListener is not currently a registered listener
     * 
     * @param monitorListener - listener to remove
     */
    public void removeListener(MonitorListener monitorListener) 
    {
        monitorListeners.remove(monitorListener);
    }

    /**
     * Notification to all registered <code>MonitorListener</code> of the current state of the <code>Initiator</code>
     */
    protected void notifyMonitorListeners()
    {
        for (MonitorListener monitorListener : monitorListeners)
        {
            monitorListener.notify(getState().getName());
        }
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#getExceptionHandler()
     */
    public IkasanExceptionHandler getExceptionHandler(){
    	return exceptionHandler;
    }
    
    /**
     * Accessor for monitorListeners
     * 
     * @return List of all <code>MonitorListeners</code> registered with this <code>Initiator</code>
     */
    public List<MonitorListener> getMonitorListeners()
    {
        return new ArrayList<MonitorListener>(monitorListeners);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#getState()
     */
    public InitiatorState getState(){
        InitiatorState result = null;
        if (isRunning()){
            result = InitiatorState.RUNNING;
            if (isRecovering()){
                result = InitiatorState.RECOVERING;
            } 
        } else{
            result = InitiatorState.STOPPED;
            if(isError()){
                result = InitiatorState.ERROR;
            } 
        }
        return result; 
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#start()
     */
    public void start() throws InitiatorOperationException
    {
        stopping=false;
        error=false;

        try
        {
            flow.start();
            startInitiator();
        }
        catch(ConfigurationException e)
        {
            // unrecoverable, so log the issue and mark this initiator as errored
            logError(null, e, moduleName, null);
            error=true;
        }
        finally
        {
            notifyMonitorListeners();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#stop()
     */
    public void stop() throws InitiatorOperationException
    {
        stopping=true;
        if (isRecovering()){
            cancelRetryCycle();
        }
        
        try
        {
            stopInitiator();
            flow.stop();
        }
        finally
        {
            notifyMonitorListeners();
        }
    }
    
	/**
	 * Invoke the flow with all available <code>Event</code>s, handing exception actions as we go
	 * 
	 * @param events
	 */
	protected void invokeFlow(List<Event> events) {
		IkasanExceptionAction exceptionAction = null;
		String currentEventId= null;
    	if (events != null && events.size() > 0){
	        for (Event event : events)
	        {
	        	currentEventId = event.getId();
				//check if this event has been noted for exclusion, and if so exclude it
				if (supportsExclusions()){
					if (exclusions.contains(currentEventId)){
						excludedEventService.excludeEvent(event, moduleName, flow.getName());
						exclusions.remove(currentEventId);
						continue;
					} 
				} 
	        	
	        	
	        	FlowInvocationContext flowInvocationContext = new FlowInvocationContext();
				try{
					flow.invoke(flowInvocationContext, event);
					handledEventCount = handledEventCount+1;
					lastEventTime = new Date();
				}catch (Throwable throwable){
					String lastComponentName = flowInvocationContext.getLastComponentName();
					exceptionAction = exceptionHandler.handleThrowable(lastComponentName, throwable);
					logError(event, throwable, lastComponentName, exceptionAction);
					break;
				}
	        }
    	}
        this.handleAction(exceptionAction, currentEventId);
	}

	/**
	 * Logs errors in a heavy weight fashion using an <code>ErrorLoggingService</code> if available
	 * 
	 * @param event
	 * @param throwable
	 * @param componentName
	 */
	protected void logError(Event event, Throwable throwable,
			String componentName, IkasanExceptionAction exceptionAction) 
	{
		if (errorLoggingService!=null)
		{
			String actionTaken = null;
			if (exceptionAction!=null)
			{
				actionTaken = exceptionAction.toString();
				if (exceptionAction instanceof RetryAction)
				{
					actionTaken+=" retryCount ["+retryCount+"]";
				}
			}
			
			
			if (event!=null)
			{
				errorLoggingService.logError(throwable, moduleName, flow.getName(), componentName, event, actionTaken);
			}
			else
			{
				//no event available, likely because one was not yet originated
				errorLoggingService.logError(throwable, moduleName, name, actionTaken);
			}
		}
		else
		{
			getLogger().warn("exception caught by initiator ["+moduleName
			        +"."+name+"], but no errorLoggingService available. Using default log.", throwable);
		}
	}

	/**
	 * Invoke the flow with a single <code>Event</code>
	 * 
	 * @param event
	 */
	protected void invokeFlow(Event event) {
		List<Event> events = null;
		if (event !=null){
			events = new ArrayList<Event>();
			events.add(event);
		}
		invokeFlow(events);
		
	}
  
	/**
	 * Handle the returned action from the flow invocation
	 * 
	 * @param action
	 *            IkasanExceptionAction to deal with
	 */
	protected void handleAction(IkasanExceptionAction action, String eventId) {
		try {
			if (action != null) {

	            if (action instanceof StopAction)
	            {
	                stopInError();
	                throw new AbortTransactionException(EXCEPTION_ACTION_IMPLIED_ROLLBACK);
	            }
	            else 
	            {
	            	//exclude
	                if(action instanceof ExcludeEventAction){
	                	if (!supportsExclusions()){
	                		//what do we do here?#
	                		getLogger().error("Initiator that doesnt support Exclusions was asked to handle an EXCLUDE! Switching to rollback and stop instead!");
	                        stopInError();
	                        throw new AbortTransactionException(UNSUPPORTED_EXCLUDE_ENCONTERED);

	                	}
	                	exclusions.add(eventId);
	                }
	            	
	            	//retry 
	                else if (!stopping){
	                	RetryAction retryAction = (RetryAction)action;
	                    Integer maxAttempts = retryAction.getMaxRetries();
	                    long delay = retryAction.getDelay();
	                    handleRetry(maxAttempts, delay);
	                }
	                throw new AbortTransactionException(EXCEPTION_ACTION_IMPLIED_ROLLBACK);
	            }
			} else {
				resume();

			}
		} catch (InitiatorOperationException e) {
			getLogger().fatal(e);
			// try stopping the initiator
			stopInError();
			throw e;
		}
	}
    
    /**
     * Handle an IkasanExceptionAction 'retry'
     * 
     * @param maxAttempts - maximum number of times to retry
     * @param delay - time to delay between retries
     * @throws InitiatorOperationException Exception to throw if we cannot retry
     */
    protected void handleRetry(Integer maxAttempts, long delay) throws InitiatorOperationException
    {
        if (retryWouldExceedLimit(maxAttempts, retryCount))
        {

            stopInError();

            getLogger().warn("Initiator [" +moduleName+"-"+name+ "] stopped. Retry [" + retryCount + "/"
                    + ((maxAttempts < 0) ? "unlimited" : maxAttempts) + "] failed after max attempts. "
                    + "Manual intervention required.");                           
        }
        else
        {
        	
            if (isRecovering())
            {
                if (getLogger().isInfoEnabled())
                {
                    getLogger().info("Initiator [" +moduleName+"-"+name+ "] failed retry [" + (retryCount) + "/"
                        + ((maxAttempts < 0) ? "unlimited" : maxAttempts) + "]. Next retry at approx ["
                        + new Date(System.currentTimeMillis() + delay) + "].");
                } 
                //increment retryCount
                retryCount = retryCount+1;
                continueRetryCycle(delay);
                
            }else
            {
                startRetryCycle(maxAttempts, delay);
                retryCount=0;
                notifyMonitorListeners();
                
            }
            
           
        }

    }
    
    private boolean retryWouldExceedLimit(Integer maxAttempts, Integer attemptCount)
    {
    	
        Integer thisAttemptCount = attemptCount==null?-1:attemptCount;
        
        return (maxAttempts != null) && (maxAttempts != RetryAction.RETRY_INFINITE) && (maxAttempts<=thisAttemptCount+1);
    }




    /**
     * Returns the Initiator to normal running, completes recovering if necessary
     * 
     * @throws InitiatorOperationException
     */
    protected void resume() throws InitiatorOperationException
    {
        if (isRecovering())
        {
            completeRetryCycle();
            notifyMonitorListeners();
        }
    }
    
    /**
     * Sets the error flag before stopping the initiator by normal means
     */
    protected void stopInError()
    {
        error = true;
        stop();
    }
    /**
     * Accessor for moduleName
     * 
     * @return name of the module 
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * Accessor for retryCount
     * 
     * @return
     */
    public Integer getRetryCount(){
        return retryCount;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#getFlow()
     */
    public Flow getFlow()
    {
        return flow;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#isError()
     */
    public boolean isError()
    {
        return error;
    }
    
    /**
     * Accessor for stopping flag
     * 
     * @return stopping flag
     */
    public boolean isStopping()
    {
        return stopping;
    }
    
    /**
     * Setter for optional ErrorLoggingService
     * @param errorLoggingService
     */
    public void setErrorLoggingService(ErrorLoggingService errorLoggingService) {
		this.errorLoggingService = errorLoggingService;
	}
    
    /**
     * Provides access to the implementation class specific logger instance
     * 
     * @return Logger instance for the extending class
     */
    protected abstract Logger getLogger();
    
    /**
     * Cancel the retry activity, and resume any business as usual activity
     * 
     */
    protected abstract void completeRetryCycle();
    
    /**
     * Cancels retry activity
     */
    protected abstract void cancelRetryCycle();

    /**
     * Only the extending class knows how to start the initiator.
     * 
     * @throws InitiatorOperationException Exception if we cannot start initiator
     */
    protected abstract void startInitiator() throws InitiatorOperationException;

    /**
     * Only the extending class knows how to stop the initiator.
     * 
     * @throws InitiatorOperationException Excetpion if we cannot stop initiator
     */
    protected abstract void stopInitiator() throws InitiatorOperationException;
    
    /**
     * Initiator needs to start a retry cycle due to an exception action. Only the extending class has the
     * knowledge to start a retry cycle.
     * 
     * @param maxAttemptss The number of retries we allow
     * @param delay The delay in milliseconds before we perform a retry
     */
    protected abstract void startRetryCycle(Integer maxAttempts, long delay) throws InitiatorOperationException;
    
    /**
     * Template method for subclasses to selectively override
     * 
     * @param delay
     */
    protected void continueRetryCycle(long delay)
    {
        //overridden where necessary
        
    }
    
    /**
     * @param excludedEventService to set
     */
    public void setExcludedEventService(ExcludedEventService excludedEventService) {
		this.excludedEventService = excludedEventService;
	}
	
	/**
	 * Returns true if an excludedEventService is present, and thus supports exclusions
	 * 
	 * @return true if exclusions are supported by this Initiator
	 */
	public boolean supportsExclusions(){
		return excludedEventService!=null;
	}
	
	/**
	 * Accessor for exclusions
	 * 
	 * @return set of eventIds noted for exclusion when next encountered
	 */
	public Set<String> getExclusions() {
		return new HashSet<String>(exclusions);
	}
	
	public long getHandledEventCount(){
		return handledEventCount;
	}
	
	public Date getLastEventTime(){
		return lastEventTime;
	}
}
