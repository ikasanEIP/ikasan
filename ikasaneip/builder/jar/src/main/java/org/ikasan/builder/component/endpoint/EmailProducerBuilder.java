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

import org.ikasan.builder.component.Builder;
import org.ikasan.component.endpoint.email.producer.EmailProducer;
import org.ikasan.component.endpoint.email.producer.EmailProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;

import java.util.List;
import java.util.Map;

/**
 * Contract for a default email producer.
 *
 * @author Ikasan Development Team.
 */
public interface EmailProducerBuilder extends Builder<Producer>
{
    static EmailProducerBuilder getInstance()
    {
        return new EmailProducerBuilderImpl( new EmailProducer() );
    }

    EmailProducerBuilder setCriticalOnStartup(boolean criticalOnStartup);

    EmailProducerBuilder setConfiguredResourceId(String configuredResourceId);

    EmailProducerBuilder setConfiguration(EmailProducerConfiguration emailProducerConfiguration);

    EmailProducerBuilder setToRecipient(String recipient);

    EmailProducerBuilder setToRecipients(List<String> recipients);

    EmailProducerBuilder setCcRecipient(String recipient);

    EmailProducerBuilder setCcRecipients(List<String> recipients);

    EmailProducerBuilder setBccRecipient(String recipient);

    EmailProducerBuilder setBccRecipients(List<String> recipients);

    EmailProducerBuilder setEmailBody(String emailBody);

    EmailProducerBuilder setEmailFormat(String emailFormat);

    EmailProducerBuilder setRuntimeEnvironment(String runtimeEnvironment);

    EmailProducerBuilder setMailSubject(String subject);

    EmailProducerBuilder setMailStoreProtocol(String mailStoreProtocol);

    EmailProducerBuilder setMailSmtpPort(int mailSmtpPort);

    EmailProducerBuilder setMailSmtpUser(String mailSmtpUser);

    EmailProducerBuilder setMailSmtpHost(String mailSmtpHost);

    EmailProducerBuilder setMailSmtpClass(String mailSmtpClass);

    EmailProducerBuilder setMailPopUser(String mailPopUser);

    EmailProducerBuilder setMailPopPort(int mailPopPort);

    EmailProducerBuilder setMailPopHost(String mailPopHost);

    EmailProducerBuilder setMailPopClass(String mailPopClass);

    EmailProducerBuilder setMailMimeAddressStrict(boolean mailMimeAddressStrict);

    EmailProducerBuilder setMailhost(String mailhost);

    EmailProducerBuilder setExtendedMailSessionProperties(Map<String,String> extendedMailSessionProperties);

    EmailProducerBuilder setHasAttachment(boolean hasAttachment);

    EmailProducerBuilder setMailDebug(boolean mailDebug);

    EmailProducerBuilder setFrom(String from);

    EmailProducerBuilder setTransportProtocol(String transportProtocol);
}

