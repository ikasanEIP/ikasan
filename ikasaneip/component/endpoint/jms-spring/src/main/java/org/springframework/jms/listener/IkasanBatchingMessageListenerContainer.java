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
package org.springframework.jms.listener;

import org.ikasan.component.endpoint.jms.consumer.IkasanListMessage;
import org.ikasan.component.endpoint.jms.consumer.MessageProvider;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.spec.configuration.Configured;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

/**
 * Extend DefaultMessageListenerContainer to ensure standard defaults are set on the container being instantiated.
 *
 * @author Ikasan Development Team
 */
public class IkasanBatchingMessageListenerContainer extends IkasanMessageListenerContainer implements MessageProvider, Configured<SpringMessageConsumerConfiguration>
{
    /**
     * Build up a faux JMS message containing multiple instances of JMS messages consumed from the destination.
     * @param consumer
     * @return
     * @throws JMSException
     */
    @Override
    protected Message receiveMessage(MessageConsumer consumer) throws JMSException
    {
        IkasanListMessage listMessage = new IkasanListMessage();
        while ( !append(listMessage, super.receiveMessage(consumer)) );
        return listMessage.size() == 0 ? null : listMessage;
    }

    /**
     * Consumer messages until no more or batch limit is reached.
     * @param listMessage
     * @param message
     * @return
     */
    public boolean append(IkasanListMessage listMessage, Message message)
    {
        if (message != null)
        {
            listMessage.add(message);
        }

        return message == null || listMessage.size() >= (configuration.getBatchSize() == null ? 1:configuration.getBatchSize().intValue());
    }
}