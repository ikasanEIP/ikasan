/*
 * $Id
 * $URL$
 *
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.framework.initiator.messagedriven.jca;

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
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.initiator.AbstractInitiator;
import org.ikasan.framework.initiator.messagedriven.jca.MessageListenerContainer;
import org.ikasan.framework.monitor.MonitorSubject;

/**
 * Abstract base class for JMS Message Driven Initiators
 *
 * Subclasses will provide an implementation for handling of one or more of the specific JMS Message types into an
 * <code>Event</code>
 *
 * @author Ikasan Development Team
 */
public abstract class JmsMessageDrivenInitiatorImpl
    extends AbstractInitiator
    implements JmsMessageDrivenInitiator, MonitorSubject, ListenerSetupFailureListener
{
    /**
     * Delay between listener setup attempts
     */
    private int listenerSetupFailureRetryDelay=10000;

    /**
     * Maximum number of times underlying container will attempt to set up (poll) for messages
     */
    private int maxListenerSetupFailureRetries = IkasanExceptionAction.RETRY_INFINITE;

    // Message for the Initiator stopping
    private static final String INITIATOR_STOPPING = "Initiator cannot process message whilst managing a stop request.";

    // Message for the Initiator's Anesthetist operating
    private static final String INITIATOR_ANESTHETIST_OPERATING = "Initiator cannot process message until anesthetist has completed.";

    // Constant describing this type of initiator, TODO: Could this not be driven off class name or instanceof?
    private static final String JMS_MESSAGE_DRIVEN_INITIATOR_TYPE = "JmsMessageDrivenInitiator";

    /** Logger for this class */
    static Logger logger = Logger.getLogger(JmsMessageDrivenInitiatorImpl.class);

    /** The message listener container */
    protected MessageListenerContainer messageListenerContainer;

    /** The Anesthetist for stopping/starting the message listener container in a retry cycle*/
    protected Anesthetist anesthetist = null;

    /** The Halt for activating/deactivating the JMS endpoint */
    protected Halt halt = null;

    /**
     * Constructor
     *
     * @param moduleName The name of the module that contains this initiator
     * @param name The name of this initiator
     * @param flow The name of the flow that this initiator starts
     */
    public JmsMessageDrivenInitiatorImpl(String moduleName, String name, Flow flow)
    {
        super(moduleName, name, flow);
    }

    // TODO This could just be a call to class name or instance of?
    public String getType()
    {
        return JMS_MESSAGE_DRIVEN_INITIATOR_TYPE;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message message)
    {
        if(this.stopping)
        {
            throw new AbortTransactionException(INITIATOR_STOPPING);
        }
        if(anesthetistOperating())
        {
            throw new AbortTransactionException(INITIATOR_ANESTHETIST_OPERATING);
        }

        Event event = null;
        try
        {
        	if (logger.isDebugEnabled())
        	{
        		logger.debug("received message with id [" + message.getJMSMessageID() + "]");
        	}

            if (message instanceof MapMessage)
            {
                event = handleMapMessage((MapMessage) message);
            }
            else if (message instanceof TextMessage)
            {
                event = handleTextMessage((TextMessage) message);
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


    protected void completeRetryCycle()
    {
        if (retryCount!=null){
            retryCount=null;
        }
    }

    protected void startRetryCycle(Integer maxAttempts, long delay)
    {
        anesthetist = new Anesthetist(delay);
        anesthetist.start();
    }

    protected void continueRetryCycle(long delay)
    {
        anesthetist = new Anesthetist(delay);
        anesthetist.start();
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
        //if there is halt object means we are stopping!
        if (this.halt != null)
        {
            return false;
        }
        //if there is an anesthetist means we are stopping/retrying
        else if (this.anesthetistOperating())
        {
            return true;
        }
        //we have to check for happy state
        else if (this.messageListenerContainer.isRunning())
        {
            return true;
        }
        else
        {
            return false;
        }
        //return ( this.halt == null && (this.messageListenerContainer.isRunning() || this.anesthetistOperating()));
        //return ((this.messageListenerContainer.isRunning() || this.anesthetistOperating()) && this.halt == null);
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
    protected void startInitiator()
    {
        this.halt = null;
        messageListenerContainer.start();
    }

    @Override
    protected void stopInitiator(){
        messageListenerContainer.stop();
    }

    /**
     * Sets the error flag before stopping the initiator
     */
    @Override
    protected void stopInError()
    {
        error = true;
        stopping = true;
        if (isRecovering())
        {
            cancelRetryCycle();
        }
        this.halt = new Halt();
        this.halt.start();
        notifyMonitorListeners();
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
    public MessageListenerContainer getMessageListenerContainer()
    {
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
     * @param message The Text Message to handle
     * @return Event The Ikasan Event created by the handling of this Text Message
     * @throws JMSException Exception if there is a problem with JMS
     * @throws EventSerialisationException TODO SuppressWarnings Exceptions aren't thrown by this parent class but should be
     *             by implementing children
     */
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
            logger.info("Created anesthetist with a sleep time of " + sleepPeriod + "ms");
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
                logger.info("Anesthetist invoked");
                putToSleep();
                logger.info("Anesthetist sleeping for [" + sleepPeriod + "]ms.");
                sleep(sleepPeriod);
                logger.info("Anesthetist woken from sleep.");
                reawaken();
            }
            catch (InterruptedException e)
            {
                // nevermind
                logger.info("Anesthetist sleep interrupted", e);
                reawaken();
            }
        }

        /**
         * Put the message listener container to sleep
         */
        private void putToSleep()
        {
            operating = true;
            logger.info("Anesthetist invoking the messageListenerConatiner stop...");
            messageListenerContainer.stop();
            logger.info("Anesthetist invoked the messageListenerConatiner stop successfully.");
        }

        /**
         * Start the message listener container
         */
        private void reawaken()
        {
            if (!cancelled)
            {
                logger.info("Anesthetist restarting messageListenerContainer...");
                messageListenerContainer.start();
                logger.info("Anesthetist restarted messageListenerContainer successfully.");
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

    /**
     * This inner class is responsible for stopping the MessageListenerContainer.
     *
     * This needs to happen in a separate thread.
     *
     * @author Ikasan Development Team
     *
     */
    private class Halt extends Thread
    {
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Thread#run()
         */
        @Override
        public void run()
        {
            logger.info("stopping messageListenerContainer...");
            messageListenerContainer.stop();
            logger.info("stopped messageListenerContainer successfully.");
        }
    }
}
