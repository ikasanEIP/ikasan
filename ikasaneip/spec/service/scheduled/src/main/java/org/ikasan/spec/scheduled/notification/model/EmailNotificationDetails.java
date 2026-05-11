package org.ikasan.spec.scheduled.notification.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface EmailNotificationDetails extends Serializable {

    /**
     * Retrieves the name of the job.
     *
     * @return the job name as a String
     */
    String getJobName();

    /**
     * Sets the name for the job.
     *
     * @param jobName the name to be assigned to the job. Must not be null or empty.
     */
    void setJobName(String jobName);



    /**
     * Retrieves the context name associated with this instance.
     *
     * @return the name of the context as a String
     */
    String getContextName();

    /**
     * Sets the context name associated with this instance.
     *
     * @param contextName the name of the context to set
     */
    void setContextName(String contextName);

    /**
     * Retrieves the name of the child context associated with this instance.
     *
     * @return the name of the child context as a String
     */
    String getChildContextName();

    /**
     * Sets the name of the child context associated with the email notification details.
     *
     * @param childContextName the name of the child context to be set
     */
    void setChildContextName(String childContextName);

    /**
     * Retrieves the type of monitor associated with the current instance.
     *
     * @return a string representing the monitor type.
     */
    String getMonitorType();

    /**
     * Sets the monitor type for the notification.
     *
     * @param monitorType the type of monitor to be associated with the notification
     */
    void setMonitorType(String monitorType);

    /**
     * Retrieves a map of template parameters to be used for an email notification.
     * The keys in the map represent parameter names, and their corresponding values
     * represent the associated parameter values.
     *
     * @return a map of email notification template*/
    Map<String,String> getEmailNotificationTemplateParameters();

    /**
     * Sets the parameters for the email notification template.
     *
     * @param emailNotificationTemplateParameters a map containing key-value pairs of template parameters
     *                                            to be used in the email notification.
     */
    void setEmailNotificationTemplateParameters(Map<String,String> emailNotificationTemplateParameters);


    /**
     * Retrieves the list of email addresses to which the notification will be sent.
     *
     * @return a List of Strings representing the email addresses of the primary recipients.
     */
    List<String> getEmailSendTo();

    /**
     * Sets the list of email addresses to which the notification will be sent.
     *
     * @param emailSendTo a list of email addresses that will be used as the primary recipients for the email notification
     */
    void setEmailSendTo(List<String> emailSendTo);

    /**
     * Retrieves the list of email addresses to include in the CC (carbon copy) field when sending an email notification.
     *
     * @return a List of Strings representing the email addresses to be included in the CC field.
     */
    List<String> getEmailSendCc();

    /**
     * Sets the list of email addresses to be included as CC (carbon copy*/
    void setEmailSendCc(List<String> emailSendCc);

    /**
     * Retrieves a list of email addresses to be included in the blind carbon copy (BCC) recipients of an email.
     *
     * @return a list of email addresses to be included*/
    List<String> getEmailSendBcc();


    /**
     * Sets the list of email addresses to be included as blind carbon copy (BCC) recipients
     * when sending an email notification.
     *
     * @param emailSendBcc a list of email addresses that will receive the email as BCC recipients.
     *                     The list must not be null and should contain valid email addresses.
     */
    void setEmailSendBcc(List<String> emailSendBcc);


    /**
     * Retrieves the subject of the email notification.
     *
     * @return the email subject as a String
     */
    String getEmailSubject();

    /**
     * Sets the subject of the email notification.
     *
     * @param emailSubject the subject to be assigned to the email. Must not be null or empty.
     */
    void setEmailSubject(String emailSubject);

    /**
     * Retrieves the body content of the email notification.
     *
     * @return the body of the email as a String
     */
    String getEmailBody();

    /**
     * Sets the body of the email notification.
     */
    void setEmailBody(String emailBody);


    /**
     * Retrieves the template for the email subject.
     *
     * @return a String representing the template used for constructing email subjects.
     */
    String getEmailSubjectTemplate();

    /**
     * Sets the email subject*/
    void setEmailSubjectTemplate(String emailSubjectTemplate);

    /**
     * Retrieves the email body template as a string.
     *
     * @return the email body template used for crafting email notifications
     */
    String getEmailBodyTemplate();

    /**
     * Sets the email body template for the notification.
     *
     * @param emailBodyTemplate the template string used to define the email body content
     */
    void setEmailBodyTemplate(String emailBodyTemplate);

        /**
     * Retrieves the attachment associated with the email notification.
     *
     * @return the attachment as a String, which represents the file path
     *         or identifier of the attachment included with the email.
     */
    String getAttachment();

    /**
     * Sets the attachment associated with the email notification.
     *
     * @param attachment the file path or identifier of the attachment to be included
     */
    void setAttachment(String attachment);

    /**
     * Determines if the email*/
    boolean isHtml();

    /**
     * Sets whether the email content should be treated as HTML or plain text.
     *
     * @param html a boolean value where true indicates that the email content should
     *             be treated as HTML, and false indicates plain text.
     */
    void setHtml(boolean html);
}
