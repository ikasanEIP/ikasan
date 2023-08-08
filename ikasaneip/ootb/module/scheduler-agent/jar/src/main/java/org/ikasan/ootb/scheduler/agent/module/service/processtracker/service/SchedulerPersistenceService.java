package org.ikasan.ootb.scheduler.agent.module.service.processtracker.service;

import org.ikasan.cli.shell.operation.service.PersistenceService;

import java.io.IOException;

public interface SchedulerPersistenceService extends PersistenceService {

    void persist(String type, String name, Process process, String resultOutput, String errorOutput);

    void removeAll(String processIdentity, String scriptPostfix) throws IOException;


    String getPersistedReturnCode(String processIdentity);

    String getResultAbsoluteFilePath(String processIdentity);

    String createCommandScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException ;

    String getScriptFilePath(String processIdentity, String scriptPostfix);
}
