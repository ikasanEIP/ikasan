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
