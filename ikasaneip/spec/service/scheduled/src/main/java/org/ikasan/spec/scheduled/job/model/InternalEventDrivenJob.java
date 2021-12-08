package org.ikasan.spec.scheduled.job.model;

import java.util.List;

public interface InternalEventDrivenJob extends SchedulerJob {
    List<String> getSuccessfulReturnCodes();

    void setSuccessfulReturnCodes(List<String> successfulReturnCodes);

    long getSecondsToWaitForProcessStart();

    void setSecondsToWaitForProcessStart(long secondsToWaitForProcessStart);

    String getWorkingDirectory();

    void setWorkingDirectory(String workingDirectory);

    String getCommandLine();

    void setCommandLine(String commandLine);

    String getStdErr();

    void setStdErr(String stdErr);

    String getStdOut();

    void setStdOut(String stdOut);

    boolean isRetryOnFail();

    void setRetryOnFail(boolean retryOnFail);
}
