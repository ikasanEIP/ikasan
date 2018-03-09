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

import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This consumer implementation provides a simple event generator to provision testing of flows quickly and easily.
 * 
 * @author Ikasan Development Team
 */
public class EventGeneratingConsumer extends AbstractConsumer
    implements ConfiguredResource<EventGeneratingConsumerConfiguration>, ResubmissionService<String>
{
    /** Logger instance */
    private static Logger logger = LoggerFactory.getLogger(EventGeneratingConsumer.class);

    /** allow techEndpoint to execute in a separate thread */
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /** handle to the future thread */
    private Future eventGeneratorThread;

    /** configuredResourceId */
    private String configuredResourceId;

    private MessageProvider<?> messageProvider = new DefaultMessageProvider();

    /** consumer configuration */
    private EventGeneratingConsumerConfiguration consumerConfiguration = new EventGeneratingConsumerConfiguration();

    public void setTestPayloadProvider(MessageProvider messageProvider)
    {
        this.messageProvider = messageProvider;
    }

    /**
     * Start the underlying event generator
     */
    public void start()
    {
        eventGeneratorThread = this.executorService.submit( new EventGenerator() );
    }

    /**
     * Stop the event generator
     */
    public void stop()
    {
        if(this.isRunning())
        {
            this.eventGeneratorThread.cancel(true);
        }
    }

    /**
     * Is the underlying event generator actively running
     * @return isRunning
     */
    public boolean isRunning()
    {
        if(this.eventGeneratorThread == null || this.eventGeneratorThread.isCancelled() || this.eventGeneratorThread.isDone()) {return false;}
        return true;
    }

    /**
     * Getter for configuration
     * @return
     */
    public EventGeneratingConsumerConfiguration getConfiguration()
    {
        return consumerConfiguration;
    }

    /**
     * Getter for configured resourceId
     * @return
     */
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    /**
     * Setter for configuration
     * @param consumerConfiguration
     */
    public void setConfiguration(EventGeneratingConsumerConfiguration consumerConfiguration)
    {
        this.consumerConfiguration = consumerConfiguration;
    }

    /**
     * Setter for configured resource Id
     * @param configuredResourceId
     */
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public void submit(String message)
    {
        eventListener.invoke( flowEventFactory.newEvent(message.toString(), message) );
    }

    /**
     * Simple event generator class implementation.
     */
    public class EventGenerator implements Runnable
    {
        public void run()
        {
            int count = 0;
            while(consumerConfiguration.getMaxEventLimit() == 0 || consumerConfiguration.getMaxEventLimit() > count)
            {
                try
                {
                    count++;
                    Object message = messageProvider.getMessage();
                    eventListener.invoke( flowEventFactory.newEvent(message.toString(), message) );
                    if(consumerConfiguration.getEventGenerationInterval() > 0 && consumerConfiguration.getBatchsize() % count == 0)
                    {
                        try
                        {
                            Thread.sleep(consumerConfiguration.getEventGenerationInterval());
                        }
                        catch(InterruptedException e)
                        {
                            if(isRunning())
                            {
                                eventListener.invoke(e);
                            }
                            break;
                        }
                    }
                }
                catch (ForceTransactionRollbackException thrownByRecoveryManager)
                {
                    throw thrownByRecoveryManager;
                }
                catch (Throwable throwable)
                {
                    eventListener.invoke(throwable);
                }
            }

            if(isRunning() && consumerConfiguration.getMaxEventLimit() <= count)
            {
                logger.info("eventGenerator stopped after reaching configured eventLimit of [" + consumerConfiguration.getMaxEventLimit() + "]");
            }
        }
    }

    /**
     * Default test payload provider based off of the consumer configuration instance.
     */
    public class DefaultMessageProvider implements MessageProvider<String>
    {
        long count;

        @Override
        public String getMessage() {
            return "Message " + count++;
        }
    }
}
