package org.ikasan.ootb.scheduler.agent.module.pointcut;

import static org.mockito.Mockito.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

public class FileMessageProviderAspectTest {

    @Mock
    private ModuleService moduleService;

    @Mock
    private Module module;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private SchedulerAgentConfiguredModuleConfiguration schedulerAgentConfiguredModuleConfiguration;

    @Mock
    private ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration> configuredModule;

    private FileMessageProviderAspect fileMessageProviderAspect;

    @Before
    public void setUp() {
        fileMessageProviderAspect = new FileMessageProviderAspect();
        ReflectionTestUtils.setField(fileMessageProviderAspect, "moduleName", "scheduler-agent");
        ReflectionTestUtils.setField(fileMessageProviderAspect, "moduleService", moduleService);
    }

    @Test
    @Ignore
    public void shouldRunProceedingJoinPointIfNotInDryRunMode() throws Throwable {
        when(moduleService.getModule("scheduler-agent")).thenReturn(module);
        when(((ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration>) (Module<Flow>) moduleService.getModule("scheduler-agent")).getConfiguration()).thenReturn(schedulerAgentConfiguredModuleConfiguration);
        when(schedulerAgentConfiguredModuleConfiguration.isDryRunMode()).thenReturn(false);

        fileMessageProviderAspect.fileMessageProviderInvoke(proceedingJoinPoint);

        verify(proceedingJoinPoint, times(1)).proceed();
        verifyNoMoreInteractions(proceedingJoinPoint);
    }

    @Test
    @Ignore
    public void shouldRunProceedingJoinPointInDryRunMode() throws Throwable {
        ReflectionTestUtils.setField(fileMessageProviderAspect, "dryRunMode", true);

        fileMessageProviderAspect.fileMessageProviderInvoke(proceedingJoinPoint);

        verifyNoMoreInteractions(proceedingJoinPoint);
    }
}