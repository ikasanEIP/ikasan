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
package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.OnException;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.spec.component.transformation.Translator;

import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.filter.FilterException;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import javax.jms.DeliveryMode;
import javax.jms.Session;
import java.io.File;
import java.util.List;

import static org.springframework.jms.listener.DefaultMessageListenerContainer.CACHE_CONNECTION;

/**
 * Sample component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-quartz.xml",
        "classpath:h2-datasource-conf.xml"
} )
public class ComponentFactory
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("#{'${file.consumer.filenames}'.split(',')}")
    List<String> sourceFilenames;

    @Value("${file.consumer.cronExpression}")
    String cronExpression;

    @Value("${file.consumer.scheduledGroupName}")
    String scheduledGroupName;

    @Value("${file.consumer.scheduledName}")
    String scheduledName;

    @Value("${file.consumer.configuredResourceId}")
    String fileConsumerConfiguredResourceId;

    @Value("${file.producer.configuredResourceId}")
    String fileProducerConfiguredResourceId;

    @Value("${file.producer.filename}")
    String targetFilename;

    @Value("${jms.producer.configuredResourceId}")
    String jmsProducerConfiguredResourceId;

    @Value("${jms.provider.url}")
    private String jmsProviderUrl;

    /**
     * Return an instance of a configured file consumer
     * @return
     */
    Consumer getFileConsumer()
    {
        return builderFactory.getComponentBuilder().fileConsumer()
                .setCronExpression(cronExpression)
                .setScheduledJobGroupName(scheduledGroupName)
                .setScheduledJobName(scheduledName)
                .setFilenames(sourceFilenames)
                .setLogMatchedFilenames(true)
                .setConfiguredResourceId(fileConsumerConfiguredResourceId)
                .build();
    }

    Producer getFileProducer()
    {
        return builderFactory.getComponentBuilder().fileProducer()
                .setConfiguredResourceId(fileProducerConfiguredResourceId)
                .setFilename(targetFilename)
                .setOverwrite(true)
                .build();
    }

    Consumer getJmsConsumer()
    {
        ActiveMQXAConnectionFactory connectionFactory = new ActiveMQXAConnectionFactory(jmsProviderUrl);

        return builderFactory.getComponentBuilder().jmsConsumer()
                .setConnectionFactory(connectionFactory)
                .setDestinationJndiName("jms.topic.test")
                .setDurableSubscriptionName("testDurableSubscription")
                .setDurable(true)
                .setAutoContentConversion(true)
                .setAutoSplitBatch(true)
                .setBatchMode(false)
                .setBatchSize(1)
                .setCacheLevel(CACHE_CONNECTION)
                .setConcurrentConsumers(1)
                .setMaxConcurrentConsumers(1)
                .setSessionAcknowledgeMode(Session.SESSION_TRANSACTED)
                .setSessionTransacted(true)
                .setPubSubDomain(false)
                .build();
    }


    Filter getFilter()
    {
        MyFilter myFilter = new MyFilter();
        myFilter.setConfiguredResourceId("myFilterPoJo");
        myFilter.setConfiguration( new MyFilterConfiguration() );
        return myFilter;
    }

    Producer getJmsProducer()
    {
        ActiveMQXAConnectionFactory connectionFactory = new ActiveMQXAConnectionFactory(jmsProviderUrl);

        return builderFactory.getComponentBuilder().jmsProducer()
                .setConfiguredResourceId(jmsProducerConfiguredResourceId)
                .setDestinationJndiName("jms.topic.test")
                .setConnectionFactory(connectionFactory)
                .setSessionAcknowledgeMode(Session.SESSION_TRANSACTED)
                .setSessionTransacted(true)
                .setPubSubDomain(false)
                .setDeliveryPersistent(true)
                .setDeliveryMode(DeliveryMode.PERSISTENT)
                .setExplicitQosEnabled(true)
                .setMessageIdEnabled(true)
                .setMessageTimestampEnabled(true)
                .build();
    }

    ExceptionResolver getSourceFlowExceptionResolver()
    {
        return builderFactory.getExceptionResolverBuilder().addExceptionToAction(TransformationException.class, OnException.excludeEvent()).build();
    }

    Converter getSourceFileConverter()
    {
        return new SourceFileConverter();
    }

    class SourceFileConverter implements Converter<List<File>,String>
    {
        @Override
        public String convert(List<File> files) throws TransformationException
        {
            File file = files.get(0);
            if(file.getName().startsWith("err"))
            {
                throw new TransformationException("Filename started with 'err'");
            }
            return file.getName();
        }
    }

    class MyFilter implements Filter, ConfiguredResource<MyFilterConfiguration>
    {
        String configuredResourceId;
        MyFilterConfiguration configuration;

        @Override
        public Object filter(Object message) throws FilterException {
            return message;
        }

        @Override
        public String getConfiguredResourceId() {
            return configuredResourceId;
        }

        @Override
        public void setConfiguredResourceId(String configuredResourceId) {
            this.configuredResourceId = configuredResourceId;
        }

        @Override
        public MyFilterConfiguration getConfiguration() {
            return configuration;
        }

        @Override
        public void setConfiguration(MyFilterConfiguration configuration) {
            this.configuration = configuration;
        }
    }

}
