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
package org.ikasan.testharness.flow.producer;

import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.springframework.jms.core.IkasanJmsTemplate;

import java.util.LinkedList;
import java.util.List;


/**
 * Dummy producer to test last messages/events in unit tests.
 *
 * It can be used with or without an actual {@link IkasanJmsTemplate} setup. If you want to use it with a
 * {@link IkasanJmsTemplate} simple provide the instance of the template in constructor (directly or in Spring config)
 * and provide "configuration" and "configuredResourceId" parameters.
 *
 * Useful for Unittest by providing a Spring config that will override the original producer with this implementation,
 * so that you can check the persisted messages by calling the {@link #getEvents()} and {@link #getEventCount()} methods.
 *
 * @author Ikasan Development Team 
 */
public class InspectableProducer<T> extends JmsTemplateProducer<T> {

    final private List<T> events;
    private boolean useJms;

    /**
     *
     * @return the value of the useJms property
     */
    public boolean isUseJms() {
        return useJms;
    }

    /**
     *
     * @param useJms configuration if the messages are processed via Jms after collection
     */
    public void setUseJms(boolean useJms) {
        this.useJms = useJms;
    }

    /**
     *
     * @return the count of the currently collected events.
     */
    public int getEventCount() {
        return events.size();
    }

    /**
     * Constructs a InspectableProducer.
     * All messages that the producer processes will be collected and not further processed.
     */
    public InspectableProducer() {
        super(new IkasanJmsTemplate());
        events = new LinkedList<>();
        useJms = false;
    }

    /**
     * Constructs a InspectableProducer with an {@link IkasanJmsTemplate}.
     * All messages that the producer processes will be collected and also send via the jms template.
     * @param jmsTemplate
     */
    public InspectableProducer(IkasanJmsTemplate jmsTemplate) {
        super(jmsTemplate);
        events = new LinkedList<>();
        useJms = true;
    }

    /**
     *
     * @return the events that the producer processed
     */
    public List<T> getEvents() {
        return events;
    }

    @Override
    public void invoke(T message) throws EndpointException {
        events.add(message);
        if (useJms) {
            super.invoke(message);
        }
    }
}
