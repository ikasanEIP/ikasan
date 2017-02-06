package org.ikasan.history.model;

import org.ikasan.spec.history.FlowInvocationMetric;

import java.util.Set;

/**
 * @author Ikasan Development Team
 */
public class FlowInvocationMetricImpl implements FlowInvocationMetric<ComponentInvocationMetricImpl>
{
    private Long id;
    private String moduleName;
    private String flowName;
    private long invocationStartTime;
    private long invocationEndTime;
    private String finalAction;
    private Set<ComponentInvocationMetricImpl> componentInvocationMetricImpls;
    private Boolean harvested = false;
    private long expiry;

    /**
     * Constructor
     *
     * @param moduleName
     * @param flowName
     * @param invocationStartTime
     * @param invocationEndTime
     * @param finalAction
     * @param componentInvocationMetricImpls
     * @param expiry
     */
    public FlowInvocationMetricImpl(String moduleName, String flowName, long invocationStartTime, long invocationEndTime
            , String finalAction, Set<ComponentInvocationMetricImpl> componentInvocationMetricImpls, long expiry)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.invocationStartTime = invocationStartTime;
        this.invocationEndTime = invocationEndTime;
        this.finalAction = finalAction;
        this.componentInvocationMetricImpls = componentInvocationMetricImpls;
        this.expiry = expiry;
    }

    private FlowInvocationMetricImpl()
    {

    }

    public Long getId()
    {
        return id;
    }

    private void setId(Long id)
    {
        this.id = id;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getFlowName()
    {
        return flowName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public long getInvocationStartTime()
    {
        return invocationStartTime;
    }

    public void setInvocationStartTime(long invocationStartTime)
    {
        this.invocationStartTime = invocationStartTime;
    }

    public long getInvocationEndTime()
    {
        return invocationEndTime;
    }

    public void setInvocationEndTime(long invocationEndTime)
    {
        this.invocationEndTime = invocationEndTime;
    }

    public String getFinalAction()
    {
        return finalAction;
    }

    public void setFinalAction(String finalAction)
    {
        this.finalAction = finalAction;
    }

    @Override
    public Set<ComponentInvocationMetricImpl> getFlowInvocationEvents()
    {
        return this.componentInvocationMetricImpls;
    }

    @Override
    public void setFlowInvocationEvents(Set<ComponentInvocationMetricImpl> componentInvocationMetricImpls)
    {
        this.componentInvocationMetricImpls = componentInvocationMetricImpls;
    }

    public Boolean getHarvested()
    {
        return harvested;
    }

    public void setHarvested(Boolean harvested)
    {
        this.harvested = harvested;
    }

    public long getExpiry()
    {
        return expiry;
    }

    public void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }
}
