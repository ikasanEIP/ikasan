package org.ikasan.dashboard.notification;

import org.ikasan.dashboard.notification.email.EmailNotifier;
import org.ikasan.dashboard.notification.model.BusinessStreamNotification;
import org.ikasan.dashboard.notification.service.BusinessStreamNotificationSchedulerService;
import org.ikasan.dashboard.notification.service.BusinessStreamNotificationService;

import org.ikasan.monitor.notifier.EmailNotifierConfiguration;
import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.metadata.BusinessStreamMetaDataService;
import org.ikasan.spec.solr.SolrGeneralService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BusinessStreamNotificationConfiguration {


    @Bean
    public EmailNotifier emailNotifier(EmailNotifierConfiguration emailConfiguration) {
        EmailNotifier emailNotifier = new EmailNotifier();
        emailNotifier.setConfiguration(emailConfiguration);

        return emailNotifier;
    }

    @Bean
    @ConfigurationProperties(prefix = "mail")
    private EmailNotifierConfiguration emailConfiguration() {
        return new EmailNotifierConfiguration();
    }

    @Bean
    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(textTemplateResolver());
        templateEngine.addTemplateResolver(htmlTemplateResolver());

        return templateEngine;
    }

    @Bean
    public List<BusinessStreamNotificationJob> businessStreamNotificationJobs(TemplateEngine emailTemplateEngine,
                                                      List<BusinessStreamNotification> businessStreamNotifications,
                                                      BusinessStreamNotificationService businessStreamNotificationService,
                                                      PlatformConfigurationService platformConfigurationService,
                                                      EmailNotifier emailNotifier) {
        return businessStreamNotifications.stream()
            .map(businessStreamNotification -> new BusinessStreamNotificationJob(emailTemplateEngine
                , businessStreamNotification, businessStreamNotificationService, platformConfigurationService, emailNotifier))
            .collect(Collectors.toList());
    }

    @Bean
    @ConfigurationProperties(prefix = "dashboard.notification")
    public List<BusinessStreamNotification> businessStreamNotifications() {
        return new ArrayList<>();
    }

    @Bean
    public BusinessStreamNotificationService businessStreamNotificationService(BusinessStreamMetaDataService businessStreamMetaDataService
        , ErrorReportingService errorReportingService, SolrGeneralService solrGeneralService) {
            return new BusinessStreamNotificationService(businessStreamMetaDataService, errorReportingService,
            solrGeneralService);
    }

    @Bean
    public BusinessStreamNotificationSchedulerService businessStreamNotificationSchedulerService(List<BusinessStreamNotificationJob> businessStreamNotificationJobs) {
        return new BusinessStreamNotificationSchedulerService(SchedulerFactory.getInstance().getScheduler()
            , CachingScheduledJobFactory.getInstance(), businessStreamNotificationJobs);
    }

    private ITemplateResolver textTemplateResolver() {
        final FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
        templateResolver.setSuffix(".txt");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    private ITemplateResolver htmlTemplateResolver() {
        final FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(2));
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }
}
