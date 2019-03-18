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
package org.ikasan.configurationService.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.configurationService.model.ConfigurationParameterObjectImpl;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ConfigurationDao interface providing
 * the Hibernate persistence for configuration instances.
 * 
 * @author Ikasan Development Team
 */
public class ConfigurationHibernateImpl extends HibernateDaoSupport 
    implements ConfigurationDao<List<ConfigurationParameter>>
{

    /** need a serialiser to serialise the incoming event payload of T */
    private Serialiser<Object,byte[]> serialiser;

    public ConfigurationHibernateImpl(SerialiserFactory serialiserFactory)
    {
        this.serialiser = serialiserFactory.getDefaultSerialiser();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#findConfiguration(java.lang.String)
     */
    public Configuration findByConfigurationId(String configurationId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(Configuration.class);
        criteria.add(Restrictions.eq("configurationId", configurationId));

        List<Configuration<List<ConfigurationParameter>>> configurations = (List<Configuration<List<ConfigurationParameter>>>) getHibernateTemplate().findByCriteria(criteria);
        if(configurations == null || configurations.size() == 0)
        {
            return null;
        }

        Configuration<List<ConfigurationParameter>> configuration = configurations.get(0);

        for(ConfigurationParameter configurationParameter:configuration.getParameters())
        {

            if(configurationParameter.getValue() != null && configurationParameter.getValue() instanceof byte[])
            {
                Object deserialisedValue = serialiser.deserialise((byte[]) configurationParameter.getValue());
                configurationParameter.setValue(deserialisedValue);
            }
        }
        return configuration;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#saveConfiguration(org.ikasan.framework.configuration.window.Configuration)
     */
    public void save(Configuration<List<ConfigurationParameter>> configuration)
    {
        //copy all configParams for later
        List<ConfigurationParameter> copyOfConfigurationParametersList =
            configuration.getParameters().stream()
                .map(cp -> new ConfigurationParameterObjectImpl(cp))
                .collect(Collectors.toList());



        // work-around for Sybase issue where it converts empty strings to single spaces.
        // See http://open.jira.com/browse/IKASAN-520
        // Where we would have persisted "" change this to a null to stop Sybase
        // inserting a single space character.
        if("".equals(configuration.getDescription()))
        {
            configuration.setDescription(null);
        }

        configuration.getParameters().forEach(configurationParameter->
        {
            if("".equals(configurationParameter.getValue()))
            {
                configurationParameter.setValue(null);
            }

            // if value !=null serialiser is used to convert value to byte
            // this is mutating original object
            // we going place the values from before serialisation before returning the object
            if(configurationParameter.getValue()!=null)
            {
                byte[] bytes = serialiser.serialise(configurationParameter.getValue());
                configurationParameter.setValue(bytes);
            }

            if("".equals(configurationParameter.getDescription()))
            {
                configurationParameter.setDescription(null);
            }
        });

        // hibernate mutates the object and amends configurations Params with Id
        getHibernateTemplate().saveOrUpdate(configuration);


    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#deleteConfiguration(org.ikasan.framework.configuration.window.Configuration)
     */
    public void delete(Configuration<List<ConfigurationParameter>> configuration)
    {
        getHibernateTemplate().delete(configuration);
    }
}
