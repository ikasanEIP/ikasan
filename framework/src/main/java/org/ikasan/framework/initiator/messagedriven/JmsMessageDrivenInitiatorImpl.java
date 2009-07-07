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
package org.ikasan.framework.initiator.messagedriven;

import java.util.ArrayList;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.ikasan.framework.event.serialisation.EventSerialisationException;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.AbstractInitiator;
import org.ikasan.framework.monitor.MonitorSubject;

/**
 * Abstract base class for JMS Message Driven Initiators
 * 
 * Subclasses will provide an implementation for handling of one or more of the specific JMS Message types into an
 * <code>Event</code>
 * 
 * @author Ikasan Development Team
 */
public abstract class JmsMessageDrivenInitiatorImpl extends AbstractInitiator implements JmsMessageDrivenInitiator, MonitorSubject, ListenerSetupFailureListener
{




    /**
     * Delay between listener setup attempts
     */
    private int listenerSetupFailureRetryDelay=10000;

    /**
     * Maximum number of times underlying container will attempt to set up (poll) for messages
     */
    private int maxListenerSetupFailureRetries = IkasanExceptionAction.RETRY_INFINITE;


	public static final String JMS_MESSAGE_DRIVEN_INITIATOR_TYPE = "JmsMessageDrivenInitiator";


    /** Logger for this class */
    static Logger logger = Logger.getLogger(JmsMessageDrivenInitiatorImpl.class);


    /** The message listener container */
    protected MessageListenerContainer messageListenerContainer;
    
    /** The Anesthetist for stopping/starting the message listener container */
    protected Anesthetist anesthetist = null;


    /**
     * Constructor
     * 
     * @param moduleName The name of the module
     * @param name The name of this initiator
     * @param flow The name of the flow it starts
     * @param exceptionHandler for handlingExceptions
     */
    public JmsMessageDrivenInitiatorImpl(String moduleName, String name, Flow flow, IkasanExceptionHandler exceptionHandler)
    {
        super(moduleName, name, flow, exceptionHandler);
    }
    
    public String getType(){
        return JMS_MESSAGE_DRIVEN_INITIATOR_TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message message)
    {
    	List<Event> events = new ArrayList<Event>();

        try
        {
            logger.info("received message with id [" + message.getJMSMessageID() + "]");
            if (message instanceof TextMessage)
            {
            	events.add(handleTextMessage((TextMessage) message));
            }
            else if (message instanceof MapMessage)
            {
            	events.add(handleMapMessage((MapMessage) message));
            }
            else if (message instanceof ObjectMessage)
            {
            	events.add(handleObjectMessage((ObjectMessage) message));
            }
            else if (message instanceof StreamMessage)
            {
            	events.add(handleStreamMessage((StreamMessage) message));
            }
            else if (message instanceof BytesMessage)
            {
            	events.add(handleBytesMessage((BytesMessage) message));
            }
        }
        catch (Throwable eventSourcingThrowable){
        	handleEventSourcingThrowable(eventSourcingThrowable);
        }
        invokeFlow(events);
    }
    




    protected void completeRetryCycle()
    {
        if (retryCount!=null){
            retryCount=null;
            notifyMonitorListeners();
        }
    }



    protected void startRetryCycle(Integer maxAttempts, long delay)
    {
        anesthetist = new Anesthetist(delay);
    }
    
    protected void continueRetryCycle(long delay)
    {
        anesthetist = new Anesthetist(delay);
    }



    @Override
    protected void cancelRetryCycle()
    {
        if (anesthetist != null)
        {
            anesthetist.cancel();
            anesthetist = null;
        }
        retryCount=null;
    }



    public boolean isRecovering()
    {
        return retryCount!=null;
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


    
    @Override
    protected void startInitiator(){
        messageListenerContainer.start();
    }
    
    @Override
    protected void stopInitiator(){
        messageListenerContainer.stop();
    }

    /**
     * @param messageListenerContainer the messageListenerContainer to set
     */
    public void setMessageListenerContainer(MessageListenerContainer messageListenerContainer)
    {
        this.messageListenerContainer = messageListenerContainer;
        messageListenerContainer.setListenerSetupExceptionListener(this);
    }
    
    /**
     * Accessor for messageListenerContainer
     * 
     * @return
     */
    public MessageListenerContainer getMessageListenerContainer(){
        return messageListenerContainer;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.messagedriven.ListenerSetupFailureListener#notifyListenerSetupFailure(java.lang.Throwable)
     */
    public void notifyListenerSetupFailure(Throwable throwable){
        handleRetry(maxListenerSetupFailureRetries, listenerSetupFailureRetryDelay);
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
     * Setter for overriding the default value (10000)of listenerSetupFailureRetryDelay
     * 
     * @param listenerSetupFailureRetryDelay in milliseconds
     */
    public void setListenerSetupFailureRetryDelay(int listenerSetupFailureRetryDelay) {
		this.listenerSetupFailureRetryDelay = listenerSetupFailureRetryDelay;
	}

	/**
	 * Setter for overriding the default value (Indefinite) of listenerSetupFailureRetryDelay
	 * 
	 * @param maxListenerSetupFailureRetries
	 */
	public void setMaxListenerSetupFailureRetries(int maxListenerSetupFailureRetries) {
		this.maxListenerSetupFailureRetries = maxListenerSetupFailureRetries;
	}
    /**
     * This inner class is responsible for putting the MessageListenerContainer to sleep for a specified period and
     * reawakening.
     * 
     * This needs to happen in a separate thread.
     * 
     * @author Ikasan Development Team
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
