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
package org.ikasan.flow.configuration.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.ConfigurationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.flow.configuration.dao.ConfigurationDao;
import org.ikasan.flow.configuration.model.Configuration;
import org.ikasan.flow.configuration.model.ConfigurationParameter;

/**
 * Implementation of the Configuration Service based on a ConfiguredResource.
 * 
 * @author Ikasan Development Team
 *
 */
public class ConfiguredResourceConfigurationService
    implements ConfigurationService<ConfiguredResource,Configuration>, 
    ConfigurationManagement<ConfiguredResource,Configuration> 
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(ConfiguredResourceConfigurationService.class);

    /** configuration DAO used for accessing the configuration outside a transaction */
    private ConfigurationDao staticConfigurationDao;
    
    /** configuration DAO used for accessing the configuration transactionally at runtime */
    private ConfigurationDao dynamicConfigurationDao;
    
    /**
     * Constructor
     * @param staticConfigurationDao - used to update configuration outside a runtime transaction
     * @param dynamicConfigurationDao - used to update configuration at runtime within a transaction
     */
    public ConfiguredResourceConfigurationService(ConfigurationDao staticConfigurationDao, ConfigurationDao dynamicConfigurationDao)
    {
        this.staticConfigurationDao = staticConfigurationDao;
        if(staticConfigurationDao == null)
        {
            throw new IllegalArgumentException("configurationDao cannot be 'null'");
        }
        
        this.dynamicConfigurationDao = dynamicConfigurationDao;
        if(dynamicConfigurationDao == null)
        {
            throw new IllegalArgumentException("dynamicConfigurationDao cannot be 'null'");
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.service.ConfigurationService#configure(java.lang.Object)
     */
    public void configure(ConfiguredResource configuredResource)
    {
        Configuration configuration = this.staticConfigurationDao.findById(configuredResource.getConfiguredResourceId());
        if(configuration == null)
        {
            throw new ConfigurationException("Failed to configure configuredResource [" 
                    + configuredResource.getConfiguredResourceId() 
                    + "]. Configuration not found!");
        }

        Object configurationObject = configuredResource.getConfiguration();
        if(configurationObject != null)
        {
            try
            {
                for(ConfigurationParameter configurationParameter:configuration.getConfigurationParameters())
                {
                    BeanUtils.setProperty(configurationObject, configurationParameter.getName(), configurationParameter.getValue());
                }

                configuredResource.setConfiguration(configurationObject);
            }
            catch (IllegalAccessException e)
            {
                throw new ConfigurationException(e);
            }
            catch (InvocationTargetException e)
            {
                throw new ConfigurationException(e);
            }
            catch(RuntimeException e)
            {
                throw new ConfigurationException("Failed configuration for configuredResource [" 
                        + configuredResource.getConfiguredResourceId() + "] " + e.getMessage(), e);
            }
        }
        else
        {
            logger.warn("Cannot configure configuredResource [" 
                    + configuredResource.getConfiguredResourceId() + "] as getConfiguration() returned 'null'");
        }
    }

    /**
     * Create a new configuration instance for the given ConfiguredResource.
     * @param ConfiguredResource
     * @return Configuration
     */
    public Configuration createConfiguration(ConfiguredResource configuredResource)
    {
        Object configuredObject = configuredResource.getConfiguration();
        if(configuredObject == null)
        {
            throw new RuntimeException("ConfiguredResource id [" 
                    + configuredResource.getConfiguredResourceId() 
                    + "] returned a 'null' configuration instance. ");
        }
        
        Configuration configuration = new Configuration(configuredResource.getConfiguredResourceId());
        List<ConfigurationParameter> configurationParameters = new ArrayList<ConfigurationParameter>();
        configuration.setConfigurationParameters(configurationParameters);

        try
        {
            Map<String,String> properties = BeanUtils.describe(configuredObject);
            for(Iterator it = properties.entrySet().iterator(); it.hasNext();) 
            {
                Map.Entry<String,String> entry = (Map.Entry) it.next();
                String name = entry.getKey();
                String value = entry.getValue();

                // TODO - is there a cleaner way of ignoring the class property ?
                if(!"class".equals(name))
                {
                    configurationParameters.add(new ConfigurationParameter(name, value));
                }
             }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }

        return configuration;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.service.ConfigurationService#update(org.ikasan.framework.configuration.model.Configuration)
     */
    public void update(ConfiguredResource configuredResource)
    {
        boolean configurationUpdated = false;
        Object runtimeConfiguration = configuredResource.getConfiguration();
        Configuration configuration = this.dynamicConfigurationDao.findById(configuredResource.getConfiguredResourceId());
        for(ConfigurationParameter configurationParameter:configuration.getConfigurationParameters())
        {
            String runtimeParameterValue;
            try
            {
                runtimeParameterValue = BeanUtils.getProperty(runtimeConfiguration, configurationParameter.getName());
            }
            catch (IllegalAccessException e)
            {
                throw new ConfigurationException(e);
            }
            catch (InvocationTargetException e)
            {
                throw new ConfigurationException(e);
            }
            catch (NoSuchMethodException e)
            {
                throw new ConfigurationException(e);
            }

            if( (runtimeParameterValue == null && configurationParameter.getValue() != null) ||
                (runtimeParameterValue != null && !(runtimeParameterValue.equals(configurationParameter.getValue()))) )
            {
                configurationUpdated = true;
                configurationParameter.setValue(runtimeParameterValue);
            }
        }

        if(configurationUpdated)
        {
            this.dynamicConfigurationDao.save(configuration);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.service.ConfigurationService#deleteConfiguration(org.ikasan.framework.configuration.model.Configuration)
     */
    public void deleteConfiguration(Configuration configuration)
    {
        this.staticConfigurationDao.delete(configuration);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.service.ConfigurationService#saveConfiguration(org.ikasan.framework.configuration.model.Configuration)
     */
    public void saveConfiguration(Configuration configuration)
    {
        this.staticConfigurationDao.save(configuration);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.service.ConfigurationService#getConfiguration(java.lang.Object)
     */
    public Configuration getConfiguration(ConfiguredResource configuredResource)
    {
        return this.staticConfigurationDao.findById(configuredResource.getConfiguredResourceId());
    }
}
