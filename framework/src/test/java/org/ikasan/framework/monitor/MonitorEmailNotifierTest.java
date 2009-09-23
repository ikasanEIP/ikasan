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

import javax.mail.internet.MimeMessage;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * This test class supports the <code>MonitorEmailNotifier</code> class.
 * 
 * @author Ikasan Development Team
 */
public class MonitorEmailNotifierTest
{
    /** Interfaces mockery */
    Mockery interfaceMockery = new Mockery();

    /** The mocked JavaMailSender instance */
    private JavaMailSender mockMailSender = this.interfaceMockery.mock(JavaMailSender.class, "mailSender");

    /** Real mailSender to create real email message */
    final JavaMailSender realMailSender = new JavaMailSenderImpl();

    /** The mocked MailMessage */
    private MimeMessage email = this.realMailSender.createMimeMessage();

    /** The object to be tested */
    MonitorEmailNotifier monitor;

    /**
     * Set up runs before each test
     */
    @Before
    public void setUp()
    {
        this.monitor = new MonitorEmailNotifier("monitorName", this.mockMailSender, "recipient@dummy", "testEnvironment");
        this.monitor.setBody("Test email notifier");
        this.monitor.setFrom("sender@dummy");
    }

    /**
     * Test notify method when Initiator notifies monitor with <code>null</code>
     * state. In such case, the monitor must send an email warning of potential
     * problems.
     */
    @Test
    public void test_notifyWithNullState()
    {
        this.interfaceMockery.checking(new Expectations()
        {
            {
                one(mockMailSender).createMimeMessage();
                will(returnValue(email));
                one(mockMailSender).send(with(any(MimeMessage.class)));
            }
        });
        this.monitor.notify(null);
    }

    /**
     * Test notify method when Initiator notifies monitor with an acceptable
     * state: i.e. running, stopped, or recovering. In such case, the monitor
     * must not send any email.
     */
    @Test
    public void test_notifyWithNotErrorState()
    {
        this.interfaceMockery.checking(new Expectations()
        {
            {
                one(mockMailSender).createMimeMessage();
                will(returnValue(email));
            }
        });
        this.monitor.notify("running");
    }

    /**
     * Test notify method when Initiator notifies monitor with error state. In
     * this case, the monitor must send an email.
     */
    @Test
    public void test_notifyWithErrorState()
    {
        this.interfaceMockery.checking(new Expectations()
        {
            {
                one(mockMailSender).createMimeMessage();
                will(returnValue(email));
                one(mockMailSender).send(with(any(MimeMessage.class)));
            }
        });
        this.monitor.notify("stoppedInError");
    }

    /**
     * Test notify method when sending an email message fails. In this case, the
     * <code>Exception</code> is swallowed as we do not want to disturb the main
     * flow we are monitoring.
     */
    @Test
    public void test_notifyWillSwallowMailException()
    {
        this.interfaceMockery.checking(new Expectations()
        {
            {
                one(mockMailSender).createMimeMessage();
                will(returnValue(email));
                one(mockMailSender).send(with(any(MimeMessage.class)));
                will(throwException(new MailSendException("error")));
            }
        });
        this.monitor.notify("stoppedInError");
        this.interfaceMockery.assertIsSatisfied();
    }

    /**
     * Teardown after each run
     */
    @After
    public void tearDown()
    {
        this.interfaceMockery.assertIsSatisfied();
    }
}
