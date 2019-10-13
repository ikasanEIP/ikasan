package org.ikasan.dashboard.broadcast;

public class FlowState
{
    private String moduleName;
    private String flowName;
    private String state;

    /**
     * Constructor
     *
     * @param moduleName
     * @param flowName
     * @param state
     */
    public FlowState(String moduleName, String flowName, String state)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.state = state;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public String getFlowName()
    {
        return flowName;
    }

    public String getState()
    {
        return state;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowState flowState = (FlowState) o;

        if (moduleName != null ? !moduleName.equals(flowState.moduleName) : flowState.moduleName != null) return false;
        if (flowName != null ? !flowName.equals(flowState.flowName) : flowState.flowName != null) return false;
        return state != null ? state.equals(flowState.state) : flowState.state == null;
    }

    @Override
    public int hashCode()
    {
        int result = moduleName != null ? moduleName.hashCode() : 0;
        result = 31 * result + (flowName != null ? flowName.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
