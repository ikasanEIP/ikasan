package org.ikasan.dashboard.security.schedule;

import org.ikasan.dashboard.notification.BusinessStreamNotificationJob;
import org.ikasan.dashboard.schedule.AbstractDashboardSchedulerService;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;

public class LdapDirectorySynchronisationSchedulerService extends AbstractDashboardSchedulerService {
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(LdapDirectorySynchronisationSchedulerService.class);

    private SecurityService securityService;

    private LdapService ldapService;


    public LdapDirectorySynchronisationSchedulerService(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory
        , SecurityService securityService, LdapService ldapService)
    {
        super(scheduler, scheduledJobFactory);

        this.securityService = securityService;
        if(this.securityService == null)
        {
            throw new IllegalArgumentException("securityService cannot be null!");
        }
        this.ldapService = ldapService;
        if(this.ldapService == null)
        {
            throw new IllegalArgumentException("ldapService cannot be null!");
        }
    }

    @PostConstruct
    public void registerJobs() {
        List<AuthenticationMethod> authenticationMethods;

        try {
            authenticationMethods = this.securityService.getAuthenticationMethods();
        }
        catch (Exception e) {
            logger.warn("Unable to retrieve ldap configuration details. This has most likely occurred as a result" +
                " of the dashboard database not yet being created.");
            return;
        }

        authenticationMethods.forEach(authenticationMethod -> {
            if (authenticationMethod.isScheduled()) {
                LdapDirectorySynchronisationJob job = new LdapDirectorySynchronisationJob(authenticationMethod,
                    this.ldapService, this.securityService);
                JobDetail jobDetail = this.scheduledJobFactory.createJobDetail
                    (job, LdapDirectorySynchronisationJob.class, job.getJobName(), "scheduled-ldap");

                super.dashboardJobDetailsMap.put(job.getJobName(), jobDetail);
                super.dashboardJobsMap.put(jobDetail.getKey().toString(), job);
            }
        });

        for (JobDetail jobDetail : super.dashboardJobDetailsMap.values()) {
            logger.info(String.format("Registering ldap synchronisation job[%s]", jobDetail.getKey().getName()));
            this.addJob(jobDetail.getKey().getName());
        }
    }
}
