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
package org.ikasan.monitor.notifier;

import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.monitor.Notifier;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

/**
 * Ikasan default email notifier implementation.
 *
 * @author Ikasan Development Team
 */
public class EmailNotifier implements Notifier<String>, ConfiguredResource<EmailNotifierConfiguration>
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(EmailNotifier.class);

    /** regular expression for splitting grouped email addresses in a single String separated by comma, semi-colon, or space */
    private static String EMAIL_ADDRESS_SPLIT_REGEXP = ",| |;";

    /** date time formatter */
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss.SSS Z");

    /** configured resource identifier */
    private String configuredResourceId;

    /** configuration */
    private EmailNotifierConfiguration configuration;

    /** mail session */
    private Session session;

    /** only interested in state changes */
    boolean notifyStateChangesOnly = true;

    /** last update sent time */
    long lastUpdateDateTime;

    /** buffer updates the occur ousaide the notification window*/
    StringBuilder pendingContent = new StringBuilder();

    @Override
    public void invoke(String environment, String moduleName, String flowName, String state)
    {
        if(configuration.isActive())
        {
        	final String name = "Module[" + moduleName + "] Flow[" + flowName + "]";
        	
            notify(environment, name, state);
        }
    }

    @Override
    public void setNotifyStateChangesOnly(boolean notifyStateChangesOnly)
    {
        this.notifyStateChangesOnly = notifyStateChangesOnly;
    }

    @Override
    public boolean isNotifyStateChangesOnly()
    {
        return this.notifyStateChangesOnly;
    }

    /**
     * Internal notify method
     * @param environment
     * @param name
     * @param state
     */
    protected void notify(String environment, String name, String state)
    {
        long now = Calendar.getInstance().getTimeInMillis();
        formatContent(now, name, state, pendingContent);

        // TODO - omit this for now as there is a bug where the notification may get missed
        // if we dont receive a subsequent notification outside the time window to force the update to go out.
        // really we need a timer thread i.e. quartz to manage that aspect. IKASAN-XXX jira ref.
//        if(this.lastUpdateDateTime + (configuration.getNotificationIntervalInSeconds().longValue() * 1000) < now)
//        {
            try
            {
                sendNotification(environment, name, state, pendingContent.toString());
                pendingContent = new StringBuilder();
                this.lastUpdateDateTime = now;
            }
            catch(MessagingException e)
            {
                throw new RuntimeException(e);
            }
//        }
    }

    /**
     * Format the buffered content
     * @param dateTime
     * @param name
     * @param state
     * @param bufferedStates
     */
    protected void formatContent(long dateTime, String name, String state, StringBuilder bufferedStates)
    {
        bufferedStates.append("[" + dateTimeFormatter.print( new DateTime(dateTime) ) + "] " + name + " is " + state + "\n");
    }

    /**
     * Send the notification email
     * @param env
     * @param name
     * @param content
     * @throws MessagingException
     */
    protected void sendNotification(String env, String name, String currentState, String content)
            throws MessagingException
    {
        MimeMessage message = new MimeMessage(session);

        message.addRecipients(Message.RecipientType.TO, toArray( configuration.getToRecipients() ));
        message.addRecipients(Message.RecipientType.CC, toArray( configuration.getCcRecipients() ));
        message.addRecipients(Message.RecipientType.BCC, toArray( configuration.getBccRecipients() ));

        if(configuration.getSubject() == null)
        {
            message.setSubject( "[" + env + "] " + name + " is " + currentState );
        }
        else
        {
            String subject = configuration.getSubject().replaceAll("\\$\\{environment\\}", env)
                    .replaceAll("\\$\\{name\\}", name)
                    .replaceAll("\\$\\{state\\}", currentState);

            message.setSubject(subject);
        }

        BodyPart bodyPart = new MimeBodyPart();
        if(content != null)
        {
            bodyPart.setText(content.toString());
        }

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);
        message.setContent(multipart);
        Transport.send(message);
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

        // fix any email Strings which contain mulitple email addresses
        emailAddresses = expandTokenisedAddresses(emailAddresses);

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

    /**
     * Ensure email addresses are tokenised correctly when seprated by commas, spaces, or semi-colons.
     * @param addresses
     * @return
     */
    protected List<String> expandTokenisedAddresses(List<String> addresses)
    {
        List<String> reviewedAddresses = new ArrayList<String>();

        for(String address:addresses)
        {
            String[] splitAddresses = address.split(EMAIL_ADDRESS_SPLIT_REGEXP);
            {
                for(String splitAddress:splitAddresses)
                {
                    if(splitAddress.length() > 0)
                    {
                        reviewedAddresses.add(splitAddress);
                    }
                }
            }
        }

        return reviewedAddresses;
    }

    @Override
    public EmailNotifierConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
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

        // reset states to default on configuration change
        this.lastUpdateDateTime = 0;
    }

    @Override
    public String getConfiguredResourceId()
    {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }
}
