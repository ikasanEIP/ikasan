package org.ikasan.topology.metadata.flow;

import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.*;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.spec.trigger.TriggerService;

import java.util.List;

public class TestFlow implements Flow, ConfiguredResource
{
    String name;
    String moduleName;
    FlowConfiguration flowConfiguration;
    TriggerService triggerService;

    public TestFlow(String name, String moduleName, FlowConfiguration flowConfiguration)
    {
        this.name = name;
        this.moduleName = moduleName;
        this.flowConfiguration = flowConfiguration;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getModuleName()
    {
        return this.moduleName;
    }

    @Override
    public List<FlowElement<?>> getFlowElements()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlowElement<?> getFlowElement(String name)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlowConfiguration getFlowConfiguration()
    {
        return flowConfiguration;
    }

    @Override
    public void setFlowListener(FlowEventListener flowEventListener)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addFlowListener(FlowEventListener flowEventListener)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeFlowListener(FlowEventListener flowEventListener)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTriggerService(TriggerService triggerService)
    {
        this.triggerService = triggerService;
    }

    @Override
    public TriggerService getTriggerService()
    {
        return this.triggerService;
    }

    @Override
    public void setFlowInvocationContextListeners(List<FlowInvocationContextListener> flowInvocationContextListeners)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FlowInvocationContextListener> getFlowInvocationContextListeners()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startPause()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stop()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void pause()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resume()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getState()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public SerialiserFactory getSerialiserFactory()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRunning()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPaused()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startContextListeners()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stopContextListeners()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean areContextListenersRunning()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getConfiguredResourceId()
    {
        return "FLOW_CONFIGURATION_ID";
    }

    @Override
    public void setConfiguredResourceId(String id)
    {

    }

    @Override
    public Object getConfiguration()
    {
        return null;
    }

    @Override
    public void setConfiguration(Object configuration)
    {

    }
}
