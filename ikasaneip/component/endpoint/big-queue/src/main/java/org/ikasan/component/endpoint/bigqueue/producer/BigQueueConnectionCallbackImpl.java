package org.ikasan.component.endpoint.bigqueue.producer;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.serialiser.Serialiser;

import java.io.IOException;

public class BigQueueConnectionCallbackImpl<IDENTIFIER, PAYLOAD> implements BigQueueConnectionCallback {
    private FlowEvent<IDENTIFIER, PAYLOAD> payload;
    private IBigQueue iBigQueue;
    Serialiser<PAYLOAD, byte[]> serialiser;

    /**
     * Constructor
     *
     * @param payload the payload that is published to the big queue by the callback.
     * @param iBigQueue the big queue that is published to.
     * @param serialiser the serialiser that is use to serialise the message that is published to the big queue.
     */
    public BigQueueConnectionCallbackImpl(FlowEvent<IDENTIFIER, PAYLOAD> payload, IBigQueue iBigQueue, Serialiser<PAYLOAD, byte[]> serialiser) {
        this.payload = payload;
        this.iBigQueue = iBigQueue;
        this.serialiser = serialiser;
    }

    @Override
    public void execute() throws IOException {
        this.iBigQueue.enqueue(this.serialiser.serialise(payload.getPayload()));
    }
}
