package org.ikasan.backup.h2;

import org.ikasan.monitor.MonitorFactory;
import org.ikasan.monitor.MonitorFactoryImpl;
import org.ikasan.spec.monitor.JobMonitor;
import org.springframework.context.annotation.Bean;

public class IkasanBackupAutoTestConfiguration {

    @Bean
    public MonitorFactory monitorFactory(){
        return new MonitorFactoryImpl();
    }

    @Bean
    public JobMonitor jobMonitor(MonitorFactory monitorFactory) {
        return monitorFactory.getJobMonitor();
    }
}
