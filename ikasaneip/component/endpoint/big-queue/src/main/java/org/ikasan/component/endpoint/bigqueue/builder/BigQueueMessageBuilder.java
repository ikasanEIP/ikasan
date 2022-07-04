package org.ikasan.component.endpoint.bigqueue.builder;

import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.spec.bigqueue.BigQueueMessage;

import java.util.Map;
import java.util.UUID;

public class BigQueueMessageBuilder<T> {

    private String messageId;
    private long createdTime = -1;
    private T message;
    private Map<String, String> messageProperties;

    public BigQueueMessageBuilder<T> withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public BigQueueMessageBuilder<T> withCreatedTime(long createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public BigQueueMessageBuilder<T> withMessage(T message) {
        this.message = message;
        return this;
    }

    public BigQueueMessageBuilder<T> withMessageProperties(Map<String, String> messageProperties) {
        this.messageProperties = messageProperties;
        return this;
    }

    public BigQueueMessage<T> build() {
        BigQueueMessage<T> bigQueueMessage = new BigQueueMessageImpl<>();
        bigQueueMessage.setMessageId(messageId == null ? UUID.randomUUID().toString() : messageId);
        bigQueueMessage.setCreatedTime(createdTime == -1 ? System.currentTimeMillis() : createdTime);
        bigQueueMessage.setMessage(message);
        bigQueueMessage.setMessageProperties(messageProperties);

        return bigQueueMessage;
    }
}
