package org.ikasan.component.endpoint.bigqueue.producer;

import java.io.IOException;

public interface BigQueueConnectionCallback {

    /**
     * The execute method on the callback.
     *
     * @throws IOException
     */
    public void execute() throws IOException;
}
