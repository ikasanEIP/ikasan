package org.ikasan.spec.scheduled.joblock.model;

public interface JobLockCacheRecord {
    String DEFAULT_ENVIRONMENT = "DEFAULT_ENVIRONMENT";

    /**
     * Returns the ID of the object.
     *
     * @return the ID as a String
     */
    String getId();

    /**
     * Sets the environment for the object identified by the given ID.
     *
     * @param id the ID of the object for which the environment needs to be set
     */
    void setEnvironment(String id);

    /**
     * Retrieves the environment associated with the object.
     *
     * @return the environment as a String
     */
    String getEnvironment();

    /**
     * Sets the job lock cache data for the job lock record.
     *
     * @param jobLockCache the job lock cache data to set
     */
    void setJobLockCache(JobLockCacheData jobLockCache);

    /**
     * Retrieves the JobLockCacheData containing information about job locks.
     *
     * @return the JobLockCacheData object containing lock information
     */
    JobLockCacheData getJobLockCache();

    /**
     * Retrieves the timestamp representing the current time in milliseconds.
     *
     * @return the timestamp in milliseconds
     */
    long getTimestamp();

    /**
     * Retrieves the timestamp representing the last modification time in milliseconds.
     *
     * @return the modified timestamp in milliseconds
     */
    long getModifiedTimestamp();
}