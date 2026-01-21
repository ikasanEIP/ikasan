package org.ikasan.component.endpoint.kafka.client.reactive.consumer;

public interface MessageProcessor<V> {
    public void process(V message);
}
