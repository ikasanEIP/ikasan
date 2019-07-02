package org.ikasan.topology.metadata.components;

import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.filter.FilterException;

public class TestFilter implements Filter
{
    @Override
    public Object filter(Object message) throws FilterException
    {
        return null;
    }
}
