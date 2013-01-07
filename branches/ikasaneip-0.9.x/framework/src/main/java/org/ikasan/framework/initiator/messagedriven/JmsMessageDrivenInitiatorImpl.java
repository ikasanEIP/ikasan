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
package org.ikasan.framework.initiator.messagedriven;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.serialisation.EventDeserialisationException;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.RetryAction;
import org.ikasan.framework.exception.StopAction;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.initiator.AbstractInitiator;
import org.ikasan.framework.monitor.MonitorSubject;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowEvent;

/**
 * Abstract base class for JMS Message Driven Initiators
 * 
 * Subclasses will provide an implementation for handling of one or more of the specific JMS Message types into an
 * <code>FlowEvent</code>
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
    private int maxListenerSetupFailureRetries = RetryAction.RETRY_INFINITE;


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
     * @param exceptionHandler handler for Exceptions
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
        FlowEvent event = null;
        try
        {
        	if (logger.isDebugEnabled()){
        		logger.debug("received message with id [" + message.getJMSMessageID() + "]");
        	}
        	
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
        catch (UnsupportedOperationException unsupportedOperationException){
        	//tell the error service
    		logError(null, unsupportedOperationException, name, StopAction.instance());
        	
        	stopInError();
        	throw new AbortTransactionException(EXCEPTION_ACTION_IMPLIED_ROLLBACK);
        }
        catch (EventDeserialisationException eventDeserialisationException){
        	//tell the error service
    		logError(null, eventDeserialisationException, name, StopAction.instance());
        	
        	stopInError();
        	throw new AbortTransactionException(EXCEPTION_ACTION_IMPLIED_ROLLBACK);
        }
        catch (Throwable eventSourcingThrowable)
        {
        	IkasanExceptionAction action = exceptionHandler.handleThrowable(name, eventSourcingThrowable);
        	//tell the error service
    		logError(null, eventSourcingThrowable, name, action);
        	handleAction(action,null);
        }
        invokeFlow(event);
    }


    protected void completeRetryCycle()
    {
        if (retryCount!=null){
            retryCount=null;
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
    	logError(null, throwable, name, new RetryAction(listenerSetupFailureRetryDelay, maxListenerSetupFailureRetries));
        handleRetry(maxListenerSetupFailureRetries, listenerSetupFailureRetryDelay);
    }

    /**
     * JMS Message specific type handling for <code>BytesMessage</code>
     * 
     * Subclasses that wish to support this <code>Message</code> type will override this
     * 
     * @param message The message to handle
     * @return FlowEvent The event containing the message
     * @throws JMSException Exception if there is a problem with JMS
     */
    protected FlowEvent handleBytesMessage(BytesMessage message) throws JMSException
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
     * @return FlowEvent
     */
    protected FlowEvent handleStreamMessage(StreamMessage message) throws JMSException, EventDeserialisationException
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
     * @return FlowEvent
     * @throws JMSException Exception if there is a problem with JMS
     */
    protected FlowEvent handleObjectMessage(ObjectMessage message) throws JMSException, EventDeserialisationException
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
     * @return FlowEvent
     * @throws JMSException Exception if there is a problem with JMS
     */
    protected FlowEvent handleMapMessage(MapMessage message) throws JMSException, EventDeserialisationException
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
     * @return FlowEvent
     * @throws JMSException Exception if there is a problem with JMS
     */
    protected FlowEvent handleTextMessage(TextMessage message) throws JMSException, EventDeserialisationException
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
