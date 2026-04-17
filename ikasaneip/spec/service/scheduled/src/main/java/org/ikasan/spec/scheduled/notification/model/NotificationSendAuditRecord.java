package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;

public interface NotificationSendAuditRecord extends Serializable {

    /**
     * Retrieves the identifier associated with the current object.
     *
     * @return a string representing the unique identifier of the object.
     */
    String getId();

    /**
     * Sets the ID for an object.
     *
     * @param id the unique identifier to be assigned to the object
     */
    void setId(String id);

    /**
     * Retrieves the NotificationSendAudit instance associated with the current record.
     *
     * @return the associated NotificationSendAudit instance.
     */
    NotificationSendAudit getNotificationSendAudit();

    /**
     * Assigns a {@link NotificationSendAudit} instance to this object.
     *
     * @param notificationSendAudit the {@link NotificationSendAudit} instance
     *                              to be associated. It must not be null.
     */
    void setNotificationSendAudit(NotificationSendAudit notificationSendAudit);

    /**
     * Retrieves the current timestamp.
     *
     * @return the current timestamp as a long value, typically representing the number of milliseconds
     *         since the Unix epoch (January 1, 1970, 00:00:00 GMT).
     */
    long getTimestamp();

    /**
     * Sets the timestamp value for the relevant entity or process.
     *
     * @param timestamp the timestamp to set, represented as the number of milliseconds
     *                  since the Unix epoch (January 1, 1970, 00:00:00 GMT).
     */
    void setTimestamp(long timestamp);

    /**
     * Retrieves the timestamp of the most recent modification.
     *
     * @return the timestamp of the last modification as a long value.
     */
    long getModifiedTimestamp();

    /**
     * Sets the modified timestamp for this record.
     *
     * @param modifiedTimestamp the new modified timestamp to be set, represented as a long value
     */
    void setModifiedTimestamp(long modifiedTimestamp);

    /**
     * Retrieves the user or entity who last modified this record.
     *
     * @return the identifier of the user or entity who performed the last modification
     */
    String getModifiedBy();

    /**
     * Updates the identifier of the user or system that last modified this record.
     *
     * @param modifiedBy the identifier of the modifier
     */
    void setModifiedBy(String modifiedBy);
}
