/*
 * $Id$  
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.dashboard.notification.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.notification.NotificationContentProducer;
import org.ikasan.dashboard.notification.Notifier;
import org.ikasan.monitor.notifier.EmailNotifierConfiguration;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class EmailNotifier implements Notifier
{
	/** logger instance */
    private static Logger logger = Logger.getLogger(EmailNotifier.class);
    
	private EmailNotifierConfiguration configuration;
	
	/** mail session */
    private Session session;

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.notification.Notifier#sendNotification()
	 */
	@Override
	public void sendNotification(NotificationContentProducer notificationContentProducer)
	{
		MimeMessage message = new MimeMessage(session);

        try
		{
        	logger.info("Sending notification to: " + notificationContentProducer.getNoitificationReceivers());
        	if(notificationContentProducer.getNoitificationReceivers() != null)
        	{
        		message.addRecipients(Message.RecipientType.TO, toArray( parseRecipients(notificationContentProducer.getNoitificationReceivers()) ));
        	}

	        message.setSubject(notificationContentProducer.getNotificationSubject());
	        logger.info("Subject: " + notificationContentProducer.getNotificationSubject());

	        BodyPart bodyPart = new MimeBodyPart();
	        logger.info("Body: " + notificationContentProducer.getNotificationContent());
	        if(notificationContentProducer.getNotificationContent() != null)
	        {
	            bodyPart.setContent(notificationContentProducer.getNotificationContent(), "text/html; charset=utf-8");
	        }

	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(bodyPart);
	        message.setContent(multipart);
	        Transport.send(message);
	        logger.info("Email sent!");
	        
		} 
        catch (Exception e)
		{
			logger.error("An exception has occurred trying to send email notification. " + notificationContentProducer, e);
		}		
	}
	
	protected List<String> parseRecipients(String recipients)
	{
		ArrayList<String> recipientsList = new ArrayList<String>();
		StringTokenizer recipientTokens = new StringTokenizer(recipients, ",");

		while (recipientTokens.hasMoreElements()) 
		{
			recipientsList.add(recipientTokens.nextToken());
		}
		
		return recipientsList;
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
