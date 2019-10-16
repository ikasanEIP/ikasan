package org.ikasan.monitor.notifier.model;

import org.ikasan.spec.flow.FlowState;

public class FlowStateImpl implements FlowState
{
    private String moduleName;
    private String flowName;
    private String state;

    @Override
    public String getModuleName()
    {
        return this.moduleName;
    }

    @Override
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    @Override
    public String getFlowName()
    {
        return this.flowName;
    }

    @Override
    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    @Override
    public String getState()
    {
        return this.state;
    }

    @Override
    public void setState(String state)
    {
        this.state = state;
    }
}
