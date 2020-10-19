package org.ikasan.component.endpoint.kafka.consumer;

public interface MessageProcessor<V> {
    public void process(V message);
}
