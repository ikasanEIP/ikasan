package org.ikasan.component.endpoint.bigqueue.consumer;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundQueueMessageRunner implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(InboundQueueMessageRunner.class);
    private IBigQueue iBigQueue;
    private MessageListener messageListener;
    private Serialiser serialiser;
    private EndpointListener endpointListener;

    /**
     *
     * @param iBigQueue
     * @param serialiser
     */
    public InboundQueueMessageRunner(IBigQueue iBigQueue, Serialiser serialiser) {
        this.iBigQueue = iBigQueue;
        this.serialiser = serialiser;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setEndpointListener(EndpointListener endpointListener) {
        this.endpointListener = endpointListener;
    }

    @Override
    public void run() {
        try {
            byte[] event = iBigQueue.peek();
            if(event == null) {
                return;
            }
            Object payload = this.serialiser.deserialise(event);
            logger.debug("Attempting to process inbound message message " + payload);
            this.messageListener.onMessage(payload);
        }
        catch (Exception e) {
            logger.debug("An exception has occurred in the inbound message runner!", e);
            if(this.endpointListener != null) {
                this.endpointListener.onException(e);
            }
        }
    }
}
