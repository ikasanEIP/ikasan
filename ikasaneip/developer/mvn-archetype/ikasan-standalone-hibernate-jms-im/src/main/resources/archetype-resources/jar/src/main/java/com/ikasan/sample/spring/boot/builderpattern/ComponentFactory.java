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

import com.ikasan.sample.person.PersonMessageProvider;
import com.ikasan.sample.person.model.Person;
import com.ikasan.sample.person.service.PersonService;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.OnException;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.filter.FilterException;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Session;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.jms.listener.DefaultMessageListenerContainer.CACHE_CONNECTION;

/**
 * Sample component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-jms.xml",
        "classpath:personDB-conf.xml",
        "classpath:h2-datasource-conf.xml"
} )
public class ComponentFactory
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${db.consumer.cronExpression}")
    String cronExpression;

    @Value("${db.consumer.configuredResourceId}")
    String dbConsumerConfiguredResourceId;

    @Value("${jms.producer.configuredResourceId}")
    String jmsProducerConfiguredResourceId;

    @Value("${jms.provider.url}")
    private String jmsProviderUrl;

    @Resource
    PersonMessageProvider personMessageProvider;

    @Resource
    PersonService personService;

    /**
     * Return an instance of a configured file consumer
     * @return
     */
    Consumer getDBConsumer()
    {
        return builderFactory.getComponentBuilder().scheduledConsumer()
                .setCronExpression(cronExpression)
                .setConfiguredResourceId(dbConsumerConfiguredResourceId)
                .setMessageProvider(personMessageProvider)
                .build();
    }

    /**
     * Return an instance of a configured file consumer
     * @return
     */
    Producer getDBProducer()
    {
        return new DBProducer(personService);
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

    Converter getObjectToXmlStringConverter()
    {
        return builderFactory.getComponentBuilder().objectToXmlStringConverter()
                .setObjectClass(Person.class)
                .build();
    }

    Converter getXmlToObjectConverter()
    {
        return builderFactory.getComponentBuilder().xmlStringToObjectConverter()
                .setClassToBeBound(com.ikasan.sample.person.model.Person.class)
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

    Splitter getListSplitter()
    {
        return builderFactory.getComponentBuilder().listSplitter().build();
    }

    class DBProducer implements Producer<Person>
    {
        /** required service */
        PersonService personService;

        /**
         * Constructor
         * @param personService
         */
        DBProducer(PersonService personService)
        {
            this.personService = personService;
            if(personService == null)
            {
                throw new IllegalArgumentException("personService cannot be 'null'");
            }
        }

        @Override
        public void invoke(Person person) throws EndpointException
        {
            personService.update(person);
        }
    }

    class MyFilter implements Filter<List<Person>>, ConfiguredResource<MyFilterConfiguration>
    {
        private final Logger logger = LoggerFactory.getLogger(MyFilter.class);

        String configuredResourceId;
        MyFilterConfiguration configuration;

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

        @Override
        public List<Person> filter(List<Person> people) throws FilterException
        {
            List<Person> peopleOldEnough = new ArrayList<Person>();

            for(Person person:people)
            {
                try
                {
                    if(configuration.ageRestriction < person.getAge())
                    {
                        peopleOldEnough.add(person);
                    }
                }
                catch(Exception e)
                {
                    logger.info("Unable to calculate age for " + person.getName(), e);
                }
            }

            if(peopleOldEnough.size() > 0)
            {
                return peopleOldEnough;
            }

            return null;
        }
    }

}
