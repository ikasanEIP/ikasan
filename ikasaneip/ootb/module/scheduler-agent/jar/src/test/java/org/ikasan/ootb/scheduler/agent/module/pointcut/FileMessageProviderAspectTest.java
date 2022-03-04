package org.ikasan.ootb.scheduler.agent.module.pointcut;

import static org.mockito.Mockito.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

        verify(proceedingJoinPoint, times(1)).proceed();
        verifyNoMoreInteractions(proceedingJoinPoint);
    }

    @Test
    public void shouldRunProceedingJoinPointInDryRunMode() throws Throwable {
        when(dryRunModeService.getDryRunMode()).thenReturn(true);

        fileMessageProviderAspect.fileMessageProviderInvoke(proceedingJoinPoint);

        verifyNoMoreInteractions(proceedingJoinPoint);
    }
}