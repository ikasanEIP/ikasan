package org.ikasan.spec.scheduled.job.model;

import org.ikasan.spec.scheduled.context.model.ContextParameter;

import java.io.Serializable;
import java.util.List;

public interface InternalEventDrivenJob extends SchedulerJob, Serializable {
    List<String> getSuccessfulReturnCodes();

    void setSuccessfulReturnCodes(List<String> successfulReturnCodes);

    String getWorkingDirectory();

    void setWorkingDirectory(String workingDirectory);

    String getCommandLine();

    void setCommandLine(String commandLine);

    long getMinExecutionTime();

    void setMinExecutionTime(long minExecutionTime);

    long getMaxExecutionTime();

    void setMaxExecutionTime(long maxExecutionTime);

    List<ContextParameter> getContextParameters();

    void setContextParameters(List<ContextParameter> contextParameters);

    List<Integer> getDaysOfWeekToRun();

    void setDaysOfWeekToRun(List<Integer> daysOfWeekToRun);

    void setTargetResidingContextOnly(boolean targetResidingContextOnly);

    boolean isTargetResidingContextOnly();

    void setParticipatesInLock(boolean participatesInLock);

    boolean isParticipatesInLock();
}
