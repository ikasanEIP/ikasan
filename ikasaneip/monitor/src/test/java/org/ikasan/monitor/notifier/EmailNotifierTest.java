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
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.monitor.Notifier;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.IOException;
import java.util.*;

/**
 * This test class supports the <code>EmailNotifier</code> class.
 * 
 * @author Ikasan Development Team
 */
public class EmailNotifierTest
{
    String sender = "ikasanUnitTest@ikasan.org";
    String ccReceiver = "ccRecipient1@ikasan.org";
    String toReceiver = "toRecipient1@ikasan.org";
    String bccReceiver = "bccRecipient1@ikasan.org";
    
    /** Logger for this class */
    private Logger logger = Logger.getLogger(EmailNotifierTest.class);
    
    /** in memory SMTP server */
    Wiser wiser;

    @Before
    public void setup()
    {
        wiser = new Wiser();
        for(int count = 0; count < 5; count++)
        	try {
                wiser.setPort(2500);
                logger.info(String.format("Attempting to start Wiser SMTP Server on port 2500"));
        		wiser.start();
        		break;
        	} catch (RuntimeException re){
        		logger.info("Failed to start Wiser SMTP server, sleeping for a couple of seconds", re);
        		try {
					Thread.sleep(2000l);
				} catch (InterruptedException e) {
				}
        	}
    }

