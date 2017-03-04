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
package org.springframework.jms.listener;

import org.ikasan.component.endpoint.jms.spring.consumer.IkasanListMessage;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.spec.exclusion.ExclusionService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

/**
 * Functional unit test cases for <code>IkasanMessageListenerContainer</code>.
 * 
 * @author Ikasan Developmnet Team
 */
public class IkasanMessageListenerContainerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    // mocked Message Consumer
    MessageConsumer messageConsumer = mockery.mock(MessageConsumer.class);

    // mocked Configuration
    SpringMessageConsumerConfiguration configuration = mockery.mock(SpringMessageConsumerConfiguration.class);

    // mocked ExclusionService
    ExclusionService exclusionService = mockery.mock(ExclusionService.class);

    // mocked jmsMessage
    Message jmsMessage = mockery.mock(Message.class);

    /**
     * Test
     */
    @Test
    public void test_successful_receive_no_batching_no_exclusions_no_split() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                // dont care about this one
                ignoring(messageConsumer);

                exactly(1).of(configuration).isBatchMode();
                will(returnValue(false));
            }
        });

        IkasanMessageListenerContainer listener = new IkasanMessageListenerContainer();
        listener.setExclusionService(exclusionService);
        listener.setConfiguration(configuration);

        Message msg = listener.receiveMessage(messageConsumer);
        Assert.assertTrue(msg instanceof Message);
        Assert.assertFalse(msg instanceof IkasanListMessage);

        mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_successful_receive_no_batching_with_exclusions_no_split() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                // dont care about this one
                ignoring(messageConsumer);

                exactly(1).of(configuration).isBatchMode();
                will(returnValue(false));
            }
        });

        IkasanMessageListenerContainer listener = new IkasanMessageListenerContainer();
        listener.setExclusionService(exclusionService);
        listener.setConfiguration(configuration);

        Message msg = listener.receiveMessage(messageConsumer);
        Assert.assertTrue(msg instanceof Message);
        Assert.assertFalse(msg instanceof IkasanListMessage);

        mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_successful_receive_no_batching_no_exclusions_with_split() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                // dont care about this one
                ignoring(messageConsumer);

                exactly(1).of(configuration).isBatchMode();
                will(returnValue(false));
            }
        });

        IkasanMessageListenerContainer listener = new IkasanMessageListenerContainer();
        listener.setExclusionService(exclusionService);
        listener.setConfiguration(configuration);

        Message msg = listener.receiveMessage(messageConsumer);
        Assert.assertTrue(msg instanceof Message);
        Assert.assertFalse(msg instanceof IkasanListMessage);

        mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_successful_receive_no_batching_with_exclusions_with_split() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                // dont care about this one
                ignoring(messageConsumer);

                exactly(1).of(configuration).isBatchMode();
                will(returnValue(false));
            }
        });

        IkasanMessageListenerContainer listener = new IkasanMessageListenerContainer();
        listener.setExclusionService(exclusionService);
        listener.setConfiguration(configuration);

        Message msg = listener.receiveMessage(messageConsumer);
        Assert.assertTrue(msg instanceof Message);
        Assert.assertFalse(msg instanceof IkasanListMessage);

        mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_successful_receive_with_batching_no_exclusions_no_split() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(messageConsumer).receive(1000L);
                will(returnValue(jmsMessage));

                exactly(1).of(jmsMessage).getJMSMessageID();
                will(returnValue("jmsId"));

                exactly(1).of(configuration).isBatchMode();
                will(returnValue(true));

                exactly(1).of(configuration).getBatchSize();
                will(returnValue(1));

                exactly(1).of(exclusionService).isBlackListEmpty();
                will(returnValue(true));
            }
        });

        IkasanMessageListenerContainer listener = new IkasanMessageListenerContainer();
        listener.setExclusionService(exclusionService);
        listener.setConfiguration(configuration);

        Message msg = listener.receiveMessage(messageConsumer);
        Assert.assertTrue(msg instanceof Message);
        Assert.assertTrue(msg instanceof IkasanListMessage);
        Assert.assertEquals("jmsId:1", msg.getJMSMessageID());
        mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_successful_receive_with_batching_no_exclusions_with_split() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(messageConsumer).receive(1000L);
                will(returnValue(jmsMessage));

                exactly(1).of(jmsMessage).getJMSMessageID();
                will(returnValue("jmsId"));

                exactly(1).of(configuration).isBatchMode();
                will(returnValue(true));

                exactly(1).of(configuration).getBatchSize();
                will(returnValue(1));

                exactly(1).of(exclusionService).isBlackListEmpty();
                will(returnValue(true));
            }
        });

        IkasanMessageListenerContainer listener = new IkasanMessageListenerContainer();
        listener.setExclusionService(exclusionService);
        listener.setConfiguration(configuration);

        Message msg = listener.receiveMessage(messageConsumer);
        Assert.assertTrue(msg instanceof Message);
        Assert.assertTrue(msg instanceof IkasanListMessage);
        Assert.assertEquals("jmsId:1", msg.getJMSMessageID());
        mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_successful_receive_with_batching_with_exclusions_no_split_batchLimitHit() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(configuration).isBatchMode();
                will(returnValue(true));

                exactly(1).of(exclusionService).isBlackListEmpty();
                will(returnValue(false));

                exactly(1).of(configuration).isAutoSplitBatch();
                will(returnValue(false));

                exactly(2).of(messageConsumer).receive(1000L);
                will(returnValue(jmsMessage));

                exactly(2).of(configuration).getBatchSize();
                will(returnValue(2));

                exactly(1).of(jmsMessage).getJMSMessageID();
                will(returnValue("jmsId"));

                exactly(1).of(exclusionService).isBlackListed("jmsId:1");
                will(returnValue(false));

                exactly(1).of(jmsMessage).getJMSMessageID();
                will(returnValue("jmsId"));
            }
        });

        IkasanMessageListenerContainer listener = new IkasanMessageListenerContainer();
        listener.setExclusionService(exclusionService);
        listener.setConfiguration(configuration);

        Message msg = listener.receiveMessage(messageConsumer);
        Assert.assertTrue(msg instanceof Message);
        Assert.assertTrue(msg instanceof IkasanListMessage);
        Assert.assertEquals("jmsId:2", msg.getJMSMessageID());
        mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_successful_receive_with_batching_with_exclusions_no_split_exclusionOnBlacklistHit() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(configuration).isBatchMode();
                will(returnValue(true));

                exactly(1).of(exclusionService).isBlackListEmpty();
                will(returnValue(false));

                exactly(1).of(configuration).isAutoSplitBatch();
                will(returnValue(false));

                exactly(2).of(messageConsumer).receive(1000L);
                will(returnValue(jmsMessage));

                exactly(2).of(configuration).getBatchSize();
                will(returnValue(3));

                exactly(2).of(jmsMessage).getJMSMessageID();
                will(returnValue("jmsId"));

                exactly(1).of(exclusionService).isBlackListed("jmsId:1");
                will(returnValue(false));

                exactly(1).of(exclusionService).isBlackListed("jmsId:2");
                will(returnValue(true));

                exactly(1).of(jmsMessage).getJMSMessageID();
                will(returnValue("jmsId"));
            }
        });

        IkasanMessageListenerContainer listener = new IkasanMessageListenerContainer();
        listener.setExclusionService(exclusionService);
        listener.setConfiguration(configuration);

        Message msg = listener.receiveMessage(messageConsumer);
        Assert.assertTrue(msg instanceof Message);
        Assert.assertTrue(msg instanceof IkasanListMessage);
        Assert.assertEquals("jmsId:2", msg.getJMSMessageID());
        mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_successful_receive_with_batching_with_exclusions_with_split_batchLimitHit() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(configuration).isBatchMode();
                will(returnValue(true));

                exactly(1).of(exclusionService).isBlackListEmpty();
                will(returnValue(false));

                exactly(1).of(configuration).isAutoSplitBatch();
                will(returnValue(true));

                exactly(1).of(messageConsumer).receive(1000L);
                will(returnValue(jmsMessage));

                exactly(1).of(jmsMessage).getJMSMessageID();
                will(returnValue("jmsId"));            }
        });

        IkasanMessageListenerContainer listener = new IkasanMessageListenerContainer();
        listener.setExclusionService(exclusionService);
        listener.setConfiguration(configuration);

        Message msg = listener.receiveMessage(messageConsumer);
        Assert.assertTrue(msg instanceof Message);
        Assert.assertTrue(msg instanceof IkasanListMessage);
        Assert.assertEquals("jmsId:1", msg.getJMSMessageID());
        mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_successful_receive_with_batching_with_exclusions_with_split_exclusionOnBlacklistHit() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(configuration).isBatchMode();
                will(returnValue(true));

                exactly(1).of(exclusionService).isBlackListEmpty();
                will(returnValue(false));

                exactly(1).of(configuration).isAutoSplitBatch();
                will(returnValue(true));

                exactly(1).of(messageConsumer).receive(1000L);
                will(returnValue(jmsMessage));

                exactly(1).of(jmsMessage).getJMSMessageID();
                will(returnValue("jmsId"));
            }
        });

        IkasanMessageListenerContainer listener = new IkasanMessageListenerContainer();
        listener.setExclusionService(exclusionService);
        listener.setConfiguration(configuration);

        Message msg = listener.receiveMessage(messageConsumer);
        Assert.assertTrue(msg instanceof Message);
        Assert.assertTrue(msg instanceof IkasanListMessage);
        Assert.assertEquals("jmsId:1", msg.getJMSMessageID());
        mockery.assertIsSatisfied();
    }

}