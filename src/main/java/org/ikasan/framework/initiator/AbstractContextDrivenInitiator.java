/* 
 * $Id: AbstractContextDrivenInitiator.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/initiator/AbstractContextDrivenInitiator.java $
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.monitor.MonitorListener;
import org.ikasan.framework.monitor.MonitorListenerNotFoundException;
import org.ikasan.framework.monitor.MonitorSubject;

/**
 * Ikasan Abstract Initiator implementation.
 * 
 * @author Ikasan Development Team
 */
public abstract class AbstractContextDrivenInitiator implements ContextDrivenInitiator, MonitorSubject
{
    /** Logger */
    private static Logger logger = Logger.getLogger(AbstractContextDrivenInitiator.class);

    /**
     * TODO - use the exception handler in the flow rather than having specific reference in the initiator
     */
    private IkasanExceptionHandler exceptionHandler;

    /** Monitor listeners for the initiator */
    protected List<MonitorListener> monitorListeners = new ArrayList<MonitorListener>();

    /** Initiator state */
    private InitiatorState state;

    /** Initiator name */
    private String name;

    /** Target flow for this initiator */
    private Flow flow;

    /**
     * Constructor
     * 
     * @param name Name of the initiator
     * @param flow The flow leading off from the initiator
     * @param exceptionHandler The exceptionHandler associated with the initiator
     */
    public AbstractContextDrivenInitiator(String name, Flow flow, IkasanExceptionHandler exceptionHandler)
    {
        this.name = name;
        this.flow = flow;
        this.exceptionHandler = exceptionHandler;
        this.setState(InitiatorState.RUNNING);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#start()
     */
    public void start() throws InitiatorOperationException
    {
        try
        {
            this.startInitiator();
            this.setState(InitiatorState.RUNNING);
        }
        catch (InitiatorOperationException e)
        {
            this.setState(InitiatorState.ERROR);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#stop()
     */
    public void stop() throws InitiatorOperationException
    {
        try
        {
            this.stopInitiator();
            this.setState(InitiatorState.STOPPED);
        }
        catch (InitiatorOperationException e)
        {
            this.setState(InitiatorState.ERROR);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.scheduling.Initiator#getInitiatorName()
     */
    public String getName()
    {
        return this.name;
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#isRunning()
     */
    public boolean isRunning()
    {
        return this.state.isRunning();
    }

   /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#isError()
     */
    public boolean isError()
    {
        return this.state.isError();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#isRecovering()
     */
    public boolean isRecovering()
    {
        return this.state.isRecovering();
    }
        
    public InitiatorState getState(){
        return state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#addListener(org.ikasan.framework.initiator.monitor.MonitorListener)
     */
    public void addListener(MonitorListener monitorListener)
    {
        if (monitorListeners.add(monitorListener))
        {
            if (logger.isDebugEnabled())
            {
                logger.info("MonitorListener [" + monitorListener.getName() + "] added to Initiator [" + this.getName()
                        + "].");
            }
        }
        else
        {
            logger.warn("Failed to add monitorListener [" + monitorListener.getName() + "] to Initiator ["
                    + this.getName() + "].");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.ikasan.framework.initiator.monitor.MonitorSubject#removeListener(org.ikasan.framework.initiator.monitor.
     * MonitorListener)
     */
    public void removeListener(MonitorListener monitorListener) throws MonitorListenerNotFoundException
    {
        if (monitorListeners.remove(monitorListener))
        {
            if (logger.isDebugEnabled())
            {
                logger.info("MonitorListener [" + monitorListener.getName() + "] removed from Initiator ["
                        + this.getName() + "].");
            }
        }
        else
        {
            throw new MonitorListenerNotFoundException("MonitorListener [" + monitorListener.getName()
                    + "] not found in Initiator [" + this.getName() + "].");
        }
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
     * Return the flow instance to invoke
     * 
     * @return flow
     */
    public Flow getFlow()
    {
        return this.flow;
    }

    /**
     * Set the state of this initiator. This change in state will also be notified to all registered monitor listeners.
     * 
     * @param state state to set
     */
    protected void setState(InitiatorState state)
    {
        this.state = state;
        this.notifyMonitorListeners();
    }

    /**
     * Standard invocation of an initiator.
     * 
     * @param context context to invoke initiator within
     */
    public void invoke(InitiatorContext context)
    {
        if (getState().isStopped())
        {
            logger.warn("Attempt to invoke an initiator in a stopped state.");
            return;
        }
        if (getState().isRecovering())
        {
            IkasanExceptionAction action = context.getIkasanExceptionAction();
            if (action == null)
            {
                throw new InvalidInitiatorStateException("Initiator in recovery state cannot have a 'null' action.");
            }
            if (logger.isInfoEnabled())
            {
                int retryLimit = action.getMaxAttempts().intValue();
                logger.info("Initiator [" + this.getName() + "] invoked. " + "Retry attempt ["
                        + (context.getRetryCount() + 1) + "/"
                        + ((retryLimit < 0) ? "unlimited" : new Integer(retryLimit)) + "].");
            }
        }
        else
        {
            if (logger.isInfoEnabled())
            {
                logger.info("Initiator [" + this.getName() + "] invoked.");
            }
        }
        // invoke flow all the time we have event activity
        this.invokeFlow(context);
    }

    /**
     * Handle the returned action from the flow invocation
     * 
     * @param action IkasanExceptionAction to deal with
     * @param context context of the initiator
     */
    protected void handleAction(IkasanExceptionAction action, InitiatorContext context)
    {
        try
        {
            if (action == null)
            {
                this.handleNullAction(context);
            }
            else if (action.getType().isStop())
            {
                this.handleStopAction(action);
                if (action.getType().isRollback()) throw new AbortTransactionException();
            }
            else if (action.getType().isRollback())
            {
                this.handleRetryAction(context, action);
                throw new AbortTransactionException();
            }
            else
            {
                // continue or skip result in the same initiator action
                this.handleContinueAction(context, action);
            }
        }
        catch (InitiatorOperationException e)
        {
            logger.fatal(e);
            this.setState(InitiatorState.ERROR);
            // try stopping this initiator
            this.stopInitiator();
            throw e;
        }
    }

    /**
     * Handle an IkasanExceptionAction 'stop'
     * 
     * @param action IkasanExceptionAction 'stop' action
     * @throws InitiatorOperationException Exception to throw if we cannot stop
     */
    protected void handleStopAction(IkasanExceptionAction action) throws InitiatorOperationException
    {
        this.stopInitiator();
        logger.warn("Initiator [" + this.getName() + "] stopped on action [" + action.getType().toString() + "]. "
                + "Manual intervention required.");
        this.setState(InitiatorState.ERROR);
    }

    /**
     * Handle an IkasanExceptionAction 'retry'
     * 
     * @param context context for the initiator
     * @param action IkasanExceptionAction 'retry' action
     * @throws InitiatorOperationException Exception to throw if we cannot retry
     */
    protected void handleRetryAction(InitiatorContext context, IkasanExceptionAction action)
            throws InitiatorOperationException
    {
        // get required values to local vars for convenience
        int limit = action.getMaxAttempts().intValue();
        long delay = action.getDelay().longValue();
        if (action.equals(context.getIkasanExceptionAction()))
        {
            int count = context.getRetryCount() + 1;
            context.setRetryCount(count);
            if (limit == InitiatorContext.INFINITE || count < limit)
            {
                if (logger.isInfoEnabled())
                {
                    logger.info("Initiator [" + this.getName() + "] failed retry [" + count + "/"
                            + ((limit < 0) ? "unlimited" : limit) + "]. Next retry at ["
                            + new Date(System.currentTimeMillis() + delay) + "].");
                }
            }
            else
            {
                // cancel the retry cycle
                this.cancelRetryCycle(context);
                // stop this initiator
                this.stop();
                logger.warn("Initiator [" + this.getName() + "] stopped. Retry [" + count + "/"
                        + ((limit < 0) ? "unlimited" : new Integer(limit)) + "] failed after max attempts. "
                        + "Manual intervention required.");
                this.setState(InitiatorState.ERROR);
            }
        }
        else
        {
            context.setIkasanExceptionAction(action);
            this.setState(InitiatorState.RECOVERING);
            this.startRetryCycle(context, limit, delay);
        }
    }

    /**
     * Handle an IkasanExceptionAction 'continue' or 'skip'
     * 
     * @param context context for the initiator
     * @param action 'continue' or 'skip' action
     * @throws InitiatorOperationException Exception to throw if we cannot skip or continue
     */
    protected void handleContinueAction(InitiatorContext context, IkasanExceptionAction action)
            throws InitiatorOperationException
    {
        if (getState().isRecovering())
        {
            this.completeRetryCycle(context);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("Initiator [" + this.getName() + "] continuing on action [" + action.getType().toString()
                    + "].");
        }
        this.setState(InitiatorState.RUNNING);
    }

    /**
     * Handle an IkasanExceptionAction 'null'
     * 
     * @param context context for initiator
     * @throws InitiatorOperationException Exception to throw if we cannot act upon the action
     */
    protected void handleNullAction(InitiatorContext context) throws InitiatorOperationException
    {
        if (getState().isRecovering())
        {
            this.completeRetryCycle(context);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("Initiator [" + this.getName() + "] continuing on action [null].");
        }
        this.setState(InitiatorState.RUNNING);
    }

    /**
     * Invoke the initiator extending classes flow
     * 
     * @param context context for initiator
     */
    protected abstract void invokeFlow(InitiatorContext context);

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
     * Initiator needs to start a retry cycle due to an ikasan exception action. Only the extending class has the
     * knowledge to start a retry cycle.
     * 
     * @param context context for the initiator
     * @param limit The number of retries we allow
     * @param delay The delay in milliseconds before we perform a retry
     */
    protected abstract void startRetryCycle(InitiatorContext context, int limit, long delay);

    /**
     * Initiator needs to halt the retry cycle prior to recovery completing successfully. Only the extending class has
     * the knowledge to start a retry cycle.
     * 
     * @param context context for this initiator
     */
    protected abstract void cancelRetryCycle(InitiatorContext context);

    /**
     * Initiator has successfully recovered so needs to cancel the retry cycle. Only the extending class has the
     * knowledge to complete a retry cycle.
     * 
     * @param context context for this initiator 
     */
    protected abstract void completeRetryCycle(InitiatorContext context);

    /**
     * Notification to all registered monitor listeners passing this initiators state change.
     */
    private void notifyMonitorListeners()
    {
        for (MonitorListener monitorListener : monitorListeners)
        {
            monitorListener.notify(new String(this.state.getName()));
        }
    }
}