    @After
    public void teardown()
    {
        wiser.stop();
    }
    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_when_not_active()
    {
        EmailNotifierConfiguration emailNotifierConfiguration = new EmailNotifierConfiguration();
        emailNotifierConfiguration.setActive(false);

        Notifier<String> notifier = new EmailNotifier();
        ((Configured)notifier).setConfiguration(emailNotifierConfiguration);

        notifier.invoke("env", "moduleName", "flowName", "stopped");

        List<WiserMessage> messages = wiser.getMessages();
        Assert.assertTrue("no messages should have been published", messages.size() == 0);
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_when_active() throws MessagingException, IOException {
        EmailNotifierConfiguration emailNotifierConfiguration = getConfiguration();

        Notifier<String> notifier = new EmailNotifier();
        ((Configured)notifier).setConfiguration(emailNotifierConfiguration);

        notifier.invoke("env", "moduleName", "flowName", "stopped");
        List<WiserMessage> messages = wiser.getMessages();

        Assert.assertTrue("Should be three messages - one per addressee", messages.size() == 3);
        for(WiserMessage message:wiser.getMessages())
        {
            Assert.assertTrue("sender should be " + sender, sender.equals(message.getEnvelopeSender()));

            MimeMessage mimeMessage = message.getMimeMessage();
            MimeMultipart mimeMultipart = (MimeMultipart)mimeMessage.getContent();
            Assert.assertTrue("should be only 1 bodypart", mimeMultipart.getCount() == 1);
            BodyPart bodyPart = mimeMultipart.getBodyPart(0);
            String content = (String)bodyPart.getContent();
            Assert.assertTrue(content.contains("Module[moduleName] Flow[flowName] is stopped"));
        }
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_no_toRecipients() throws MessagingException, IOException {
        EmailNotifierConfiguration emailNotifierConfiguration = getConfiguration();
        emailNotifierConfiguration.setToRecipients(new ArrayList<String>());

        Notifier<String> notifier = new EmailNotifier();
        ((Configured)notifier).setConfiguration(emailNotifierConfiguration);

        notifier.invoke("env", "moduleName", "flowName", "stopped");
        List<WiserMessage> messages = wiser.getMessages();

        Assert.assertTrue("Should be three messages - one per addressee", messages.size() == 2);
        for(WiserMessage message:wiser.getMessages())
        {
            Assert.assertTrue("sender should be " + sender, sender.equals(message.getEnvelopeSender()));

            MimeMessage mimeMessage = message.getMimeMessage();
            MimeMultipart mimeMultipart = (MimeMultipart)mimeMessage.getContent();
            Assert.assertTrue("should be only 1 bodypart", mimeMultipart.getCount() == 1);
            BodyPart bodyPart = mimeMultipart.getBodyPart(0);
            String content = (String)bodyPart.getContent();
            Assert.assertTrue(content.contains("Module[moduleName] Flow[flowName] is stopped"));
        }
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_multi_toRecipients_in_one_line() throws MessagingException, IOException {
        EmailNotifierConfiguration emailNotifierConfiguration = getConfiguration();
        emailNotifierConfiguration.getToRecipients().add("first@email.com,second@email.com third@email.com;forth@email.com, fifth@email.com");
        emailNotifierConfiguration.getBccRecipients().add("sixth@email.com , seventh@email.com; ;, ; ");
        emailNotifierConfiguration.getCcRecipients().add(" eigth@email.com ");

        Notifier<String> notifier = new EmailNotifier();
        ((Configured)notifier).setConfiguration(emailNotifierConfiguration);

        notifier.invoke("env", "moduleName", "flowName", "stopped");
        List<WiserMessage> messages = wiser.getMessages();

        Assert.assertTrue("Should be eleven messages - one per addressee", messages.size() == 11);
        for(WiserMessage message:wiser.getMessages())
        {
            Assert.assertTrue("sender should be " + sender, sender.equals(message.getEnvelopeSender()));

            MimeMessage mimeMessage = message.getMimeMessage();
            MimeMultipart mimeMultipart = (MimeMultipart)mimeMessage.getContent();
            Assert.assertTrue("should be only 1 bodypart", mimeMultipart.getCount() == 1);
            BodyPart bodyPart = mimeMultipart.getBodyPart(0);
            String content = (String)bodyPart.getContent();
            Assert.assertTrue(content.contains("Module[moduleName] Flow[flowName] is stopped"));
        }
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_null_toRecipients() throws MessagingException, IOException {
        EmailNotifierConfiguration emailNotifierConfiguration = getConfiguration();
        emailNotifierConfiguration.setToRecipients(null);

        Notifier<String> notifier = new EmailNotifier();
        ((Configured)notifier).setConfiguration(emailNotifierConfiguration);

        notifier.invoke("env", "moduleName", "flowName", "stopped");
        List<WiserMessage> messages = wiser.getMessages();

        Assert.assertTrue("Should be three messages - one per addressee", messages.size() == 2);
        for(WiserMessage message:wiser.getMessages())
        {
            Assert.assertTrue("sender should be " + sender, sender.equals(message.getEnvelopeSender()));

            MimeMessage mimeMessage = message.getMimeMessage();
            MimeMultipart mimeMultipart = (MimeMultipart)mimeMessage.getContent();
            Assert.assertTrue("should be only 1 bodypart", mimeMultipart.getCount() == 1);
            BodyPart bodyPart = mimeMultipart.getBodyPart(0);
            String content = (String)bodyPart.getContent();
            Assert.assertTrue(content.contains("Module[moduleName] Flow[flowName] is stopped"));
        }
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_no_ccRecipients()
    {
        EmailNotifierConfiguration emailNotifierConfiguration = getConfiguration();
        emailNotifierConfiguration.setCcRecipients(new ArrayList<String>());

        Notifier<String> notifier = new EmailNotifier();
        ((Configured)notifier).setConfiguration(emailNotifierConfiguration);

        notifier.invoke("env", "moduleName", "flowName", "stopped");
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_null_ccRecipients()
    {
        EmailNotifierConfiguration emailNotifierConfiguration = getConfiguration();
        emailNotifierConfiguration.setCcRecipients(null);

        Notifier<String> notifier = new EmailNotifier();
        ((Configured)notifier).setConfiguration(emailNotifierConfiguration);

        notifier.invoke("env", "moduleName", "flowName", "stopped");
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_no_bccRecipients()
    {
        EmailNotifierConfiguration emailNotifierConfiguration = getConfiguration();
        emailNotifierConfiguration.setBccRecipients(new ArrayList<String>());

        Notifier<String> notifier = new EmailNotifier();
        ((Configured)notifier).setConfiguration(emailNotifierConfiguration);

        notifier.invoke("env", "moduleName", "flowName", "stopped");
    }

    /**
     * Test successful invoke.
     */
    @Test
    public void test_successful_notifier_null_bccRecipients()
    {
        EmailNotifierConfiguration emailNotifierConfiguration = getConfiguration();
        emailNotifierConfiguration.setBccRecipients(null);

        Notifier<String> notifier = new EmailNotifier();
        ((Configured)notifier).setConfiguration(emailNotifierConfiguration);

        notifier.invoke("env", "moduleName", "flowName", "stopped");
    }

    /**
     * Return a populated configuration instance.
     * @return
     */
    protected EmailNotifierConfiguration getConfiguration()
    {
        EmailNotifierConfiguration configuration = new EmailNotifierConfiguration();
        configuration.setSubject("Subject Line");
        configuration.setMailHost("localhost");
        configuration.setMailSmtpPort(2500);

        configuration.setActive(true);

        List<String> toRecipient = new ArrayList<String>();
        toRecipient.add(toReceiver);
        configuration.setToRecipients(toRecipient);

        List<String> ccRecipient = new ArrayList<String>();
        ccRecipient.add(ccReceiver);
        configuration.setCcRecipients(ccRecipient);

        List<String> bccRecipient = new ArrayList<String>();
        bccRecipient.add(bccReceiver);
        configuration.setBccRecipients(bccRecipient);

        Map<String,String> props = new HashMap<String,String>();
        configuration.setExtendedMailSessionProperties(props);
        configuration.setNotificationIntervalInSeconds(1L);

        configuration.setMailFrom(sender);

        return configuration;
    }
}
