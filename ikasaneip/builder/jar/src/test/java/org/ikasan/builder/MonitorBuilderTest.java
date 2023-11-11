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
package org.ikasan.builder;

import org.ikasan.monitor.MonitorFactory;
import org.ikasan.monitor.notifier.DashboardFlowNotifier;
import org.ikasan.monitor.notifier.EmailNotifierConfiguration;
import org.ikasan.monitor.notifier.NotifierFactory;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.monitor.FlowMonitor;
import org.ikasan.spec.monitor.FlowNotifier;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.Notifier;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This test class supports the <code>MonitorBuilder</code> class.
 * 
 * @author Ikasan Development Team
 */
class MonitorBuilderTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };
    
    /** Mock ApplicationContext */
    final ApplicationContext applicationContext = mockery.mock(ApplicationContext.class, "mockApplicationContext");

    /** Mock MonitorFactory */
    final MonitorFactory monitorFactory = mockery.mock(MonitorFactory.class, "mockMonitorFactory");

    /** Mock Monitor */
    final FlowMonitor monitor = mockery.mock(FlowMonitor.class, "mockMonitor");

    /** Mock NotifierFactory */
    final NotifierFactory notifierFactory = mockery.mock(NotifierFactory.class, "mockNotifierFactory");

    /** Mock NotifierFactory */
    final DashboardRestService flowCacheStateRestService = mockery.mock(DashboardRestService.class, "mockDashboardRestService");

    /** Mock Notifier */
    final FlowNotifier notifier = mockery.mock(FlowNotifier.class, "mockNotifier");

    /** DashboardNotifier */
    final DashboardFlowNotifier dashboardNotifier = mockery.mock(DashboardFlowNotifier.class, "mockDashboardNotifier");

    /**
     * Test failed constructor.
     */
    @Test
    void test_failed_constructor_no_monitorFactory()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new MonitorBuilder(null, null, null);
        });
    }

    /**
     * Test failed constructor.
     */
    @Test
    void test_failed_constructor_no_notifierFactory()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new MonitorBuilder(monitorFactory, null, null);
        });
    }

    /**
     * Test failed constructor.
     */
    @Test
    void test_failed_constructor_no_platform_configuration()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new MonitorBuilder(monitorFactory, notifierFactory, null);
        });
    }

    /**
     * Test successful monitor with dashboard notifier.
     */
    @Test
    void test_successful_flowStateChangeMonitor_withDashboardNotifier()
    {
        List<Notifier> notifiers = new ArrayList<Notifier>();
        notifiers.add(dashboardNotifier);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(applicationContext).getBean(MonitorFactory.class);
                will(returnValue(monitorFactory));
                exactly(1).of(applicationContext).getBean(NotifierFactory.class);
                will(returnValue(notifierFactory));
                exactly(1).of(applicationContext).getBean("flowCacheStateRestService", DashboardRestService.class);
                will(returnValue(flowCacheStateRestService));

                exactly(1).of(monitorFactory).getFlowMonitor();
                will(returnValue(monitor));
                exactly(1).of(notifierFactory).getDashboardFlowNotifier(flowCacheStateRestService);
                will(returnValue(dashboardNotifier));
                exactly(1).of(monitor).setNotifiers(notifiers);
            }
        });

        BuilderFactory builderFactory = new BuilderFactory();
        builderFactory.setApplicationContext(applicationContext);
        MonitorBuilder monitorBuilder = builderFactory.getMonitorBuilder();
        Monitor monitor = monitorBuilder.withFlowStateChangeMonitor()
                .withDashboardNotifier().build();
        assertNotNull(monitor, "monitor cannot be 'null'");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful monitor with email notifier.
     */
    @Test
    void test_successful_flowStateChangeMonitor_withEmailNotifier()
    {
        List<Notifier> notifiers = new ArrayList<Notifier>();
        notifiers.add(notifier);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(applicationContext).getBean(MonitorFactory.class);
                will(returnValue(monitorFactory));
                exactly(1).of(applicationContext).getBean(NotifierFactory.class);
                will(returnValue(notifierFactory));
                exactly(1).of(applicationContext).getBean("flowCacheStateRestService", DashboardRestService.class);
                will(returnValue(flowCacheStateRestService));

                exactly(1).of(monitorFactory).getFlowMonitor();
                will(returnValue(monitor));
                exactly(1).of(notifierFactory).getEmailFlowNotifier();
                will(returnValue(notifier));
                exactly(1).of(monitor).setNotifiers(notifiers);
            }
        });

        EmailNotifierConfiguration emailNotifierConfiguration = new EmailNotifierConfiguration();

        BuilderFactory builderFactory = new BuilderFactory();
        builderFactory.setApplicationContext(applicationContext);
        MonitorBuilder monitorBuilder = builderFactory.getMonitorBuilder();
        Monitor monitor = monitorBuilder.withFlowStateChangeMonitor()
                .withEmailNotifier(emailNotifierConfiguration).build();
        assertNotNull(monitor, "monitor cannot be 'null'");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful monitor with multiple notifiers.
     */
    @Test
    void test_successful_flowStateChangeMonitor_multipleNotifiers()
    {
        List<Notifier> notifiers = new ArrayList<Notifier>();
        notifiers.add(dashboardNotifier);
        notifiers.add(notifier);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(applicationContext).getBean(MonitorFactory.class);
                will(returnValue(monitorFactory));
                exactly(1).of(applicationContext).getBean(NotifierFactory.class);
                will(returnValue(notifierFactory));
                exactly(1).of(applicationContext).getBean("flowCacheStateRestService", DashboardRestService.class);
                will(returnValue(flowCacheStateRestService));

                exactly(1).of(monitorFactory).getFlowMonitor();
                will(returnValue(monitor));
                exactly(1).of(notifierFactory).getEmailFlowNotifier();
                will(returnValue(notifier));
                exactly(1).of(notifierFactory).getDashboardFlowNotifier(flowCacheStateRestService);
                will(returnValue(dashboardNotifier));
                exactly(1).of(monitor).setNotifiers(notifiers);
            }
        });

        EmailNotifierConfiguration emailNotifierConfiguration = new EmailNotifierConfiguration();

        BuilderFactory builderFactory = new BuilderFactory();
        builderFactory.setApplicationContext(applicationContext);
        MonitorBuilder monitorBuilder = builderFactory.getMonitorBuilder();
        Monitor monitor = monitorBuilder.withFlowStateChangeMonitor()
                .withDashboardNotifier().withEmailNotifier(emailNotifierConfiguration).build();
        assertNotNull(monitor, "monitor cannot be 'null'");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful monitor with custom notifier.
     */
    @Test
    void test_successful_flowStateChangeMonitor_custom_notifier()
    {
        List<Notifier> notifiers = new ArrayList<Notifier>();
        notifiers.add(notifier);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(applicationContext).getBean(MonitorFactory.class);
                will(returnValue(monitorFactory));
                exactly(1).of(applicationContext).getBean(NotifierFactory.class);
                will(returnValue(notifierFactory));
                exactly(1).of(applicationContext).getBean("flowCacheStateRestService", DashboardRestService.class);
                will(returnValue(flowCacheStateRestService));

                exactly(1).of(monitorFactory).getFlowMonitor();
                will(returnValue(monitor));
                exactly(1).of(monitor).setNotifiers(notifiers);
            }
        });

        BuilderFactory builderFactory = new BuilderFactory();
        builderFactory.setApplicationContext(applicationContext);
        MonitorBuilder monitorBuilder = builderFactory.getMonitorBuilder();
        Monitor monitor = monitorBuilder.withFlowStateChangeMonitor().withNotifier(notifier).build();
        assertNotNull(monitor, "monitor cannot be 'null'");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful monitor with multiple custom notifiers.
     */
    @Test
    void test_successful_flowStateChangeMonitor_custom_notifiers()
    {
        List<Notifier> notifiers = new ArrayList<Notifier>();
        notifiers.add(notifier);
        notifiers.add(notifier);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(applicationContext).getBean(MonitorFactory.class);
                will(returnValue(monitorFactory));
                exactly(1).of(applicationContext).getBean(NotifierFactory.class);
                will(returnValue(notifierFactory));
                exactly(1).of(applicationContext).getBean("flowCacheStateRestService", DashboardRestService.class);
                will(returnValue(flowCacheStateRestService));

                exactly(1).of(monitorFactory).getFlowMonitor();
                will(returnValue(monitor));
                exactly(1).of(monitor).setNotifiers(notifiers);
            }
        });

        BuilderFactory builderFactory = new BuilderFactory();
        builderFactory.setApplicationContext(applicationContext);
        MonitorBuilder monitorBuilder = builderFactory.getMonitorBuilder();
        Monitor monitor = monitorBuilder.withFlowStateChangeMonitor()
                .withNotifier(notifier)
                .withNotifier(notifier)
                .build();
        assertNotNull(monitor, "monitor cannot be 'null'");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful monitor with custom monitor.
     */
    @Test
    void test_successful_flowStateChangeMonitor_custom_monitor()
    {
        List<Notifier> notifiers = new ArrayList<Notifier>();
        notifiers.add(notifier);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(applicationContext).getBean(MonitorFactory.class);
                will(returnValue(monitorFactory));
                exactly(1).of(applicationContext).getBean(NotifierFactory.class);
                will(returnValue(notifierFactory));
                exactly(1).of(applicationContext).getBean("flowCacheStateRestService", DashboardRestService.class);
                will(returnValue(flowCacheStateRestService));

                exactly(1).of(monitor).setNotifiers(notifiers);
            }
        });

        BuilderFactory builderFactory = new BuilderFactory();
        builderFactory.setApplicationContext(applicationContext);
        MonitorBuilder monitorBuilder = builderFactory.getMonitorBuilder();
        Monitor _monitor = monitorBuilder.withMonitor(monitor)
                .withNotifier(notifier)
                .build();
        assertNotNull(_monitor, "monitor cannot be 'null'");
        mockery.assertIsSatisfied();
    }
}
