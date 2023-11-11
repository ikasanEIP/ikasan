/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.configurationService.service;

import jakarta.annotation.Resource;
import org.ikasan.configurationService.dao.ConfigurationDao;
import org.ikasan.configurationService.model.*;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure Java based sample of Ikasan EIP for sourcing prices from a tech endpoint.
 * 
 * @author Ikasan Development Team
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
class ConfiguredResourceConfigurationManagementTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};
    
    @Resource
    ConfigurationDao configurationServiceDao;

    @Resource
    ConfigurationManagement configurationManagement;

    ConfiguredResource configuredResource = mockery.mock(ConfiguredResource.class, "mockedConfiguredResource");

    @Test
    void test_instantiation()
    {
        assertTrue(configurationManagement != null, "configurationManagement cannot be 'null'");
    }

    /**
     * Test the failed creation of a configuration through the configurationManagement contract implementation.
     * This fails as there is no instance of a configuration class registered with the configuredResource.
     */
    @Test
        @DirtiesContext
    void test_failed_configurationManagement_create_configuration_not_configuration_instance_on_configuredResource()
    {
        assertThrows(RuntimeException.class, () -> {
            // expectations
            mockery.checking(new Expectations()
            {
                {
                    // once the configuration instance
                    one(configuredResource).getConfiguration();
                    will(returnValue(null));

                    // get resourceId for adding to the exception
                    exactly(2).of(configuredResource).getConfiguredResourceId();
                    will(returnValue("configuredResourceId"));
                }
            });

            configurationManagement.createConfiguration(configuredResource);
            this.mockery.assertIsSatisfied();
        });
    }


    /**
     * Test the successful creation of a dao through the configurationManagement contract implementation.
     */
    @Test
        @DirtiesContext
    void test_configurationManagement_create_configuration()
    {
        final SampleConfiguration runtimeConfiguration = new SampleConfiguration();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // once the dao instance
                one(configuredResource).getConfiguration();
                will(returnValue(runtimeConfiguration));

                // get resourceId for adding to the exception
                one(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
            }
        });

        Object configuration = configurationManagement.createConfiguration(configuredResource);
        assertTrue(configuration instanceof Configuration);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test the successful save of a mixed based dao through the configurationManagement contract implementation.
     */
    @Test
        @DirtiesContext
    void test_configurationManagement_save_mixed_based_configuration()
    {
        Configuration<List<ConfigurationParameter>> configuration =
                new DefaultConfiguration("configuredResourceId", new ArrayList<ConfigurationParameter>());
        configuration.getParameters().add( new ConfigurationParameterStringImpl("name", "value", "desc"));
        configuration.getParameters().add( new ConfigurationParameterIntegerImpl("name", Integer.valueOf(10), "desc"));
        configuration.getParameters().add( new ConfigurationParameterLongImpl("name", Long.valueOf(10), "desc"));

        List<String> listVals = new ArrayList<String>();
        listVals.add("one");
        listVals.add("two");
        listVals.add("three");
        configuration.getParameters().add( new ConfigurationParameterListImpl("name", listVals, "desc"));

        Map<String,String> mapVals = new HashMap<String,String>();
        mapVals.put("one", "1");
        mapVals.put("two", "2");
        mapVals.put("three", "3");
        configuration.getParameters().add( new ConfigurationParameterMapImpl("name", mapVals, "desc"));

        configurationManagement.saveConfiguration(configuration);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test the successful delete of a dao through the configurationManagement contract implementation.
     */
    @Test
    void test_configurationManagement_delete_configuration()
    {
        Configuration<List<ConfigurationParameter>> configuration =
                new DefaultConfiguration("configuredResourceId", new ArrayList<ConfigurationParameter>());
        configuration.getParameters().add( new ConfigurationParameterStringImpl("name", "value", "desc"));
        this.configurationServiceDao.save(configuration);

        configurationManagement.deleteConfiguration(configuration);
        this.mockery.assertIsSatisfied();
    }


}
