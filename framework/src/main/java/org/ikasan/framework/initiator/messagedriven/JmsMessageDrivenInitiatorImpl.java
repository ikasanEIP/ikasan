/*
 * $Id: JmsMessageDrivenInitiatorImpl.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/initiator/messagedriven/JmsMessageDrivenInitiatorImpl.java $
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
package org.ikasan.framework.initiator.messagedriven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.serialisation.EventSerialisationException;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.InitiatorContext;
import org.ikasan.framework.initiator.InitiatorState;
import org.ikasan.framework.monitor.MonitorListener;
import org.ikasan.framework.monitor.MonitorSubject;

/**
 * Abstract base class for JMS Message Driven Initiators
 * 
 * Subclasses will provide an implementation for handling of one or more of the specific JMS Message types into an
 * <code>Event</code>
 * 
 * @author Ikasan Development Team
 */
public abstract class JmsMessageDrivenInitiatorImpl implements JmsMessageDrivenInitiator, MonitorSubject
{
    /** Exception action is an implied rollback message */
    static final String EXCEPTION_ACTION_IMPLIED_ROLLBACK = "Exception Action implied rollback";

    /** Logger for this class */
    static Logger logger = Logger.getLogger(JmsMessageDrivenInitiatorImpl.class);

    /** Monitor listeners for the initiator */
    protected List<MonitorListener> monitorListeners = new ArrayList<MonitorListener>();


    /** The message listener container */
    MessageListenerContainer messageListenerContainer;

    /** The flow */
    protected Flow flow;

    /** The module name */
    protected String moduleName;

    /** The name of this initiator */
    protected String name;

    /** The map of retries for this initiator */
    protected Map<IkasanExceptionAction, Integer> retries = new HashMap<IkasanExceptionAction, Integer>();

    /** The Anesthetist for stopping/starting the message listener container */
    protected Anesthetist anesthetist = null;

    /** An error flag */
    protected boolean error = false;

    /**
     * Constructor
     * 
     * @param moduleName The name of the module
     * @param name The name of this initiator
     * @param flow The name of the flow it starts
     */
    public JmsMessageDrivenInitiatorImpl(String moduleName, String name, Flow flow)
    {
        this.moduleName = moduleName;
        this.name = name;
        this.flow = flow;
        //this.setState(InitiatorState.RUNNING);
        notifyMonitorListeners();
    }

