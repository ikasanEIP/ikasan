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
package org.ikasan.ootb.scheduled.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.scheduled.event.model.DryRunParameters;
import org.ikasan.spec.scheduled.event.model.Outcome;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;

/**
 * Scheduled Process Event defines the core event managed by the Scheduler Agent
 * and reports schedule fire details and process execution details.
 *
 * @author Ikasan Development Team
 *
 */
public class ScheduledProcessEventImpl implements ScheduledProcessEvent<Outcome, DryRunParameters>, HarvestEvent
{
    protected Long id;
    protected String agentName;
    protected String agentHostname;
    protected String jobName;
    protected String jobGroup;
    protected String jobDescription;
    protected String commandLine;
    protected int returnCode;
    protected boolean successful;
    protected Outcome outcome;
    protected String resultOutput;
    protected String resultError;
    protected long pid;
    protected String user;
    protected long fireTime;
    protected long nextFireTime;
    protected long completionTime;
    protected boolean harvested;
    protected long harvestedDateTime;
    protected boolean dryRun = false;
    protected boolean jobStarting = false;
    protected DryRunParameters dryRunParameters;


    public Long getId()
    {
        return id;
    }

    private void setId(Long id)
    {
        this.id = id;
    }


    @Override
    public String getAgentName() {
        return agentName;
    }

    @Override
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public String getAgentHostname() {
        return agentHostname;
    }

    @Override
    public void setAgentHostname(String agentHostname) {
        this.agentHostname = agentHostname;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String getJobGroup() {
        return jobGroup;
    }

    @Override
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    @Override
    public String getJobDescription() {
        return this.jobDescription;
    }

    @Override
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    @Override
    public String getCommandLine() {
        return commandLine;
    }

    @Override
    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public String getResultOutput() {
        return resultOutput;
    }

    @Override
    public void setResultOutput(String resultOutput) {
        this.resultOutput = resultOutput;
    }

    @Override
    public String getResultError() {
        return resultError;
    }

    @Override
    public void setResultError(String resultError) {
        this.resultError = resultError;
    }

    @Override
    public long getPid() {
        return pid;
    }

    @Override
    public void setPid(long pid) {
        this.pid = pid;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public long getFireTime() {
        return fireTime;
    }

    @Override
    public void setFireTime(long fireTime) {
        this.fireTime = fireTime;
    }

    @Override
    public long getNextFireTime() {
        return nextFireTime;
    }

    @Override
    public void setNextFireTime(long nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    @Override
    public void setHarvested(boolean harvested) {
        this.harvested = harvested;
    }

    public boolean isHarvested() {
        return harvested;
    }

    public long getHarvestedDateTime() {
        return harvestedDateTime;
    }

    public void setHarvestedDateTime(long harvestedDateTime) {
        this.harvestedDateTime = harvestedDateTime;
    }

    @Override
    public int getReturnCode()
    {
        return returnCode;
    }

    @Override
    public void setReturnCode(int returnCode)
    {
        this.returnCode = returnCode;
    }

    @Override
    public boolean isSuccessful()
    {
        return successful;
    }

    @Override
    public void setSuccessful(boolean successful)
    {
        this.successful = successful;
    }

    @Override
    public Outcome getOutcome()
    {
        return outcome;
    }

    @Override
    public void setOutcome(Outcome outcome)
    {
        this.outcome = outcome;
    }

    @Override
    public long getCompletionTime()
    {
        return completionTime;
    }

    @Override
    public void setCompletionTime(long completionTime)
    {
        this.completionTime = completionTime;
    }

    @Override
    public boolean isDryRun() {
        return this.dryRun;
    }

    @Override
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    @Override
    public void setDryRunParameters(DryRunParameters dryRunParameters) {
        this.dryRunParameters = dryRunParameters;
    }

    @Override
    public DryRunParameters getDryRunParameters() {
        return this.dryRunParameters;
    }

    @Override
    public boolean isJobStarting() {
        return this.jobStarting;
    }

    @Override
    public void setJobStarting(boolean jobStarting) {
        this.jobStarting = jobStarting;
    }



    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
