package org.ikasan.component.factory.spring.endpoint;

public class JmsConsumerComponentFactoryConfiguration {

    private String destination;

    private boolean autoContentConversion;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isAutoContentConversion() {
        return autoContentConversion;
    }

    public void setAutoContentConversion(boolean autoContentConversion) {
        this.autoContentConversion = autoContentConversion;
    }

}