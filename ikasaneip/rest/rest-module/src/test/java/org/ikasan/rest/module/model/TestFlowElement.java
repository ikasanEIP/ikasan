package org.ikasan.rest.module.model;

import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;

import java.util.Map;

public class TestFlowElement implements FlowElement
{
    private Object component;
    private String componentName;
    private String description;
    private Object configuration;

    public TestFlowElement(Object component, String componentName, String description, Object configuration)
    {
        this.component = component;
        this.componentName = componentName;
        this.description = description;
        this.configuration = configuration;
    }

    @Override
    public Object getFlowComponent()
    {
        return component;
    }

    @Override
    public String getComponentName()
    {
        return componentName;
    }

    @Override
    public FlowElement getTransition(String transitionName)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, FlowElement> getTransitions()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public FlowElementInvoker getFlowElementInvoker()
    {
        return new TestFlowInvoker();
    }

    @Override
    public void setFlowElementInvoker(FlowElementInvoker flowElementInvoker)
    {
        throw new UnsupportedOperationException();
    }
}
