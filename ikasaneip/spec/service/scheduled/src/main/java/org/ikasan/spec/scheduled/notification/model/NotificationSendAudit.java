package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;

public interface NotificationSendAudit extends Serializable {

    String getJobName();

    void setJobName(String jobName);

    String getContextInstanceId();

    void setContextInstanceId(String contextInstanceId);

    String getMonitorType();

    void setMonitorType(String monitorType);

    String getNotifierType();

    void setNotifierType(String notifierType);

    boolean isNotificationSend();

    void setNotificationSend(boolean notificationSend);

}
