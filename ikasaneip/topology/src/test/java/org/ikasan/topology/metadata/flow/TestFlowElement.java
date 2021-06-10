package org.ikasan.topology.metadata.flow;

import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;

import java.util.Map;

public class TestFlowElement implements FlowElement
{
    private Object component;
    private String componentName;
    private String description;
    Map<String, FlowElement> transitions;

    public TestFlowElement(Object component, String componentName, String description, Map<String, FlowElement> transitions)
    {
        this.component = component;
        this.componentName = componentName;
        this.description = description;
        this.transitions = transitions;
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
        return transitions.get(transitionName);
    }

    @Override
    public Map<String, FlowElement> getTransitions()
    {
        return transitions;
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
