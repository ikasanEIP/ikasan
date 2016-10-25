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
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.management.ManagedResource;
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
 * Test class for JmsMessageConverter.
 *
 * @author Ikasan Development Team
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
        EmailProducerConfiguration emailProducerConfiguration = getConfiguration(false, null);

        EmailProducer emailProducer = new EmailProducer();
        ((Configured) emailProducer).setConfiguration(emailProducerConfiguration);
        ((ManagedResource)emailProducer).startManagedResource();

        emailProducer.invoke(getEmailPayload(false,null));
        List<WiserMessage> messages = wiser.getMessages();

        Assert.assertTrue("Should be three messages - one per addressee", messages.size() == 3);
        for (WiserMessage message : wiser.getMessages()) {
            Assert.assertTrue("sender should be " + sender, sender.equals(message.getEnvelopeSender()));

            MimeMessage mimeMessage = message.getMimeMessage();
            MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
            Assert.assertTrue("should be only 1 bodypart", mimeMultipart.getCount() == 1);
            BodyPart bodyPart = mimeMultipart.getBodyPart(0);
            String content = (String) bodyPart.getContent();
            Assert.assertTrue("The email content should be empty", content.isEmpty());
            Assert.assertTrue("Should fild email format as \"text/plain\"", bodyPart.getContentType().contains("text/plain"));
        }
    }

    @Test
    public void test_successful_email_contentFromConfig() throws MessagingException, IOException {
        EmailProducerConfiguration emailProducerConfiguration = getConfiguration(false, "This content is from config");

        EmailProducer emailProducer = new EmailProducer();
        ((Configured) emailProducer).setConfiguration(emailProducerConfiguration);
        ((ManagedResource)emailProducer).startManagedResource();

        emailProducer.invoke(getEmailPayload(false,null));
        List<WiserMessage> messages = wiser.getMessages();

        Assert.assertTrue("Should be three messages - one per addressee", messages.size() == 3);
        for (WiserMessage message : wiser.getMessages()) {
            Assert.assertTrue("sender should be " + sender, sender.equals(message.getEnvelopeSender()));

            MimeMessage mimeMessage = message.getMimeMessage();
            MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
            Assert.assertTrue("should be only 1 bodypart", mimeMultipart.getCount() == 1);
            BodyPart bodyPart = mimeMultipart.getBodyPart(0);
            String content = (String) bodyPart.getContent();
            Assert.assertEquals("The email content should be from config", "This content is from config", content);
            Assert.assertTrue("Should find email format as \"text/plain\"", bodyPart.getContentType().contains("text/plain"));
        }
    }

    @Test
    public void test_successful_email_contentFromPayload() throws MessagingException, IOException {
        EmailProducerConfiguration emailProducerConfiguration = getConfiguration(false, "This content is from config");

        EmailProducer emailProducer = new EmailProducer();
        ((Configured) emailProducer).setConfiguration(emailProducerConfiguration);
        ((ManagedResource)emailProducer).startManagedResource();

        emailProducer.invoke(getEmailPayload(false,"The content is from payload"));
        List<WiserMessage> messages = wiser.getMessages();

        Assert.assertTrue("Should be three messages - one per addressee", messages.size() == 3);
        for (WiserMessage message : wiser.getMessages()) {
            Assert.assertTrue("sender should be " + sender, sender.equals(message.getEnvelopeSender()));

            MimeMessage mimeMessage = message.getMimeMessage();
            MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
            Assert.assertTrue("should be only 1 bodypart", mimeMultipart.getCount() == 1);
            BodyPart bodyPart = mimeMultipart.getBodyPart(0);
            String content = (String) bodyPart.getContent();
            Assert.assertEquals("The email content should be from payload", "The content is from payload", content );
            Assert.assertTrue("Should find email format as \"text/plain\"", bodyPart.getContentType().contains("text/plain"));
        }
    }


    @Test
    public void test_successful_email_withAttachment() throws MessagingException, IOException {

        EmailProducerConfiguration emailProducerConfiguration = getConfiguration(true, null);

        EmailProducer emailProducer = new EmailProducer();
        ((Configured) emailProducer).setConfiguration(emailProducerConfiguration);
        ((ManagedResource)emailProducer).startManagedResource();

        emailProducer.invoke(getEmailPayload(true, null));
        List<WiserMessage> messages = wiser.getMessages();

        Assert.assertTrue("Should be three messages - one per addressee", messages.size() == 3);
        for (WiserMessage message : wiser.getMessages()) {
            Assert.assertTrue("sender should be " + sender, sender.equals(message.getEnvelopeSender()));

            MimeMessage mimeMessage = message.getMimeMessage();
            MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
            Assert.assertTrue("should be 2 bodypart", mimeMultipart.getCount() == 2);
            BodyPart bodyPart = mimeMultipart.getBodyPart(0);
            String content = (String) bodyPart.getContent();
            Assert.assertTrue("The email content should be empty", content.isEmpty());  BodyPart attachment = mimeMultipart.getBodyPart(1);
            Assert.assertEquals("Check attachment file name", "testAttachment", attachment.getFileName());
            Assert.assertTrue("Check file content", IOUtils.toString(attachment.getDataHandler().getDataSource().getInputStream()).contains("1997,Ford,E350"));
            Assert.assertTrue("Should find email format as \"text/plain\"", bodyPart.getContentType().contains("text/plain"));

        }
    }

    @Test
    public void testMailServerFailure()throws IOException{

        wiser.stop();
        EmailProducerConfiguration emailProducerConfiguration = getConfiguration(true, null);

        EmailProducer emailProducer = new EmailProducer();
        ((Configured) emailProducer).setConfiguration(emailProducerConfiguration);
        ((ManagedResource)emailProducer).startManagedResource();

        try {
            emailProducer.invoke(getEmailPayload(false, null));
            Assert.assertTrue("Expecting mail server connection issue", false);
        }catch(EndpointException e){
            Assert.assertTrue("Expecting mail server connection issue", e.getMessage().contains("Could not connect to SMTP host"));
        }
    }

    private EmailPayload getEmailPayload(boolean addAttachment, String emailBody) throws IOException {

        DefaultEmailPayload payload = new DefaultEmailPayload();
        payload.setEmailBody(emailBody);

        if (addAttachment) {
            InputStream resourceAsStream = getClass().getResourceAsStream("/testAttachment.csv");
            payload.addAttachment("testAttachment", "text/csv", IOUtils.toByteArray(resourceAsStream));
        }
        System.out.println(payload.toString());
        return payload;

    }

    private EmailProducerConfiguration getConfiguration(boolean hasAttachment, String emailBody) {

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
        configuration.setEmailFormat("text/plain");
        configuration.setEmailBody(emailBody);


        return configuration;
    }


}
