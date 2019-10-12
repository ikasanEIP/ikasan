package org.ikasan.spec.flow;

public interface FlowState
{
    /**
     * Get module name.
     *
     * @return
     */
    public String getModuleName();

    /**
     * Set module name.
     *
     * @param moduleName
     */
    public void setModuleName(String moduleName);

    /**
     * Get flow name.
     *
     * @return
     */
    public String getFlowName();

    /**
     * Set flow name.
     *
     * @param flowName
     */
    public void setFlowName(String flowName);

    /**
     * Get state.
     *
     * @return
     */
    public String getState();

    /**
     * Set state.
     *
     * @param state
     */
    public void setState(String state);
}
