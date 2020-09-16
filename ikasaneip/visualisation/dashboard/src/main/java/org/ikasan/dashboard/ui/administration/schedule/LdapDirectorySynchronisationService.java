package org.ikasan.dashboard.ui.administration.schedule;

import org.ikasan.dashboard.notification.BusinessStreamNotificationJob;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class LdapDirectorySynchronisationService {
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(LdapDirectorySynchronisationService.class);


    private SecurityService securityService;

    private LdapService ldapService;

    private Scheduler scheduler;

    private ScheduledJobFactory scheduledJobFactory;

    private List<JobDetail> ldapDirectorySynchronisationJobDetails;

    private Map<String, LdapDirectorySynchronisationJob> ldapDirectorySynchronisationJobMap;

    private Map<String, JobDetail> ldapDirectorySynchronisationJobDetailsMap;

    public LdapDirectorySynchronisationService(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory
        , SecurityService securityService, LdapService ldapService)
    {
        this.scheduler = scheduler;
        if(this.scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be null!");
        }
        this.scheduledJobFactory = scheduledJobFactory;
        if(this.scheduledJobFactory == null)
        {
            throw new IllegalArgumentException("scheduledJobFactory cannot be null!");
        }
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

        this.ldapDirectorySynchronisationJobMap = new HashMap<>();
        this.ldapDirectorySynchronisationJobDetailsMap = new HashMap<>();
        this.ldapDirectorySynchronisationJobDetails = new ArrayList<>();
    }

    @PostConstruct
    public void scheduleJobs() {
        List<AuthenticationMethod> authenticationMethods = this.securityService.getAuthenticationMethods();

        this.ldapDirectorySynchronisationJobMap = new HashMap<>();
        this.ldapDirectorySynchronisationJobDetailsMap = new HashMap<>();
        this.ldapDirectorySynchronisationJobDetails = new ArrayList<>();

        authenticationMethods.forEach(authenticationMethod -> {
            if(authenticationMethod.isScheduled()) {
                LdapDirectorySynchronisationJob job = new LdapDirectorySynchronisationJob(authenticationMethod,
                    this.ldapService, this.securityService);
                JobDetail jobDetail = this.scheduledJobFactory.createJobDetail
                (job, BusinessStreamNotificationJob.class, job.getJobName(), "notify");

                this.ldapDirectorySynchronisationJobDetails.add(jobDetail);
                this.ldapDirectorySynchronisationJobDetailsMap.put(job.getJobName(), jobDetail);
                this.ldapDirectorySynchronisationJobMap.put(jobDetail.getKey().toString(), job);
            }
        });

        this.registerJobs();
    }

    public void registerJobs()
    {
        for(JobDetail jobDetail: this.ldapDirectorySynchronisationJobDetails)
        {
            this.addJob(jobDetail.getKey().getName());
        }
    }

    public void removeJob(String jobName)
    {
        try
        {
            if(this.scheduler.checkExists(this.ldapDirectorySynchronisationJobDetailsMap.get(jobName).getKey()))
            {
                this.scheduler.deleteJob(this.ldapDirectorySynchronisationJobDetailsMap.get(jobName).getKey());
            }
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void addJob(String jobName)
    {
        try
        {
            // remove the job so we can reschedule it if the cron expression has changed.
            this.removeJob(jobName);

            if(!this.scheduler.checkExists(this.ldapDirectorySynchronisationJobDetailsMap.get(jobName).getKey()))
            {
                JobDetail jobDetail = this.ldapDirectorySynchronisationJobDetailsMap.get(jobName);
                JobKey jobkey = jobDetail.getKey();
                Trigger trigger = getCronTrigger(jobkey, this.ldapDirectorySynchronisationJobMap.get(jobkey.toString()).getCronExpression());
                Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
                logger.info("Scheduled ldap sycnhronisation job ["
                    + jobkey.toString()
                    + "] starting at [" + scheduledDate + "] using cron expression ["
                    + this.ldapDirectorySynchronisationJobMap.get(jobkey.toString()).getCronExpression() + "]");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method factory for creating a cron trigger
     *
     * @return jobDetail
     * @throws ParseException
     */
    protected Trigger getCronTrigger(JobKey jobkey, String cronExpression)
    {
        TriggerBuilder triggerBuilder = newTrigger().withIdentity(jobkey.getName(), jobkey.getGroup());

        CronScheduleBuilder cronScheduleBuilder = cronSchedule(cronExpression);

        triggerBuilder.withSchedule(cronScheduleBuilder);
        return triggerBuilder.build();
    }
}
