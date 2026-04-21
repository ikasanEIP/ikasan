package org.ikasan.ootb.scheduler.agent.rest;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Separate from {@link JobUtilsApplicationTest} to keep process-tree termination rules unit-tested
 * in isolation with mocked {@link ProcessHandle} instances (ordering, destroy mode, and failure paths).
 * {@link JobUtilsApplicationTest} remains focused on endpoint, security, and Spring/MockMvc wiring behavior.
 */
public class JobUtilsApplicationProcessTreeTest
{
    @Test
    public void destroyProcessTreeDestroysDescendantsInReversePidOrderThenParent() throws Exception
    {
        JobUtilsApplication application = new JobUtilsApplication();

        ProcessHandle parent = mock(ProcessHandle.class);
        ProcessHandle child100 = mock(ProcessHandle.class);
        ProcessHandle child200 = mock(ProcessHandle.class);

        when(parent.pid()).thenReturn(10L);
        when(child100.pid()).thenReturn(100L);
        when(child200.pid()).thenReturn(200L);

        when(parent.children()).thenReturn(Stream.of(child100, child200));
        when(child100.children()).thenReturn(Stream.empty());
        when(child200.children()).thenReturn(Stream.empty());

        when(parent.isAlive()).thenReturn(true);
        when(child100.isAlive()).thenReturn(true);
        when(child200.isAlive()).thenReturn(true);

        when(parent.destroy()).thenReturn(true);
        when(child100.destroy()).thenReturn(true);
        when(child200.destroy()).thenReturn(true);

        boolean result = invokeDestroyProcessTree(application, parent, false);

        assertTrue(result);

        org.mockito.InOrder inOrder = inOrder(child200, child100, parent);
        inOrder.verify(child200).destroy();
        inOrder.verify(child100).destroy();
        inOrder.verify(parent).destroy();

        verify(child200, never()).destroyForcibly();
        verify(child100, never()).destroyForcibly();
        verify(parent, never()).destroyForcibly();
    }

    @Test
    public void destroyProcessTreeUsesDestroyForciblyWhenDestroyFlagTrue() throws Exception
    {
        JobUtilsApplication application = new JobUtilsApplication();

        ProcessHandle parent = mock(ProcessHandle.class);
        ProcessHandle child = mock(ProcessHandle.class);

        when(parent.pid()).thenReturn(10L);
        when(child.pid()).thenReturn(101L);

        when(parent.children()).thenReturn(Stream.of(child));
        when(child.children()).thenReturn(Stream.empty());

        when(parent.isAlive()).thenReturn(true);
        when(child.isAlive()).thenReturn(true);

        when(parent.destroyForcibly()).thenReturn(true);
        when(child.destroyForcibly()).thenReturn(true);

        boolean result = invokeDestroyProcessTree(application, parent, true);

        assertTrue(result);

        verify(child).destroyForcibly();
        verify(parent).destroyForcibly();
        verify(child, never()).destroy();
        verify(parent, never()).destroy();
    }

    @Test
    public void destroyProcessTreeReturnsFalseAndDoesNotDestroyParentWhenDescendantFails() throws Exception
    {
        JobUtilsApplication application = new JobUtilsApplication();

        ProcessHandle parent = mock(ProcessHandle.class);
        ProcessHandle child = mock(ProcessHandle.class);

        when(parent.pid()).thenReturn(10L);
        when(child.pid()).thenReturn(101L);

        when(parent.children()).thenReturn(Stream.of(child));
        when(child.children()).thenReturn(Stream.empty());

        when(child.isAlive()).thenReturn(true, true);
        when(parent.isAlive()).thenReturn(true);

        when(child.destroy()).thenReturn(false);

        boolean result = invokeDestroyProcessTree(application, parent, false);

        assertFalse(result);
        verify(child).destroy();
        verify(parent, never()).destroy();
    }

    private boolean invokeDestroyProcessTree(JobUtilsApplication application, ProcessHandle processHandle, boolean destroy) throws Exception
    {
        Method method = JobUtilsApplication.class.getDeclaredMethod("destroyProcessTree", ProcessHandle.class, boolean.class);
        method.setAccessible(true);
        return (boolean) method.invoke(application, processHandle, destroy);
    }
}
