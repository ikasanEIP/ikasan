package org.ikasan.configurationService.metadata.flow;

import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedService;
import org.ikasan.spec.replay.ReplayRecordService;
import org.ikasan.spec.resubmission.ResubmissionService;

import java.util.List;

public class TestFlowConfiguration implements FlowConfiguration
{
    FlowElement<Consumer> consumerFlowElement;

    public TestFlowConfiguration(FlowElement<Consumer> consumerFlowElement)
    {
        this.consumerFlowElement = consumerFlowElement;
    }

    @Override
    public FlowElement<Consumer> getConsumerFlowElement()
    {
        return consumerFlowElement;
    }

    @Override
    public List<FlowElement<?>> getFlowElements()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ManagedService> getManagedServices()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FlowElement<ManagedResource>> getManagedResourceFlowElements()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FlowElement<?>> getFlowElementInvokerConfiguredResources()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FlowElement<ConfiguredResource>> getConfiguredResourceFlowElements()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FlowElement<ConfiguredResource>> getDynamicConfiguredResourceFlowElements()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FlowElement<IsErrorReportingServiceAware>> getErrorReportingServiceAwareFlowElements()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResubmissionService getResubmissionService()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReplayRecordService getReplayRecordService()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void configure(ConfiguredResource configuredResource)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(ConfiguredResource dynamicConfiguredResource)
    {
        throw new UnsupportedOperationException();
    }
}
