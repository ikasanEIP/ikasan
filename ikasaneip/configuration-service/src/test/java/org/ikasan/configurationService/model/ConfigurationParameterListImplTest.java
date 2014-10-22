package org.ikasan.configurationService.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigurationParameterListImplTest
{
    @Test
    public void testConfigurationParameterListImplWithNullValue()
    {
        ConfigurationParameterListImpl parameterListImpl = new ConfigurationParameterListImpl("name", null);
        assertNotNull(parameterListImpl);
    }
}
