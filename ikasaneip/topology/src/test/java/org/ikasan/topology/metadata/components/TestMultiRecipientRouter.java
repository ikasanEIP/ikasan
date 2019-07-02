package org.ikasan.topology.metadata.components;

import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.RouterException;

import java.util.List;

public class TestMultiRecipientRouter implements MultiRecipientRouter
{
    @Override
    public List<String> route(Object messageToRoute) throws RouterException
    {
        return null;
    }
}
