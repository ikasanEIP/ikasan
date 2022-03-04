package org.ikasan.ootb.scheduler.agent.module.pointcut;

import static org.mockito.Mockito.*;
import static org.quartz.TriggerBuilder.newTrigger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.ikasan.serialiser.model.JobExecutionContextDefaultImpl;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.Trigger;

@RunWith(MockitoJUnitRunner.class)
public class FileMessageProviderAspectTest {

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private DryRunModeService dryRunModeService;

    @InjectMocks
    private FileMessageProviderAspect fileMessageProviderAspect;

    @Test
    public void shouldRunProceedingJoinPointIfNotInDryRunMode() throws Throwable {
        when(dryRunModeService.getDryRunMode()).thenReturn(false);

        fileMessageProviderAspect.fileMessageProviderInvoke(proceedingJoinPoint);

        verify(dryRunModeService).getDryRunMode();
        verify(proceedingJoinPoint, times(1)).proceed();
        verifyNoMoreInteractions(proceedingJoinPoint);
    }

    @Test
    public void shouldRunProceedingJoinPointInDryRunMode() throws Throwable {
        JobExecutionContextDefaultImpl context = new JobExecutionContextDefaultImpl();
        Trigger trigger = newTrigger().withIdentity("Job Name", "Job Group").build();
        context.setTrigger(trigger);
        Object[] args = {context};

        when(dryRunModeService.getDryRunMode()).thenReturn(true);
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        fileMessageProviderAspect.fileMessageProviderInvoke(proceedingJoinPoint);

        verify(dryRunModeService).getDryRunMode();
        verify(proceedingJoinPoint).getArgs();
        verifyNoMoreInteractions(proceedingJoinPoint);
    }
}