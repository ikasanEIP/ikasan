/* 
 * $Id: SchedulerFactoryTest.java 3629 2011-04-18 10:00:52Z mitcje $
 * $URL: http://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/scheduler/src/test/java/org/ikasan/scheduler/SchedulerFactoryTest.java $
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

import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import jakarta.jms.TextMessage;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This test class supports the <code>JmsSampleFlow</code> class.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JmsSampleFlowTest
{

    private static String SAMPLE_MESSAGE = "Hello world!";

    private Logger logger = LoggerFactory.getLogger(JmsSampleFlowTest.class);
    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private JmsTemplate jmsTemplate;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Resource
    private JmsListenerEndpointRegistry registry;

    @Resource
    private ErrorReportingServiceFactory errorReportingServiceFactory;

    private ErrorReportingService errorReportingService;

    @Resource
    private ExclusionManagementService exclusionManagementService;

    public IkasanFlowTestRule flowTestRule;

    @Before
    public void setup(){

        flowTestRule = new IkasanFlowTestRule();

        flowTestRule.withFlow(moduleUnderTest.getFlow("${sourceFlowName}"));

        errorReportingService = errorReportingServiceFactory.getErrorReportingService();

    }

    @After
    public void teardown(){

        // consume messages from source queue if any were left
        MessageListenerVerifier mlv = new MessageListenerVerifier(brokerUrl, "source", registry);
        mlv.start();


        flowTestRule.stopFlow();
    }

    @Test
    public void test_Jms_Sample_Flow() throws Exception
    {


        final MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "target", registry);
        messageListenerVerifier.start();

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker")
            .producer("JMS Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(1000L);

        flowTestRule.assertIsSatisfied();

        assertEquals(1, messageListenerVerifier.getCaptureResults().size());
        assertEquals(((TextMessage)messageListenerVerifier.getCaptureResults().get(0)).getText(),
            SAMPLE_MESSAGE);


    }

    @Test
    public void test_exclusion()
    {

        final MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "target", registry);
        messageListenerVerifier.start();

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        // update broker config to force exception throwing
        ExceptionGenerationgBroker exceptionGenerationgBroker = (ExceptionGenerationgBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGenerationgBroker.setShouldThrowExclusionException(true);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(2000L);

        flowTestRule.assertIsSatisfied();

        //verify no messages were published
        assertEquals(0, messageListenerVerifier.getCaptureResults().size());

        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(SampleGeneratedException.class.getName(), error.getExceptionClass());
        assertEquals("ExcludeEvent", error.getAction());

        // Verify the exclusion was stored to DB was stored in DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(1, exclusions.size());
        ExclusionEvent exclusionEvent = (ExclusionEvent) exclusions.get(0);
        assertEquals(error.getUri(), exclusionEvent.getErrorUri());


    }

    @Test
    public void test_flow_in_recovery()
    {

        final MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "target", registry);
        messageListenerVerifier.start();

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        // setup custom broker to throw an exception
        ExceptionGenerationgBroker exceptionGenerationgBroker = (ExceptionGenerationgBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGenerationgBroker.setShouldThrowRecoveryException(true);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(2000L);
        assertEquals("recovering",flowTestRule.getFlowState());

        flowTestRule.assertIsSatisfied();

        //verify no messages were published
        assertEquals(0, messageListenerVerifier.getCaptureResults().size());

        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(EndpointException.class.getName(), error.getExceptionClass());
        assertEquals("Retry (delay=10000, maxRetries=-1)", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());


    }

    @Test
    public void test_flow_stopped_in_error()
    {

        final MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "target", registry);
        messageListenerVerifier.start();

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);


        // setup custom broker to throw an exception
        ExceptionGenerationgBroker exceptionGenerationgBroker = (ExceptionGenerationgBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGenerationgBroker.setShouldThrowStoppedInErrorException(true);

        //Setup component expectations

        flowTestRule
            .withErrorEndState()
            .consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(2000L);
        assertEquals("stoppedInError",flowTestRule.getFlowState());

        flowTestRule.assertIsSatisfied();

        //verify no messages were published
        assertEquals(0, messageListenerVerifier.getCaptureResults().size());

        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(RuntimeException.class.getName(), error.getExceptionClass());
        assertEquals("Stop", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());


    }

}
