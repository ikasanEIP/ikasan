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
package org.ikasan.monitor;

import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.Notifier;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * This test class supports the <code>DefaultMonitorImpl</code> class.
 * 
 * @author Ikasan Development Team
 */
public class DefaultMonitorImplTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setThreadingPolicy(new Synchroniser());
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private ExecutorService executorService = mockery.mock(ExecutorService.class);

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_when_not_active()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(0).of(executorService).execute(with(any(Runnable.class)));
            }
        });

        MonitorConfiguration monitorConfiguration = new MonitorConfiguration();
        monitorConfiguration.setActive(false);

        Monitor<String> monitor = new DefaultMonitorImpl<>(executorService);
        ((Configured)monitor).setConfiguration(monitorConfiguration);

        monitor.invoke("stopped");
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_monitor_after_destroy()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(executorService).shutdown();
                exactly(1).of(executorService).isShutdown();
                will(returnValue(true));
                // executor service execute should not be called
                exactly(0).of(executorService).execute(with(any(Runnable.class)));
            }
        });

        MonitorConfiguration monitorConfiguration = new MonitorConfiguration();
        monitorConfiguration.setActive(true);

        List<Notifier> notifiers = new ArrayList<>();
        Notifier notifier = new StubbedNotifier("myNotifierName", "state is stopped");
        notifier.setNotifyStateChangesOnly(false);
        notifiers.add(notifier);

        Monitor<String> monitor = new DefaultMonitorImpl<>(executorService);
        ((Configured)monitor).setConfiguration(monitorConfiguration);
        monitor.setNotifiers(notifiers);
        monitor.destroy();

        monitor.invoke("stopped");
        mockery.assertIsSatisfied();
    }


    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_when_active_no_notifiers()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(0).of(executorService).execute(with(any(Runnable.class)));
            }
        });

        MonitorConfiguration monitorConfiguration = new MonitorConfiguration();
        monitorConfiguration.setActive(true);

        Monitor<String> monitor = new DefaultMonitorImpl<>(executorService);
        ((Configured)monitor).setConfiguration(monitorConfiguration);

        monitor.invoke("stopped");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_when_active_with_notifiers_state_changes_only_false()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(executorService).isShutdown();
                will(returnValue(false));
                exactly(1).of(executorService).execute(with(any(Runnable.class)));
            }
        });

        MonitorConfiguration monitorConfiguration = new MonitorConfiguration();
        monitorConfiguration.setActive(true);
        List<Notifier> notifiers = new ArrayList<>();
        Notifier notifier = new StubbedNotifier("myNotifierName", "state is stopped");
        notifier.setNotifyStateChangesOnly(false);
        notifiers.add(notifier);

        Monitor<String> monitor = new DefaultMonitorImpl<>(executorService);
        ((Configured)monitor).setConfiguration(monitorConfiguration);
        monitor.setNotifiers(notifiers);

        monitor.invoke("state is stopped");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_when_active_with_notifiers_state_changes_only_true()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(executorService).isShutdown();
                will(returnValue(false));
                exactly(1).of(executorService).execute(with(any(Runnable.class)));
            }
        });

        MonitorConfiguration monitorConfiguration = new MonitorConfiguration();
        monitorConfiguration.setActive(true);
        List<Notifier> notifiers = new ArrayList<>();
        Notifier notifier = new StubbedNotifier("myNotifierName", "state is stopped");
        notifier.setNotifyStateChangesOnly(true);
        notifiers.add(notifier);

        Monitor<String> monitor = new DefaultMonitorImpl<>(executorService);
        ((Configured)monitor).setConfiguration(monitorConfiguration);
        monitor.setNotifiers(notifiers);

        monitor.invoke("state is stopped");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_when_active_with_notifiers_state_changes_only_true_multiple_invokes()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(6).of(executorService).isShutdown();
                will(returnValue(false));
                exactly(3).of(executorService).execute(with(any(Runnable.class)));
            }
        });

        MonitorConfiguration monitorConfiguration = new MonitorConfiguration();
        monitorConfiguration.setActive(true);
        List<Notifier> notifiers = new ArrayList<>();
        Notifier notifier = new StubbedNotifier("myNotifierName", "state is stopped");
        notifier.setNotifyStateChangesOnly(true);
        notifiers.add(notifier);

        Monitor<String> monitor = new DefaultMonitorImpl<>(executorService);
        ((Configured)monitor).setConfiguration(monitorConfiguration);
        monitor.setNotifiers(notifiers);

        monitor.invoke("state is stopped");
        monitor.invoke("state is stopped");
        monitor.invoke("state is started");
        monitor.invoke("state is started");
        monitor.invoke("state is stopped");
        monitor.invoke("state is stopped");
        mockery.assertIsSatisfied();
    }

    class StubbedNotifier implements Notifier<String>
    {
        String name;
        String expectation;
        boolean notifyStateChangesOnly;

        public StubbedNotifier(String name, String expectation)
        {
            this.name = name;
            this.expectation = expectation;
        }

        @Override
        public void invoke(String env, String name, String notification)
        {
            Assert.assertTrue(expectation.equals(notification));
        }

        @Override
        public void setNotifyStateChangesOnly(boolean notifyStateChangesOnly) {
            this.notifyStateChangesOnly = notifyStateChangesOnly;
        }

        @Override
        public boolean isNotifyStateChangesOnly() {
            return notifyStateChangesOnly;
        }
    }
}
