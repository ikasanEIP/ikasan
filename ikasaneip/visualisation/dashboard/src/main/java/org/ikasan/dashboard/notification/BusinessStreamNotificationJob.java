package org.ikasan.dashboard.notification;

import org.ikasan.dashboard.notification.email.EmailNotifier;
import org.ikasan.dashboard.notification.model.BusinessStreamExclusions;
import org.ikasan.dashboard.notification.model.BusinessStreamNotification;
import org.ikasan.dashboard.notification.model.EmailNotification;
import org.ikasan.dashboard.notification.service.BusinessStreamNotificationService;
import org.ikasan.dashboard.schedule.DashboardJob;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;

public class BusinessStreamNotificationJob implements Job, DashboardJob {

    private static Logger logger = LoggerFactory.getLogger(BusinessStreamNotificationJob.class);

    public static final String LAST_RUN_TIMESTAMP = "-last-run-timestamp";

    private TemplateEngine templateEngine;
    private BusinessStreamNotification businessStreamNotification;
    private BusinessStreamNotificationService businessStreamNotificationService;
    private PlatformConfigurationService platformConfigurationService;
    private EmailNotifier emailNotifier;

    public BusinessStreamNotificationJob(TemplateEngine templateEngine, BusinessStreamNotification businessStreamNotification,
                                         BusinessStreamNotificationService businessStreamNotificationService,
                                         PlatformConfigurationService platformConfigurationService,
                                         EmailNotifier emailNotifier) {
        this.templateEngine = templateEngine;
        if(this.templateEngine == null) {
            throw new IllegalArgumentException("templateEngine cannot be null!");
        }
        this.businessStreamNotification = businessStreamNotification;
        if(this.businessStreamNotification == null) {
            throw new IllegalArgumentException("businessStreamNotification cannot be null!");
        }
        this.businessStreamNotificationService = businessStreamNotificationService;
        if(this.businessStreamNotificationService == null) {
            throw new IllegalArgumentException("businessStreamNotificationService cannot be null!");
        }
        this.platformConfigurationService = platformConfigurationService;
        if(this.platformConfigurationService == null) {
            throw new IllegalArgumentException("platformConfigurationService cannot be null!");
        }
        this.emailNotifier = emailNotifier;
        if(this.emailNotifier == null) {
            throw new IllegalArgumentException("emailNotifier cannot be null!");
        }
    }

    private void saveLastRunTimestamp()
    {
        this.platformConfigurationService.saveConfigurationValue(this.getJobName() + LAST_RUN_TIMESTAMP
            , String.valueOf(System.currentTimeMillis()));
    }

    private Long getLastRunTimestamp() {
        String lastRun = this.platformConfigurationService.getConfigurationValue(this.getJobName() + LAST_RUN_TIMESTAMP);

        if(lastRun == null || lastRun.isEmpty()) {
            return 0L;
        }

        return Long.valueOf(lastRun);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        final Context ctx = new Context();

        try {
            long lastRunTimestamp = this.getLastRunTimestamp();

            if(!this.businessStreamNotification.isNewExclusionsOnlyNotification()) {
                lastRunTimestamp = 0L;
            }
            else if (lastRunTimestamp == 0L && this.businessStreamNotification.getLastRunTimestamp() > 0L) {
                lastRunTimestamp = this.businessStreamNotification.getLastRunTimestamp();
            }

            Optional<BusinessStreamExclusions> businessStreamExclusions = this.businessStreamNotificationService
                .getBusinessStreamExclusions(this.businessStreamNotification.getBusinessStreamName(), lastRunTimestamp,
                    this.businessStreamNotification.getResultSize());

            if(businessStreamExclusions.isPresent()) {
                ctx.setVariable("businessStreamModel", businessStreamExclusions.get());

                final String content = this.templateEngine.process(businessStreamNotification.getEmailBodyTemplate(), ctx);
                final String subject = this.templateEngine.process(businessStreamNotification.getEmailSubjectTemplate(), ctx);

                EmailNotification emailNotification = new EmailNotification(this.businessStreamNotification.getRecipientList(),
                    subject, content, this.businessStreamNotification.isHtml());

                this.emailNotifier.sendNotification(emailNotification);

            }
            else {
                logger.debug(String.format("No exclusions to report on [%s].", this.businessStreamNotification.getJobName()));
            }
        }
        catch (Exception e) {
            throw new JobExecutionException(e);
        }

        this.saveLastRunTimestamp();
    }

    @Override
    public String getJobName() {
        return this.businessStreamNotification.getJobName();
    }

    @Override
    public String getCronExpression() {
        return this.businessStreamNotification.getCronExpression();
    }
}
