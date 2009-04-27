/*
 * $Id: MonitorJNDINotifierTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/monitor/MonitorJNDINotifierTest.java $
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

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingException;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.common.component.Status;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>MonitorJNDINotifier</code> class.
 * 
 * @author Ikasan Development Team
 */
public class MonitorJNDINotifierTest
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
    
    /**
     * Mock objects
     */
    final MonitorJNDINotifier monitorJNDINotifier = mockery.mock(MonitorJNDINotifier.class);
    /** Mock context */
    final Context context = mockery.mock(Context.class);
    /** Mock name parser */
    final NameParser nameParser = mockery.mock(NameParser.class);
    /** Mock name URL */
    final Name nameUrl = mockery.mock(Name.class);

    /**
     * Real objects
     */
    final String name = "monitorName";
    
    /**
     * URL
     */
    final String url = "where/to/bind/to";

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test successful known notification and JNDI binding
     * @throws NamingException 
     */
    @Test
    public void test_successful_notificationAndJNDIBind()
        throws NamingException
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(context).bind(with(any(String.class)), with(any(Status.class)));
            }
        });

        //
        // run test
        MonitorJNDINotifier notifier = new MonitorJNDINotifier(name, context, url);
        notifier.notify("stoppedInError");
    }

    /**
     * Test successful known notification and JNDI binding
     * @throws NamingException 
     */
    @Test
    public void test_successful_unknownNotificationAndJNDIBind()
        throws NamingException
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(context).bind(with(any(String.class)), with(any(Status.class)));
            }
        });

        //
        // run test
        MonitorJNDINotifier notifier = new MonitorJNDINotifier(name, context, url);
        notifier.notify(null);
    }

    /**
     * Test successful known notification and JNDI binding on first notify
     * followed by a different notification and bind.
     * @throws NamingException 
     */
    @Test
    public void test_successful_twoDifferentKnownNotificationAndJNDIBinds()
        throws NamingException
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(2).of(context).bind(with(any(String.class)), with(any(Status.class)));
            }
        });

        //
        // run test
        MonitorJNDINotifier notifier = new MonitorJNDINotifier(name, context, url);
        notifier.notify("stopped");
        notifier.notify("running");
    }

    /**
     * Test for bind failure due to general Naming Exception.
     * @throws NamingException 
     */
    @Test
    public void test_failedBind_dueToNamingException()
        throws NamingException
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(context).bind(with(any(String.class)), with(any(Status.class)));
                will(throwException(new NamingException()));
            }
        });

        //
        // run test
        MonitorJNDINotifier notifier = new MonitorJNDINotifier(name, context, url);
        notifier.notify("stopped");
    }

    /**
     * Test for bind failure due to NameAlreadyBound Exception.
     * This should result in the name being rebound thus still updating
     * the JNDI url.
     * @throws NamingException 
     */
    @Test
    public void test_failedBind_dueToNameAlredyBoundException()
        throws NamingException
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(context).bind(with(any(String.class)), with(any(Status.class)));
                will(throwException(new NameAlreadyBoundException()));
                
                exactly(1).of(context).rebind(with(any(String.class)), with(any(Status.class)));
            }
        });

        //
        // run test
        MonitorJNDINotifier notifier = new MonitorJNDINotifier(name, context, url);
        notifier.notify("stopped");
    }

    /**
     * Test for bind failure due to name not found.
     * Therefore, we need to create the full context of this name and then rebind.
     * @throws NamingException 
     */
    @Test
    public void test_failedBind_dueToNameNotFound()
        throws NamingException
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(context).bind(with(any(String.class)), with(any(Status.class)));
                will(throwException(new NameNotFoundException()));
                
                // creation of context
                exactly(1).of(context).getNameParser(with(any(String.class)));
                will(returnValue(nameParser));
                
                exactly(1).of(nameParser).parse(url);
                exactly(1).of(context).rebind(with(any(String.class)), with(any(Status.class)));                
            }
        });

        //
        // run test
        MonitorJNDINotifier notifier = new MonitorJNDINotifier(name, context, url);
        notifier.notify("stopped");
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        mockery.assertIsSatisfied();
    }

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(MonitorJNDINotifierTest.class);
    }

}
