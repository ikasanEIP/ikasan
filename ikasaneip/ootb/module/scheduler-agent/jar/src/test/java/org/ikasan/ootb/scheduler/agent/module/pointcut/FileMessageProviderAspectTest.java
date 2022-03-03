package org.ikasan.ootb.scheduler.agent.module.pointcut;

import static org.mockito.Mockito.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class FileMessageProviderAspectTest {

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    private FileMessageProviderAspect fileMessageProviderAspect;

    @Before
    public void setUp() {
        fileMessageProviderAspect = new FileMessageProviderAspect();
    }

    @Test
    @Ignore
    public void shouldRunProceedingJoinPointIfNotInDryRunMode() throws Throwable {
        ReflectionTestUtils.setField(fileMessageProviderAspect, "dryRunMode", false);

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