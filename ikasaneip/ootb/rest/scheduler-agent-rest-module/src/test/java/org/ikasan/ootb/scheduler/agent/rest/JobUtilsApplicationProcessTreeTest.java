package org.ikasan.ootb.scheduler.agent.rest;

import org.ikasan.ootb.scheduled.processtracker.ProcessKillUtils;
import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ProcessKillUtils} process-tree termination behaviour.
 * Kept in the REST module because mockito-inline is not on the classpath here,
 * so the standard subclass-based MockMaker can proxy the ProcessHandle interface
 * on Java 21 without bytecode instrumentation issues.
 */
public class JobUtilsApplicationProcessTreeTest
{
    @Test
    public void destroyProcessTree_noChildren_destroysParentGracefully() {
        ProcessHandle parent = mock(ProcessHandle.class);
        when(parent.pid()).thenReturn(10L);
        when(parent.children()).thenReturn(Stream.empty());
        when(parent.isAlive()).thenReturn(true);
        when(parent.destroy()).thenReturn(true);

        assertTrue(ProcessKillUtils.destroyProcessTree(parent, false));

        verify(parent).destroy();
        verify(parent, never()).destroyForcibly();
    }

    @Test
    public void destroyProcessTree_noChildren_destroysParentForcibly() {
        ProcessHandle parent = mock(ProcessHandle.class);
        when(parent.pid()).thenReturn(10L);
        when(parent.children()).thenReturn(Stream.empty());
        when(parent.isAlive()).thenReturn(true);
        when(parent.destroyForcibly()).thenReturn(true);

        assertTrue(ProcessKillUtils.destroyProcessTree(parent, true));

        verify(parent).destroyForcibly();
        verify(parent, never()).destroy();
    }

    @Test
    public void destroyProcessTree_withChildren_destroysChildrenBeforeParent() {
        ProcessHandle parent = mock(ProcessHandle.class);
        ProcessHandle child1 = mock(ProcessHandle.class);
        ProcessHandle child2 = mock(ProcessHandle.class);

        when(parent.pid()).thenReturn(10L);
        when(child1.pid()).thenReturn(20L);
        when(child2.pid()).thenReturn(30L);

        // child1 is returned first by the OS, so it is destroyed first (stream order, not PID order)
        when(parent.children()).thenReturn(Stream.of(child1, child2));
        when(child1.children()).thenReturn(Stream.empty());
        when(child2.children()).thenReturn(Stream.empty());

        when(parent.isAlive()).thenReturn(true);
        when(child1.isAlive()).thenReturn(true);
        when(child2.isAlive()).thenReturn(true);

        when(parent.destroy()).thenReturn(true);
        when(child1.destroy()).thenReturn(true);
        when(child2.destroy()).thenReturn(true);

        assertTrue(ProcessKillUtils.destroyProcessTree(parent, false));

        // Both children destroyed before the parent; order follows the children stream
        inOrder(child1, child2, parent).verify(child1).destroy();
        inOrder(child1, child2, parent).verify(child2).destroy();
        inOrder(child1, child2, parent).verify(parent).destroy();
    }

    @Test
    public void destroyProcessTree_withGrandchildren_destroysLeavesBeforeParent() {
        ProcessHandle parent = mock(ProcessHandle.class);
        ProcessHandle child = mock(ProcessHandle.class);
        ProcessHandle grandchild = mock(ProcessHandle.class);

        when(parent.pid()).thenReturn(10L);
        when(child.pid()).thenReturn(20L);
        when(grandchild.pid()).thenReturn(30L);

        when(parent.children()).thenReturn(Stream.of(child));
        when(child.children()).thenReturn(Stream.of(grandchild));
        when(grandchild.children()).thenReturn(Stream.empty());

        when(parent.isAlive()).thenReturn(true);
        when(child.isAlive()).thenReturn(true);
        when(grandchild.isAlive()).thenReturn(true);

        when(parent.destroy()).thenReturn(true);
        when(child.destroy()).thenReturn(true);
        when(grandchild.destroy()).thenReturn(true);

        assertTrue(ProcessKillUtils.destroyProcessTree(parent, false));

        // Depth-first: grandchild destroyed before child, child before parent
        inOrder(grandchild, child, parent).verify(grandchild).destroy();
        inOrder(grandchild, child, parent).verify(child).destroy();
        inOrder(grandchild, child, parent).verify(parent).destroy();
    }

