package org.ikasan.dashboard.notification.email;

import org.ikasan.dashboard.notification.model.EmailNotification;
import org.ikasan.monitor.notifier.EmailNotifierConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * 
 * @author Ikasan Development Team
 *
 */
public class EmailNotifier
{
	/** logger instance */
    private static Logger logger = LoggerFactory.getLogger(EmailNotifier.class);
    
	private EmailNotifierConfiguration configuration;
	
	/** mail session */
    private Session session;

	public void sendNotification(EmailNotification emailNotification)
	{
		MimeMessage message = new MimeMessage(session);

        try
		{
        	logger.debug("Sending notification to: " + emailNotification.getRecipients());
        	if(emailNotification.getRecipients() != null)
        	{
        		message.addRecipients(Message.RecipientType.TO, toArray(emailNotification.getRecipients()));
        	}

	        message.setSubject(emailNotification.getSubject());
	        logger.debug("Subject: " + emailNotification.getSubject());

	        BodyPart bodyPart = new MimeBodyPart();
	        logger.debug("Body: " + emailNotification.getBody());
	        if(emailNotification.getBody() != null && emailNotification.isHtml())
	        {
	            bodyPart.setContent(emailNotification.getBody(), "text/html; charset=utf-8");
	        }
            else if(emailNotification.getBody() != null)
            {
                bodyPart.setContent(emailNotification.getBody(), "text/plain; charset=utf-8");
            }

	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(bodyPart);
	        message.setContent(multipart);
	        Transport.send(message);
	        logger.info("Email sent!");


		}
        catch (Exception e)
		{
			logger.error("An exception has occurred trying to send email notification. " + emailNotification, e);
		}		
	}
	
	/**
     * Convert the email addresses to actual Address implementations
     * @param emailAddresses
     * @return
     */
    protected Address[] toArray(List<String> emailAddresses)
    {
        if(emailAddresses == null)
        {
            return null;
        }

        int index = 0;
        Address[] addresses = new Address[emailAddresses.size()];
        for(String emailAddress:emailAddresses)
        {
            try
            {
                addresses[index++] = new InternetAddress(emailAddress);
            }
            catch(AddressException e)
            {
                logger.warn("Invalid email address", e);
            }
        }

        return addresses;
    }
    
	
    public EmailNotifierConfiguration getConfiguration()
    {
        return configuration;
    }


    public void setConfiguration(EmailNotifierConfiguration configuration)
    {
        this.configuration = configuration;

        Properties mailProperties = new Properties();

        mailProperties.put("mail.debug", configuration.isMailDebug());

        if(configuration.getMailFrom() != null)
        {
            mailProperties.put("mail.from", configuration.getMailFrom());
        }

        mailProperties.put("mail.mime.access.strict", configuration.getMailMimeAddressStrict());

        if(configuration.getMailHost() != null)
        {
            mailProperties.put("mail.host", configuration.getMailHost());
        }

        if(configuration.getMailStoreProtocol() != null)
        {
            mailProperties.put("mail.store.protocol", configuration.getMailStoreProtocol());
        }

        if(configuration.getMailTransportProtocol() != null)
        {
            mailProperties.put("mail.transport.protocol", configuration.getMailTransportProtocol());
        }

        if(configuration.getMailUser() != null)
        {
            mailProperties.put("mail.user", configuration.getMailUser());
        }

        if(configuration.getMailSmtpClass() != null)
        {
            mailProperties.put("mail.smtp.class", configuration.getMailSmtpClass());
        }

        if(configuration.getMailSmtpHost() != null)
        {
            mailProperties.put("mail.smtp.host", configuration.getMailSmtpHost());
        }

        if(configuration.getMailSmtpPort() > 0)
        {
            mailProperties.put("mail.smtp.port", configuration.getMailSmtpPort());
        }

        if(configuration.getMailSmtpUser() != null)
        {
            mailProperties.put("mail.smtp.user", configuration.getMailSmtpUser());
        }

        if(configuration.getMailPopClass() != null)
        {
            mailProperties.put("mail.pop.class", configuration.getMailPopClass());
        }

        if(configuration.getMailPopHost() != null)
        {
            mailProperties.put("mail.pop.host", configuration.getMailPopHost());
        }

        if(configuration.getMailPopPort() > 0)
        {
            mailProperties.put("mail.pop.port", configuration.getMailPopPort());
        }

        if(configuration.getMailPopUser() != null)
        {
            mailProperties.put("mail.pop.user", configuration.getMailPopUser());
        }

        mailProperties.putAll(configuration.getExtendedMailSessionProperties());

        session = javax.mail.Session.getInstance(mailProperties);

    }

}