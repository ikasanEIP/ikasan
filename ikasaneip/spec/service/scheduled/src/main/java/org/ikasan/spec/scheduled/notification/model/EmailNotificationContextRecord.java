package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;

public interface EmailNotificationContextRecord extends Serializable {

    String getId();

    void setId(String id);

    String getContextName();

    void setContextName(String contextName);

    EmailNotificationContext getEmailNotificationContext();

    void setEmailNotificationContext(EmailNotificationContext emailNotificationContext);

    long getTimestamp();

    void setTimestamp(long timestamp);

    long getModifiedTimestamp();

    void setModifiedTimestamp(long modifiedTimestamp);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);

}
