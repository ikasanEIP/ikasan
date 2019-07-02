package org.ikasan.topology.metadata.components;

import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;

public class TestBroker implements Broker
{
    @Override
    public Object invoke(Object o) throws EndpointException
    {
        return null;
    }
}
