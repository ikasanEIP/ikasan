package org.ikasan.dashboard.schedule;

import org.ikasan.scheduler.SchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DashboardScheduleConfiguration {

    @Bean
    public DashboardSchedulerService dashboardSchedulerService() {
        return new DashboardSchedulerService(SchedulerFactory.getInstance().getScheduler());
    }

}
