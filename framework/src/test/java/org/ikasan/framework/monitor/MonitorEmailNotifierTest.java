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
package org.ikasan.framework.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import org.hamcrest.text.StringContains;
import org.ikasan.framework.initiator.InitiatorState;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * This test class supports the {@link MonitorEmailNotifier} class.
 * 
 * @author Ikasan Development Team
 */
public class MonitorEmailNotifierTest
{
    /** Interfaces mockery */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** The mocked JavaMailSender instance */
    private final  JavaMailSender mockMailSender = this.mockery.mock(JavaMailSender.class, "mailSender");

    /** The mocked MailMessage */
    private final MimeMessage mockEmail = this.mockery.mock(MimeMessage.class, "emailMessage");

    /** The object to be tested */
    private MonitorEmailNotifier monitorToTest;

    // Various setup objects
    /** Monitor name */
    private final String monitorName = "testEmailNotifier";

    /** Runtime environment */
    private final String environment = "unitTest";

    /** Dummy test mail list 1*/
    private final String devDistributionList = "dev@dummy";

    /** Dummy test mail list 2*/
    private final String usersDistributionList = "users@dummy";

    /**
     * Monitor will send email to configured distribution list(s) if current
     * initiator state matches a configured state.
     * 
     * @throws MessagingException Thrown if error creating and/or sending email
     */
    @Test public void current_state_matches_send_email_to_distribution_lists() throws MessagingException
    {
        // Setup test objects
        final List<String> stoppedNotificationDistributionList = new ArrayList<String>();
        stoppedNotificationDistributionList.add(this.devDistributionList);
        stoppedNotificationDistributionList.add(this.usersDistributionList);

        final List<String> stoppedInErrorNotificationDistributionList = new ArrayList<String>();
        stoppedInErrorNotificationDistributionList.add(this.devDistributionList);

        final Map<String, List<String>> stateToDistributionListMap = new HashMap<String, List<String>>();
        stateToDistributionListMap.put(InitiatorState.STOPPED.getName(), stoppedNotificationDistributionList);
        stateToDistributionListMap.put(InitiatorState.ERROR.getName(), stoppedInErrorNotificationDistributionList);

        final String currentInitiatorState = InitiatorState.STOPPED.getName();

        // Setup the object to be tested
        this.monitorToTest = new MonitorEmailNotifier(this.monitorName, this.mockMailSender, stateToDistributionListMap, this.environment);

        final InternetAddress [] addresses = {new InternetAddress(this.devDistributionList), new InternetAddress(this.usersDistributionList)};

        // Setup expectations
        this.setDefaultExpectations(addresses, currentInitiatorState);

        // Run the test
        this.monitorToTest.notify(currentInitiatorState);

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * If current initiator state does not match states the monitor is configured to report on,
     * monitor will not send an email.
     */
    @Test public void no_match_for_current_state_no_email_sent()
    {
        // Create a monitor
        this.monitorToTest = new MonitorEmailNotifier(this.monitorName, this.mockMailSender, this.devDistributionList, this.environment);

        // Run the test
        this.monitorToTest.notify("a.state");

        // Make assertions
        /* Monitor should not try to send an email since it is configured to look for stoppedInError
         * and current status was 'a state'.
         */
    }

    /**
     * If monitor no state/distribution list map is configured, initiator will send emails only
     * if current status matches <code>stoppedInError</code>
     * 
     * @throws MessagingException Thrown if error creating and/or sending email
     */
    @Test public void monitor_reports_for_stoppedInError_by_default() throws MessagingException
    {
        // Setup test objects
        final List<String> stoppedNotificationDistributionList = new ArrayList<String>();
        stoppedNotificationDistributionList.add(this.devDistributionList);
        stoppedNotificationDistributionList.add(this.usersDistributionList);

        final InternetAddress [] addresses = {new InternetAddress(this.devDistributionList), new InternetAddress(this.usersDistributionList)};

        final String currentInitiatorState = InitiatorState.ERROR.getName();

        // Setup the object to be tested
        this.monitorToTest = new MonitorEmailNotifier(this.monitorName, this.mockMailSender, stoppedNotificationDistributionList, this.environment);

        // Expectations
        this.setDefaultExpectations(addresses, currentInitiatorState);

        // Run the test
        this.monitorToTest.notify(currentInitiatorState);

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * All exceptions thrown from creating or sending an email message must be swallowed
     * as this monitor must not disrupt the initiator (business flow) it is monitoring
     */
    @Test
    public void monitor_swallows_exceptions()
    {
        this.monitorToTest = new MonitorEmailNotifier("monitorName", this.mockMailSender, this.devDistributionList, "unitTest");

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(mockMailSender).createMimeMessage(); // Don't care about return object here
                one(mockMailSender).send(with(any(MimeMessage.class)));will(throwException(new MailSendException("error")));
            }
        });

        // Run the test
        this.monitorToTest.notify(InitiatorState.ERROR.getName());

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * MonitorEmailNotifier has optional attributes to set: <code>from</code> and/or <code>body</code>.
     * If <code>from</code> is set, the from header is added to email message. If <code>body</code> is set,
     * this value will be appended to default message.
     * 
     * @throws MessagingException Thrown if error creating and/or sending email
     */
    @Test public void when_optional_parameters_are_set_include_in_email_message() throws MessagingException
    {
        // Create the object to be tested
        this.monitorToTest = new MonitorEmailNotifier("monitorName", this.mockMailSender, this.devDistributionList, "unitTest");

        // Setup test objects
        final String fromEmail = "someone@dummy";
        final String configuredEmailText = "Custom text appended to default message.";
        
        final String currentInitiatorState = InitiatorState.ERROR.getName();

        final InternetAddress fromAddress = new InternetAddress(fromEmail);
        final InternetAddress [] addresses = {new InternetAddress(this.devDistributionList)};

        // Default expectations
        this.setDefaultExpectations(addresses, currentInitiatorState);
        // Expectations as result of setting optionals
        this.mockery.checking(new Expectations()
        {
            {
                //.. add sender
                one(mockEmail).setFrom(fromAddress);
            }
        });

        // Run the test
        this.monitorToTest.setFrom(fromEmail);
        this.monitorToTest.setBody(configuredEmailText);
        this.monitorToTest.notify(currentInitiatorState);

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * Cleanup after each test case
     */
    @After public void teardown()
    {
        this.monitorToTest = null;
    }

    /**
     * Setting default expectations when sending an email given a state, recipient list, and
     * expected email text.
     * 
     * @param addresses Email distribution lists
     * @param reportedState Current initiator state to report
     * 
     * @throws MessagingException Thrown if error creating and/or sending email
     */
    private void setDefaultExpectations(final InternetAddress[] addresses, final String reportedState) throws MessagingException
    {
        this.mockery.checking(new Expectations()
        {
            {
                // Because current status matches an entry in the map, create an email
                one(mockMailSender).createMimeMessage();will(returnValue(mockEmail));
                //.. add recipients
                one(mockEmail).setRecipients(with(equal(RecipientType.TO)), with(equal(addresses)));
                //.. add the subject
                one(mockEmail).setSubject(with(StringContains.containsString(environment)));
                //.. add the message body
                one(mockEmail).setText(with(StringContains.containsString(reportedState)));
                // .. then send it
                one(mockMailSender).send(mockEmail);
            }
        });
    }
}
