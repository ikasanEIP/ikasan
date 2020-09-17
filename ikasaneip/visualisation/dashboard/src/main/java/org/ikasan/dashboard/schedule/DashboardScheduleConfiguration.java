package org.ikasan.dashboard.schedule;

import org.ikasan.scheduler.SchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DashboardScheduleConfiguration {

    @Bean
    public DashboardSchedulerLifeCycleService dashboardSchedulerService() {
        return new DashboardSchedulerLifeCycleService(SchedulerFactory.getInstance().getScheduler());
    }

}
