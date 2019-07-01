package org.ikasan.topology.metadata.components;

import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;

public class TestProducer implements Producer<String>
{
    @Override
    public void invoke(String payload) throws EndpointException
    {

    }
}
