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
package org.ikasan.builder.component;

import org.hamcrest.CoreMatchers;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.component.endpoint.jms.spring.listener.ArjunaIkasanMessageListenerContainer;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Ignore;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.naming.Context;
import javax.transaction.TransactionManager;
import java.util.HashMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class ComponentBuilderTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock applicationContext
     */
    final ApplicationContext applicationContext = mockery.mock(ApplicationContext.class, "mockApplicationContext");
    final TransactionManager transactionManager = mockery.mock(TransactionManager.class, "mockTransactionManager");
    final JtaTransactionManager jtaTransactionManager = mockery.mock(JtaTransactionManager.class, "mockJtaTransactionManager");

    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");
    final AopProxyProvider aopProxyProvider = mockery.mock(AopProxyProvider.class, "mockAopProxyProvider");
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");

    @Test
    public void test_successful_scheduledConsumer() {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(applicationContext).getBean(Scheduler.class);
                will(returnValue(scheduler));
                oneOf(applicationContext).getBean(ScheduledJobFactory.class);
                will(returnValue(scheduledJobFactory));

                oneOf(applicationContext).getBean(AopProxyProvider.class);
                will(returnValue(aopProxyProvider));


            }
        });

        componentBuilder.scheduledConsumer();

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_jmsConsumer() {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(applicationContext).getBean(TransactionManager.class);
                will(returnValue(transactionManager));
                oneOf(applicationContext).getBean(JtaTransactionManager.class);
                will(returnValue(jtaTransactionManager));

                oneOf(applicationContext).getBean(AopProxyProvider.class);
                will(returnValue(aopProxyProvider));

            }
        });

        componentBuilder.jmsConsumer();

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_jmsProducer() {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);

        // expectations
        mockery.checking(new Expectations()
        {
            {


            }
        });

        componentBuilder.jmsProducer();

        mockery.assertIsSatisfied();
    }


}
