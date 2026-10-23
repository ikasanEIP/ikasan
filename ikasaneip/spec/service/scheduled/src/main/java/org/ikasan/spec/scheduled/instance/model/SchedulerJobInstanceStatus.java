package org.ikasan.spec.scheduled.instance.model;


public class SchedulerJobInstanceStatus {
    private String jobName;
    private String agentName;
    private InstanceStatus instanceStatus;

    /**
     * Retrieves the name of the job.
     *
     * @return the name of the job as a String
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Sets the name of the job.
     *
     * @param jobName the name to be assigned to the job
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Retrieves the name of the agent associated with this instance.
     *
     * @return the agent name as a String
     */
    public String getAgentName() {
        return agentName;
    }

    /**
     * Sets the name of the agent associated with the job instance.
     *
     * @param agentName the name of the agent to associate with this job instance
     */
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    /**
     * Retrieves the current status of the instance.
     *
     * @return the current instance status.
     */
    public InstanceStatus getInstanceStatus() {
        return instanceStatus;
    }

    /**
     * Updates the instance status of this job instance.
     *
     * @param instanceStatus the new status to be set for the job instance
     */
    public void setInstanceStatus(InstanceStatus instanceStatus) {
        this.instanceStatus = instanceStatus;
    }
}
