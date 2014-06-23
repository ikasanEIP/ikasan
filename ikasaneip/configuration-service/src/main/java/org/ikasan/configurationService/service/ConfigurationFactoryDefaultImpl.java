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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.ikasan.configurationService.model.*;
import org.ikasan.spec.configuration.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the ConfigurationFactory for creating configuration and configuration parameter instances.
 * 
 * @author Ikasan Development Team
 *
 */
public class ConfigurationFactoryDefaultImpl
    implements ConfigurationFactory
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(ConfigurationFactoryDefaultImpl.class);

    /** singleton instance */
    private static ConfigurationFactory configurationFactory = new ConfigurationFactoryDefaultImpl();

    /**
     * Get an instance of the configurationFactory.
     * This is a thread-safe singleton.
     * @return
     */
    public static ConfigurationFactory getInstance()
    {
        return configurationFactory;
    }

    /**
     * Singleton so don't let this be instantiated
     */
    private ConfigurationFactoryDefaultImpl()
    {
        // hide the constructor
    }

    public Configuration<List<ConfigurationParameter>> createConfiguration(String configurationResourceId)
    {
        return new DefaultConfiguration(configurationResourceId, new ArrayList<ConfigurationParameter>());
    }

    /**
     *
     * @param runtimeConfiguration
     * @return
     */
    public Configuration<List<ConfigurationParameter>> createConfiguration(String configurationResourceId, Object runtimeConfiguration)
    {
        if(runtimeConfiguration == null)
        {
            throw new ConfigurationException("Runtime configuration object cannot be 'null'");
        }

        Configuration<List<ConfigurationParameter>> configuration = new DefaultConfiguration(configurationResourceId, new ArrayList<ConfigurationParameter>());

        try
        {
            Map<String,Object> properties = PropertyUtils.describe(runtimeConfiguration);

            for(Map.Entry<String,Object> entry: properties.entrySet())
            {
                String name = entry.getKey();
                Object value = entry.getValue();

                // TODO - is there a cleaner way of ignoring the class property ?
                if(!"class".equals(name))
                {
                    if(value == null)
                    {
                        Class<?> cls = PropertyUtils.getPropertyType(runtimeConfiguration, name);
                        if(cls.isAssignableFrom(String.class))
                        {
                            configuration.getParameters().add( new ConfigurationParameterStringImpl(name, null) );
                        }
                        else if(cls.isAssignableFrom(Long.class))
                        {
                            configuration.getParameters().add( new ConfigurationParameterLongImpl(name, null) );
                        }
                        else if(cls.isAssignableFrom(Integer.class))
                        {
                            configuration.getParameters().add( new ConfigurationParameterIntegerImpl(name, null) );
                        }
                        else if(cls.isAssignableFrom(Boolean.class))
                        {
                            configuration.getParameters().add( new ConfigurationParameterBooleanImpl(name, null) );
                        }
                        else if(cls.isAssignableFrom(List.class))
                        {
                            configuration.getParameters().add( new ConfigurationParameterListImpl(name, null) );
                        }
                        else if(cls.isAssignableFrom(Map.class))
                        {
                            configuration.getParameters().add( new ConfigurationParameterMapImpl(name, null) );
                        }
                        else
                        {
                            logger.warn("Ignoring unsupported configurationParameter class [" + cls.getName() + "].");
                        }
                    }
                    else
                    {
                        if (value instanceof String)
                        {
                            configuration.getParameters().add( new ConfigurationParameterStringImpl(name, (String)value) );
                        }
                        else if (value instanceof Long)
                        {
                            configuration.getParameters().add( new ConfigurationParameterLongImpl(name, (Long)value) );
                        }
                        else if (value instanceof Integer)
                        {
                            configuration.getParameters().add( new ConfigurationParameterIntegerImpl(name, (Integer)value) );
                        }
                        else if (value instanceof Boolean)
                        {
                            configuration.getParameters().add( new ConfigurationParameterBooleanImpl(name, (Boolean)value) );
                        }
                        else if (value instanceof List)
                        {
                            configuration.getParameters().add( new ConfigurationParameterListImpl(name, (List)value) );
                        }
                        else if (value instanceof Map)
                        {
                            configuration.getParameters().add( new ConfigurationParameterMapImpl(name, (Map)value) );
                        }
                        else
                        {
                            logger.warn("Ignoring unsupported configurationParameter class [" + value.getClass().getName() + "].");
                        }
                    }
                }
            }

            return configuration;
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new ConfigurationException(e);
        }
    }
}
