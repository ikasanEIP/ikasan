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
import org.ikasan.monitor.notifier.DashboardNotifier;
import org.ikasan.monitor.notifier.EmailNotifierConfiguration;
import org.ikasan.monitor.notifier.NotifierFactory;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.Notifier;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple Monitor with notifiers builder.
 * 
 * @author Ikasan Development Team
 */
public class MonitorBuilder
{
    // default monitor factory
    MonitorFactory monitorFactory;

    // default notifier factory
    NotifierFactory notifierFactory;

    // allow override of default monitor
    Monitor monitor;

    // platform configuration service
    PlatformConfigurationService platformConfigurationService;

	/**
     * Constuctor
     *
     * @param monitorFactory
     * @param notifierFactory
     * @param platformConfigurationService
     */
    public MonitorBuilder(MonitorFactory monitorFactory, NotifierFactory notifierFactory
        , PlatformConfigurationService platformConfigurationService)
    {
        this.monitorFactory = monitorFactory;
        if(monitorFactory == null)
        {
            throw new IllegalArgumentException("monitorFactory cannot be 'null'");
        }

        this.notifierFactory = notifierFactory;
        if(notifierFactory == null)
        {
            throw new IllegalArgumentException("notifierFactory cannot be 'null'");
        }

        this.platformConfigurationService = platformConfigurationService;
        if(platformConfigurationService == null)
        {
            throw new IllegalArgumentException("notifierFactory cannot be 'null'");
        }
    }

    /**
     * Override the default monitor
     * @param monitor
     * @return
     */
    public NotifierBuilder withMonitor(Monitor monitor)
    {
        this.monitor = monitor;
        return new NotifierBuilder();
    }

    /**
     * Default monitor to track and pass flow state changes to any registered notifiers.
     * @return
     */
    public NotifierBuilder withFlowStateChangeMonitor()
    {
        this.monitor = this.monitorFactory.getMonitor();
        return new NotifierBuilder();
    }


    /**
     * Notifier builder instance for creating notifiers for push notifications of the
     * associated monitor this notifier is attached to.
     * @author Ikasan Development Team
     */
    public class NotifierBuilder
    {
        // list of notifiers to associate with the monitor
        List<Notifier> notifiers = new ArrayList<Notifier>();

        /**
         * Allow specification of a specific notifier
         * @param notifier
         * @return
         */
        public NotifierBuilder withNotifier(Notifier notifier)
        {
            this.notifiers.add(notifier);
            return this;
        }

        /**
         * Allow specification of a list of notifiers
         * @param notifiers
         * @return
         */
        public NotifierBuilder withNotifiers(List<Notifier> notifiers)
        {
            this.notifiers = notifiers;
            return this;
        }

        /**
         * Get an instance of the default email notifier based on the provided configuration.
         * @param emailNotifierConfiguration
         * @return
         */
        public NotifierBuilder withEmailNotifier(EmailNotifierConfiguration emailNotifierConfiguration)
        {
            Notifier notifier = notifierFactory.getEmailNotifier();
            if(notifier instanceof ConfiguredResource)
            {
                ((Configured<EmailNotifierConfiguration>)notifier).setConfiguration(emailNotifierConfiguration);
            }

            return withNotifier(notifier);
        }

        /**
         * Get an instance of the default dashboard notifier.
         * @return
         */
        public NotifierBuilder withDashboardlNotifier()
        {
            Notifier notifier = notifierFactory.getDashboardNotifier();
            ((DashboardNotifier)notifier).setPlatformConfigurationService(platformConfigurationService);
            return withNotifier(notifier);
        }

        /**
         * Build an instance of the monitor with associated notifiers.
         * @return
         */
        public Monitor build()
        {
            if(monitor == null)
            {
                monitor = monitorFactory.getMonitor();
            }

            monitor.setNotifiers(notifiers);
            return monitor;
        }

    }
}




