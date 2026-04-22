package org.ikasan.ootb.scheduled.processtracker.service;

import org.ikasan.cli.shell.operation.service.PersistenceService;
import org.ikasan.ootb.scheduled.processtracker.model.SchedulerIkasanProcess;

import java.io.IOException;

public interface SchedulerPersistenceService extends PersistenceService {

    void persist(String type, String name, ProcessHandle process, String resultOutput, String errorOutput, long fireTime);

    /**
     * Returns the managed process record for the given PID, or null if this PID is not
     * tracked by the scheduler agent. Used to guard kill operations against arbitrary PIDs.
     */
    SchedulerIkasanProcess findByPid(long pid);

    void removeAll(String processIdentity) throws IOException;

    void removeAll(long pid) throws IOException;

    String getPersistedReturnCode(String processIdentity);

    String getResultAbsoluteFilePath(String processIdentity);

    String createCommandScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException ;

    String createCommandWrapperScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException ;

    String getScriptFilePath(String processIdentity, String scriptPostfix);
}
