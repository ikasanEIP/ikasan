package org.ikasan.ootb.scheduled.processtracker.service;

import org.ikasan.cli.shell.operation.service.PersistenceService;

import java.io.IOException;

public interface SchedulerPersistenceService extends PersistenceService {

    void persist(String type, String name, ProcessHandle process, String resultOutput, String errorOutput, long fireTime);

    void removeAll(String processIdentity, String scriptPostfix) throws IOException;

    void removeAll(long pid) throws IOException;

    String getPersistedReturnCode(String processIdentity);

    String getResultAbsoluteFilePath(String processIdentity);

    String createCommandScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException ;

    String createCommandWrapperScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException ;

    String getScriptFilePath(String processIdentity, String scriptPostfix);
}
