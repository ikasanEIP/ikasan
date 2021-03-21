package org.ikasan.component.factory.spring;

import org.apache.commons.lang3.StringUtils;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;

public class CustomConverter implements Converter<String,String>,
    ConfiguredResource<CustomConverterConfiguration>
{
    private String configuredResourceId;

    private CustomConverterConfiguration configuration;

    private String flowName;

    @Override public String convert(String payload) throws TransformationException
    {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(configuration.getPrependText()))
        {
            stringBuilder.append(configuration.getPrependText());
        }
        stringBuilder.append(payload);
        if (StringUtils.isNotEmpty(configuration.getAppendText()))
        {
            stringBuilder.append(configuration.getAppendText());
        }
        if (configuration.isUpperCase())
        {
            return stringBuilder.toString().toUpperCase();
        }
        if (StringUtils.isNotEmpty(flowName)){
            stringBuilder.append("_" + flowName);
        }
        return stringBuilder.toString();
    }

    @Override public String getConfiguredResourceId()
    {
        return configuredResourceId;
    }

    @Override public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override public CustomConverterConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override public void setConfiguration(CustomConverterConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public String getFlowName()
    {
        return flowName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }
}