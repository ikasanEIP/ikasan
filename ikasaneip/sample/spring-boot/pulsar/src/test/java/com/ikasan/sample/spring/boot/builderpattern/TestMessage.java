package com.ikasan.sample.spring.boot.builderpattern;

import java.util.Objects;

/**
 * Test message class for schema testing in Pulsar sample flows
 */
public class TestMessage {
    private String id;
    private String content;
    private long timestamp;

    public TestMessage() {
    }

    public TestMessage(String id, String content, long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestMessage that = (TestMessage) o;
        return timestamp == that.timestamp &&
               Objects.equals(id, that.id) &&
               Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, timestamp);
    }

    @Override
    public String toString() {
        return "TestMessage{" +
               "id='" + id + '\'' +
               ", content='" + content + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}
