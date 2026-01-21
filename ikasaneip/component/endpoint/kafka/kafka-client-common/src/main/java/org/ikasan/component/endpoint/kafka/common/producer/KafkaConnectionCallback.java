package org.ikasan.component.endpoint.kafka.producer;

import java.io.IOException;

public interface KafkaConnectionCallback {

    /**
     * The execute method on the callback.
     *
     * @throws IOException
     */
    public void execute() throws Throwable;
}
