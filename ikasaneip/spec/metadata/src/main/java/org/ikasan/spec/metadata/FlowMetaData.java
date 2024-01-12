package org.ikasan.spec.metadata;

import java.util.List;

public interface FlowMetaData
{
    /**
     * Set the flow name.
     *
     * @param name the flow name
     */
    public  void setName(String name);

    /**
     * Get the flow name.
     *
     * @return the flow name.
     */
    public String getName();

    /**
     * Set the flow consumer.
     *
     * @param consumer the flow consumer
     */
    public void setConsumer(FlowElementMetaData consumer);

    /**
     * Get the flow consumer.
     *
     * @return the consumer
     */
    public FlowElementMetaData getConsumer();

    /**
     * Get the transitions.
     *
     * @return
     */
    public List<Transition> getTransitions();

    /**
     * Set the transitions.
     *
     * @param transitions
     */
    public void setTransitions(List<Transition> transitions);

    /**
     * Get the transitions.
     *
     * @return
     */
    public List<FlowElementMetaData> getFlowElements();

    /**
     * Set the transitions.
     *
     * @param transitions
     */
    public void setFlowElements(List<FlowElementMetaData> transitions);

    /**
     * Get the flow configuration id.
     *
     * @return
     */
    public String getConfigurationId();

    /**
     * Set the flow confuguration id.
     *
     * @param configurationId
     */
    public void setConfigurationId(String configurationId);

    /**
     * Get the flow startup type.
     *
     * @return
     */
    public String getFlowStartupType();

    /**
     * Set the flow startup type.
     *
     * @param flowStartupType
     */
    public void setFlowStartupType(String flowStartupType);

    /**
     * Get the flow startup comment.
     *
     * @return
     */
    public String getFlowStartupComment();

    /**
     * Set the flow startup type.
     *
     * @param flowStartupComment
     */
    public void setFlowStartupComment(String flowStartupComment);

}
