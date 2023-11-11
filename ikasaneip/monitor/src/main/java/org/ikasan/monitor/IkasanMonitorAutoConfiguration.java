package org.ikasan.monitor;

import org.ikasan.monitor.notifier.*;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.monitor.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Optional;

@AutoConfiguration
@EnableConfigurationProperties
public class IkasanMonitorAutoConfiguration
{

    private final Environment environment;

    public IkasanMonitorAutoConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean(destroyMethod = "destroy")
    public MonitorFactory monitorFactory(){
        return new MonitorFactoryImpl();
    }

    @Bean
    public NotifierFactory notifierFactory(){
        return new NotifierFactoryImpl();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnBean({MonitorFactory.class})
    @ConditionalOnMissingBean
    public FlowMonitor flowMonitor(MonitorFactory monitorFactory, Optional<List<FlowNotifier>> notifiers){
        FlowMonitor monitor = monitorFactory.getFlowMonitor();
        notifiers.ifPresent(n -> monitor.setNotifiers(n));
        String environmentName = environment.getProperty("environment");
        if ( environmentName!=null && !environmentName.isEmpty() ) {
            monitor.setEnvironment(environmentName);
        }
        return monitor;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnBean({MonitorFactory.class})
    @ConditionalOnMissingBean
    public JobMonitor jobMonitor(MonitorFactory monitorFactory, Optional<List<JobNotifier>> notifiers){
        JobMonitor monitor = monitorFactory.getJobMonitor();
        notifiers.ifPresent(n -> monitor.setNotifiers(n));
        String environmentName = environment.getProperty("environment");
        if ( environmentName!=null && !environmentName.isEmpty() ) {
            monitor.setEnvironment(environmentName);
        }
        String moduleName = environment.getProperty("module.name");
        if ( moduleName!=null && !moduleName.isEmpty() ) {
            monitor.setModuleName(moduleName);
        }
        return monitor;
    }

    @Bean
    @ConditionalOnBean({NotifierFactory.class})
    @ConditionalOnProperty(prefix = "ikasan.dashboard.extract", name = "enabled", havingValue = "true")
    public FlowNotifier dashboardFlowNotifier(NotifierFactory notifierFactory, DashboardRestService flowCacheStateRestService){
        return notifierFactory.getDashboardFlowNotifier(flowCacheStateRestService);
    }

    @Bean
    @ConditionalOnBean({NotifierFactory.class})
    @ConditionalOnProperty(prefix = "ikasan.monitor.notifier.mail", name = "enabled", havingValue = "true")
    public FlowNotifier emailFlowNotifier(NotifierFactory notifierFactory, EmailNotifierConfiguration emailNotifierConfiguration){
        EmailFlowNotifier emailNotifier = (EmailFlowNotifier) notifierFactory.getEmailFlowNotifier();
        emailNotifier.setConfiguration(emailNotifierConfiguration);
        return emailNotifier;
    }

    @Bean
    @ConditionalOnBean({NotifierFactory.class})
    @ConditionalOnProperty(prefix = "ikasan.job.monitor.notifier.mail", name = "enabled", havingValue = "true")
    public JobNotifier emailJobNotifier(NotifierFactory notifierFactory, EmailNotifierConfiguration emailNotifierConfiguration){
        EmailJobNotifier emailNotifier = (EmailJobNotifier) notifierFactory.getEmailJobNotifier();
        emailNotifier.setConfiguration(emailNotifierConfiguration);
        return emailNotifier;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ikasan.monitor.notifier.mail")
    public EmailNotifierConfiguration emailNotifierConfiguration(){
        return new EmailNotifierConfiguration();
    }
}
