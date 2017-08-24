package org.ikasan.spec.history;

import java.util.Set;

/**
 * @author Ikasan Development Team
 */
public interface FlowInvocationMetric<EVENT>
{
    /**
     * Get the module name
     * @return
     */
    public String getModuleName();

    /**
     * Set the module name
     *
     * @param moduleName
     */
    public void setModuleName(String moduleName);

    /**
     * Get the flow name
     *
     * @return
     */
    public String getFlowName();

    /**
     * Set the flow name
     *
     * @param flowName
     */
    public void setFlowName(String flowName);

    /**
     * Get the flow invocation start time
     *
     * @return
     */
    public long getInvocationStartTime();

    /**
     * Set the flow invocation start time.
     *
     * @param invocationStartTime
     */
    public void setInvocationStartTime(long invocationStartTime);

    /**
     * Get the flow invocation end time
     *
     * @return
     */
    public long getInvocationEndTime();

    /**
     * Set the flow invocation end time
     *
     * @param invocationEndTime
     */
    public void setInvocationEndTime(long invocationEndTime);

    /**
     * Get the final action.
     *
     * @return
     */
    public String getFinalAction();

    /**
     * Set the final action.
     *
     * @param finalAction
     */
    public void setFinalAction(String finalAction);

    /**
     * Get the flow invocation events
     *
     * @return
     */
    public Set<EVENT> getFlowInvocationEvents();

    /**
     * Set the flow invocation events.
     *
     * @param events
     */
    public void setFlowInvocationEvents(Set<EVENT> events);

    /**
     * Get the harvested flag.
     *
     * @return
     */
    public Boolean getHarvested();

    /**
     * Set the harvested flag.
     *
     * @param harvested
     */
    public void setHarvested(Boolean harvested);

    /**
     * Get the metric expiry.
     *
     * @return
     */
    public long getExpiry();

    /**
     * Set the metric expiry.
     *
     * @param expiry
     */
    public void setExpiry(long expiry);

    /**
     * Get the error URI.
     *
     * @return
     */
    public String getErrorUri();

    /**
     * Set the error URI.
     *
     * @param errorUri
     */
    public void setErrorUri(String errorUri);
}
