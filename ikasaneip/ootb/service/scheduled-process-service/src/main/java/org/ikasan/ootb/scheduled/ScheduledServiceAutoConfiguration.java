package org.ikasan.ootb.scheduled;

import org.hibernate.SessionFactory;
import org.ikasan.dashboard.DashboardRestServiceImpl;
import org.ikasan.harvesting.HarvestingJobImpl;
import org.ikasan.housekeeping.HousekeepingJobImpl;
import org.ikasan.ootb.scheduled.dao.HibernateScheduledProcessEventDao;
import org.ikasan.ootb.scheduled.service.ScheduledProcessServiceImpl;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.harvest.HarvestingJob;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.ikasan.spec.scheduled.ScheduledProcessEventDao;
import org.ikasan.spec.scheduled.ScheduledProcessService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Scheduler service related configuration required by the scheduler ootb module.
 */
public class ScheduledServiceAutoConfiguration
{
    public static final String SCHEDULED_PROCESS_EVENTS_PATH = "/rest/harvest/scheduled";

    @Bean
    public HarvestingJob scheduledProcessEventJob(HarvestService scheduledProcessService, Environment environment, DashboardRestService scheduleProcessEventsDashboardRestService)
    {
        return new HarvestingJobImpl("scheduledProcessEventJob", scheduledProcessService, environment, scheduleProcessEventsDashboardRestService);
    }

    @Bean
    public HousekeepingJob scheduledProcessEventHousekeepingJob(HousekeepService scheduledProcessService, Environment environment)
    {
        return new HousekeepingJobImpl("scheduledProcessEventHousekeepingJob", scheduledProcessService, environment);
    }

    @Bean
    public DashboardRestService scheduleProcessEventsDashboardRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory customHttpRequestFactory)
    {
        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, SCHEDULED_PROCESS_EVENTS_PATH);
    }

    @Bean
    public ScheduledProcessService scheduledProcessService(ScheduledProcessEventDao scheduledProcessEventDao) {
        return new ScheduledProcessServiceImpl(scheduledProcessEventDao);
    }

    @Bean
    public ScheduledProcessEventDao scheduledProcessEventDao(SessionFactory sessionFactory) {
        HibernateScheduledProcessEventDao scheduledProcessEventDao = new HibernateScheduledProcessEventDao();
        scheduledProcessEventDao.setSessionFactory(sessionFactory   );

        return scheduledProcessEventDao;
    }

    @Bean
    public SessionFactory sessionFactory(@Qualifier("ikasanXaDataSourceInstance") DataSource dataSource
        , @Qualifier("platformHibernateProperties") Map hibernateProperties) throws IOException {
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(dataSource);
        Properties properties = new Properties();
        properties.putAll(hibernateProperties);
        localSessionFactoryBean.setHibernateProperties(properties);
        localSessionFactoryBean.setMappingResources("org/ikasan/ootb/scheduled/model/ScheduledProcessEventImpl.hbm.xml");
        localSessionFactoryBean.afterPropertiesSet();

        return localSessionFactoryBean.getObject();
    }
}