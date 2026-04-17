package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;

public interface NotificationSendAudit extends Serializable {

    /**
     * Retrieves the name of the job.
     *
     * @return the name of the job as a String.
     */
    String getJobName();

    /**
     * Sets the name of the job.
     *
     * @param jobName The name to assign to the job. It must be a non-null, non-empty string.
     */
    void setJobName(String jobName);

    /**
     * Retrieves the unique identifier associated with the context instance.
     *
     * @return a {@code String} representing the context instance's unique identifier.
     */
    String getContextInstanceId();

    /**
     * Sets the identifier of the context instance associated with this notification audit entry.
     *
     * @param contextInstanceId the unique identifier of the context instance
     */
    void setContextInstanceId(String contextInstanceId);

    /**
     * Retrieves the name of the context associated with the notification audit.
     *
     * @return the name of the context as a String
     */
    String getContextName();

    /**
     * Sets the context name associated with this entity.
     *
     * @param contextName the name of the context to be set
     */
    void setContextName(String contextName);

    /**
     * Retrieves the type of monitor associated with the current instance.
     *
     * @return a string representing the type of monitor.
     */
    String getMonitorType();

    /**
     * Sets the monitor type for this notification audit.
     *
     * @param monitorType the type of monitor to be set
     */
    void setMonitorType(String monitorType);

    /**
     * Retrieves the type of notifier.
     *
     * @return the type of notifier as a String
     */
    String getNotifierType();

    /**
     * Sets the type of notifier used for sending notifications.
     *
     * @param notifierType the type of notifier, represented as a string
     */
    void setNotifierType(String notifierType);

    /**
     * Checks whether the notification has been sent.
     *
     * @return true if the notification has been sent successfully; false otherwise.
     */
    boolean isNotificationSend();

    /**
     * Sets the flag indicating whether a notification has been sent.
     *
     * @param notificationSend the boolean value to indicate if the notification is sent (true) or not (false)
     */
    void setNotificationSend(boolean notificationSend);

}
