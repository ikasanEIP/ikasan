package org.ikasan.spec.module.client;

import java.util.function.Consumer;

public interface LogStreamingService<T> {
    /**
     * Replay an event to a specific flow in a module.
     *
     * @param host              the host where the log file is e.g. http://localhost:8080
     * @param path              the controller path - normally /logs
     * @param fullFilePathToLog the path to the log file on the server including the file name e.g. logs/spring.log
     * @param dataConsumer      the consumer that will stream the events e.g. Consumer&lt;ServerSentEvent&lt;String&gt;&gt; dataConsumer
     * @param errorConsumer     the error consumer if wanting to handle errors
     */
    public void streamLogFile(String host,
                              String path,
                              String fullFilePathToLog,
                              Consumer<T> dataConsumer,
                              Consumer<Throwable> errorConsumer,
                              Runnable completeConsumer) throws InterruptedException;
}
