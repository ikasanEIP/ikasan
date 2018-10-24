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
package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.ikasan.trigger.model.Trigger;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.SocketUtils;

import javax.jms.TextMessage;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * This test class supports the <code>SimpleExample</code> class.
 * 
 * @author Ikasan Development Team
 */

public class ApplicationTest
{
    public EmbeddedActiveMQBroker broker;

    private IkasanApplication ikasanApplication;

    private ErrorReportingService errorReportingService;

    private ExclusionManagementService exclusionManagementService;

    private WiretapService wiretapService;

    private static String SAMPLE_MESSAGE = "Hello world!";

    private Flow flowUUT;

    @Before public void setup()
    {
        broker = new EmbeddedActiveMQBroker();

        int randomeValue = new Random(10).nextInt();
        String brokerName = "embedded-broker"+randomeValue;

        broker.setBrokerName(brokerName);

        broker.start();
        // startup spring context
        String[] args = { "--server.port="+ SocketUtils.findAvailableTcpPort(8000,9000),
                "--jms.broker.name="+brokerName
        };

        ikasanApplication = IkasanApplicationFactory.getIkasanApplication(Application.class, args);
        System.out.println("Check is module healthy.");

        // / you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = ikasanApplication.getBean(Module.class);
        flowUUT = (Flow) module.getFlow("Jms Sample Flow");

        // get hold of errorReportingService
        ErrorReportingServiceFactory errorReportingServiceFactory = ikasanApplication
                .getBean("errorReportingServiceFactory", ErrorReportingServiceFactory.class);
        errorReportingService = errorReportingServiceFactory.getErrorReportingService();

        // get hold of errorReportingService
        exclusionManagementService = ikasanApplication
                .getBean("exclusionManagementService", ExclusionManagementService.class);

        // get hold of errorReportingService
        wiretapService = ikasanApplication.getBean("wiretapService", WiretapService.class);

        // add wiretap to flow
        Trigger trigger = new Trigger(module.getName(), flowUUT.getName(), "after", "wiretapJob", "JMS Consumer",
                new HashMap<String, String>()
                {{put("timeToLive", "100");}});
        JobAwareFlowEventListener wiretapListener = ikasanApplication.getBean(JobAwareFlowEventListener.class);
        wiretapListener.addDynamicTrigger(trigger);

        // Prepare test data
        JmsTemplate jmsTemplate = ikasanApplication.getBean(JmsTemplate.class);
        String message = SAMPLE_MESSAGE;
        System.out.println("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);
    }

    @After
    public void teardown()
    {
        ikasanApplication.close();
        broker.stop();
    }

    @Test
    public void test_successful_message_processing() throws Exception
    {

        // Get MessageListenerVerifier and start it
        MessageListenerVerifier messageListenerVerifierTarget =  ikasanApplication.getBean("messageListenerVerifierTarget",MessageListenerVerifier.class);
        messageListenerVerifierTarget.start();

        // Perform Test
        startFlow();


        // Set expectation
        assertEquals(1, messageListenerVerifierTarget.getCaptureResults().size());
        assertEquals(((TextMessage)messageListenerVerifierTarget.getCaptureResults().get(0)).getText(),
            SAMPLE_MESSAGE);

        //verify wiretap
        PagedSearchResult<WiretapEvent> wiretaps = (PagedSearchResult<WiretapEvent>) wiretapService.findWiretapEvents(0,1,null,true, null, "", "",null,null,null,null,null);
        assertEquals(1, wiretaps.getPagedResults().size());
        assertEquals(wiretaps.getPagedResults().get(0).getEvent(), SAMPLE_MESSAGE);

    }

    @Test
    public void test_exclusion() throws Exception
    {

        // Prepare test data

        // setup custome broker to throw an exception
        ExceptionGenerationgBroker exceptionGenerationgBroker = (ExceptionGenerationgBroker) flowUUT.getFlowElement("Exception Generating Broker").getFlowComponent();
        exceptionGenerationgBroker.setShouldThrowExclusionException(true);

        // Perform Test
        startFlow();

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

        //verify wiretap
        PagedSearchResult<WiretapEvent> wiretaps = (PagedSearchResult<WiretapEvent>) wiretapService.findWiretapEvents(0,1,null,true, null, "", "",null,null,null,null,null);
        assertEquals(0, wiretaps.getPagedResults().size());

    }

    @Test
    public void test_flow_in_recovery() throws Exception
    {

        // Prepare test data

        // setup custome broker to throw an exception
        ExceptionGenerationgBroker exceptionGenerationgBroker = (ExceptionGenerationgBroker) flowUUT.getFlowElement("Exception Generating Broker").getFlowComponent();
        exceptionGenerationgBroker.setShouldThrowRecoveryException(true);

        // Perform Test
        flowUUT.start();
        pause(5000);
        assertEquals("recovering",flowUUT.getState());

        flowUUT.stop();
        pause(2000);
        assertEquals("stopped",flowUUT.getState());


        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(EndpointException.class.getName(), error.getExceptionClass());
        assertEquals("Retry (delay=10000, maxRetries=-1)", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());
        
        //verify wiretap was not stored to DB
        PagedSearchResult<WiretapEvent> wiretaps = (PagedSearchResult<WiretapEvent>) wiretapService.findWiretapEvents(0,1,null,true, null, "", "",null,null,null,null,null);
        assertEquals(0, wiretaps.getPagedResults().size());

    }

    @Test
    public void test_flow_stopped_in_error() throws Exception
    {

        // Prepare test data

        // setup custome broker to throw an exception
        ExceptionGenerationgBroker exceptionGenerationgBroker = (ExceptionGenerationgBroker) flowUUT.getFlowElement("Exception Generating Broker").getFlowComponent();
        exceptionGenerationgBroker.setShouldThrowStoppedInErrorException(true);

        // Perform Test
        flowUUT.start();
        pause(5000);
        assertEquals("stoppedInError",flowUUT.getState());


        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(RuntimeException.class.getName(), error.getExceptionClass());
        assertEquals("Stop", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());


        //verify wiretap was not stored to DB
        PagedSearchResult<WiretapEvent> wiretaps = (PagedSearchResult<WiretapEvent>) wiretapService.findWiretapEvents(0,1,null,true, null, "", "",null,null,null,null,null);
        assertEquals(0, wiretaps.getPagedResults().size());

    }

    
    private void startFlow(){
        // start flow
        flowUUT.start();

        pause(5000);
        assertEquals("running",flowUUT.getState());
        flowUUT.stop();
        pause(2000);
        assertEquals("stopped",flowUUT.getState());

    }

    /**
     * Sleep for value in millis
     * @param value
     */
    private void pause(long value)
    {
        try
        {
            Thread.sleep(value);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
