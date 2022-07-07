package org.ikasan.spec.bigqueue.message;

import java.util.Map;

public interface BigQueueMessage<T> {

    void setMessageId(String messageId);

    String getMessageId();

    void setCreatedTime(long createdTime);

    long getCreatedTime();

    void setMessage(T message);

    T getMessage();

    void setMessageProperties(Map<String, String> messageProperties);

    Map<String, String> getMessageProperties();
}