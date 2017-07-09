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
package org.ikasan.component.endpoint.jms.spring.consumer;

import org.ikasan.component.endpoint.jms.JmsEventIdentifierServiceImpl;
import org.ikasan.component.endpoint.jms.consumer.JmsMessageConverter;
import org.ikasan.component.endpoint.jms.consumer.MessageProvider;
import org.ikasan.exclusion.service.IsExclusionServiceAware;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.*;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.IkasanMessageListenerContainer;
import org.springframework.util.ErrorHandler;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Consumer wrapping Spring's JMS Container.
 * @author Ikasan Development Team
 */
public class JmsContainerConsumer
        implements MessageListener, ExceptionListener, ErrorHandler,
        Consumer<EventListener<?>,EventFactory>, Converter<Message,Object>,
        ManagedIdentifierService<ManagedEventIdentifierService>, ConfiguredResource<SpringMessageConsumerConfiguration>
		, ResubmissionService<Message>, IsExclusionServiceAware
{
    /** Logger instance */
    private Logger logger = LoggerFactory.getLogger(JmsContainerConsumer.class);

    /** configured Resource identifier */
    String configuredResourceId;

    /** Factory for creating the event instance to be pushed to the flow */
    EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** Handle to the flow to be invoked with the event from the above factory */
    EventListener eventListener;

    /** Underlying technical implementation providing the message */
    MessageProvider messageProvider;

    /** Service for stamping the event with a unique identifier */
    protected ManagedEventIdentifierService<?,Message> managedEventIdentifierService = new JmsEventIdentifierServiceImpl();

    /** handle to the configuration */
    private SpringMessageConsumerConfiguration configuration;
    private ExclusionService exclusionService;

    /**
     * Setter for the underlying message provider tech
     * @param messageProvider
     */
    public void setMessageProvider(MessageProvider messageProvider)
    {
        this.messageProvider = messageProvider;
    }

    @Override
    public void setListener(EventListener eventListener)
    {
        this.eventListener = eventListener;
    }

    @Override
    public void setEventFactory(EventFactory flowEventFactory)
    {
        this.flowEventFactory = flowEventFactory;
    }

    @Override
    public EventFactory getEventFactory()
    {
        return this.flowEventFactory;
    }

    @Override
    public void start()
    {
        this.messageProvider.start();
    }

    @Override
    public boolean isRunning()
    {
        return this.messageProvider.isRunning();
    }

    @Override
    public void stop()
    {
        this.messageProvider.stop();
    }

    /**
     * Invoke the eventListener with the given flowEvent.
     * @param flowEvent
     */
    protected void invoke(FlowEvent flowEvent)
    {
        if(this.eventListener == null)
        {
            throw new RuntimeException("No active eventListeners registered for flowEvent!");
        }

        this.eventListener.invoke(flowEvent);
    }

    /**
     * Invoke the eventListener with the given resubmission.
     * @param resubmission
     */
    protected void invoke(Resubmission resubmission)
    {
        if(this.eventListener == null)
        {
            throw new RuntimeException("No active eventListeners registered for resubmission event!");
        }

        this.eventListener.invoke(resubmission);
    }

    @Override
    public void onMessage(Message message)
    {
        try
        {
            if(message instanceof IkasanListMessage && configuration.isAutoSplitBatch())
            {
                IkasanListMessage msgs = (IkasanListMessage)message;
                for(Message msg:msgs)
                {
                    FlowEvent<?,?> flowEvent = flowEventFactory.newEvent(
                            ( (this.managedEventIdentifierService != null) ? this.managedEventIdentifierService.getEventIdentifier(msg) : msg.hashCode()),
                            msg);
                    invoke(flowEvent);
                }
            }
            else
            {
                FlowEvent<?,?> flowEvent = flowEventFactory.newEvent(
                        ( (this.managedEventIdentifierService != null) ? this.managedEventIdentifierService.getEventIdentifier(message) : message.hashCode()),
                        message);
                invoke(flowEvent);
            }
        }
        catch (ManagedEventIdentifierException e)
        {
            this.eventListener.invoke(e);
        }
    }
    
    /* (non-Javadoc)
	 * @see org.ikasan.spec.resubmission.ResubmissionService#submit(java.lang.Object)
	 */
	@Override
	public void submit(Message event)
	{
		logger.debug("attempting to submit event: " + event);

        try
        {
            if(event instanceof IkasanListMessage && configuration.isAutoSplitBatch())
            {
                IkasanListMessage msgs = (IkasanListMessage)event;
                for(Message msg:msgs)
                {
                    FlowEvent<?,?> flowEvent = flowEventFactory.newEvent(
                            ( (this.managedEventIdentifierService != null) ? this.managedEventIdentifierService.getEventIdentifier(msg) : msg.hashCode()),
                            msg);
                    invoke(new Resubmission(flowEvent));
                }
            }
            else
            {
                FlowEvent<?,?> flowEvent = flowEventFactory.newEvent(
                        ( (this.managedEventIdentifierService != null) ? this.managedEventIdentifierService.getEventIdentifier(event) : event.hashCode()),
                        event);
                invoke(new Resubmission(flowEvent));
            }
        }
        catch (ManagedEventIdentifierException e)
        {
            this.eventListener.invoke(e);
        }
	}

    @Override
    public void setManagedIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
    {
        this.managedEventIdentifierService = managedEventIdentifierService;
    }

    @Override
    public void onException(JMSException jmsException)
    {
        // added to work around IKASAN-739
        boolean recovered = false;
        try
        {
            if ( messageProvider instanceof IkasanMessageListenerContainer
                    && jmsException instanceof javax.jms.IllegalStateException)
            {
                IkasanMessageListenerContainer imlc = (IkasanMessageListenerContainer)messageProvider;
                imlc.recoverSharedConnection();
                recovered = true;
            }
        }
        catch (JMSException ex)
        {
            logger.warn("Unable to recover from JMSException");
        }
        finally
        {
            if(!recovered && eventListener != null)
            {
                this.eventListener.invoke(jmsException);
            }
            else
            {
                logger.error("onException reported after eventListener stopped listening.", jmsException);
            }

        }

    }

    @Override
    public void handleError(Throwable throwable)
    {
        if (throwable instanceof ForceTransactionRollbackException)
        {
            logger.info("Ignoring rethrown ForceTransactionRollbackException");
            return;
        }
        if(eventListener != null)
        {
            this.eventListener.invoke(throwable);
        }
        else
        {
            logger.error("handleError reported after eventListener stopped listening.", throwable);
        }
    }

    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public SpringMessageConsumerConfiguration getConfiguration()
    {
        if(this.messageProvider != null && this.messageProvider instanceof Configured)
        {
            return ((Configured<SpringMessageConsumerConfiguration>)this.messageProvider).getConfiguration();
        }

        return null;
    }

    @Override
    public void setConfiguration(SpringMessageConsumerConfiguration configuration)
    {
        this.configuration = configuration;
        if(this.messageProvider != null && this.messageProvider instanceof Configured)
        {
            ((Configured<SpringMessageConsumerConfiguration>)this.messageProvider).setConfiguration(configuration);
        }
    }

    @Override
    public Object convert(Message message) throws TransformationException
    {
        try
        {
            if(!this.configuration.isAutoContentConversion())
            {
                return message;
            }

            if(message instanceof IkasanListMessage)
            {
                List msgs = new ArrayList();
                for(Message msg:(IkasanListMessage)message)
                {
                    msgs.add( JmsMessageConverter.extractContent(msg) );
                }

                return msgs;
            }

            return JmsMessageConverter.extractContent(message);
        }
        catch(JMSException e)
        {
            throw new TransformationException(e);
        }
    }

    @Override
    public void setExclusionService(ExclusionService exclusionService) {

        if (messageProvider instanceof IsExclusionServiceAware) {
            ((IsExclusionServiceAware) messageProvider).setExclusionService(exclusionService);
        }

        this.exclusionService = exclusionService;
    }
}
