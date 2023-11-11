package org.ikasan.ootb.scheduler.agent.module.pointcut;

import static org.mockito.Mockito.*;
import static org.quartz.TriggerBuilder.newTrigger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.ikasan.component.endpoint.quartz.consumer.CorrelatingScheduledConsumer;
import org.ikasan.serialiser.model.JobExecutionContextDefaultImpl;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.Trigger;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class FileMessageProviderAspectTest {

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private DryRunModeService dryRunModeService;

    @InjectMocks
    private FileMessageProviderAspect fileMessageProviderAspect;

    @Test
    void shouldRunProceedingJoinPointIfNotInDryRunMode() throws Throwable {

        JobExecutionContextDefaultImpl context = new JobExecutionContextDefaultImpl();
        Trigger trigger = newTrigger().withIdentity("Job Name", "Job Group").build();
        context.setTrigger(trigger);

        context.setJobDataMap(new JobDataMap());
        context.getMergedJobDataMap().put(CorrelatingScheduledConsumer.CORRELATION_ID
            , UUID.randomUUID().toString());

        Object[] args = {context};

        when(dryRunModeService.getDryRunMode()).thenReturn(false);
        when(dryRunModeService.isJobDryRun("Job Name")).thenReturn(false);
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        fileMessageProviderAspect.fileMessageProviderInvoke(proceedingJoinPoint);

        verify(dryRunModeService, times(1)).getDryRunMode();
        verify(dryRunModeService, times(1)).isJobDryRun("Job Name");
        verify(proceedingJoinPoint).getArgs();
        verify(proceedingJoinPoint, times(1)).proceed();
        verifyNoMoreInteractions(proceedingJoinPoint);
        verifyNoMoreInteractions(dryRunModeService);
    }

    @Test
    void shouldRunProceedingJoinPointInDryRunMode() throws Throwable {
        JobExecutionContextDefaultImpl context = new JobExecutionContextDefaultImpl();
        Trigger trigger = newTrigger().withIdentity("Job Name", "Job Group").build();
        context.setTrigger(trigger);

        context.setJobDataMap(new JobDataMap());
        context.getMergedJobDataMap().put(CorrelatingScheduledConsumer.CORRELATION_ID
            , UUID.randomUUID().toString());

        Object[] args = {context};

        when(dryRunModeService.getDryRunMode()).thenReturn(true);
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        fileMessageProviderAspect.fileMessageProviderInvoke(proceedingJoinPoint);

        verify(dryRunModeService, times(2)).getDryRunMode();
        verify(proceedingJoinPoint, times(2)).getArgs();
        verifyNoMoreInteractions(proceedingJoinPoint);
    }

    @Test
    void shouldRunProceedingJoinPointInJobDryRunMode() throws Throwable {
        JobExecutionContextDefaultImpl context = new JobExecutionContextDefaultImpl();
        Trigger trigger = newTrigger().withIdentity("Job Name", "Job Group").build();
        context.setTrigger(trigger);

        context.setJobDataMap(new JobDataMap());
        context.getMergedJobDataMap().put(CorrelatingScheduledConsumer.CORRELATION_ID
            , UUID.randomUUID().toString());

        Object[] args = {context};

        when(dryRunModeService.getDryRunMode()).thenReturn(false);
        when(dryRunModeService.isJobDryRun("Job Name")).thenReturn(true);
        when(dryRunModeService.getJobFileName(any(String.class))).thenReturn("filename");
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        fileMessageProviderAspect.fileMessageProviderInvoke(proceedingJoinPoint);

        verify(dryRunModeService, times(2)).getDryRunMode();
        verify(dryRunModeService, times(2)).isJobDryRun("Job Name");
        verify(dryRunModeService, times(1)).getJobFileName(any(String.class));
        verify(proceedingJoinPoint, times(2)).getArgs();
        verifyNoMoreInteractions(proceedingJoinPoint);
        verifyNoMoreInteractions(dryRunModeService);
    }
}