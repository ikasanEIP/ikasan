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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.ikasan.configurationService.dao.ConfigurationCacheImpl;
import org.ikasan.configurationService.dao.ConfigurationDao;
import org.ikasan.spec.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Configuration Service based on a ConfiguredResource.
 * 
 * @author Ikasan Development Team
 * 
 */
public class ConfiguredResourceConfigurationService implements ConfigurationService<ConfiguredResource>,
        ConfigurationManagement<ConfiguredResource, Configuration>
{
    /** Logger */
    private final static Logger logger = LoggerFactory.getLogger(ConfiguredResourceConfigurationService.class);

    /** configuration DAO used for accessing the configuration outside a transaction */
    private ConfigurationDao<List<ConfigurationParameter>> staticConfigurationDao;

    /** configuration DAO used for accessing the configuration transactionally at runtime */
    private ConfigurationDao<List<ConfigurationParameter>> dynamicConfigurationDao;

    /** Default factory for the creation of configurations and configuration parameters */
    private ConfigurationFactory configurationFactory = ConfigurationFactoryDefaultImpl.getInstance();

    /**
     * Default configuration service returns a cached based instance.
     * 
     * @return
     */
    public static ConfigurationService getDefaultConfigurationService()
    {
        return new ConfiguredResourceConfigurationService(new ConfigurationCacheImpl(), new ConfigurationCacheImpl());
    }

    /**
     * Allow the configurationFactory to be overridden
     * 
     * @param configurationFactory
     */
    public void setConfigurationFactory(ConfigurationFactory configurationFactory)
    {
        this.configurationFactory = configurationFactory;
    }

    /**
     * Constructor
     * 
     * @param staticConfigurationDao - used to update configuration outside a runtime transaction
     * @param dynamicConfigurationDao - used to update configuration at runtime within a transaction
     */
    public ConfiguredResourceConfigurationService(ConfigurationDao staticConfigurationDao,
            ConfigurationDao dynamicConfigurationDao)
    {
        this.staticConfigurationDao = staticConfigurationDao;
        if (staticConfigurationDao == null)
        {
            throw new IllegalArgumentException("configurationDao cannot be 'null'");
        }
        this.dynamicConfigurationDao = dynamicConfigurationDao;
        if (dynamicConfigurationDao == null)
        {
            throw new IllegalArgumentException("dynamicConfigurationDao cannot be 'null'");
        }
        // override some default converters to ensure null is default assignments
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new LongConverter(null), Long.class);
    }

    /**
     * Retrieves the persisted configuration based on the configuredResource's configurationId and applies the persisted
     * configuration to the configuration of the configuredResource. This is typically called just prior to the flow
     * being started.
     * 
     * @param configuredResource
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.configuration.service.ConfigurationService#configure(java.lang.Object)
     */
    public void configure(ConfiguredResource configuredResource)
    {
        Configuration<List<ConfigurationParameter>> persistedConfiguration = this.staticConfigurationDao
            .findByConfigurationId(configuredResource.getConfiguredResourceId());
        if (persistedConfiguration == null)
        {
            logger.warn("No persisted dao for configuredResource [" + configuredResource.getConfiguredResourceId()
                    + "]. Default programmatic dao will be used.");
            return;
        }
        Object runtimeConfiguration = configuredResource.getConfiguration();
        if (runtimeConfiguration != null)
        {
            try
            {
                for (ConfigurationParameter persistedConfigurationParameter : persistedConfiguration.getParameters())
                {
                    BeanUtils.setProperty(runtimeConfiguration, persistedConfigurationParameter.getName(),
                        persistedConfigurationParameter.getValue());
                }
                configuredResource.setConfiguration(runtimeConfiguration);
            }
            catch (IllegalAccessException e)
            {
                throw new ConfigurationException(e);
            }
            catch (InvocationTargetException e)
            {
                throw new ConfigurationException(e);
            }
            catch (RuntimeException e)
            {
                throw new ConfigurationException("Failed dao for configuredResource ["
                        + configuredResource.getConfiguredResourceId() + "] " + e.getMessage(), e);
            }
        }
        else
        {
            logger.warn("Cannot configure configuredResource [" + configuredResource.getConfiguredResourceId()
                    + "] as getConfiguration() returned 'null'");
        }
    }

    /**
     * Create a new dao instance for the given ConfiguredResource.
     * 
     * @param configuredResource
     * @return Configuration
     */
    public Configuration createConfiguration(ConfiguredResource configuredResource)
    {
        try
        {
            return configurationFactory.createConfiguration(configuredResource.getConfiguredResourceId(),
                configuredResource.getConfiguration());
        }
        catch (ConfigurationException e)
        {
            throw new ConfigurationException("Failed to configure configuredResource id ["
                    + configuredResource.getConfiguredResourceId(), e);
        }
    }

    /**
     * Updates the persisted dao with the current configuredResource's dao. This is typically used for dynamically
     * updated dao ie. sequence numbers which change onEvent.
     * 
     * @param configuredResource
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.dao.service.ConfigurationService#update(org.ikasan.framework.dao.model.Configuration)
     */
    public void update(ConfiguredResource configuredResource)
    {
        boolean configurationUpdated = false;
        Object runtimeConfiguration = configuredResource.getConfiguration();
        Configuration<List<ConfigurationParameter>> persistedConfiguration = this.dynamicConfigurationDao
            .findByConfigurationId(configuredResource.getConfiguredResourceId());
        if (persistedConfiguration != null)
        {
            for (ConfigurationParameter persistedConfigurationParameter : persistedConfiguration.getParameters())
            {
                String runtimeParameterValue;
                try
                {
                    runtimeParameterValue = BeanUtils.getProperty(runtimeConfiguration,
                        persistedConfigurationParameter.getName());
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
                if ((runtimeParameterValue == null && persistedConfigurationParameter.getValue() != null)
                        || (runtimeParameterValue != null && !(runtimeParameterValue
                            .equals(persistedConfigurationParameter.getValue()))))
                {
                    configurationUpdated = true;
                    persistedConfigurationParameter.setValue(runtimeParameterValue);
                }
            }
            if (configurationUpdated)
            {
                this.dynamicConfigurationDao.save(persistedConfiguration);
            }
        } else {
            logger.debug("Update being attempted without the configuration ever having been persisted, will persist now");
            this.dynamicConfigurationDao.save(this.createConfiguration(configuredResource));            
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.dao.service.ConfigurationService#deleteConfiguration(org.ikasan.framework.dao.model.
     * Configuration)
     */
    public void deleteConfiguration(Configuration configuration)
    {
        this.staticConfigurationDao.delete(configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.dao.service.ConfigurationService#saveConfiguration(org.ikasan.framework.dao.model.Configuration
     * )
     */
    public void saveConfiguration(Configuration configuration)
    {
        this.staticConfigurationDao.save(configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.dao.service.ConfigurationService#getConfiguration(java.lang.Object)
     */
    public Configuration getConfiguration(ConfiguredResource configuredResource)
    {
        return this.staticConfigurationDao.findByConfigurationId(configuredResource.getConfiguredResourceId());
    }

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.ConfigurationManagement#getConfiguration(java.lang.String)
	 */
	@Override
	public Configuration getConfiguration(String configuredResourceId)
	{
		return this.staticConfigurationDao.findByConfigurationId(configuredResourceId);
	}
}
