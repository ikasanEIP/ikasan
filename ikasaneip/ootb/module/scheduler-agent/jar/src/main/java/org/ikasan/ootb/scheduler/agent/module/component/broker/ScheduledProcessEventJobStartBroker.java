package org.ikasan.ootb.scheduler.agent.module.component.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansoft.bigqueue.IBigQueue;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;

import java.io.IOException;

/**
 * This class is responsible for notify the dashboard that a scheduler job is starting. It does this by publishing
 * an event to the outbound queue which is subsequently published to the dashboard via the outbound flow.
 *
 * @author Ikasan Development Team
 */
public class ScheduledProcessEventJobStartBroker implements Broker<ScheduledProcessEvent, ScheduledProcessEvent> {
    private IBigQueue outboundQueue;
    private ObjectMapper objectMapper;

    public ScheduledProcessEventJobStartBroker(IBigQueue outboundQueue) {
        this.outboundQueue = outboundQueue;
        if(this.outboundQueue == null) {
            throw new IllegalArgumentException("outboundQueue cannot be null!");
        }

        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ScheduledProcessEvent invoke(ScheduledProcessEvent scheduledProcessEvent) throws EndpointException {
        try {
             this.outboundQueue.enqueue(this.objectMapper.writeValueAsBytes(scheduledProcessEvent));
             return scheduledProcessEvent;
        }
        catch (IOException e) {
            throw new EndpointException(e);
        }
    }
}
