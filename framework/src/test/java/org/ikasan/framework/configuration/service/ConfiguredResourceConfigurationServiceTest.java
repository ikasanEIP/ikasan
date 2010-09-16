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
    
    /** mock configuration parameter */
    final ConfigurationParameter configurationParameter = mockery.mock(ConfigurationParameter.class, "mockConfigurationParameter");
    
    /** mock resource configuration instance */
    final Object configurationObject = mockery.mock(Object.class, "mockObject");
    
    /** instance on test */
    final ConfigurationService<ConfiguredResource> configurationService = 
        new ConfiguredResourceConfigurationService(configurationDao);

    /**
     * Test failed constructor due to null configurationDAO.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorDueToNullConfigurationDao()
    {
        new ConfiguredResourceConfigurationService(null);
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
     * resource is null i.e. there is no confgiuration object to provide the 
     * resource with a configuration. In this case just warn.
     */
    @Test 
    public void test_configure_without_any_configuiration_as_configurationObject_is_null()
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

}
