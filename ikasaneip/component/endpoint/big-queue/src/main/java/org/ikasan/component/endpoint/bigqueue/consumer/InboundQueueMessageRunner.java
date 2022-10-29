package org.ikasan.component.endpoint.bigqueue.consumer;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.spec.serialiser.Serialiser;

public class InboundQueueMessageRunner implements Runnable {
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

            this.messageListener.onMessage(this.serialiser.deserialise(event));
        }
        catch (Exception e) {
            if(this.endpointListener != null) {
                this.endpointListener.onException(e);
            }
        }
    }
}
