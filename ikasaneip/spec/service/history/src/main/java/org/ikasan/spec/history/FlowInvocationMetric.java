package org.ikasan.spec.history;

import java.util.Set;

/**
 * @author Ikasan Development Team
 */
public interface FlowInvocationMetric<EVENT>
{
    public String getModuleName();

    public void setModuleName(String moduleName);

    public String getFlowName();

    public void setFlowName(String flowName);

    public long getInvocationStartTime();

    public void setInvocationStartTime(long invocationStartTime);

    public long getInvocationEndTime();

    public void setInvocationEndTime(long invocationEndTime);

    public String getFinalAction();

    public void setFinalAction(String finalAction);

    public Set<EVENT> getFlowInvocationEvents();

    public void setFlowInvocationEvents(Set<EVENT> events);

    public Boolean getHarvested();

    public void setHarvested(Boolean harvested);

    public long getExpiry();

    public void setExpiry(long expiry);
}
