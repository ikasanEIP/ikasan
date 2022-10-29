package org.ikasan.component.endpoint.bigqueue.consumer.configuration;

public class BigQueueConsumerConfiguration {

    private boolean putErrorsToBackOfQueue = false;

    public boolean isPutErrorsToBackOfQueue() {
        return putErrorsToBackOfQueue;
    }

    public void setPutErrorsToBackOfQueue(boolean putErrorsToBackOfQueue) {
        this.putErrorsToBackOfQueue = putErrorsToBackOfQueue;
    }
}
