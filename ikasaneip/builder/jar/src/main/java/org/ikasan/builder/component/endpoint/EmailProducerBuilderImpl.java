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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Ikasan provided email producer builder implementation.
 *
 * @author Ikasan Development Team
 */
public class EmailProducerBuilderImpl implements EmailProducerBuilder
{
    EmailProducer emailProducer;

    /**
     * Constructor
     * @param emailProducer
     */
    public EmailProducerBuilderImpl(EmailProducer emailProducer)
    {
        this.emailProducer = emailProducer;
        if(emailProducer == null)
        {
            throw new IllegalArgumentException("emailProducer cannot be 'null");
        }
    }

    @Override
    public EmailProducerBuilder setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.emailProducer.setCriticalOnStartup(criticalOnStartup);
        return this;
    }

    @Override
    public EmailProducerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.emailProducer.setConfiguredResourceId(configuredResourceId);
        return this;
    }

    @Override
    public EmailProducerBuilder setConfiguration(EmailProducerConfiguration emailProducerConfiguration)
    {
        this.emailProducer.setConfiguration(emailProducerConfiguration);
        return this;
    }

    @Override
    public EmailProducerBuilder setToRecipient(String recipient)
    {
        return this.setToRecipients(Arrays.asList(recipient));
    }

    @Override
    public EmailProducerBuilder setToRecipients(List<String> recipients)
    {
        this.emailProducer.getConfiguration().setToRecipients(recipients);
        return this;
    }

    @Override
    public EmailProducerBuilder setCcRecipient(String recipient)
    {
        return this.setCcRecipients(Arrays.asList(recipient));
    }

    @Override
    public EmailProducerBuilder setCcRecipients(List<String> recipients)
    {
        this.emailProducer.getConfiguration().setCcRecipients(recipients);
        return this;
    }

    @Override
    public EmailProducerBuilder setBccRecipient(String recipient)
    {
        return this.setBccRecipients(Arrays.asList(recipient));
    }

    @Override
    public EmailProducerBuilder setBccRecipients(List<String> recipients)
    {
        this.emailProducer.getConfiguration().setBccRecipients(recipients);
        return this;
    }

    @Override
    public EmailProducerBuilder setEmailBody(String emailBody)
    {
        this.emailProducer.getConfiguration().setEmailBody(emailBody);
        return this;
    }

    @Override
    public EmailProducerBuilder setEmailFormat(String emailFormat)
    {
        this.emailProducer.getConfiguration().setEmailFormat(emailFormat);
        return this;
    }

    @Override
    public EmailProducerBuilder setExtendedMailSessionProperties(Map<String,String> extendedMailSessionProperties)
    {
        this.emailProducer.getConfiguration().setExtendedMailSessionProperties(extendedMailSessionProperties);
        return this;
    }

    @Override
    public EmailProducerBuilder setHasAttachment(boolean hasAttachment)
    {
        this.emailProducer.getConfiguration().setHasAttachment(hasAttachment);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailDebug(boolean mailDebug)
    {
        this.emailProducer.getConfiguration().setMailDebug(mailDebug);
        return this;
    }

    @Override
    public EmailProducerBuilder setFrom(String from)
    {
        this.emailProducer.getConfiguration().setMailFrom(from);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailhost(String mailhost)
    {
        this.emailProducer.getConfiguration().setMailHost(mailhost);
        return this;
    }

    @Override
    public EmailProducerBuilder setUser(String user)
    {
        this.emailProducer.getConfiguration().setMailUser(user);
        return this;
    }

    @Override
    public EmailProducerBuilder setPassword(String password)
    {
        this.emailProducer.getConfiguration().setPassword(password);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailMimeAddressStrict(boolean mailMimeAddressStrict)
    {
        this.emailProducer.getConfiguration().setMailMimeAddressStrict(mailMimeAddressStrict);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailPopClass(String mailPopClass)
    {
        this.emailProducer.getConfiguration().setMailPopClass(mailPopClass);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailPopHost(String mailPopHost)
    {
        this.emailProducer.getConfiguration().setMailPopHost(mailPopHost);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailPopPort(int mailPopPort)
    {
        this.emailProducer.getConfiguration().setMailPopPort(mailPopPort);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailPopUser(String mailPopUser)
    {
        this.emailProducer.getConfiguration().setMailPopUser(mailPopUser);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailSmtpClass(String mailSmtpClass)
    {
        this.emailProducer.getConfiguration().setMailSmtpClass(mailSmtpClass);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailSmtpHost(String mailSmtpHost)
    {
        this.emailProducer.getConfiguration().setMailSmtpHost(mailSmtpHost);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailSmtpUser(String mailSmtpUser)
    {
        this.emailProducer.getConfiguration().setMailSmtpUser(mailSmtpUser);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailSmtpPort(int mailSmtpPort)
    {
        this.emailProducer.getConfiguration().setMailSmtpPort(mailSmtpPort);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailStoreProtocol(String mailStoreProtocol)
    {
        this.emailProducer.getConfiguration().setMailStoreProtocol(mailStoreProtocol);
        return this;
    }

    @Override
    public EmailProducerBuilder setMailSubject(String subject)
    {
        this.emailProducer.getConfiguration().setSubject(subject);
        return this;
    }

    @Override
    public EmailProducerBuilder setRuntimeEnvironment(String runtimeEnvironment)
    {
        this.emailProducer.getConfiguration().setRuntimeEnvironment(runtimeEnvironment);
        return this;
    }


    @Override
    public EmailProducerBuilder setTransportProtocol(String transportProtocol)
    {
        this.emailProducer.getConfiguration().setMailTransportProtocol(transportProtocol);
        return this;
    }

    @Override
    public Producer build()
    {
        return this.emailProducer;
    }
}

