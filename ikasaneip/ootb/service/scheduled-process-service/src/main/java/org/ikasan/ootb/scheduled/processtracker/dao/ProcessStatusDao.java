package org.ikasan.ootb.scheduled.processtracker.dao;

import java.io.IOException;

public interface ProcessStatusDao {

    String createCommandScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException ;

    String createCommandWrapperScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException ;

    String getPersistedReturnCode(String processIdentity);

    void removeScriptAndResult(String processIdentity, String scriptPostfix) throws IOException ;

    String getResultAbsoluteFilePath(String processIdentity);

    String getScriptFilePath(String processIdentity, String scriptPostfix);
}
