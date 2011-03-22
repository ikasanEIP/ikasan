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
package org.ikasan.framework.configuration.service;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.configuration.ConfiguredResource;
import org.ikasan.framework.configuration.dao.ConfigurationDao;
import org.ikasan.framework.configuration.model.Configuration;
import org.ikasan.framework.configuration.model.ConfigurationParameter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for {@link ConfiguredResourceConfigurationService}
 * 
 * @author Ikasan Development Team
 *
 */
public class ConfiguredResourceConfigurationServiceTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mock configurationDao */
    final ConfigurationDao configurationDao = mockery.mock(ConfigurationDao.class, "mockConfigurationDao");
    
    /** mock configuredResource */
    final ConfiguredResource configuredResource = mockery.mock(ConfiguredResource.class, "mockConfiguredResource");
    
    /** mock configuration */
    final Configuration configuration = mockery.mock(Configuration.class, "mockConfiguration");
    
    /** mock configuration */
    final Configuration runtimeConfiguration = mockery.mock(Configuration.class, "mockConfiguration");
    
    /** mock configuration parameter */
    final ConfigurationParameter configurationParameter = mockery.mock(ConfigurationParameter.class, "mockConfigurationParameter");
    
    /** mock resource configuration instance */
    final Object configurationObject = mockery.mock(Object.class, "mockObject");
    
    /** configuration service instance on test */
    final ConfigurationService<ConfiguredResource,Configuration> configurationService = 
        new ConfiguredResourceConfigurationService(configurationDao, configurationDao);

    /** configuration management service instance on test */
    final ConfigurationManagement<ConfiguredResource,Configuration> configurationManagement = 
        new ConfiguredResourceConfigurationService(configurationDao, configurationDao);

    /**
     * Test failed constructor due to null static configurationDAO.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorDueToNullStaticConfigurationDao()
    {
        new ConfiguredResourceConfigurationService(null, null);
    }

    /**
     * Test failed constructor due to null dynamic configurationDAO.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorDueToNullDynamicConfigurationDao()
    {
        new ConfiguredResourceConfigurationService(configurationDao, null);
    }

    /**
     * Test successful configure invocation.
     */
    @Test 
    public void test_successful_configure()
    {
        final List<ConfigurationParameter> configurationParams = new ArrayList<ConfigurationParameter>();
        configurationParams.add(configurationParameter);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the configured resources unique identifier
                exactly(1).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
                
                // find the persisted configuration based on this identifier
                one(configurationDao).findById("configuredResourceId");
                will(returnValue(configuration));
                
                // get the configuration instance from the configuredResource
                one(configuredResource).getConfiguration();
                will(returnValue(configurationObject));

                // populate the configuration object with the retrieved configuration instance properties
                one(configuration).getConfigurationParameters();
                will(returnValue(configurationParams));
                
                // apply parameter to the configuration object
                one(configurationParameter).getName();
                will(returnValue("configurationParameterName"));
                one(configurationParameter).getValue();
                will(returnValue("configurationParameterValue"));
                
                // re-populate the configuredResource with the newly configured configuration object
                one(configuredResource).setConfiguration(configurationObject);
            }
        });
        
        // run the test
        configurationService.configure(configuredResource);
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed configure invocation due to no configuration found 
     * for the given configured resource id.
     */
    @Test (expected = ConfigurationException.class)
    public void test_failed_configure_due_to_no_configuration_found()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the configured resources unique identifier
                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
                
                // find the persisted configuration based on this identifier
                one(configurationDao).findById("configuredResourceId");
                will(returnValue(null));
            }
        });
        
        // run the test
        configurationService.configure(configuredResource);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test configure invocation where the configuration object returned by the configured
     * resource is null i.e. there is no configuration object to provide the 
     * resource with a configuration. In this case just warn.
     */
    @Test 
    public void test_configure_without_any_configuration_as_configurationObject_is_null()
    {
        final List<ConfigurationParameter> configurationParams = new ArrayList<ConfigurationParameter>();
        configurationParams.add(configurationParameter);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the configured resources unique identifier
                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
                
                // find the persisted configuration based on this identifier
                one(configurationDao).findById("configuredResourceId");
                will(returnValue(configuration));
                
                // get the configuration instance from the configuredResource
                one(configuredResource).getConfiguration();
                will(returnValue(null));
            }
        });
        
        // run the test
        configurationService.configure(configuredResource);
        mockery.assertIsSatisfied();
    }

    /**
     * Test update invocation where the configuration object has been changed 
     * at runtime.
     */
    @Test 
    public void test_update_with_runtime_configuration_changed()
    {
        final List<ConfigurationParameter> configurationParams = new ArrayList<ConfigurationParameter>();
        configurationParams.add(configurationParameter);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the runtime configuration
                exactly(1).of(configuredResource).getConfiguration();
                will(returnValue(new StubbedRuntimeConfiguration()));
                
                // get the persisted configuration
                exactly(1).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
                one(configurationDao).findById("configuredResourceId");
                will(returnValue(configuration));

                exactly(1).of(configurationParameter).getName();
                will(returnValue("field1"));
                exactly(1).of(configurationParameter).getValue();
                will(returnValue("field0Value"));
                exactly(1).of(configurationParameter).setValue("field1Value");
                
                exactly(1).of(configuration).getConfigurationParameters();
                will(returnValue(configurationParams));
                
                // so update persisted configuration with runtime version
                one(configurationDao).save(configuration);
            }
        });
        
        // run the test
        configurationService.update(configuredResource);
        mockery.assertIsSatisfied();
    }

    /**
     * Test update invocation where the configuration object has not been changed 
     * at runtime.
     */
    @Test 
    public void test_update_with_runtime_configuration_unchanged()
    {
        final List<ConfigurationParameter> configurationParams = new ArrayList<ConfigurationParameter>();
        configurationParams.add(configurationParameter);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the runtime configuration
                exactly(1).of(configuredResource).getConfiguration();
                will(returnValue(new StubbedRuntimeConfiguration()));
                
                // get the persisted configuration
                exactly(1).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
                one(configurationDao).findById("configuredResourceId");
                will(returnValue(configuration));

                exactly(1).of(configurationParameter).getName();
                will(returnValue("field1"));
                exactly(1).of(configurationParameter).getValue();
                will(returnValue("field1Value"));
                
                exactly(1).of(configuration).getConfigurationParameters();
                will(returnValue(configurationParams));
            }
        });
        
        // run the test
        configurationService.update(configuredResource);
        mockery.assertIsSatisfied();
    }

    /**
     * Test retrieval of a configuredResource's configuration.
     */
    @Test 
    public void test_getConfiguration()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the configured resources unique identifier
                exactly(1).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
                
                // find the persisted configuration based on this identifier
                one(configurationDao).findById("configuredResourceId");
                will(returnValue(configuration));
            }
        });
        
        // run the test
        configurationManagement.getConfiguration(configuredResource);
        mockery.assertIsSatisfied();
    }

    /**
     * Test saving of a configuredResource's configuration.
     */
    @Test 
    public void test_saveConfiguration()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // update the configuration
                one(configurationDao).save(configuration);
            }
        });
        
        // run the test
        configurationManagement.saveConfiguration(configuration);
        mockery.assertIsSatisfied();
    }

    /**
     * Test deleting of a configuredResource's configuration.
     */
    @Test 
    public void test_deleteConfiguration()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // delete the configuration
                one(configurationDao).delete(configuration);
            }
        });
        
        // run the test
        configurationManagement.deleteConfiguration(configuration);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test creation of a configuredResource's configuration.
     */
    @Test 
    public void test_createConfiguration()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the configuration model instance
                one(configuredResource).getConfiguration();
                will(returnValue(configurationObject));
                
                // get the configuredResources id
                one(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
            }
        });
        
        // run the test
        configurationManagement.createConfiguration(configuredResource);
        mockery.assertIsSatisfied();
    }

    /**
     * Stubbed test configuration instance
     * @author mitcje
     *
     */
    public class StubbedRuntimeConfiguration
    {
        private String field1 = "field1Value";

        public String getField1()
        {
            return field1;
        }

        public void setField1(String field1)
        {
            this.field1 = field1;
        }
        
    }

}
