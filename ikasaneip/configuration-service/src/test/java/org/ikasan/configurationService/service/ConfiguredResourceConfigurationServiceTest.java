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

import org.ikasan.configurationService.dao.ConfigurationDao;
import org.ikasan.configurationService.model.ConfigurationParameterObjectImpl;
import org.ikasan.configurationService.model.DefaultConfiguration;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Test class for ConfiguredResourceConfigurationService based on
 * the implementation of a ConfigurationService contract.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = {
        "/configuration-service-conf.xml",
        "/serialiser-service-conf.xml",
        "/transaction-conf.xml",
        "/h2-datasource-conf.xml",
        "/substitute-components.xml"
})
public class ConfiguredResourceConfigurationServiceTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    @Resource
    ConfigurationDao configurationServiceDao;

    @Resource
    ConfigurationService<ConfiguredResource> configurationService;

    ConfiguredResource configuredResource = mockery.mock(ConfiguredResource.class, "mockedConfiguredResource");

    @Test
    public void test_instantiation() {
        Assert.assertTrue("configurationService cannot be 'null'", configurationService != null);
    }

    /**
     * Test the setting of a static configuration on a configuredResource at runtime
     * where no persisted configuration exists outside of that of the coded version.
     * This is typically invoked on the start of a flow where the flow identifies
     * all configuredResources and ensures the latest configuration is applied
     * prior to starting the moving components.
     */
    @Test
    @DirtiesContext
    public void test_configurationService_setting_of_a_static_configuration_no_configuration() {
        // expectations
        mockery.checking(new Expectations() {
            {
                // once to try to locate a configuration based on this id
                one(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceIdNotFound"));

                // once to log the fact we did not find one
                one(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
            }
        });

        configurationService.configure(configuredResource);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test the setting of a static dao on a configuredResource at runtime
     * where a persisted dao exists.
     * This is typically invoked on the start of a flow where the flow identifies
     * all configuredResources and ensures the latest dao is applied
     * prior to starting the moving components.
     */
    @Test
    @DirtiesContext
    public void test_configurationService_setting_of_a_static_configuration_with_configuration() {
        final SampleConfiguration runtimeConfiguration = new SampleConfiguration();
        final Configuration<List<ConfigurationParameter>> persistedConfiguration = new DefaultConfiguration("configuredResourceId");

        // add a string parameter
        persistedConfiguration.getParameters().add(new ConfigurationParameterObjectImpl("one", "1", "Number One"));

        // save it
        this.configurationServiceDao.save(persistedConfiguration);

        // expectations
        mockery.checking(new Expectations() {
            {
                // once to try to locate a dao based on this id
                one(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                // once to try to locate a dao based on this id
                one(configuredResource).getConfiguration();
                will(returnValue(runtimeConfiguration));

                // set the dao on the resource
                one(configuredResource).setConfiguration(runtimeConfiguration);
            }
        });

        configurationService.configure(configuredResource);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test the setting of a static dao on a configuredResource at runtime
     * where a persisted dao exists.
     * This is typically invoked on the start of a flow where the flow identifies
     * all configuredResources and ensures the latest dao is applied
     * prior to starting the moving components.
     */
    @Test
    @DirtiesContext
    public void test_configurationService_setting_of_a_static_configuration_with_configuration_extendedClass() {
        final ExtendedSampleConfiguration runtimeConfiguration = new ExtendedSampleConfiguration();
        final Configuration<List<ConfigurationParameter>> persistedConfiguration = new DefaultConfiguration("configuredResourceId");

        // add a string parameter
        persistedConfiguration.getParameters().add(new ConfigurationParameterObjectImpl("one", "1", "Number One"));

        // save it
        this.configurationServiceDao.save(persistedConfiguration);

        // expectations
        mockery.checking(new Expectations() {
            {
                // once to try to locate a dao based on this id
                one(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                // once to try to locate a dao based on this id
                one(configuredResource).getConfiguration();
                will(returnValue(runtimeConfiguration));

                // set the dao on the resource
                one(configuredResource).setConfiguration(runtimeConfiguration);
            }
        });

        configurationService.configure(configuredResource);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test the setting of a static dao on a configuredResource at runtime
     * where a persisted dao exists.
     * This is typically invoked on the start of a flow where the flow identifies
     * all configuredResources and ensures the latest dao is applied
     * prior to starting the moving components.
     */
    @Test
    @DirtiesContext
    public void test_configurationService_setting_of_a_static_configuration_with_configuration_null_resource_configuration() {
        final Configuration<List<ConfigurationParameter>> persistedConfiguration = new DefaultConfiguration("configuredResourceId");
        ConfigurationParameter stringParam = new ConfigurationParameterObjectImpl("name", "value", "description");
        persistedConfiguration.getParameters().add(stringParam);
        this.configurationServiceDao.save(persistedConfiguration);

        // expectations
        mockery.checking(new Expectations() {
            {
                // once to try to locate a dao based on this id
                one(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                // once to try to locate a dao based on this id
                one(configuredResource).getConfiguration();
                will(returnValue(null));

                // log warning
                one(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
            }
        });

        configurationService.configure(configuredResource);
        this.mockery.assertIsSatisfied();
    }


    /**
     * Test the setting of a static dao on a configuredResource at runtime
     * where a persisted dao exists.
     * This is typically invoked on the start of a flow where the flow identifies
     * all configuredResources and ensures the latest dao is applied
     * prior to starting the moving components.
     */
    @Test
    @DirtiesContext
    public void test_configurationService_update_of_a_dynamic_configuration() {
        ConfigurationParameter stringParam = new ConfigurationParameterObjectImpl("one", "0", "description");
        final Configuration<List<ConfigurationParameter>> persistedConfiguration = new DefaultConfiguration("configuredResourceId");
        persistedConfiguration.getParameters().add(stringParam);
        this.configurationServiceDao.save(persistedConfiguration);

        final SampleConfiguration runtimeConfiguration = new SampleConfiguration();
        runtimeConfiguration.setOne("1");

        // expectations
        mockery.checking(new Expectations() {
            {
                // once to try to locate a dao based on this id
                one(configuredResource).getConfiguration();
                will(returnValue(runtimeConfiguration));

                // find by dao id
                one(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
            }
        });

        configurationService.update(configuredResource);
        this.mockery.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void test_configurationService_update_of_a_dynamic_configuration_that_hasnt_been_saved_previously() {
        ConfigurationParameter stringParam = new ConfigurationParameterObjectImpl("one", "0", "description");
        final Configuration<List<ConfigurationParameter>> persistedConfiguration = new DefaultConfiguration("configuredResourceId");
        persistedConfiguration.getParameters().add(stringParam);

        final ExtendedExtendedSampleConfiguration runtimeConfiguration = new ExtendedExtendedSampleConfiguration();
        runtimeConfiguration.setOne("1");
        runtimeConfiguration.setPrimitiveBool(false);

        TestConfiguredResource configuredResource = new TestConfiguredResource("configuredResourceId", runtimeConfiguration);

        configurationService.update(configuredResource);

        TestConfiguredResource emptyConfiguration = new TestConfiguredResource("configuredResourceId", new ExtendedExtendedSampleConfiguration());

        configurationService.configure(emptyConfiguration);
        Assert.assertEquals("Should have retrieved config from db with parameter value set", "1", ((ExtendedExtendedSampleConfiguration)emptyConfiguration.getConfiguration()).getOne());
        Assert.assertEquals("Should have retrieved config from db with parameter value set", false, ((ExtendedExtendedSampleConfiguration)emptyConfiguration.getConfiguration()).isPrimitiveBool());
        this.mockery.assertIsSatisfied();
    }



    @Test
    @DirtiesContext
    public void test_configurationService_update_of_a_dynamic_configuration_with_map_property() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");

        final SampleConfiguration runtimeConfiguration = new SampleConfiguration();
        runtimeConfiguration.setMap(map);

        TestConfiguredResource configuredResource = new TestConfiguredResource("configuredResourceId", runtimeConfiguration);

        configurationService.update(configuredResource);

        ((SampleConfiguration)configuredResource.getConfiguration()).getMap().put("extraKey","extraValue");

        configurationService.update(configuredResource); // update twice to ensure configuration is persisted


        TestConfiguredResource emptyconfiguredResource = new TestConfiguredResource("configuredResourceId", new SampleConfiguration());

        configurationService.configure(emptyconfiguredResource);
        Assert.assertEquals("Should have retrieved config from db with parameter value set", map, ((SampleConfiguration)emptyconfiguredResource.getConfiguration()).getMap());
    }


    private static class TestConfiguredResource implements ConfiguredResource{
        private String configurationId;
        private Object configuration;

        public TestConfiguredResource(String configurationId, Object configuration)
        {
            this.configurationId = configurationId;
            this.configuration = configuration;
        }

        @Override public String getConfiguredResourceId()
        {
            return configurationId;
        }

        @Override public void setConfiguredResourceId(String id)
        {
            configurationId = id;
        }

        @Override public Object getConfiguration()
        {
            return configuration;
        }

        @Override public void setConfiguration(Object configuration)
        {
            this.configuration= configuration;
        }
    }

}
