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
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.ExceptionListener;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.spec.event.Resubmission;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * This consumer implementation provides a simple event generator to provision testing of flows quickly and easily.
 * 
 * @author Ikasan Development Team
 */
public class EventGeneratingConsumer extends AbstractConsumer
    implements ConfiguredResource<EventGeneratingConsumerConfiguration>, ResubmissionService<String>, MessageListener<String>, ExceptionListener<Throwable>
{
    /** Logger instance */
    private static Logger logger = LoggerFactory.getLogger(EventGeneratingConsumer.class);

    /** allow techEndpoint to execute in a separate thread */
    private ExecutorService executorService;

    /** handle to the future thread */
    private Future eventGeneratorThread;

    /** configuredResourceId */
    private String configuredResourceId;

    /** configuration */
    private EventGeneratingConsumerConfiguration consumerConfiguration = new EventGeneratingConsumerConfiguration();

    /** provider of messages */
    private MessageGenerator messageGenerator;

    /** resubmission event factory */
    private ResubmissionEventFactory resubmissionEventFactory;

    /**
     * Constructor
     * @param messageGenerator
     */
    public EventGeneratingConsumer(ExecutorService executorService, MessageGenerator messageGenerator)
    {
        this.executorService = executorService;
        if(executorService == null)
        {
            throw new IllegalArgumentException("executorService cannot be 'null'");
        }

        this.messageGenerator = messageGenerator;
        if(messageGenerator == null)
        {
            throw new IllegalArgumentException("messageGenerator cannot be 'null'");
        }
    }

    /**
     * Start the underlying event generator
     */
    public void start()
    {
        eventGeneratorThread = this.executorService.submit( messageGenerator );
    }

    /**
     * Stop the event generator
     */
    public void stop()
    {
        if(this.isRunning())
        {
            messageGenerator.stop();
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
        if(messageGenerator instanceof Configured)
        {
            ((Configured)messageGenerator).setConfiguration(consumerConfiguration);
        }
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
    public void onResubmission(String message)
    {
        this.eventListener.invoke( this.resubmissionEventFactory.newResubmissionEvent( flowEventFactory.newEvent(message.toString(), message) ) );
    }

    @Override
    public void setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory)
    {
        this.resubmissionEventFactory = resubmissionEventFactory;
    }

    @Override
    public void onMessage(String message)
    {
        eventListener.invoke( flowEventFactory.newEvent(message.toString(), message) );
    }

    @Override
    public void onException(Throwable throwable)
    {
        eventListener.invoke(throwable);
    }
}
