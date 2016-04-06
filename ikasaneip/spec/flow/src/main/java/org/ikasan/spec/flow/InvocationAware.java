package org.ikasan.spec.flow;

/**
 * Marker interface for components that can have their FlowElementInvocation injected
 *
 * @author Ikasan Development Team
 */
public interface InvocationAware<ID, METRIC>
{
    /**
     * Sets the flow element invocation
     * @param flowElementInvocation the invocation
     */
    void setFlowElementInvocation(FlowElementInvocation<ID, METRIC> flowElementInvocation);

    /**
     * Sets the flow element invocation
     * @param flowElementInvocation the invocation
     */
    void unsetFlowElementInvocation(FlowElementInvocation<ID, METRIC> flowElementInvocation);

}
