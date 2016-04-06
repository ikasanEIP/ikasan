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
     *
     * Typically the aware component would create a short lived reference this flowElementInvocation
     *
     * @param flowElementInvocation the invocation
     */
    void setFlowElementInvocation(FlowElementInvocation<ID, METRIC> flowElementInvocation);

    /**
     * Unsets the flow element invocation
     *
     * Typically the aware component would null any reference it held to this flowElementInvocation
     *
     * @param flowElementInvocation the invocation
     */
    void unsetFlowElementInvocation(FlowElementInvocation<ID, METRIC> flowElementInvocation);

}
