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
package org.ikasan.component.endpoint.util.consumer;

import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.event.ExceptionListener;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.event.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This consumer implementation provides a simple event generator to provision testing of flows quickly and easily.
 * 
 * @author Ikasan Development Team
 */
public class SimpleMessageGenerator implements MessageGenerator, Configured<EventGeneratingConsumerConfiguration>
{
    /** Logger instance */
    private static Logger logger = LoggerFactory.getLogger(SimpleMessageGenerator.class);

    /** configuration */
    private EventGeneratingConsumerConfiguration consumerConfiguration = new EventGeneratingConsumerConfiguration();

    /** message listener instance */
    private MessageListener messageListener;

    /** exception listener instance */
    private ExceptionListener exceptionListener;

    /** control the thread execution */
    private volatile boolean running;

    public void run()
    {
        setRunning(true);

        if(messageListener == null)
        {
            throw new IllegalStateException("messageListener cannot be 'null");
        }

        while(isRunning())
        {
            execute();
        }
    }

    public void execute()
    {
        long count = 0;
        while(consumerConfiguration.getMaxEventLimit() == 0 || consumerConfiguration.getMaxEventLimit() > count)
        {
            try
            {
                count++;
                this.messageListener.onMessage( "Message " + count );
                if(consumerConfiguration.getEventGenerationInterval() > 0 && count % consumerConfiguration.getEventsPerInterval() == 0)
                {
                    sleep();
                }
            }
            catch (ForceTransactionRollbackException thrownByRecoveryManager)
            {
                logger.debug("ForceTransactionRollbackException", thrownByRecoveryManager);
                //throw thrownByRecoveryManager;
            }
            catch (Throwable throwable)
            {
                if(this.exceptionListener == null)
                {
                    throw throwable;
                }

                this.exceptionListener.onException(throwable);
            }

            if( !isRunning() )
            {
                break;
            }
        }

        logger.info("eventGenerator stopped. EventLimit [" + consumerConfiguration.getMaxEventLimit() + "]");
    }

    private void sleep()
    {
        try
        {
            Thread.sleep(consumerConfiguration.getEventGenerationInterval());
        }
        catch(InterruptedException e)
        {
            logger.debug(e.getMessage(), e);
        }
    }

    @Override
    public EventGeneratingConsumerConfiguration getConfiguration()
    {
        return consumerConfiguration;
    }

    @Override
    public void setConfiguration(EventGeneratingConsumerConfiguration consumerConfiguration)
    {
        this.consumerConfiguration = consumerConfiguration;
    }

    @Override
    public void setMessageListener(MessageListener messageListener)
    {
        this.messageListener = messageListener;
    }

    @Override
    public void setExceptionListener(ExceptionListener exceptionListener)
    {
        this.exceptionListener = exceptionListener;
    }

    @Override
    public void stop()
    {
        setRunning(false);
    }

    protected void setRunning(boolean running)
    {
        this.running = running;
    }

    protected boolean isRunning()
    {
        return this.running;
    }
}

