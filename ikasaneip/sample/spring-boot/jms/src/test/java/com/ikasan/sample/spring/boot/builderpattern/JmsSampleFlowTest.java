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
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
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
import org.ikasan.testharness.flow.rule.IkasanStandaloneFlowTestRule;
import org.ikasan.trigger.model.Trigger;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.SocketUtils;

import java.util.*;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * This test class supports the <code>SimpleExample</code> class.
 * 
 * @author Ikasan Development Team
 */

public class JmsSampleFlowTest
{

    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();


    private static String SAMPLE_MESSAGE = "Hello world!";

    public IkasanStandaloneFlowTestRule flowTestRule = new IkasanStandaloneFlowTestRule("Jms Sample Flow",Application.class);

    @Rule
    public TestRule chain = RuleChain.outerRule(broker).around(flowTestRule);

    @Test
    public void test_Jms_Sample_Flow() throws Exception
    {

        //
        // Prepare test data
        JmsTemplate jmsTemplate = flowTestRule.getIkasanApplication().getBean(JmsTemplate.class);
        String message = SAMPLE_MESSAGE;
        System.out.println("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker")
            .producer("JMS Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(5000L);

    }

}
