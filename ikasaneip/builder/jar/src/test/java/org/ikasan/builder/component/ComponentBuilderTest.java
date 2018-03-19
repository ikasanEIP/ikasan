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

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.builder.component.endpoint.FileConsumerBuilder;
import org.ikasan.builder.component.endpoint.FileProducerBuilder;
import org.ikasan.component.endpoint.util.consumer.EventGeneratingConsumer;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.filter.duplicate.model.FilterEntryConverter;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.MessageListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.Scheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;

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
    final DuplicateFilterService duplicateFilterService = mockery.mock(DuplicateFilterService.class, "mockDuplicateFilterService");
    final FilterEntryConverter filterEntryConverter = mockery.mock(FilterEntryConverter.class, "mockFilterEntryConverter");

    final BaseFileTransferDao baseFileTransferDao = mockery.mock(BaseFileTransferDao.class, "mockBaseFileTransferDao");
    final FileChunkDao fileChunkDao = mockery.mock(FileChunkDao.class, "mockFileChunkDao");
    final TransactionalResourceCommandDAO transactionalResourceCommandDAO = mockery.mock(TransactionalResourceCommandDAO.class, "mockTransactionalResourceCommandDAO");
    final MessageListener messageListener = mockery.mock(MessageListener.class, "mockMessageListener");

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
    public void test_successful_fileConsumer()
    {
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

        FileConsumerBuilder fileConsumerBuilder = componentBuilder.fileConsumer();

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_fileProducer()
    {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);

        FileProducerBuilder fileProducerBuilder = componentBuilder.fileProducer();

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_sftpConsumer() {
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

                oneOf(applicationContext).getBean(JtaTransactionManager.class);
                will(returnValue(jtaTransactionManager));

                oneOf(applicationContext).getBean(BaseFileTransferDao.class);
                will(returnValue(baseFileTransferDao));

                oneOf(applicationContext).getBean(FileChunkDao.class);
                will(returnValue(fileChunkDao));

                oneOf(applicationContext).getBean(TransactionalResourceCommandDAO.class);
                will(returnValue(transactionalResourceCommandDAO));
            }
        });

        componentBuilder.sftpConsumer();

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_sftpProducer() {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(applicationContext).getBean(JtaTransactionManager.class);
                will(returnValue(jtaTransactionManager));

                oneOf(applicationContext).getBean(BaseFileTransferDao.class);
                will(returnValue(baseFileTransferDao));

                oneOf(applicationContext).getBean(FileChunkDao.class);
                will(returnValue(fileChunkDao));

                oneOf(applicationContext).getBean(TransactionalResourceCommandDAO.class);
                will(returnValue(transactionalResourceCommandDAO));
            }
        });

        componentBuilder.sftpProducer();

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_ftpConsumer() {
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

                oneOf(applicationContext).getBean(JtaTransactionManager.class);
                will(returnValue(jtaTransactionManager));

                oneOf(applicationContext).getBean(BaseFileTransferDao.class);
                will(returnValue(baseFileTransferDao));

                oneOf(applicationContext).getBean(FileChunkDao.class);
                will(returnValue(fileChunkDao));

                oneOf(applicationContext).getBean(TransactionalResourceCommandDAO.class);
                will(returnValue(transactionalResourceCommandDAO));
            }
        });

        componentBuilder.ftpConsumer();

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_ftpProducer() {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(applicationContext).getBean(JtaTransactionManager.class);
                will(returnValue(jtaTransactionManager));

                oneOf(applicationContext).getBean(BaseFileTransferDao.class);
                will(returnValue(baseFileTransferDao));

                oneOf(applicationContext).getBean(FileChunkDao.class);
                will(returnValue(fileChunkDao));

                oneOf(applicationContext).getBean(TransactionalResourceCommandDAO.class);
                will(returnValue(transactionalResourceCommandDAO));
            }
        });

        componentBuilder.ftpProducer();

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
                oneOf(applicationContext).getBean(TransactionManager.class);
                will(returnValue(transactionManager));

            }
        });

        componentBuilder.jmsProducer();

        mockery.assertIsSatisfied();
    }


    /**
     * Test listSplitter.
     */
    @Test
    public void test_successful_listSplitter()
    {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);
        Splitter splitter = componentBuilder.listSplitter().build();
        assertTrue("instance should be a Splitter", splitter instanceof Splitter);
    }

    /**
     * Test devNullProducer builder.
     */
    @Test
    public void test_successful_devNullProducer()
    {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);
        Producer producer = componentBuilder.devNullProducer().build();
        assertTrue("instance should be a Producer", producer instanceof Producer);
    }

    /**
     * Test logProducer builder.
     */
    @Test
    public void test_successful_logProducer()
    {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);
        Producer producer = componentBuilder.logProducer().build();
        assertTrue("instance should be a Producer", producer instanceof Producer);
    }

    /**
     * Test eventGeneratingConsumer builder.
     */
    @Test
    public void test_successful_eventGeneratingConsumer()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                oneOf(applicationContext).getBean(AopProxyProvider.class);
                will(returnValue(aopProxyProvider));

                oneOf(aopProxyProvider).applyPointcut(with(any(String.class)), with(any(EventGeneratingConsumer.class)));
                will(returnValue(messageListener));
            }
        });

        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);
        Consumer consumer = componentBuilder.eventGeneratingConsumer().build();
        assertTrue("instance should be a Consumer", consumer instanceof Consumer);
        mockery.assertIsSatisfied();
    }

    /**
     * Test messageFilterBuilder.
     */
    @Test
    public void test_successful_messageFilterBuilder_withConfiguration()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(applicationContext).getBean(DuplicateFilterService.class);
                will(returnValue(duplicateFilterService));
            }
        });

        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);
        Filter filter = componentBuilder.messageFilter().setFilterEntryConverter(filterEntryConverter).setConfiguredResourceId("configuredResourceId").build();
        assertTrue("instance should be a Filter", filter instanceof Filter);

        assertTrue("configuredResourceId should be 'configuredResourceId'",  "configuredResourceId".equals(((ConfiguredResource) filter).getConfiguredResourceId()));
    }

}
