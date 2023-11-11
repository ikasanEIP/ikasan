package org.ikasan.configurationService.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigurationParameterListImplTest
{
    @Test
    void testConfigurationParameterListImplWithNullValue()
    {
        ConfigurationParameterListImpl parameterListImpl = new ConfigurationParameterListImpl("name", null);
        assertNotNull(parameterListImpl);
    }
}
