package org.ikasan.spec.scheduled.job.model;

import org.ikasan.spec.scheduled.context.model.ContextParameter;

import java.io.Serializable;
import java.util.List;

public interface InternalEventDrivenJob extends SchedulerJob, Serializable {
    /**
     * Get the list of return codes that will be deemed successful.
     *
     * @return
     */
    List<String> getSuccessfulReturnCodes();

    /**
     * Set the list of return codes that will be deemed successful.
     * @param successfulReturnCodes
     */
    void setSuccessfulReturnCodes(List<String> successfulReturnCodes);

    /**
     * Get the directory that the process will be executed from.
     *
     * @return
     */
    String getWorkingDirectory();

    /**
     * Set the directory that the process will be executed from.
     *
     * @param workingDirectory
     */
    void setWorkingDirectory(String workingDirectory);

    /**
     * Get the contents of the command that will be executed by the process.
     *
     * @return
     */
    String getCommandLine();

    /**
     * Set the contents of the command that will be executed by the process.
     * @param commandLine
     */
    void setCommandLine(String commandLine);

    /**
     * Get the minimum execution time that the job is expected to run for.
     *
     * @return
     */
    long getMinExecutionTime();

    /**
     * Set the minimum execution time that the job is expected to run for.
     *
     * @param minExecutionTime
     */
    void setMinExecutionTime(long minExecutionTime);

    /**
     * Get the maximum execution time that the job is expected to run for.
     *
     * @return
     */
    long getMaxExecutionTime();

    /**
     * Set the maximum execution time that the job is expected to run for.
     *
     * @param maxExecutionTime
     */
    void setMaxExecutionTime(long maxExecutionTime);

    /**
     * Get the parameters that are passed to the process.
     *
     * @return
     */
    List<ContextParameter> getContextParameters();

    /**
     * Set the parameters that are passed to the process.
     *
     * @param contextParameters
     */
    void setContextParameters(List<ContextParameter> contextParameters);

    /**
     * Get the days of the week that the job will run. 0 -> 6 == Sunday -> Saturday
     *
     * @return
     */
    List<Integer> getDaysOfWeekToRun();

    /**
     * Set the days of the week that the job will run. 0 -> 6 == Sunday -> Saturday
     *
     * @param daysOfWeekToRun
     */
    void setDaysOfWeekToRun(List<Integer> daysOfWeekToRun);

    /**
     * Set flog to indicate that the job should only run in the context that it resides within.
     *
     * @param targetResidingContextOnly
     */
    void setTargetResidingContextOnly(boolean targetResidingContextOnly);

    /**
     * Get flog to indicate that the job should only run in the context that it resides within.
     * @return
     */
    boolean isTargetResidingContextOnly();

    /**
     * Set flag to indicate that a job participates in a lock.
     *
     * @param participatesInLock
     */
    void setParticipatesInLock(boolean participatesInLock);

    /**
     * Get flag to indicate that a job participates in a lock.
     *
     * @return
     */
    boolean isParticipatesInLock();

    /**
     * An optional property to set how you want the getCommandLine to be executed, by default if the agent is
     * deployed on a unix environment it will default to Bash, windows it will default to cmd.
     * @return
     */
    String getExecutionEnvironmentProperties();

    /**
     * An optional property to set how you want the getCommandLine to be executed, by default if the agent is
     * deployed on a unix environment it will default to Bash, windows it will default to cmd.
     *
     * To set, passing a pipe delimited string to represent the String array that represent the commands that will be
     * used by the java.lang.ProcessBuilder to set how we want to execute the script from getCommandLine
     * @return
     */
    void setExecutionEnvironmentProperties(String executionEnvironmentProperties);

    /**
     * Set flag to indicate that a job can be repeated.
     *
     * @param jobRepeatable
     */
    void setJobRepeatable(boolean jobRepeatable);

    /**
     * Set flag to indicate that a job can be repeated.
     *
     * @return
     */
    boolean isJobRepeatable();
}