    public Flow getFlow()
    {
        return flow;
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
        logger.info("returning state:"+result);
        return result; 
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#addListener(org.ikasan.framework.initiator.monitor.MonitorListener)
     */
    public void addListener(MonitorListener monitorListener)
    {
        monitorListeners.add(monitorListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.ikasan.framework.initiator.monitor.MonitorSubject#removeListener(org.ikasan.framework.initiator.monitor.
     * MonitorListener)
     */
    public void removeListener(MonitorListener monitorListener) 
    {
        monitorListeners.remove(monitorListener);
    }

    /**
     * Notification to all registered monitor listeners passing this initiators state change.
     */
    private void notifyMonitorListeners()
    {
        for (MonitorListener monitorListener : monitorListeners)
        {
            monitorListener.notify(getState().getName());
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message message)
    {
        Event event = null;
        try
        {
            logger.info("received message with id [" + message.getJMSMessageID() + "]");
            if (message instanceof TextMessage)
            {
                event = handleTextMessage((TextMessage) message);
            }
            else if (message instanceof MapMessage)
            {
                event = handleMapMessage((MapMessage) message);
            }
            else if (message instanceof ObjectMessage)
            {
                event = handleObjectMessage((ObjectMessage) message);
            }
            else if (message instanceof StreamMessage)
            {
                event = handleStreamMessage((StreamMessage) message);
            }
            else if (message instanceof BytesMessage)
            {
                event = handleBytesMessage((BytesMessage) message);
            }
        }
        catch (JMSException jmsException)
        {
            stopInError();
            throw new RuntimeException(jmsException);
        }
        catch (EventSerialisationException eventSerialisationException)
        {
            stopInError();
            throw new RuntimeException(eventSerialisationException);
        }
        IkasanExceptionAction action = flow.invoke(event);
        handleAction(action);
    }

    /**
     * Handle an IkasanExceptionAction
     * 
     * @param action Exception action to handle
     */
    private void handleAction(IkasanExceptionAction action)
    {
        if (action == null)
        {
            clearRetries();
        }
        else
        {
            if (action.getType().isStop())
            {
                stopInError();
                if (action.getType().isRollback())
                {
                    throw new RuntimeException(EXCEPTION_ACTION_IMPLIED_ROLLBACK);
                }
            }
            else if (action.getType().isRollback())
            {
                handleRetryAction(action);
                throw new RuntimeException(EXCEPTION_ACTION_IMPLIED_ROLLBACK);
            }
            else
            {
                clearRetries();
            }
        }
    }

    private void clearRetries()
    {
        if (!retries.isEmpty()){
            retries.clear();
            notifyMonitorListeners();
        }
    }

    /**
     * Handle a retry action
     * 
     * @param action Retry action to handle
     */
    private void handleRetryAction(IkasanExceptionAction action)
    {
        Integer attemptCount = retries.get(action);
        if (attemptCount == null)
        {
            attemptCount = 0;
        }
        // increment the attemptCount
        attemptCount = attemptCount + 1;
        retries.put(action, attemptCount);
        logger.info("this is attempt [" + attemptCount + "] for this action");
        Integer maxAttempts = action.getMaxAttempts();
        if ((maxAttempts != null) && (maxAttempts != InitiatorContext.INFINITE) && (maxAttempts <= attemptCount))
        {
            // max attempts exceeded
            stopInError();

        }
        else
        {
            long delay = action.getDelay().longValue();
            anesthetist = new Anesthetist(delay);
            notifyMonitorListeners();
        }
    }

    /**
     * 
     */
    private void stopInError()
    {
        error = true;
        stop();
    }

    /**
     * Cancel the retry action
     */
    private void cancelRetry()
    {
        if (anesthetist != null)
        {
            anesthetist.cancel();
            anesthetist = null;
        }
    }

    public String getName()
    {
        return name;
    }

    public boolean isError()
    {
        return error;
    }

    public boolean isRecovering()
    {
        return !retries.isEmpty();
    }

    public boolean isRunning()
    {
        return (messageListenerContainer.isRunning() || anesthetistOperating());
    }

    /**
     * Return true if the anesthetist is operating
     * 
     * @return true if the anesthetist is operating
     */
    protected boolean anesthetistOperating()
    {
        return (anesthetist != null) && (anesthetist.isOperating());
    }

    public void start()
    {
        messageListenerContainer.start();
        notifyMonitorListeners();
    }

    public void stop()
    {
        // cancel any existing anesthetist
        cancelRetry();
        messageListenerContainer.stop();
        notifyMonitorListeners();
    }

    /**
     * @param messageListenerContainer the messageListenerContainer to set
     */
    public void setMessageListenerContainer(MessageListenerContainer messageListenerContainer)
    {
        this.messageListenerContainer = messageListenerContainer;
    }

    /**
     * JMS Message specific type handling for <code>BytesMessage</code>
     * 
     * Subclasses that wish to support this <code>Message</code> type will override this
     * 
     * @param message The message to handle
     * @return Event The event containing the message
     * @throws JMSException Exception if there is a problem with JMS
     * @throws EventSerialisationException SuppressWarnings Exceptions aren't thrown by this parent class but should be
     *             by implementing children
     */
    @SuppressWarnings("unused")
    protected Event handleBytesMessage(BytesMessage message) throws JMSException, EventSerialisationException
    {
        throw new UnsupportedOperationException("This Initiator does not support BytesMessage [" + message.toString()
                + "]");
    }

    /**
     * JMS Message specific type handling for <code>StreamMessage</code>
     * 
     * Subclasses that wish to support this <code>Message</code> type will override this
     * 
     * @param message The message to handle
     * @return Event
     */
    protected Event handleStreamMessage(StreamMessage message)
    {
        throw new UnsupportedOperationException("This Initiator does not support StreamMessage [" + message.toString()
                + "]");
    }

    /**
     * JMS Message specific type handling for <code>ObjectMessage</code>
     * 
     * Subclasses that wish to support this <code>Message</code> type will override this
     * 
     * @param message The message to handle
     * @return Event
     * @throws JMSException Exception if there is a problem with JMS
     * @throws EventSerialisationException SuppressWarnings Exceptions aren't thrown by this parent class but should be
     *             by implementing children
     */
    @SuppressWarnings("unused")
    protected Event handleObjectMessage(ObjectMessage message) throws JMSException, EventSerialisationException
    {
        throw new UnsupportedOperationException("This Initiator does not support ObjectMessage [" + message.toString()
                + "]");
    }

    /**
     * JMS Message specific type handling for <code>MapMessage</code>
     * 
     * Subclasses that wish to support this <code>Message</code> type will override this
     * 
     * @param message The message to handle
     * @return Event
     * @throws JMSException Exception if there is a problem with JMS
     * @throws EventSerialisationException SuppressWarnings Exceptions aren't thrown by this parent class but should be
     *             by implementing children
     */
    @SuppressWarnings("unused")
    protected Event handleMapMessage(MapMessage message) throws JMSException, EventSerialisationException
    {
        throw new UnsupportedOperationException("This Initiator does not support MapMessage [" + message.toString()
                + "]");
    }

    /**
     * JMS Message specific type handling for <code>TextMessage</code>
     * 
     * Subclasses that wish to support this <code>Message</code> type will override this
     * 
     * @param message The message to handle
     * @return Event
     * @throws JMSException Exception if there is a problem with JMS
     * @throws EventSerialisationException SuppressWarnings Exceptions aren't thrown by this parent class but should be
     *             by implementing children
     */
    @SuppressWarnings("unused")
    protected Event handleTextMessage(TextMessage message) throws JMSException, EventSerialisationException
    {
        throw new UnsupportedOperationException("This Initiator does not support TextMessage [" + message.toString()
                + "]");
    }

    /**
     * This inner class is responsible for putting the MessageListenerContainer to sleep for a specified period and
     * reawakening.
     * 
     * This needs to happen in a separate thread.
     * 
     * @author duncro
     * 
     */
    private class Anesthetist extends Thread
    {
        /** The period for the Anesthetist to put the container to sleep for */
        long sleepPeriod;

        /** Flag on whether the Anesthetist is currently operating or not */
        boolean operating = false;

        /** Flag covering whether the Anesthetist operation has been cancelled */
        boolean cancelled = false;

        /**
         * Constructor
         * 
         * @param sleepPeriod The amount of time to put the initiator to sleep in milliseconds
         */
        public Anesthetist(long sleepPeriod)
        {
            this.sleepPeriod = sleepPeriod;
            logger.info("setting anesthetist to sleep the messageListenerConatiner for " + sleepPeriod + "ms");
            putToSleep();
            start();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run()
        {
            
            try
            {
                sleep(sleepPeriod);
                reawaken();
            }
            catch (InterruptedException e)
            {
                // nevermind
                reawaken();
            }
        }

        /**
         * Put the message listener container to sleep
         */
        private void putToSleep()
        {
            operating = true;
            messageListenerContainer.stop();
            
        }

        /**
         * Start the message listener container
         */
        private void reawaken()
        {
            if (!cancelled)
            {
                logger.info("reawakening sleeping messageListenerContainer");
                messageListenerContainer.start();
            }
            operating = false;
        }

        /**
         * True if we're currently operating
         * 
         * @return true if we're currently operating
         */
        public boolean isOperating()
        {
            return operating;
        }

        /**
         * Cancel the operation
         */
        public void cancel()
        {
            logger.info("cancelling any anesthetist operation");
            cancelled = true;
        }
    }
}
