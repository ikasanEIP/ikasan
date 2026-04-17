package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;

public interface EmailNotificationContextRecord extends Serializable {

    /**
     * Retrieves the unique identifier associated with this instance.
     *
     * @return a string representing the unique identifier.
     */
    String getId();

    /**
     * Sets the identifier for the object.
     *
     * @param id the identifier to be set; must not be null or empty
     */
    void setId(String id);

    /**
     * Retrieves the name of the context associated with the notification.
     *
     * @return the name of the context as a String
     */
    String getContextName();

    /**
     * Sets the context name associated with this email notification context record.
     *
     * @param contextName the name of the context to be set
     */
    void setContextName(String contextName);

    /**
     * Retrieves the email notification context associated with the current record.
     *
     * @return an instance of {@code EmailNotificationContext} representing the email notification context.
     */
    EmailNotificationContext getEmailNotificationContext();

    /**
     * Sets the email notification context for this object.
     *
     * @param emailNotificationContext the {@link EmailNotificationContext} instance containing
     *                                 the necessary configuration and data for email notifications.
     */
    void setEmailNotificationContext(EmailNotificationContext emailNotificationContext);

    /**
     * Retrieves the current timestamp.
     *
     * @return the current timestamp as a long value, typically representing the number of milliseconds since the Unix epoch.
     */
    long getTimestamp();

    /**
     * Sets the timestamp for the object.
     *
     * @param timestamp The timestamp value to set, represented as the
     *                  number of milliseconds since the Unix epoch.
     */
    void setTimestamp(long timestamp);

    /**
     * Retrieves the timestamp of the last modification.
     *
     * @return the timestamp indicating the last modification, represented as a long value.
     */
    long getModifiedTimestamp();

    /**
     * Sets the modified timestamp representing when the record was last updated.
     *
     * @param modifiedTimestamp the timestamp of the last modification, expressed as a long value
     */
    void setModifiedTimestamp(long modifiedTimestamp);

    /**
     * Retrieves the identifier of the entity or user who last modified the record.
     *
     * @return the identifier of the modifier as a String.
     */
    String getModifiedBy();

    /**
     * Sets the identifier of the user or system that modified this record.
     *
     * @param modifiedBy the identifier of the user or system that performed the modification
     */
    void setModifiedBy(String modifiedBy);

}
