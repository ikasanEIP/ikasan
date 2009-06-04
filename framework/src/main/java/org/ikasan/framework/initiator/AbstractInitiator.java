/* 
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.Flow;
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
    
    protected IkasanExceptionHandler exceptionHandler;
    
    /**
     * Service for logging errors in a heavyweight fashion
     */
    protected ErrorLoggingService errorLoggingService;
    

    /**
	 * @param errorLoggingService the errorLoggingService to set
	 */
	public void setErrorLoggingService(ErrorLoggingService errorLoggingService) {
		this.errorLoggingService = errorLoggingService;
	}

	/**
     * Constructor
     * 
     * @param moduleName The name of the module
     * @param name The name of this initiator
     * @param flow The name of the flow it starts
     * @param exceptionHandler for handling exceptions
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
        startInitiator();
        notifyMonitorListeners();
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
            stopInitiator();
            notifyMonitorListeners();

    }
    
    /**
	 * Flow invocation routine.
	 * 
	 * Attempts to play each Event down the flow in sequence, interrupted only if an exception action
	 * is encountered. 
	 */
	protected void invokeFlow(List<Event> events) {
		IkasanExceptionAction exceptionAction = null;
		if (events != null && !events.isEmpty()) {
			for (Event event : events) {
				exceptionAction = flow.invoke(event);
				if (exceptionAction != null) {
					break;
				}
			}
		}
		handleAction(exceptionAction);
	}
  
    /**
     * Handle the returned action from the flow invocation
     * 
     * @param action IkasanExceptionAction to deal with
     */
    protected void handleAction(IkasanExceptionAction action)
    {
        try
        {
            if (action == null)
            {
                resume();
            }
            else if (action.getType().isStop())
            {
                stopInError();
                if (action.getType().isRollback()){
                    throw new AbortTransactionException(EXCEPTION_ACTION_IMPLIED_ROLLBACK);
                }
            }
            else if (action.getType().isRollback())
            {
                if (!stopping){
                    Integer maxAttempts = action.getMaxAttempts();
                    long delay = action.getDelay().longValue();
                    handleRetry(maxAttempts, delay);
                }
                throw new AbortTransactionException(EXCEPTION_ACTION_IMPLIED_ROLLBACK);
            }
            else
            {
                // continue or skip result in the same initiator action
                resume();
            }
        }
        catch (InitiatorOperationException e)
        {
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
        Integer thisAttemptCount = attemptCount==null?0:attemptCount;
        
        return (maxAttempts != null) && (maxAttempts != IkasanExceptionAction.RETRY_INFINITE) && (maxAttempts <= thisAttemptCount+1);
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
}
