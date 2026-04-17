package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;

public interface EmailNotificationDetailsRecord extends Serializable {

    /**
     * Retrieves the unique identifier associated with this object.
     *
     * @return the unique identifier as a String
     */
    String getId();

    /**
     * Sets the identifier for the object.
     *
     * @param id the unique identifier to be assigned to the object
     */
    void setId(String id);

    /**
     * Retrieves the name of the job.
     *
     * @return the name of the job as a String
     */
    String getJobName();

    /**
     * Sets the name of the job.
     *
     * @param jobName The name to assign to the job. It must be a non-null, non-empty String.
     */
    void setJobName(String jobName);

    /**
     * Retrieves the name of the context associated with the email notification details.
     *
     * @return the name of the context as a {@code String}.
     */
    String getContextName();

    /**
     * Sets the name of the context associated with this record.
     *
     * @param contextName the name of the context to set
     */
    void setContextName(String contextName);

    /**
     * Retrieves the monitor type associated with the current instance.
     *
     * @return a {@code String} representing the type of monitor.
     */
    String getMonitorType();

    /**
     * Sets the type of monitor associated with the email notification details.
     *
     * @param monitorType the type of monitor to set, which specifies the kind of monitoring
     *                    operation or category applicable to this notification.
     */
    void setMonitorType(String monitorType);

    /**
     * Retrieves the details of the email notification associated with the record.
     *
     * @return an instance of {@link EmailNotificationDetails} containing information such as
     *         job name, context name, email recipients, subject, body, and other related details.
     */
    EmailNotificationDetails getEmailNotificationDetails();

    /**
     * Updates the email notification details for the record.
     *
     * @param emailNotificationDetails the email notification details to set. This object contains
     *                                  various attributes such as email recipients, subject, body,
     *                                  and configuration details required for email notifications.
     */
    void setEmailNotificationDetails(EmailNotificationDetails emailNotificationDetails);

    /**
     * Retrieves the current timestamp.
     *
     * @return the current timestamp as a long value representing the number of milliseconds since the Unix epoch (January 1, 1970, 00:00:00 GMT).
     */
    long getTimestamp();

    /**
     * Sets the timestamp value.
     *
     * @param timestamp the timestamp to be set, represented as the number of milliseconds
     *                  since the Unix epoch (January 1, 1970, 00:00:00 GMT).
     */
    void setTimestamp(long timestamp);

    /**
     * Retrieves the timestamp of the last modification.
     *
     * @return the timestamp of the last modification as a long value,
     *         representing the number of milliseconds since the epoch (January 1, 1970, 00:00:00 GMT).
     */
    long getModifiedTimestamp();

    /**
     * Sets the timestamp when the record was last modified.
     *
     * @param modifiedTimestamp the timestamp representing the last modification time, in milliseconds since the epoch
     */
    void setModifiedTimestamp(long modifiedTimestamp);

    /**
     * Retrieves the user or identifier that last modified the associated record.
     *
     * @return a string representing the modifier of the record.
     */
    String getModifiedBy();

    /**
     * Sets the identifier of the user or system that modified this record.
     *
     * @param modifiedBy the identifier of the user or system that performed the modification
     */
    void setModifiedBy(String modifiedBy);
}
