package org.ikasan.ootb.scheduler.agent.module.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

import java.util.List;

public class EnrichedContextualisedScheduledProcessEvent extends ContextualisedScheduledProcessEventImpl {

    @JsonIgnore
    private Process process;
    @JsonIgnore
    private ProcessHandle processHandle;
    @JsonIgnore
    private boolean detached;                   // The Process is now detached, only ProcessHandle information is available
    @JsonIgnore
    private boolean detachedAlreadyFinished;    // The Process is now detached and has already completed.
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

    @JsonIgnore
    public ProcessHandle getProcessHandle() {
        return processHandle;
    }

    @JsonIgnore
    public void setProcessHandle(ProcessHandle processHandle) {
        this.processHandle = processHandle;
        if (processHandle!=null) {
            detached = true;
        }
    }

    @JsonIgnore
    public void setDetachedAlreadyFinished(boolean detachedAlreadyFinished) {
        this.detached = detachedAlreadyFinished;
        this.detachedAlreadyFinished = detachedAlreadyFinished;
    }    
    @JsonIgnore
    public boolean isDetachedAlreadyFinished() {
        return detachedAlreadyFinished;
    }
    @JsonIgnore
    public void setDetached(boolean detached) {
        this.detached = detached;
    }    
    @JsonIgnore
    public boolean isDetached() {
        return detached;
    }

    @JsonIgnore
    /**
     * The identity is used during persistence of the process details to assist restart after agent restart
     */
    public String getProcessIdentity() {
        return getContextInstanceId() + getJobName();
    }

    @JsonIgnore
    public void setDetailsFromProcess() {
        ProcessHandle.Info info = null;

        if (detachedAlreadyFinished) {
            setUser("Detatched process");
            setCommandLine("Detatched process");
        } else {
            if (isDetached()) {
                info = processHandle.info();
            } else {
                info = process.info();
            }

            if(info != null && !info.user().isEmpty()) {
                setUser( info.user().get() );
            }

            if(info != null && !info.commandLine().isEmpty()) {
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
            "process=" + process +
            ", processHandle=" + processHandle +
            ", detached=" + detached +
            ", detachedAlreadyFinished=" + detachedAlreadyFinished +
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
