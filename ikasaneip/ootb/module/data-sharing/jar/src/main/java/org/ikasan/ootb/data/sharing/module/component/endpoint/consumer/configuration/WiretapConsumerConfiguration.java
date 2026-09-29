package org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration;

import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;

import java.util.List;

public class WiretapConsumerConfiguration extends ScheduledConsumerConfiguration {
    private List<String> moduleNames;
    private long queryTimestamp;
    private long queryWindowMilliseconds = 100000;
    private int batchSize = 100;
    private int offset = 0;

    public List<String> getModuleNames() {
        return moduleNames;
    }

    public void setModuleNames(List<String> moduleNames) {
        this.moduleNames = moduleNames;
    }

    public long getQueryTimestamp() {
        return queryTimestamp;
    }

    public void setQueryTimestamp(long queryTimestamp) {
        this.queryTimestamp = queryTimestamp;
    }

    public long getQueryWindowMilliseconds() {
        return queryWindowMilliseconds;
    }

    public void setQueryWindowMilliseconds(long queryWindowMilliseconds) {
        this.queryWindowMilliseconds = queryWindowMilliseconds;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
