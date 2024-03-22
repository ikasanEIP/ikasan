package org.ikasan.backup.h2;

import org.ikasan.monitor.MonitorFactory;
import org.ikasan.monitor.MonitorFactoryImpl;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.monitor.JobMonitor;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.ikasan.spec.module.Module;

public class IkasanBackupAutoTestConfiguration {

    @Bean
    public MonitorFactory monitorFactory(){
        return new MonitorFactoryImpl();
    }

    @Bean
    public JobMonitor jobMonitor(MonitorFactory monitorFactory) {
        return monitorFactory.getJobMonitor();
    }

    @Bean
    public Module<Flow> module() {
        return Mockito.mock(Module.class);
    }
}
