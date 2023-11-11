package org.ikasan.monitor;

import org.ikasan.monitor.notifier.DashboardFlowNotifier;
import org.ikasan.monitor.notifier.EmailFlowNotifier;
import org.ikasan.monitor.notifier.EmailJobNotifier;
import org.ikasan.monitor.notifier.EmailNotifierConfiguration;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.monitor.FlowMonitor;
import org.ikasan.spec.monitor.JobMonitor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class IkasanMonitorAutoConfigurationTest
{
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(IkasanMonitorAutoConfiguration.class));

    @Test
    void testFlowMonitorWithEmailNotifierWhenPropertiesSet(){
        contextRunner.withPropertyValues("environment=TEST")
                     .withUserConfiguration(TestIkasanConfig.class)
                     .run(context -> {
                         assertThat(context).hasSingleBean(FlowMonitor.class);
                         FlowMonitor monitor = context.getBean(FlowMonitor.class);
                         assertThat(monitor.getEnvironment()).isEqualTo("TEST");
                         assertThat(monitor.getNotifiers()).isEmpty();
                     });

    }

    @Test
    void testJobMonitorWithEmailNotifierWhenPropertiesSet(){
        contextRunner.withPropertyValues("environment=TEST")
            .withUserConfiguration(TestIkasanConfig.class)
            .run(context -> {
                assertThat(context).hasSingleBean(JobMonitor.class);
                JobMonitor monitor = context.getBean(JobMonitor.class);
                assertThat(monitor.getEnvironment()).isEqualTo("TEST");
                assertThat(monitor.getNotifiers()).isEmpty();
            });

    }

    @Test
    void testFlowMonitorWithNoNotifiers(){
        contextRunner.withPropertyValues(
            "ikasan.monitor.notifier.mail.enabled=true",
            "ikasan.monitor.notifier.mail.mail-host=testhost",
            "ikasan.job.monitor.notifier.mail.enabled=true"
                                        )
                     .withUserConfiguration(TestIkasanConfig.class)
                     .run(context -> {
                         assertThat(context).hasSingleBean(FlowMonitor.class);
                         assertThat(context).hasSingleBean(EmailFlowNotifier.class);
                         FlowMonitor monitor = context.getBean(FlowMonitor.class);
                         assertThat(monitor.getNotifiers()).hasSize(1);
                         EmailNotifierConfiguration c = context.getBean(EmailNotifierConfiguration.class);
                         assertThat(c.getMailHost()).isEqualTo("testhost");
                     });

    }

    @Test
    void testJobMonitorWithNoNotifiers(){
        contextRunner.withPropertyValues(
            "ikasan.monitor.notifier.mail.mail-host=testhost",
            "ikasan.job.monitor.notifier.mail.enabled=true"
        )
            .withUserConfiguration(TestIkasanConfig.class)
            .run(context -> {
                assertThat(context).hasSingleBean(FlowMonitor.class);
                assertThat(context).hasSingleBean(EmailJobNotifier.class);
                JobMonitor monitor = context.getBean(JobMonitor.class);
                assertThat(monitor.getNotifiers()).hasSize(1);
                EmailNotifierConfiguration c = context.getBean(EmailNotifierConfiguration.class);
                assertThat(c.getMailHost()).isEqualTo("testhost");
            });

    }

    @Test
    void testFlowMonitorWithDashboardNotifiers(){
        contextRunner.withPropertyValues("ikasan.dashboard.extract.enabled=true")
                     .withUserConfiguration(TestIkasanConfig.class)
                     .run(context -> {
                         assertThat(context).hasSingleBean(FlowMonitor.class);
                         assertThat(context).hasSingleBean(DashboardFlowNotifier.class);
                         FlowMonitor monitor = context.getBean(FlowMonitor.class);
                         assertThat(monitor.getNotifiers()).hasSize(1);
                     });

    }

    @Configuration
    static class TestIkasanConfig{

        @Bean
        public DashboardRestService flowCacheStateRestService(){
            return Mockito.mock(DashboardRestService.class);
        }
    }
}
