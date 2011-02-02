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
package org.ikasan.framework.initiator.eventdriven;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.initiator.AbstractInitiator;
import org.ikasan.framework.monitor.MonitorSubject;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowEvent;

/**
 * FlowEvent Driven Initiator implementation.
 *
 * @author Ikasan Development Team
 */
public class EventDrivenInitiatorImpl<T>
    extends AbstractInitiator
    implements EventDrivenInitiator<T>, MonitorSubject
{
    private static final String INITIATOR_STOPPING = "Initiator cannot process message whilst managing a stop request.";

    private static final String INITIATOR_ANESTHETIST_OPERATING = "Initiator cannot process message until anesthetist has completed.";

    public static final String EVENT_DRIVEN_INITIATOR_TYPE = "FlowEventDrivenInitiator";

    /** Logger for this class */
    static Logger logger = Logger.getLogger(EventDrivenInitiatorImpl.class);

    /** message endpoint manager */
    protected MessageEndpointManager messageEndpointManager;

    /** The Anesthetist for stopping/starting the message listener container in a retry cycle*/
    protected Anesthetist anesthetist = null;

    /** The Halt for activating/deactivating the jms endpoint */
    protected Halt halt = null;

    protected EventFactory<FlowEvent> eventFactory;
    
    /**
     * Constructor
     *
     * @param moduleName The name of the module
     * @param name The name of this initiator
     * @param flow The name of the flow it starts
     */
    public EventDrivenInitiatorImpl(String moduleName, String name, Flow flow, EventFactory eventFactory, IkasanExceptionHandler exceptionHandler)
    {
        super(moduleName, name, flow, exceptionHandler);
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }

        if(name == null)
        {
            throw new IllegalArgumentException("Initiator name cannot be 'null'");
        }

        if(flow == null)
        {
            throw new IllegalArgumentException("flow cannot be 'null'");
        }

        if(exceptionHandler == null)
        {
            throw new IllegalArgumentException("exceptionHandler cannot be 'null'");
        }

        this.eventFactory = eventFactory;
        if(eventFactory == null)
        {
            throw new IllegalArgumentException("eventFactory cannot be 'null'");
        }

    }

    public String getType()
    {
        return EVENT_DRIVEN_INITIATOR_TYPE;
    }

    public void onException(Throwable throwable)
    {
        if(this.stopping)
        {
            throw new AbortTransactionException(INITIATOR_STOPPING);
        }
        if(anesthetistOperating())
        {
            throw new AbortTransactionException(INITIATOR_ANESTHETIST_OPERATING);
        }

        IkasanExceptionAction action = exceptionHandler.handleThrowable(name, throwable);
        // tell the error service
        logError(null, throwable, name, action);
        handleAction(action, null);
    }
    
    public void onEvent(T event)
    {
        if(this.stopping)
        {
            throw new AbortTransactionException(INITIATOR_STOPPING);
        }
        if(anesthetistOperating())
        {
            throw new AbortTransactionException(INITIATOR_ANESTHETIST_OPERATING);
        }

        FlowEvent flowEvent = eventFactory.newEvent("identifier", event);
        invokeFlow(flowEvent);
    }

    protected void completeRetryCycle()
    {
        if (retryCount!=null)
        {
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
        else if (this.messageEndpointManager.isRunning())
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
        messageEndpointManager.start();
    }

    @Override
    protected void stopInitiator()
    {
        messageEndpointManager.stop();
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
     * @param messageEndpointManager
     */
    public void setMessageEndpointManager(MessageEndpointManager messageEndpointManager)
    {
        this.messageEndpointManager = messageEndpointManager;
    }

    /**
     * Accessor for messageEndpointManager
     *
     * @return
     */
    public MessageEndpointManager getMessageEndpointManager()
    {
        return this.messageEndpointManager;
    }

    @Override
    protected Logger getLogger()
    {
        return logger;
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
            messageEndpointManager.stop();
            logger.info("Anesthetist invoked the messageListenerConatiner stop successfully.");
        }

        /**
         * Start the message listener container
         */
        private void reawaken()
        {
            if (!cancelled)
            {
                logger.info("Anesthetist restarting messageEndpointManager...");
                messageEndpointManager.start();
                logger.info("Anesthetist restarted messageEndpointManager successfully.");
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
            logger.info("stopping messageEndpointManager...");
            messageEndpointManager.stop();
            logger.info("stopped messageEndpointManager successfully.");
        }
    }
}
