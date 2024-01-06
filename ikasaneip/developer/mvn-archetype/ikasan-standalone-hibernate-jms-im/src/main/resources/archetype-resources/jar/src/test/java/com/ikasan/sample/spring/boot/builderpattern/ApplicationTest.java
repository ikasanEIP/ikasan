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

import com.ikasan.sample.person.dao.PersonDao;
import com.ikasan.sample.person.model.Person;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.annotation.Resource;
import jakarta.jms.TextMessage;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>Application</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { com.ikasan.sample.spring.boot.builderpattern.Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(locations = {
        "/transaction-pointcut-components-on-test.xml"
})
public class ApplicationTest
{
    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private JmsListenerEndpointRegistry registry;

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private PersonDao personDao;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    public IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule();

    @Before
    public void setup()
    {
        Person person = new Person();
        person.setId(1);
        person.setName("ikasan");
        person.setDobDayOfMonth(6);
        person.setDobMonthOfYear(7);
        person.setDobYear(2005);
        personDao.saveOrUpdate(person);
    }

    @After
    public void shutdown() throws IOException
    {
        flowTestRule.stopFlow();

        List<Person> persons = personDao.findAll();
        for(Person person:persons)
        {
            personDao.delete(person);
        }
    }

    @Test
    @DirtiesContext
    public void sourceFlow_test_db_to_jms() throws Exception
    {
        flowTestRule.withFlow(moduleUnderTest.getFlow("dbToJMSFlow"));

        // Get MessageListenerVerifier and start the listener
        final MessageListenerVerifier messageListenerVerifierTarget = new MessageListenerVerifier(brokerUrl, "jms.topic.test", registry);
        messageListenerVerifierTarget.start();

        // Setup component expectations
        flowTestRule.consumer("DB Consumer")
            .filter("My Filter")
            .splitter("Split list")
            .converter("Person to XML")
            .producer("JMS Producer");

        flowTestRule.startFlow();
        flowTestRule.sleep(1000L);
        flowTestRule.fireScheduledConsumer();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(1000L);

        flowTestRule.assertIsSatisfied();

        // Set expectation
        String expectedPersonXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><id>1</id><name>ikasan</name><dobDayOfMonth>6</dobDayOfMonth><dobMonthOfYear>7</dobMonthOfYear><dobYear>2005</dobYear></person>";
        assertTrue(messageListenerVerifierTarget.getCaptureResults().size()>=1);
        assertEquals(((TextMessage)messageListenerVerifierTarget.getCaptureResults().get(0)).getText(), expectedPersonXml);
    }

    @Test
    @DirtiesContext
    public void targetFlow_test_jms_to_db() throws Exception
    {
        flowTestRule.withFlow(moduleUnderTest.getFlow("jmsToDbFlow"));

        // update flow consumer  with file producer name
        SpringMessageConsumerConfiguration jmsConfiguration = flowTestRule.getComponentConfig("JMS Consumer",SpringMessageConsumerConfiguration.class);
        jmsConfiguration.setDestinationJndiName("private.file.queue.test");

        // Setup component expectations
        flowTestRule.consumer("JMS Consumer")
            .converter("XML to Person")
            .producer("DB Producer");

        flowTestRule.startFlow();
        flowTestRule.sleep(1000L);

        // Prepare test data
        String personXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><id>1</id><name>ikasan</name><dobDayOfMonth>6</dobDayOfMonth><dobMonthOfYear>7</dobMonthOfYear><dobYear>2005</dobYear></person>";
        System.out.println("Sending a JMS message.[" + personXml + "]");
        jmsTemplate.convertAndSend("private.file.queue.test", personXml);

        flowTestRule.sleep(1000L);

        flowTestRule.assertIsSatisfied();

        List<Person> persons = personDao.findAll();
        assertNotNull("One person should exist", persons);
        assertTrue("One person should exist, but found " + persons.size(), persons.size() == 1);
    }

}