    @Test
    public void destroyProcessTree_usesDestroyForciblyWhenFlagTrue() {
        ProcessHandle parent = mock(ProcessHandle.class);
        ProcessHandle child = mock(ProcessHandle.class);

        when(parent.pid()).thenReturn(10L);
        when(child.pid()).thenReturn(20L);

        when(parent.children()).thenReturn(Stream.of(child));
        when(child.children()).thenReturn(Stream.empty());

        when(parent.isAlive()).thenReturn(true);
        when(child.isAlive()).thenReturn(true);

        when(parent.destroyForcibly()).thenReturn(true);
        when(child.destroyForcibly()).thenReturn(true);

        assertTrue(ProcessKillUtils.destroyProcessTree(parent, true));

        verify(child).destroyForcibly();
        verify(parent).destroyForcibly();
        verify(child, never()).destroy();
        verify(parent, never()).destroy();
    }

    @Test
    public void destroyProcessTree_childAlreadyDead_treatedAsSuccess_parentStillDestroyed() {
        ProcessHandle parent = mock(ProcessHandle.class);
        ProcessHandle child = mock(ProcessHandle.class);

        when(parent.pid()).thenReturn(10L);
        when(child.pid()).thenReturn(20L);

        when(parent.children()).thenReturn(Stream.of(child));
        when(child.children()).thenReturn(Stream.empty());

        when(parent.isAlive()).thenReturn(true);
        when(child.isAlive()).thenReturn(false); // already dead before we try

        when(parent.destroy()).thenReturn(true);

        assertTrue(ProcessKillUtils.destroyProcessTree(parent, false));

        verify(child, never()).destroy();
        verify(child, never()).destroyForcibly();
        verify(parent).destroy();
    }

    @Test
    public void destroyProcessTree_childDestroyReturnsFalseButProcessGone_treatedAsSuccess() {
        ProcessHandle parent = mock(ProcessHandle.class);
        ProcessHandle child = mock(ProcessHandle.class);

        when(parent.pid()).thenReturn(10L);
        when(child.pid()).thenReturn(20L);

        when(parent.children()).thenReturn(Stream.of(child));
        when(child.children()).thenReturn(Stream.empty());

        // destroy() says false but the process has since disappeared (race: died on its own)
        when(child.isAlive()).thenReturn(true, false);
        when(child.destroy()).thenReturn(false);

        when(parent.isAlive()).thenReturn(true);
        when(parent.destroy()).thenReturn(true);

        assertTrue(ProcessKillUtils.destroyProcessTree(parent, false));

        verify(parent).destroy();
    }

    @Test
    public void destroyProcessTree_childCannotBeDestroyed_returnsFalseAndParentIsSpared() {
        ProcessHandle parent = mock(ProcessHandle.class);
        ProcessHandle child = mock(ProcessHandle.class);

        when(parent.pid()).thenReturn(10L);
        when(child.pid()).thenReturn(20L);

        when(parent.children()).thenReturn(Stream.of(child));
        when(child.children()).thenReturn(Stream.empty());

        when(child.isAlive()).thenReturn(true, true); // still alive after failed destroy
        when(child.destroy()).thenReturn(false);
        when(parent.isAlive()).thenReturn(true);

        assertFalse(ProcessKillUtils.destroyProcessTree(parent, false));

        verify(child).destroy();
        // Parent must not be touched so a second kill attempt remains possible
        verify(parent, never()).destroy();
        verify(parent, never()).destroyForcibly();
    }

    @Test
    public void destroyProcessTree_parentAlreadyDead_returnsTrueWithoutCallingDestroy() {
        ProcessHandle parent = mock(ProcessHandle.class);
        when(parent.pid()).thenReturn(10L);
        when(parent.children()).thenReturn(Stream.empty());
        when(parent.isAlive()).thenReturn(false);

        assertTrue(ProcessKillUtils.destroyProcessTree(parent, false));

        verify(parent, never()).destroy();
        verify(parent, never()).destroyForcibly();
    }
}
