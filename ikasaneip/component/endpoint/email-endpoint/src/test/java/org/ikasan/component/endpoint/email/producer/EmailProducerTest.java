package org.ikasan.component.endpoint.email.producer;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.Configured;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xualys on 17/09/2015.
 */
public class EmailProducerTest {

    String sender = "ikasanUnitTest@ikasan.org";
    String ccReceiver = "ccRecipient1@ikasan.org";
    String toReceiver = "toRecipient1@ikasan.org";
    String bccReceiver = "bccRecipient1@ikasan.org";

    /**
     * Logger for this class
     */
    private Logger logger = Logger.getLogger(EmailProducerTest.class);

    /**
     * in memory SMTP server
     */
    Wiser wiser;

    @Before
    public void setup() {
        wiser = new Wiser();
        while (true)
            try {
                wiser.setPort(2500);
                logger.info(String.format("Attempting to start Wiser SMTP Server on port 2500"));
                wiser.start();
                break;
            } catch (RuntimeException re) {
                logger.info("Failed to start Wiser SMTP server, sleeping for a couple of seconds", re);
                try {
                    Thread.sleep(2000l);
                } catch (InterruptedException e) {
                }
            }
    }

    @After
    public void teardown() {
        wiser.stop();
    }

    @Test
    public void test_successful_email_withoutAttachment() throws MessagingException, IOException {
        EmailProducerConfiguration emailNotifierConfiguration = getConfiguration(false);

        EmailProducer emailProducer = new EmailProducer();
        ((Configured) emailProducer).setConfiguration(emailNotifierConfiguration);

        emailProducer.invoke(getEmailPayload(false));
        List<WiserMessage> messages = wiser.getMessages();

        Assert.assertTrue("Should be three messages - one per addressee", messages.size() == 3);
        for (WiserMessage message : wiser.getMessages()) {
            Assert.assertTrue("sender should be " + sender, sender.equals(message.getEnvelopeSender()));

            MimeMessage mimeMessage = message.getMimeMessage();
            MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
            Assert.assertTrue("should be only 1 bodypart", mimeMultipart.getCount() == 1);
            BodyPart bodyPart = mimeMultipart.getBodyPart(0);
            String content = (String) bodyPart.getContent();
            Assert.assertTrue(content.contains("This is the email body"));
        }
    }


    @Test
    public void test_successful_email_withAttachment() throws MessagingException, IOException {
        EmailProducerConfiguration emailNotifierConfiguration = getConfiguration(true);

        EmailProducer emailProducer = new EmailProducer();
        ((Configured) emailProducer).setConfiguration(emailNotifierConfiguration);

        emailProducer.invoke(getEmailPayload(true));
        List<WiserMessage> messages = wiser.getMessages();

        Assert.assertTrue("Should be three messages - one per addressee", messages.size() == 3);
        for (WiserMessage message : wiser.getMessages()) {
            Assert.assertTrue("sender should be " + sender, sender.equals(message.getEnvelopeSender()));

            MimeMessage mimeMessage = message.getMimeMessage();
            MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
            Assert.assertTrue("should be 2 bodypart", mimeMultipart.getCount() == 2);
            BodyPart bodyPart = mimeMultipart.getBodyPart(0);
            String content = (String) bodyPart.getContent();
            Assert.assertTrue(content.contains("This is the email body"));
            BodyPart attachment = mimeMultipart.getBodyPart(1);
            Assert.assertEquals("Check attachment file name", "testAttachment", attachment.getFileName());
            Assert.assertTrue("Check file content", IOUtils.toString(attachment.getDataHandler().getDataSource().getInputStream()).contains("1997,Ford,E350"));

        }
    }

    private EmailPayload getEmailPayload(boolean addAttachment) throws IOException {

        DefaultEmailPayload payload = new DefaultEmailPayload();
        payload.setEmailBody("This is the email body");
        payload.setEmailFormat("text/plain");

        if (addAttachment) {
            InputStream resourceAsStream = getClass().getResourceAsStream("/testAttachment.csv");
            payload.addAttachment("testAttachment", "text/csv", IOUtils.toByteArray(resourceAsStream));
        }
        return payload;

    }

    private EmailProducerConfiguration getConfiguration(boolean hasAttachment) {

        EmailProducerConfiguration configuration = new EmailProducerConfiguration();

        configuration.setSubject("Subject Line");
        configuration.setMailHost("localhost");
        configuration.setMailSmtpPort(2500);

        configuration.setHasAttachment(hasAttachment);

        List<String> toRecipient = new ArrayList<String>();
        toRecipient.add(toReceiver);
        configuration.setToRecipients(toRecipient);

        List<String> ccRecipient = new ArrayList<String>();
        ccRecipient.add(ccReceiver);
        configuration.setCcRecipients(Lists.newArrayList(ccReceiver));
        configuration.setBccRecipients(Lists.newArrayList(bccReceiver));
        configuration.setHasAttachment(hasAttachment);

        Map<String, String> props = new HashMap<String, String>();
        configuration.setExtendedMailSessionProperties(props);

        configuration.setMailFrom(sender);


        return configuration;
    }


}
