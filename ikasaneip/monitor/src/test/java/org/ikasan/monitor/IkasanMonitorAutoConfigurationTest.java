package org.ikasan.monitor;

import org.ikasan.monitor.notifier.DashboardNotifier;
import org.ikasan.monitor.notifier.EmailNotifier;
import org.ikasan.monitor.notifier.EmailNotifierConfiguration;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.monitor.Monitor;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class IkasanMonitorAutoConfigurationTest
{
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(IkasanMonitorAutoConfiguration.class));

    @Test
    public void testMonitorWithEmailNotifierWhenPropertiesSet(){
        contextRunner.withPropertyValues("environment=TEST")
                     .withUserConfiguration(TestIkasanConfig.class)
                     .run(context -> {
                         assertThat(context).hasSingleBean(Monitor.class);
                         Monitor monitor = context.getBean(Monitor.class);
                         assertThat(monitor.getEnvironment()).isEqualTo("TEST");
                         assertThat(monitor.getNotifiers()).isEmpty();
                     });

    }

    @Test
    public void testMonitorWithNoNotifiers(){
        contextRunner.withPropertyValues(
            "ikasan.monitor.notifier.mail.enabled=true",
            "ikasan.monitor.notifier.mail.mail-host=testhost"
                                        )
                     .withUserConfiguration(TestIkasanConfig.class)
                     .run(context -> {
                         assertThat(context).hasSingleBean(Monitor.class);
                         assertThat(context).hasSingleBean(EmailNotifier.class);
                         Monitor monitor = context.getBean(Monitor.class);
                         assertThat(monitor.getNotifiers()).hasSize(1);
                         EmailNotifierConfiguration c = context.getBean(EmailNotifierConfiguration.class);
                         assertThat(c.getMailHost()).isEqualTo("testhost");
                     });

    }

    @Test
    public void testMonitorWithDashboardNotifiers(){
        contextRunner.withPropertyValues("ikasan.dashboard.extract.enabled=true")
                     .withUserConfiguration(TestIkasanConfig.class)
                     .run(context -> {
                         assertThat(context).hasSingleBean(Monitor.class);
                         assertThat(context).hasSingleBean(DashboardNotifier.class);
                         Monitor monitor = context.getBean(Monitor.class);
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
