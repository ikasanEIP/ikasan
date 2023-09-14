package org.ikasan.ootb.scheduler.agent.module.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.DetachableProcess;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

import java.util.List;

public class EnrichedContextualisedScheduledProcessEvent extends ContextualisedScheduledProcessEventImpl {
    @JsonIgnore
    private transient DetachableProcess detachableProcess;

    @JsonIgnore
    private List<ContextParameterInstance> contextParameters;

    @JsonIgnore
    public DetachableProcess getDetachableProcess() {
        return detachableProcess;
    }

    @JsonIgnore
    public void setDetachableProcess(DetachableProcess detachableProcess) {
        this.detachableProcess = detachableProcess;
    }

    @JsonIgnore
    /*
     * The identity is used during persistence of the process details to assist restart after agent restart
     */
    public String getProcessIdentity() {
        String despacedJobName = getJobName() == null ? getJobName() : getJobName().replaceAll(" ", "_");
        return getContextInstanceId() + "-" + despacedJobName;
    }

    @JsonIgnore
    public void setDetailsFromProcess() {
        if (detachableProcess.isDetachedAlreadyFinished()) {
            setUser("Detatched process");
            setCommandLine("Detatched process");
        } else {
            ProcessHandle.Info info = detachableProcess.getInfo();
            if(info != null && info.user().isPresent()) {
                setUser( info.user().get() );
            }

            if(info != null && info.commandLine().isPresent()) {
                setCommandLine( info.commandLine().get() );
            }
            else {
                setCommandLine(getInternalEventDrivenJob().getCommandLine());
            }
        }
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
        return "EnrichedContextualisedScheduledProcessEvent{" +
            ", detachableProcess=" + detachableProcess +
            ", contextParameters=" + contextParameters +
            ", id=" + id +
            ", agentName='" + agentName + '\'' +
            ", agentHostname='" + agentHostname + '\'' +
            ", jobName='" + jobName + '\'' +
            ", jobGroup='" + jobGroup + '\'' +
            ", jobDescription='" + jobDescription + '\'' +
            ", commandLine='" + commandLine + '\'' +
            ", returnCode=" + returnCode +
            ", successful=" + successful +
            ", outcome=" + outcome +
            ", resultOutput='" + resultOutput + '\'' +
            ", resultError='" + resultError + '\'' +
            ", pid=" + pid +
            ", user='" + user + '\'' +
            ", fireTime=" + fireTime +
            ", nextFireTime=" + nextFireTime +
            ", completionTime=" + completionTime +
            ", harvested=" + harvested +
            ", harvestedDateTime=" + harvestedDateTime +
            ", dryRun=" + dryRun +
            ", jobStarting=" + jobStarting +
            ", dryRunParameters=" + dryRunParameters +
            '}';
    }
}
