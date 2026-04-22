package org.ikasan.ootb.scheduled.processtracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for terminating OS processes and their full descendant trees.
 * Used by both the REST kill endpoint and the internal timeout kill path so that
 * the same tree-aware termination logic is applied in all cases.
 */
public class ProcessKillUtils {

    private static final Logger logger = LoggerFactory.getLogger(ProcessKillUtils.class);

    private ProcessKillUtils() {}

    /**
     * Destroys a process tree by discovering descendant processes first (depth-first, leaves first),
     * then terminating the parent. This minimises the window in which new child processes could
     * be spawned and escape the kill.
     * -
     * If any descendant cannot be destroyed the parent is intentionally left alive so that a
     * second attempt can be made.
     *
     * @param processHandle the root of the tree to terminate
     * @param forcibly if true uses destroyForcibly() (SIGKILL on Unix); if false uses destroy() (SIGTERM)
     * @return true if every process in the tree was successfully terminated
     */
    public static boolean destroyProcessTree(ProcessHandle processHandle, boolean forcibly) {
        List<ProcessHandle> descendants = getDescendantProcesses(processHandle);

        logger.warn("About to kill pid {} with process handle {} and descendant pids {}",
            processHandle.pid(), processHandle,
            descendants.stream().map(ProcessHandle::pid).collect(Collectors.toList()));

        boolean descendantsDestroyed = descendants.stream()
            .map(child -> destroyProcess(child, forcibly))
            .reduce(true, Boolean::logicalAnd);

        return descendantsDestroyed && destroyProcess(processHandle, forcibly);
    }

    /**
     * Recursively collects all descendant processes depth-first (leaves first).
     */
    private static List<ProcessHandle> getDescendantProcesses(ProcessHandle processHandle) {
        List<ProcessHandle> descendants = new ArrayList<>();
        processHandle.children().forEach(child -> {
            descendants.addAll(getDescendantProcesses(child));
            descendants.add(child);
        });
        return descendants;
    }

    /**
     * Terminates a single process. Returns true if the process is no longer alive after the call.
     */
    private static boolean destroyProcess(ProcessHandle processHandle, boolean forcibly) {
        if (!processHandle.isAlive()) {
            logger.info("Pid {} is already terminated", processHandle.pid());
            return true;
        }
        boolean result = forcibly ? processHandle.destroyForcibly() : processHandle.destroy();
        logger.info("Termination request for pid {} returned {} using destroyForcibly={}",
            processHandle.pid(), result, forcibly);
        return result || !processHandle.isAlive();
    }
}
