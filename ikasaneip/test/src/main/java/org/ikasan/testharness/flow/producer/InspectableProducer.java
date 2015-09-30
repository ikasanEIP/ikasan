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
