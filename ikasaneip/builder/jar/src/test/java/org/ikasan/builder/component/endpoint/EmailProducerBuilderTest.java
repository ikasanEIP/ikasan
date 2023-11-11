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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class supports the <code>EmailProducerBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
class EmailProducerBuilderTest
{

    @Test
    void test_emailproducerbuilder_build_invalid_properties()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            EmailProducerBuilder.getInstance().build();
        });
    }

    @Test
    void test_emailproducerbuilder_getInstance()
    {
        assertTrue(EmailProducerBuilder.getInstance() instanceof EmailProducerBuilder);
    }

    @Test
    void test_emailproducerbuilder_build_success() {

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

        assertTrue(emailProducer instanceof EmailProducer, "instance should be a emailProducer");

        assertEquals("configuredResourceId", ((ConfiguredResource)emailProducer).getConfiguredResourceId(), "configuredResourceId should be 'configuredResourceId'");

        EmailProducerConfiguration configuration = (
                (ConfiguredResource<EmailProducerConfiguration>) emailProducer).getConfiguration();

        assertEquals("ikasan1", configuration.getToRecipients().get(0), "recipient should be ikasan1");
        assertEquals(recipients, configuration.getToRecipients(), "recipients should be ikasan1 and ikasan2");
        assertEquals("ikasan1", configuration.getCcRecipients().get(0), "CC recipient should be ikasan1");
        assertEquals(recipients, configuration.getCcRecipients(), "CC recipients should be ikasan1 and ikasan2");
        assertEquals("ikasan1", configuration.getBccRecipients().get(0), "BCC recipient should be ikasan1");
        assertEquals(recipients, configuration.getBccRecipients(), "BCC recipients should be ikasan1 and ikasan2");
        assertEquals("My email body", configuration.getEmailBody(), "email body should be 'My email body'");
        assertEquals("subject", configuration.getSubject(), "email subject should be 'subject'");
        assertEquals("mailFormat", configuration.getEmailFormat(), "email format should be 'mailFormat'");
        assertEquals("Me", configuration.getMailFrom(), "email from should be 'Me'");
        assertEquals(Boolean.FALSE, configuration.isHasAttachment(), "email hasAttachment should be 'false'");
        assertEquals(Boolean.TRUE, configuration.getMailDebug(), "email debug should be 'true'");
        assertEquals("mailhost", configuration.getMailHost(), "email host should be 'mailhost'");
        assertEquals(Boolean.TRUE, configuration.getMailMimeAddressStrict(), "mimeAddressRestricted should be 'true'");
        assertEquals("popClass", configuration.getMailPopClass(), "popClass should be 'popClass'");
        assertEquals("popHost", configuration.getMailPopHost(), "popHost should be 'popHost'");
        assertEquals(23, configuration.getMailPopPort(), "popPort should be '23'");
        assertEquals("popUsername", configuration.getMailPopUser(), "popUser should be 'popUser'");
        assertNull(configuration.getExtendedMailSessionProperties(), "ExtendedMailSessionProperties should be 'null'");
        assertEquals("mailhost", configuration.getMailHost(), "Mailhost should be 'host'");
        assertEquals("subject", configuration.getSubject(), "subject should be 'subject'");
        assertEquals("smtpClass", configuration.getMailSmtpClass(), "smtpClass should be 'smtpClass'");
        assertEquals("smtpHost", configuration.getMailSmtpHost(), "smtpHost should be 'smtpHost'");
        assertEquals(23, configuration.getMailSmtpPort(), "smtpPort should be '23'");
        assertEquals("smtpUsername", configuration.getMailSmtpUser(), "smtpUser should be 'smtpUsername'");
        assertEquals("protocol", configuration.getMailStoreProtocol(), "protocol should be 'protocol'");
        assertEquals("runtimeEnv", configuration.getRuntimeEnvironment(), "runtimeEnv should be 'runtimeEnv'");
        assertEquals("transportProtocol", configuration.getMailTransportProtocol(), "transportProtocol should be 'transportProtocol'");
    }


}
