package org.ikasan.component.endpoint.kafka.producer;

public interface KafkaKeyProvider<KEY> {

    /**
     * Get the key used for kafka messages
     *
     * @return
     */
    public KEY getKey();
}
