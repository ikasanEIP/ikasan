package org.ikasan.rest.module.model;

import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;

import java.util.List;

public class TestFlowInvoker implements FlowElementInvoker, ConfiguredResource
{
    @Override
    public String getConfiguredResourceId()
    {
        return "FLOW_INVOKER_CONFIGURATION_ID";
    }

    @Override
    public void setConfiguredResourceId(String id)
    {

    }

    @Override
    public Object getConfiguration()
    {
        return null;
    }

    @Override
    public void setConfiguration(Object configuration)
    {

    }

    @Override
    public FlowElement invoke(List flowEventListeners, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement flowElement)
    {
        return null;
    }

    @Override
    public void setIgnoreContextInvocation(boolean ignoreContextInvocation)
    {

    }

    @Override
    public void setInvokeContextListeners(boolean invokeContextListeners)
    {

    }

    @Override
    public String getInvokerType()
    {
        return null;
    }

    @Override
    public void setFlowInvocationContextListeners(List list)
    {

    }
}
