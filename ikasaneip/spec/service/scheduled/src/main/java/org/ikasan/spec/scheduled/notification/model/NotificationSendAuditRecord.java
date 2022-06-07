package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;

public interface NotificationSendAuditRecord extends Serializable {

    String getId();

    void setId(String id);

    NotificationSendAudit getNotificationSendAudit();

    void setNotificationSendAudit(NotificationSendAudit notificationSendAudit);

    long getTimestamp();

    void setTimestamp(long timestamp);

    long getModifiedTimestamp();

    void setModifiedTimestamp(long modifiedTimestamp);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);
}
