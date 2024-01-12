package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface EmailNotificationContext extends Serializable  {

    /**
     * Get the name of the Context for the notification
     * @return
     */
    String getContextName();

    /**
     * Get the name of the Context for the notification
     * @param contextName
     */
    void setContextName(String contextName);

    /**
     * List of all monitor types that we want listen and send a notification for this context.
     * @return
     */
    List<String> getMonitorTypes();

    /**
     * Set monitorTypes for this context
     * @param monitorTypes
     */
    void setMonitorTypes(List<String> monitorTypes);

    /**
     * List of email address to send to
     * @return
     */
    List<String> getEmailSendTo();

    /** Set email address to send to
     * @param emailSendTo
     */
    void setEmailSendTo(List<String> emailSendTo);

    /**
     * Map of email address to send to for a given monitor type
     * @return
     */
    Map<String, List<String>> getEmailSendToByMonitorType();

    /** Map email address to send to by monitor type
     * @param emailSendToByMonitorType
     */
    void setEmailSendToByMonitorType(Map<String, List<String>> emailSendToByMonitorType);

    /**
     * List of email address to send to cc
     * @return
     */
    List<String> getEmailSendCc();

    /**
     * Set email address to send to cc
     * @param emailSendCc
     */
    void setEmailSendCc(List<String> emailSendCc);

    /**
     * Map of email address to send to cc for a given monitor type
     * @return
     */
    Map<String, List<String>> getEmailSendCcByMonitorType();

    /** Map email address to send to cc by monitor type
     * @param emailSendCcByMonitorType
     */
    void setEmailSendCcByMonitorType(Map<String, List<String>> emailSendCcByMonitorType);

    /**
     * List of email address to send to bcc
     * @return
     */
    List<String> getEmailSendBcc();

    /**
     * Set email address to send to bcc
     * @param emailSendBcc
     */
    void setEmailSendBcc(List<String> emailSendBcc);

    /**
     * Map of email address to send to bcc for a given monitor type
     * @return
     */
    Map<String, List<String>> getEmailSendBccByMonitorType();

    /** Map email address to send to bcc by monitor type
     * @param emailSendBccByMonitorType
     */
    void setEmailSendBccByMonitorType(Map<String, List<String>> emailSendBccByMonitorType);

    /**
     * Get notification templates for subject
     * @return
     */
    Map<String, String> getEmailSubjectNotificationTemplate();

    /**
     * Set notification templates for subject
     */
    void setEmailSubjectNotificationTemplate(Map<String, String> emailSubjectNotificationTemplate);

    /**
     * Get notification templates for body
     * @return
     */
    Map<String, String> getEmailBodyNotificationTemplate();

    /**
     * Set notification templates for subject
     * @param emailBodyNotificationTemplate
     */
    void setEmailBodyNotificationTemplate(Map<String, String> emailBodyNotificationTemplate);

    /**
     * Get any attachments
     * @return
     */
    String getAttachment();

    /**
     * Set attachments
     * @param attachment
     */
    void setAttachment(String attachment);

    /**
     * Flat to inform to return an html format email
     * @return
     */
    boolean isHtml();

    /**
     * Set true of false to send email as html
     * @param html
     */
    void setHtml(boolean html);
}
