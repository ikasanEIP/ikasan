package org.ikasan.topology.metadata.model;

import org.ikasan.spec.metadata.FlowElementMetaData;
import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.metadata.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlowMetaDataImpl implements FlowMetaData
{
    private String name;
    private FlowElementMetaData consumer;
    private List<Transition> transitions = new ArrayList<>();
    private List<FlowElementMetaData> flowElements = new ArrayList<>();
    private String configurationId;
    private String flowStartupType;
    private String flowStartupComment;

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setConsumer(FlowElementMetaData consumer)
    {
        this.consumer = consumer;
    }

    @Override
    public FlowElementMetaData getConsumer()
    {
        return this.consumer;
    }

    @Override
    public List<Transition> getTransitions()
    {
        return this.transitions;
    }

    @Override
    public void setTransitions(List<Transition> transitions)
    {
        this.transitions = transitions;
    }

    @Override
    public List<FlowElementMetaData> getFlowElements()
    {
        return this.flowElements;
    }

    @Override
    public void setFlowElements(List<FlowElementMetaData> flowElements)
    {
        this.flowElements = flowElements;
    }

    @Override
    public String getConfigurationId()
    {
        return this.configurationId;
    }

    @Override
    public void setConfigurationId(String configurationId)
    {
        this.configurationId = configurationId;
    }

    @Override
    public String getFlowStartupType() {
        return this.flowStartupType;
    }

    @Override
    public void setFlowStartupType(String flowStartupType) {
        this.flowStartupType = flowStartupType;
    }

    @Override
    public String getFlowStartupComment() {
        return flowStartupComment;
    }

    @Override
    public void setFlowStartupComment(String flowStartupComment) {
        this.flowStartupComment = flowStartupComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowMetaDataImpl that = (FlowMetaDataImpl) o;
        return Objects.equals(name, that.name)
            && Objects.equals(consumer, that.consumer)
            && Objects.equals(transitions, that.transitions)
            && Objects.equals(flowElements, that.flowElements)
            && Objects.equals(configurationId, that.configurationId)
            && Objects.equals(flowStartupType, that.flowStartupType)
            && Objects.equals(flowStartupComment, that.flowStartupComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, consumer, transitions, flowElements, configurationId
            , flowStartupType, flowStartupComment);
    }
}
