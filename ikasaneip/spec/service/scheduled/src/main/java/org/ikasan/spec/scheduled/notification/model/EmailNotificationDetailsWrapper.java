package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;
import java.util.List;

public interface EmailNotificationDetailsWrapper extends Serializable {

    List<EmailNotificationDetails> getEmailNotificationDetails();

    void setEmailNotificationDetails(List<EmailNotificationDetails> emailNotificationDetails);
}
