package org.ikasan.history.model;

import org.ikasan.spec.history.FlowInvocation;

import java.util.Set;

/**
 * @author Ikasan Development Team
 */
public class FlowInvocationImpl implements FlowInvocation<MessageHistoryFlowEvent>
{
    private Long id;
    private String moduleName;
    private String flowName;
    private long invocationStartTime;
    private long invocationEndTime;
    private String finalAction;
    private Set<MessageHistoryFlowEvent> messageHistoryFlowEvents;
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
     * @param messageHistoryFlowEvents
     * @param expiry
     */
    public FlowInvocationImpl(String moduleName, String flowName, long invocationStartTime, long invocationEndTime
            , String finalAction, Set<MessageHistoryFlowEvent> messageHistoryFlowEvents, long expiry)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.invocationStartTime = invocationStartTime;
        this.invocationEndTime = invocationEndTime;
        this.finalAction = finalAction;
        this.messageHistoryFlowEvents = messageHistoryFlowEvents;
        this.expiry = expiry;
    }

    private FlowInvocationImpl()
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
    public Set<MessageHistoryFlowEvent> getFlowInvocationEvents()
    {
        return this.messageHistoryFlowEvents;
    }

    @Override
    public void setFlowInvocationEvents(Set<MessageHistoryFlowEvent> messageHistoryFlowEvents)
    {
        this.messageHistoryFlowEvents = messageHistoryFlowEvents;
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
