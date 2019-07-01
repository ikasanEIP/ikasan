package org.ikasan.topology.metadata.components;

import org.ikasan.spec.component.routing.RouterException;
import org.ikasan.spec.component.routing.SingleRecipientRouter;

public class TestSingleRecipientRouter implements SingleRecipientRouter
{
    @Override
    public String route(Object messageToRoute) throws RouterException
    {
        return null;
    }
}
