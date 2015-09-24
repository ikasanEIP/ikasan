package org.ikasan.component.endpoint.email.producer;
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
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;
/**
 * Email Endpoiunt Producer
 * @author Ikasan Development Team
 */
public class EmailProducer implements Producer<EmailPayload>, ManagedResource, ConfiguredResource<EmailProducerConfiguration> {
    /** logger instance */
    private static Logger logger = Logger.getLogger(EmailProducer.class);

    // configured resource identifier
    private String configurationId;

    // configuration bean
    private EmailProducerConfiguration configuration;

    /** mail session */
    private Session session;

    private ManagedResourceRecoveryManager managedResourceRecoveryManager;

    private Map<Message.RecipientType, Address[]> recipients;

    /**
     * determines whether this managed resource failure will fail the startup of the flow
     */
    protected boolean isCriticalOnStartup = true;



    @Override
    public void invoke(EmailPayload payload) throws EndpointException {



        MimeMessage message = new MimeMessage(session);

        try {
            message.addRecipients(Message.RecipientType.TO, recipients.get(Message.RecipientType.TO));
            message.addRecipients(Message.RecipientType.CC, recipients.get(Message.RecipientType.CC));
            message.addRecipients(Message.RecipientType.BCC, recipients.get(Message.RecipientType.BCC));

            message.setSubject(configuration.getSubject());
            Multipart multipart = new MimeMultipart();

            MimeBodyPart bodyPart = new MimeBodyPart();
            String bodyContent = payload.formatEmailBody(payload.getEmailBody(), configuration.getEmailBody(), configuration.getEmailFormat());
            //mail library does not accept email body as null.
            if(bodyContent==null){
                bodyContent = "";
            }
            bodyPart.setContent(bodyContent, configuration.getEmailFormat());
            multipart.addBodyPart(bodyPart);

            if(configuration.hasAttachment){

                List<String> attachmentNames = payload.getAttachmentNames();
                if(attachmentNames!= null){
                    for(String attachmentName:attachmentNames){
                        MimeBodyPart mailAttachment = new MimeBodyPart();
                            DataSource dataSource=new ByteArrayDataSource(payload.getAttachment(attachmentName),payload.getAttachmentType(attachmentName));
                            mailAttachment.setDataHandler(new DataHandler(dataSource));
                            mailAttachment.setFileName(attachmentName);
                            multipart.addBodyPart(mailAttachment);
                    }
                }

            }
            message.setContent(multipart);
            Transport.send(message);
        }catch(MessagingException e){
            throw new EndpointException(e);
       }

    }


    @Override
    public EmailProducerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(EmailProducerConfiguration configuration) {
        this.configuration = configuration;

    }

    private void setupConfiguration(EmailProducerConfiguration configuration) {
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

        session = Session.getInstance(mailProperties);
        recipients = Maps.newHashMap();
        recipients.put(Message.RecipientType.TO, toArray(configuration.getToRecipients()));
        recipients.put(Message.RecipientType.CC, toArray(configuration.getCcRecipients()));
        recipients.put(Message.RecipientType.BCC, toArray(configuration.getBccRecipients()));
    }

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }



    private Address[] toArray(List<String> emailAddresses)
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

    @Override
    public void startManagedResource() {
        setupConfiguration(configuration);
    }

    @Override
    public void stopManagedResource() {

    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager) {

        this.managedResourceRecoveryManager = managedResourceRecoveryManager;
    }

    @Override
    public boolean isCriticalOnStartup() {
        return isCriticalOnStartup;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup) {
       this.isCriticalOnStartup = criticalOnStartup;
    }

}
