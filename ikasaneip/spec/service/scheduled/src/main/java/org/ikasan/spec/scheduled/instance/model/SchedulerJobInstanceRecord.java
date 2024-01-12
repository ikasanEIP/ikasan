package org.ikasan.spec.scheduled.instance.model;

public interface SchedulerJobInstanceRecord {

    /**
     * Get the id.
     *
     * @return
     */
    String getId();

    /**
     * Get the job type.
     *
     * @return
     */
    String getType();

    /**
     * Get the job name.
     *
     * @return
     */
    String getJobName();

    /**
     * Set the job name.
     *
     * @param jobName
     */
    void setJobName(String jobName);

    /**
     * Get the job display name
     *
     * @return
     */
    String getDisplayName();

    /**
     * Set the job display name
     *
     * @param displayName
     */
    void setDisplayName(String displayName);

    /**
     * Get the context name.
     *
     * @return
     */
    String getContextName();

    /**
     * Set the context name.
     *
     * @param contextName
     */
    void setContextName(String contextName);

    /**
     * Get the child context name.
     *
     * @return
     */
    String getChildContextName();

    /**
     * Set the child context name.
     *
     * @param childContextName
     */
    void setChildContextName(String childContextName);

    /**
     * Get the context instance id.
     *
     * @return
     */
    String getContextInstanceId();

    /**
     * Set the context instance id.
     *
     * @param contextInstanceId
     */
    void setContextInstanceId(String contextInstanceId);

    /**
     * Get the scheduler job instance.
     *
     * @return
     */
    SchedulerJobInstance getSchedulerJobInstance();

    /**
     * Set the scheduler job instance.
     *
     * @param schedulerJobInstance
     */
    void setSchedulerJobInstance(SchedulerJobInstance schedulerJobInstance);

    /**
     * Get the status.
     *
     * @return
     */
    String getStatus();

    /**
     * Set the status.
     *
     * @param status
     */
    void setStatus(String status);

    /**
     * Set the flag to indicate that the job only targets
     * its residing context.
     *
     * @param targetResidingContextOnly
     */
    void setTargetResidingContextOnly(boolean targetResidingContextOnly);

    /**
     * Flag to indicate that the job targets its residing contect only.
     * @return
     */
    boolean isTargetResidingContextOnly();

    /**
     * Set the floag to indicate that the job participates in a lock.
     *
     * @param participatesInLock
     */
    void setParticipatesInLock(boolean participatesInLock);

    /**
     * Flag to indicate if the job participates in a lock.
     *
     * @return
     */
    boolean isParticipatesInLock();

    /**
     * Get the start time.
     *
     * @return
     */
    long getStartTime();

    /**
     * Set the start time.
     */
    void setStartTime(long endTime);

    /**
     * Get the end time.
     *
     * @return
     */
    long getEndTime();

    /**
     * Set the end time.
     */
    void setEndTime(long endTime);

    /**
     * Get the created time stamp of the job instance.
     *
     * @return
     */
    long getTimestamp();

    /**
     * Set the created time stamp of the job instance.
     *
     * @param timestamp
     */
    void setTimestamp(long timestamp);

    /**
     * Get the modified time stamp of the job instance.
     *
     * @return
     */
    long getModifiedTimestamp();

    /**
     * Set the created time stamp of the job instance.
     *
     * @param timestamp
     */
    void setModifiedTimestamp(long timestamp);

    /**
     * Get the user name of who modified the job.
     *
     * @return
     */
    String getModifiedBy();

    /**
     * Set the user name of who modified the job.
     *
     * @param modifiedBy
     */
    void setModifiedBy(String modifiedBy);

    /**
     * Get the username of who manually submitted the job.
     *
     * @return
     */
    String getManuallySubmittedBy();

    /**
     * Set the username of who manually submitted the job.
     *
     * @param manuallySubmittedBy
     */
    void setManuallySubmittedBy(String manuallySubmittedBy);
}
