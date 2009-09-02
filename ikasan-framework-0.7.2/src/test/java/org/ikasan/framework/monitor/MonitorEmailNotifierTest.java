/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
