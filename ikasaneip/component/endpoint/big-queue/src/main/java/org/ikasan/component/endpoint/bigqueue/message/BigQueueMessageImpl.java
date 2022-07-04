package org.ikasan.component.endpoint.bigqueue.message;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.ikasan.spec.bigqueue.BigQueueMessage;

import java.util.Map;

public class BigQueueMessageImpl<T> implements BigQueueMessage<T> {

    private String messageId;
    private long createdTime;
    private T message;
    private Map<String, String> messageProperties;

    @Override
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String getMessageId() {
        return this.messageId;
    }

    @Override
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public long getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setMessage(T message) {
        this.message = message;
    }

    @Override
    public T getMessage() {
        return this.message;
    }

    @Override
    public void setMessageProperties(Map<String, String> messageProperties) {
        this.messageProperties = messageProperties;
    }

    @Override
    public Map<String, String> getMessageProperties() {
        return this.messageProperties;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
