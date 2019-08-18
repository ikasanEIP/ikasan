package org.ikasan.module.metadata.model;

import org.ikasan.spec.metadata.FlowElementMetaData;

public class SolrFlowElementMetaDataImpl implements FlowElementMetaData
{
    private String componentName;
    private String description;
    private String componentType;
    private String implementingClass;
    private boolean isConfigurable = false;
    private String configurationId;
    private String invokerConfigurationId;


    @Override
    public String getComponentName()
    {
        return componentName;
    }

    @Override
    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }

    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String getComponentType()
    {
        return this.componentType;
    }

    @Override
    public void setComponentType(String componentType)
    {
        this.componentType = componentType;
    }

    @Override
    public String getImplementingClass()
    {
        return this.implementingClass;
    }

    @Override
    public void setImplementingClass(String implementingClass)
    {
        this.implementingClass = implementingClass;
    }

    @Override
    public boolean isConfigurable()
    {
        return this.isConfigurable;
    }

    @Override
    public void setConfigurable(boolean configurable)
    {
        this.isConfigurable = configurable;
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
    public String getInvokerConfigurationId()
    {
        return invokerConfigurationId;
    }

    @Override
    public void setInvokerConfigurationId(String configurationId)
    {
        this.invokerConfigurationId = configurationId;
    }
}
