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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ikasan.configurationService.dao.ConfigurationCacheImpl;
import org.ikasan.configurationService.dao.ConfigurationDao;
import org.ikasan.configurationService.model.*;
import org.ikasan.configurationService.util.ReflectionUtils;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationException;
import org.ikasan.spec.configuration.ConfigurationFactory;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /** need a serialiser to serialise the incoming event payload of T */
    private Serialiser<Object,byte[]> serialiser;

    /**
     * Default configuration service returns a cached based instance.
     * 
     * @return
     */
    public static ConfigurationService getDefaultConfigurationService(SerialiserFactory serialiserFactory)
    {
        return new ConfiguredResourceConfigurationService(new ConfigurationCacheImpl(), new ConfigurationCacheImpl(), serialiserFactory);
    }


    /**
     * Constructor
     * 
     * @param staticConfigurationDao - used to update configuration outside a runtime transaction
     * @param dynamicConfigurationDao - used to update configuration at runtime within a transaction
     */
    public ConfiguredResourceConfigurationService(ConfigurationDao staticConfigurationDao,
            ConfigurationDao dynamicConfigurationDao, SerialiserFactory serialiserFactory)
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
        this.serialiser = serialiserFactory.getDefaultSerialiser();

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

        Configuration<List<ConfigurationParameter>> persistedConfiguration =
            getPersistedConfiguration(this.dynamicConfigurationDao,
                configuredResource.getConfiguredResourceId());

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
                    ReflectionUtils.setProperty( runtimeConfiguration, persistedConfigurationParameter.getName(),
                            persistedConfigurationParameter.getValue() );
                }
                configuredResource.setConfiguration(runtimeConfiguration);
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e)
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
            Object runtimeConfiguration = configuredResource.getConfiguration();
            String configurationResourceId = configuredResource.getConfiguredResourceId();
            if (runtimeConfiguration == null) {
                throw new ConfigurationException("Runtime configuration object cannot be 'null'");
            }

            Configuration<List<ConfigurationParameter>> configuration = new DefaultConfiguration(configurationResourceId, new ArrayList<ConfigurationParameter>());

            Map<String, Object> properties = ReflectionUtils.getPropertiesIgnoringExceptions(runtimeConfiguration);
            // We wrap this in a TreeMap because PropertyUtils does not offer ordering (as of version 1.9.1) and several
            // tests require implicit ordering (and it's not a bad thing to have ordering anyhow)
            TreeMap<String, Object> orderedProperties = new TreeMap<>(properties);

            for (Map.Entry<String, Object> entry : orderedProperties.entrySet())
            {
                String name = entry.getKey();
                Object value = entry.getValue();

                    if (value == null)
                    {
                        configuration.getParameters().add(new ConfigurationParameterObjectImpl(name, null));
                    }
                    else
                    {
                        byte[] serialisedValue = serialiser.serialise(value);
                        configuration.getParameters().add(new ConfigurationParameterObjectImpl(name, value, serialisedValue));
                    }

            }

            return configuration;
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
     * @see org.ikasan.framework.dao.service.ConfigurationService#update(org.ikasan.framework.dao.window.Configuration)
     */
    public void update(ConfiguredResource configuredResource)
    {
        boolean configurationUpdated = false;
        Object runtimeConfiguration = configuredResource.getConfiguration();

        Configuration<List<ConfigurationParameter>> persistedConfiguration =
            getPersistedConfiguration(this.dynamicConfigurationDao,
                configuredResource.getConfiguredResourceId());

        if (persistedConfiguration != null)
        {

            for (ConfigurationParameter persistedConfigurationParameter : persistedConfiguration.getParameters())
            {
                Object runtimeParameterValue;
                try
                {
                    runtimeParameterValue = ReflectionUtils.getProperty( runtimeConfiguration,
                            persistedConfigurationParameter.getName() );
                }
                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e)
                {
                    throw new ConfigurationException(e);
                }

                if ((runtimeParameterValue == null && persistedConfigurationParameter.getValue() != null)
                        || (runtimeParameterValue != null && !(runtimeParameterValue
                            .equals(persistedConfigurationParameter.getValue()))))
                {
                    configurationUpdated = true;
                    byte[] serialisedValue = serialiser.serialise(runtimeParameterValue);
                    persistedConfigurationParameter.setSerialisedValue(serialisedValue);
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
     * @see org.ikasan.framework.dao.service.ConfigurationService#deleteConfiguration(org.ikasan.framework.dao.window.
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
     * org.ikasan.framework.dao.service.ConfigurationService#saveConfiguration(org.ikasan.framework.dao.window.Configuration
     * )
     */
    public void saveConfiguration(Configuration configuration)
    {

        if(configuration.getParameters() != null && configuration.getParameters() instanceof List)
            ((List<ConfigurationParameter>)configuration.getParameters()).stream()
                .filter(configurationParameter -> configurationParameter.getValue()!=null)
                // we going place the values from before serialisation before returning the object
                .forEach(configurationParameter -> {
                byte[] serialisedValue = serialiser.serialise(configurationParameter.getValue());
                configurationParameter.setSerialisedValue(serialisedValue);

        });

        this.staticConfigurationDao.save(configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.dao.service.ConfigurationService#getConfiguration(java.lang.Object)
     */
    public Configuration getConfiguration(ConfiguredResource configuredResource)
    {
        return getConfiguration(configuredResource.getConfiguredResourceId());
    }

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.ConfigurationManagement#getConfiguration(java.lang.String)
	 */
	@Override
	public Configuration getConfiguration(String configuredResourceId)
	{

        return getPersistedConfiguration(staticConfigurationDao,configuredResourceId);
    }

    private Configuration getPersistedConfiguration(ConfigurationDao configurationDao, String configuredResourceId)
    {
        Configuration<List<ConfigurationParameter>> persistedConfiguration = configurationDao
            .findByConfigurationId(configuredResourceId);
        if (persistedConfiguration != null)
        {
            persistedConfiguration.getParameters().stream()
                .filter(configurationParameter -> configurationParameter.getSerialisedValue() != null)
                .forEach(configurationParameter -> {
                    // this is mutating original object
                    // we going place the values from before serialisation before returning the object
                    Object deserialisedValue = serialiser.deserialise(configurationParameter.getSerialisedValue());
                    configurationParameter.setValue(deserialisedValue);
                });
        }
        return persistedConfiguration;
    }
}
