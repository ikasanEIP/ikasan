package org.ikasan.spec.scheduled.job.model;

import java.util.List;

public interface InternalEventDrivenJob extends SchedulerJob {
    List<String> getSuccessfulReturnCodes();

    void setSuccessfulReturnCodes(List<String> successfulReturnCodes);

    String getWorkingDirectory();

    void setWorkingDirectory(String workingDirectory);

    String getCommandLine();

    void setCommandLine(String commandLine);
}
