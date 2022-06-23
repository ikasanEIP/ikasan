package org.ikasan.spec.scheduled.notification.model;

import java.util.List;

public interface EmailNotificationDetailsWrapper {

    List<EmailNotificationDetails> getEmailNotificationDetails();

    void setEmailNotificationDetails(List<EmailNotificationDetails> emailNotificationDetails);
}
