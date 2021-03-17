package com.ikasan.component.factory;

public class JmsConsumerComponentFactoryConfiguration {

    private String destination;

    private boolean autoConversion;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isAutoConversion() {
        return autoConversion;
    }

    public void setAutoConversion(boolean autoConversion) {
        this.autoConversion = autoConversion;
    }

}
