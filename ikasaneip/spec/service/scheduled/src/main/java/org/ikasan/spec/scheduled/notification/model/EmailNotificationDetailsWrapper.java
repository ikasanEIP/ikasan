package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;
import java.util.List;

public interface EmailNotificationDetailsWrapper extends Serializable {

    /**
     * Retrieves the details for email notifications.
     *
     * @return a list of {@code EmailNotificationDetails} objects, each containing
     *         information such as job name, context names, monitor type, and
     *         email communication specifics like recipients and subject.
     */
    List<EmailNotificationDetails> getEmailNotificationDetails();

    /**
     * Configures the details for email notifications by setting a list of
     * {@link EmailNotificationDetails} objects. Each object in the list
     * encapsulates the properties and settings for an individual email notification.
     *
     * @param emailNotificationDetails a list of {@link EmailNotificationDetails} instances
     *                                  containing the configuration for each email notification.
     */
    void setEmailNotificationDetails(List<EmailNotificationDetails> emailNotificationDetails);
}
