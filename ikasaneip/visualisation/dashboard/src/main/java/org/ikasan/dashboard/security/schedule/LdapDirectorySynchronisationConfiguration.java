package org.ikasan.dashboard.security.schedule;

import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
public class LdapDirectorySynchronisationConfiguration {

    @Bean
    @DependsOn("dashboardSchedulerService")
    public LdapDirectorySynchronisationSchedulerService ldapDirectorySynchronisationService(SecurityService securityService, LdapService ldapService) {
        return new LdapDirectorySynchronisationSchedulerService(SchedulerFactory.getInstance().getScheduler()
            , CachingScheduledJobFactory.getInstance(), securityService, ldapService);
    }
}
