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

import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.DryRunParameters;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;

import java.util.List;

/**
 * Contextualised Scheduled Process Event defines the core event managed by the Scheduler Agent that runs within a context
 * and reports schedule fire details and process execution details.
 *
 * @author Ikasan Development Team
 *
 */
public class ContextualisedScheduledProcessEventImpl extends ScheduledProcessEventImpl implements ContextualisedScheduledProcessEvent<Outcome, DryRunParameters>, HarvestEvent
{
    private String contextId;
    private List<String> childContextIds;
    private String contextInstanceId;
    private boolean skipped;
    private InternalEventDrivenJobInstance internalEventDrivenJob;
    private boolean raisedDueToFailureResubmission;

    @Override
    public String getContextName() {
        return this.contextId;
    }

    @Override
    public void setContextName(String contextName) {
        this.contextId = contextName;
    }

    @Override
    public List<String> getChildContextNames() {
        return childContextIds;
    }

    @Override
    public void setChildContextNames(List<String> childContextId) {
        this.childContextIds = childContextId;
    }

    @Override
    public String getContextInstanceId() {
        return this.contextInstanceId;
    }

    @Override
    public void setContextInstanceId(String contextInstanceId) {
        this.contextInstanceId = contextInstanceId;
    }

    @Override
    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    @Override
    public boolean isSkipped() {
        return this.skipped;
    }

    @Override
    public void setInternalEventDrivenJob(InternalEventDrivenJobInstance internalEventDrivenJob) {
        this.internalEventDrivenJob = internalEventDrivenJob;
    }

    @Override
    public InternalEventDrivenJobInstance getInternalEventDrivenJob() {
        return this.internalEventDrivenJob;
    }

    @Override
    public boolean isRaisedDueToFailureResubmission() {
        return raisedDueToFailureResubmission;
    }

    @Override
    public void setRaisedDueToFailureResubmission(boolean raisedDueToFailureResubmission) {
        this.raisedDueToFailureResubmission = raisedDueToFailureResubmission;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContextualisedScheduledProcessEventImpl{");
        sb.append("contextId='").append(contextId).append('\'');
        if(childContextIds != null) {
            sb.append(", childContextIds=[ ");
            childContextIds.forEach(id -> sb.append("[").append(id).append("] "));
        }
        else {
            sb.append(", childContextIds='").append(this.childContextIds).append('\'');
        }
        sb.append("], contextInstanceId='").append(contextInstanceId).append('\'');
        sb.append(", skipped=").append(skipped);
        sb.append(", internalEventDrivenJob=").append(internalEventDrivenJob);
        sb.append(", id=").append(id);
        sb.append(", agentName='").append(agentName).append('\'');
        sb.append(", agentHostname='").append(agentHostname).append('\'');
        sb.append(", jobName='").append(jobName).append('\'');
        sb.append(", jobGroup='").append(jobGroup).append('\'');
        sb.append(", jobDescription='").append(jobDescription).append('\'');
        sb.append(", commandLine='").append(commandLine).append('\'');
        sb.append(", returnCode=").append(returnCode);
        sb.append(", successful=").append(successful);
        sb.append(", outcome=").append(outcome);
        sb.append(", resultOutput='").append(resultOutput).append('\'');
        sb.append(", resultError='").append(resultError).append('\'');
        sb.append(", pid=").append(pid);
        sb.append(", user='").append(user).append('\'');
        sb.append(", fireTime=").append(fireTime);
        sb.append(", nextFireTime=").append(nextFireTime);
        sb.append(", completionTime=").append(completionTime);
        sb.append(", harvested=").append(harvested);
        sb.append(", harvestedDateTime=").append(harvestedDateTime);
        sb.append(", dryRun=").append(dryRun);
        sb.append(", jobStarting=").append(jobStarting);
        sb.append(", dryRunParameters=").append(dryRunParameters);
        sb.append('}');
        return sb.toString();
    }
}
