package org.ikasan.component.endpoint.email.producer;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.util.List;
import java.util.Properties;
/**
 * Created by xualys on 16/09/2015.
 */
public class EmailProducer implements Producer<EmailPayload>, ConfiguredResource<EmailProducerConfiguration> {
    /** logger instance */
    private static Logger logger = Logger.getLogger(EmailProducer.class);

    // configured resource identifier
    private String configurationId;

    // configuration bean
    private EmailProducerConfiguration configuration;

    /** mail session */
    private Session session;



    @Override
    public void invoke(EmailPayload payload) throws EndpointException {



        MimeMessage message = new MimeMessage(session);

        try {
            message.addRecipients(Message.RecipientType.TO, toArray(configuration.getToRecipients()));
            message.addRecipients(Message.RecipientType.CC, toArray(configuration.getCcRecipients()));
            message.addRecipients(Message.RecipientType.BCC, toArray(configuration.getBccRecipients()));

            if (configuration.getSubject() == null) {
                message.setSubject("[" + configuration.getRuntimeEnvironment() + "]: No Subject");
            } else {
                String subject = "[" + configuration.getRuntimeEnvironment() + "]: " + configuration.getSubject();
                message.setSubject(subject);
            }
            Multipart multipart = new MimeMultipart();

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(payload.getEmailBody(), payload.getEmailFormat());
            multipart.addBodyPart(bodyPart);

            if(configuration.hasAttachment){

                List<String> attachmentNames = payload.getAttachmentNames();
                if(attachmentNames!= null){
                    for(String attachmentName:attachmentNames){
                        MimeBodyPart mailAttachment = new MimeBodyPart();

                            DataSource dataSource=new ByteArrayDataSource(payload.getAttachment(attachmentName),payload.getAttachmentType(attachmentName));
                            mailAttachment.setDataHandler(new DataHandler(dataSource));
                            mailAttachment.setFileName(attachmentName);
                         //   mailAttachment.setContent(payload.getAttachment(attachmentName), payload.getAttachmentType(attachmentName));
                           // mailAttachment.setHeader("Content-Type", payload.getAttachmentType(attachmentName));
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

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }



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
}
