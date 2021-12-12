/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.spec.scheduled.event.model;

/**
 * Model contract for a Scheduled Process Event for OOTB Scheduler Agents.
 *
 * @param <OUTCOME>
 */
public interface ScheduledProcessEvent<OUTCOME, DRY_RUN_PARAMS extends DryRunParameters>
{
    /**
     * Get descriptive outcome of the scheduled process flow
     * @return
     */
    OUTCOME getOutcome();

    /**
     * Set descriptive outcome of the scheduled process flow
     * @param outcome
     */
    void setOutcome(OUTCOME outcome);

    /**
     * Get the name of the agent
     * @return
     */
    String getAgentName();

    /**
     * Set the name of the agent, usually module name
     * @param agentName
     */
    void setAgentName(String agentName);

    /**
     * Get the name of the agent host
     * @return
     */
    String getAgentHostname();

    /**
     * Set the name of the agent host.
     * @param agentHostname
     */
    void setAgentHostname(String agentHostname);

    /**
     * Get scheduled job name
     * @return
     */
    String getJobName();

    /**
     * Set scheduled job name
     * @param jobName
     */
    void setJobName(String jobName);

    /**
     * Get the job description.
     * @return
     */
    String getJobDescription();

    /**
     * Set the job description.
     * @param jobDescription
     */
    void setJobDescription(String jobDescription);

    /**
     * Get the scheduled job group.
     * @return
     */
    String getJobGroup();

    /**
     * Set the scheduled job group.
     * @param jobGroup
     */
    void setJobGroup(String jobGroup);

    /**
     * Get the command line to be executed.
     * @return
     */
    String getCommandLine();

    /**
     * Set the command line to be executed.
     * @param commandLine
     */
    void setCommandLine(String commandLine);

    /**
     * Get the return code of the executed process from the command line.
     * @return
     */
    int getReturnCode();

    /**
     * Set the return code of the executed process.
     * @param result
     */
    void setReturnCode(int result);

    /**
     * Return if the execution was successful.
     * @return
     */
    boolean isSuccessful();

    /**
     * Set the execution as successful or not.
     * @param successful
     */
    void setSuccessful(boolean successful);

    /**
     * Get standard out.
     * @return
     */
    String getResultOutput();

    /**
     * Set standard out.
     * @param resultOutput
     */
    void setResultOutput(String resultOutput);

    /**
     * Get standard error.
     * @return
     */
    String getResultError();

    /**
     * Set standard error.
     * @param resultError
     */
    void setResultError(String resultError);

    /**
     * Get the PID of the executed process of the command line.
     * @return
     */
    long getPid();

    /**
     * Set the PID from the executed process of the command line.
     * @param pid
     */
    void setPid(long pid);

    /**
     * Get the user the command line is run as.
     * @return
     */
    String getUser();

    /**
     * Set the user the command line is run as.
     * @param user
     */
    void setUser(String user);

    /**
     * Get the scheduled fire time.
     * @return
     */
    long getFireTime();

    /**
     * Set the scheduled fire time.
     * @param fireTime
     */
    void setFireTime(long fireTime);

    /**
     * Get the next scheduled fire time in the future.
     * @return
     */
    long getNextFireTime();

    /**
     * Set the next scheduled fire time in the future.
     * @param nextFireTime
     */
    void setNextFireTime(long nextFireTime);

    /**
     * Get the scheduled execution completion time.
     * @return
     */
    long getCompletionTime();

    /**
     * Set the scheduled execution completion time.
     * @param completionTime
     */
    void setCompletionTime(long completionTime);

    /**
     * Boolean flag to indicate if the job was a dry run.
     *
     * @return
     */
    boolean isJobStarting();

    /**
     * Set the boolean flag to indicate if the job was a dry run.
     *
     * @return
     */
    void setJobStarting(boolean jobStarting);

    /**
     * Boolean flag to indicate if the job was a dry run.
     *
     * @return
     */
    boolean isDryRun();

    /**
     * Set the boolean flag to indicate if the job was a dry run.
     *
     * @return
     */
    void setDryRun(boolean dryRun);

    /**
     * Set the parameters that will determine the dry run behaviour.
     *
     * @param dryRunParameters
     */
    void setDryRunParameters(DRY_RUN_PARAMS dryRunParameters);

    /**
     * Get the parameters that will determine the dry run behaviour.
     *
     * @return
     */
    DRY_RUN_PARAMS getDryRunParameters();
}
