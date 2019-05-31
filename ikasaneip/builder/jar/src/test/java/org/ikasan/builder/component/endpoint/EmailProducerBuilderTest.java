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
package org.ikasan.builder.component.endpoint;

import org.ikasan.component.endpoint.email.producer.EmailProducer;
import org.ikasan.component.endpoint.email.producer.EmailProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This test class supports the <code>EmailProducerBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class EmailProducerBuilderTest
{

    @Test
    public void test_emailproducerbuilder_build_invalid_properties()
    {
        new EmailProducerBuilderImpl( new EmailProducer() ).build();
    }

    @Test
    public void test_emailproducerbuilder_getInstance()
    {
        EmailProducerBuilder emailProducerBuilder = EmailProducerBuilder.getInstance();
        emailProducerBuilder.build();
    }

    @Test
    public void test_emailproducerbuilder_build_success() {

        EmailProducerBuilder emailProducerBuilder = EmailProducerBuilder.getInstance();

        List<String> recipients = new ArrayList<String>();
        recipients.add("ikasan1");
        recipients.add("ikasan2");

        Producer emailProducer = emailProducerBuilder
                .setConfiguration(new EmailProducerConfiguration())
                .setConfiguredResourceId("configuredResourceId")
                .setCriticalOnStartup(true)
                .setToRecipient("Ikasan")
                .setToRecipients(recipients)
                .setCcRecipient("Ikasan")
                .setCcRecipients(recipients)
                .setBccRecipient("Ikasan")
                .setBccRecipients(recipients)
                .setEmailBody("My email body")
                .setMailSubject("My email Subject")
                .setEmailFormat("mailFormat")
                .setFrom("Me")
                .setTransportProtocol("transportProtocol")
                .setHasAttachment(false)
                .setMailDebug(true)
                .setMailhost("mailhost")
                .setMailPopClass("popClass")
                .setMailPopHost("popHost")
                .setMailPopPort(23)
                .setMailPopUser("popUsername")
                .setExtendedMailSessionProperties(null)
                .setMailMimeAddressStrict(true)
                .setMailhost("mailhost")
                .setMailSubject("subject")
                .setMailSmtpClass("smtpClass")
                .setMailSmtpHost("smtpHost")
                .setMailSmtpPort(23)
                .setMailSmtpUser("smtpUsername")
                .setMailStoreProtocol("protocol")
                .setRuntimeEnvironment("runtimeEnv")
                .build();

        assertTrue("instance should be a emailProducer", emailProducer instanceof EmailProducer);

        assertTrue("configuredResourceId should be 'configuredResourceId'",
                "configuredResourceId".equals(((ConfiguredResource)emailProducer).getConfiguredResourceId()));

        EmailProducerConfiguration configuration = (
                (ConfiguredResource<EmailProducerConfiguration>) emailProducer).getConfiguration();

        assertEquals("recipient should be ikasan1", "ikasan1", configuration.getToRecipients().get(0));
        assertEquals("recipients should be ikasan1 and ikasan2", recipients, configuration.getToRecipients());
        assertEquals("CC recipient should be ikasan1", "ikasan1", configuration.getCcRecipients().get(0));
        assertEquals("CC recipients should be ikasan1 and ikasan2", recipients, configuration.getCcRecipients());
        assertEquals("BCC recipient should be ikasan1", "ikasan1", configuration.getBccRecipients().get(0));
        assertEquals("BCC recipients should be ikasan1 and ikasan2", recipients, configuration.getBccRecipients());
        assertEquals("email body should be 'My email body'", "My email body", configuration.getEmailBody());
        assertEquals("email subject should be 'subject'", "subject", configuration.getSubject());
        assertEquals("email format should be 'mailFormat'", "mailFormat", configuration.getEmailFormat());
        assertEquals("email from should be 'Me'", "Me", configuration.getMailFrom());
        assertEquals("email hasAttachment should be 'false'", Boolean.FALSE, configuration.isHasAttachment());
        assertEquals("email debug should be 'true'", Boolean.TRUE, configuration.getMailDebug());
        assertEquals("email host should be 'mailhost'", "mailhost", configuration.getMailHost());
        assertEquals("mimeAddressRestricted should be 'true'", Boolean.TRUE, configuration.getMailMimeAddressStrict());
        assertEquals("popClass should be 'popClass'", "popClass", configuration.getMailPopClass());
        assertEquals("popHost should be 'popHost'", "popHost", configuration.getMailPopHost());
        assertEquals("popPort should be '23'", 23, configuration.getMailPopPort());
        assertEquals("popUser should be 'popUser'", "popUsername", configuration.getMailPopUser());
        assertNull("ExtendedMailSessionProperties should be 'null'", configuration.getExtendedMailSessionProperties());
        assertEquals("Mailhost should be 'host'", "mailhost", configuration.getMailHost());
        assertEquals("subject should be 'subject'", "subject", configuration.getSubject());
        assertEquals("smtpClass should be 'smtpClass'", "smtpClass", configuration.getMailSmtpClass());
        assertEquals("smtpHost should be 'smtpHost'", "smtpHost", configuration.getMailSmtpHost());
        assertEquals("smtpPort should be '23'", 23, configuration.getMailSmtpPort());
        assertEquals("smtpUser should be 'smtpUsername'", "smtpUsername", configuration.getMailSmtpUser());
        assertEquals("protocol should be 'protocol'", "protocol", configuration.getMailStoreProtocol());
        assertEquals("runtimeEnv should be 'runtimeEnv'", "runtimeEnv", configuration.getRuntimeEnvironment());
        assertEquals("transportProtocol should be 'transportProtocol'", "transportProtocol", configuration.getMailTransportProtocol());
    }


}
