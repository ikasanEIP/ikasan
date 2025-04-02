package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.spec.event.ManagedEventIdentifierException;
import org.ikasan.spec.event.ManagedEventIdentifierService;

public class SampleIdentifierService implements ManagedEventIdentifierService<String, String> {
    @Override
    public void setEventIdentifier(String s, String s2) throws ManagedEventIdentifierException {

    }

    @Override
    public String getEventIdentifier(String s) throws ManagedEventIdentifierException {
        return s;
    }
}
