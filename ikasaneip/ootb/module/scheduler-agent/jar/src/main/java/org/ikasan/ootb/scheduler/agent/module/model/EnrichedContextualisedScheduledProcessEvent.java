package org.ikasan.ootb.scheduler.agent.module.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

import java.util.List;

public class EnrichedContextualisedScheduledProcessEvent extends ContextualisedScheduledProcessEventImpl {

    @JsonIgnore
    private Process process;
    @JsonIgnore
    private List<ContextParameterInstance> contextParameters;

    /**
     * Get the process
     *
     * @return
     */
    @JsonIgnore
    public Process getProcess() {
        return process;
    }

    /**
     * Set the process
     *
     * @param process
     */
    @JsonIgnore
    public void setProcess(Process process) {
        this.process = process;
    }

    /**
     * Set the context parameters.
     *
     * @param contextParameters
     */
    @JsonIgnore
    public void setContextParameters(List<ContextParameterInstance> contextParameters) {
        this.contextParameters = contextParameters;
    }

    /**
     * Set the context parameters.
     *
     * @return
     */
    @JsonIgnore
    public List<ContextParameterInstance> getContextParameters() {
        return this.contextParameters;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EnrichedContextualisedScheduledProcessEvent{");
        sb.append("id=").append(id);
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
        if(contextParameters != null && !contextParameters.isEmpty()) {
            sb.append(", contextParameters=[");
            contextParameters.forEach(param -> sb.append(param).append(","));
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        }
        sb.append('}');
        return sb.toString();
    }
}
