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
package org.ikasan.ootb.scheduled;

import org.ikasan.harvesting.HarvestingJobImpl;
import org.ikasan.housekeeping.HousekeepingJobImpl;
import org.ikasan.ootb.scheduled.dao.HibernateScheduledProcessEventDao;
import org.ikasan.ootb.scheduled.service.ScheduledProcessServiceImpl;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.harvest.HarvestingJob;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.ikasan.spec.scheduled.event.dao.ScheduledProcessEventDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Scheduler service related configuration required by the scheduler ootb module.
 */
public class ScheduledServiceAutoConfiguration
{
    public static final String SCHEDULED_PROCESS_EVENTS_PATH = "/rest/harvest/scheduled";

    @Bean
    public HarvestingJob scheduledProcessEventJob(@Qualifier("scheduledProcessService") HarvestService scheduledProcessService, Environment environment
        , @Qualifier("scheduleProcessEventsDashboardRestService") DashboardRestService scheduleProcessEventsDashboardRestService)
    {
        return new HarvestingJobImpl("scheduledProcessEventJob", scheduledProcessService, environment, scheduleProcessEventsDashboardRestService);
    }

    @Bean
    public HousekeepingJob scheduledProcessEventHousekeepingJob(@Qualifier("scheduledProcessService") HousekeepService scheduledProcessService, Environment environment)
    {
        return new HousekeepingJobImpl("scheduledProcessEventHousekeepingJob", scheduledProcessService, environment);
    }

    @Bean(name = "scheduledProcessService")
    public ScheduledProcessServiceImpl scheduledProcessService(ScheduledProcessEventDao scheduledProcessEventDao) {
        return new ScheduledProcessServiceImpl(scheduledProcessEventDao);
    }

    @Bean
    public ScheduledProcessEventDao scheduledProcessEventDao() {
        HibernateScheduledProcessEventDao scheduledProcessEventDao = new HibernateScheduledProcessEventDao();
        return scheduledProcessEventDao;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean scheduledProcessEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("scheduled-process");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:scheduled-process-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }
}