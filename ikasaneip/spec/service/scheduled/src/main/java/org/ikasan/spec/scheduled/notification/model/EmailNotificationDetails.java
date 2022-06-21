package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;
import java.util.List;

public interface EmailNotificationDetails extends Serializable {

    String getJobName();

    void setJobName(String jobName);

    String getContextName();

    void setContextName(String contextName);

    String getMonitorType();

    void setMonitorType(String monitorType);

    List<String> getEmailNotificationTemplateParameters();

    void setEmailNotificationTemplateParameters(List<String> emailNotificationTemplateParameters);

    List<String> getEmailSendTo();

    void setEmailSendTo(List<String> emailSendTo);

    List<String> getEmailSendCc();

    void setEmailSendCc(List<String> emailSendCc);

    List<String> getEmailSendBcc();

    void setEmailSendBcc(List<String> emailSendBcc);

    String getEmailSubject();

    void setEmailSubject(String emailSubject);

    String getEmailBody();

    void setEmailBody(String emailBody);

    String getEmailSubjectTemplate();

    void setEmailSubjectTemplate(String emailSubjectTemplate);

    String getEmailBodyTemplate();

    void setEmailBodyTemplate(String emailBodyTemplate);

    String getAttachment();

    void setAttachment(String attachment);

    boolean isHtml();

    void setHtml(boolean html);

}
