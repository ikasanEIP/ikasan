package org.ikasan.dashboard.broadcast;

public class FlowState
{
    private String moduleName;
    private String flowName;
    private State state;

    /**
     * Constructor
     *
     * @param moduleName
     * @param flowName
     * @param state
     */
    public FlowState(String moduleName, String flowName, State state)
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

    public State getState()
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
        return state != null ? state.getFlowState().equals(flowState.state.getFlowState()) : flowState.state == null;
    }

    @Override
    public int hashCode()
    {
        int result = moduleName != null ? moduleName.hashCode() : 0;
        result = 31 * result + (flowName != null ? flowName.hashCode() : 0);
        result = 31 * result + (state.getFlowState() != null ? state.getFlowState().hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("FlowState{");
        sb.append("moduleName='").append(moduleName).append('\'');
        sb.append(", flowName='").append(flowName).append('\'');
        sb.append(", state=").append(state);
        sb.append('}');
        return sb.toString();
    }
}
