/* 
 * $Id: $
 * $URL: $
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

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * This test class supports the <code>SimpleExample</code> class.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MyApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JmsFlowTest
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(JmsFlowTest.class);

    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private JmsListenerEndpointRegistry registry;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    private static String SAMPLE_MESSAGE = "Hello world!";

    public IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule();

    @Before
    public void setup(){

        flowTestRule.withFlow(moduleUnderTest.getFlow("Jms Flow"));
    }

    @Test
    public void test_Jms_Flow()
    {

        // Prepare test data
        final MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "target", registry);
        messageListenerVerifier.start();

        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        //Setup component expectations
        flowTestRule.consumer("consumer")
            .producer("producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(2000L);

        flowTestRule.assertIsSatisfied();

        assertEquals(1,messageListenerVerifier.getCaptureResults().size());

    }

}
