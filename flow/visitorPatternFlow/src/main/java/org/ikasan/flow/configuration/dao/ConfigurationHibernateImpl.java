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
package org.ikasan.flow.configuration.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.flow.configuration.model.Configuration;
import org.ikasan.flow.configuration.model.ConfigurationParameter;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Implementation of the ConfigurationDao interface providing
 * the Hibernate persistence for configuration instances.
 * 
 * @author Ikasan Development Team
 */
public class ConfigurationHibernateImpl extends HibernateDaoSupport 
    implements ConfigurationDao
{
    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#findConfiguration(java.lang.String)
     */
    public Configuration findById(String configurationId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(Configuration.class);
        criteria.add(Restrictions.eq("configurationId", configurationId));

        List<Configuration> configuration = getHibernateTemplate().findByCriteria(criteria);
        if(configuration == null || configuration.size() == 0)
        {
            return null;
        }

        return configuration.get(0);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#saveConfiguration(org.ikasan.framework.configuration.model.Configuration)
     */
    public void save(Configuration configuration)
    {
        // work-around for Sybase issue where it converts empty strings to single spaces.
        // See http://open.jira.com/browse/IKASAN-520
        // Where we would have persisted "" change this to a null to stop Sybase
        // inserting a single space character.
        if("".equals(configuration.getDescription()))
        {
            configuration.setDescription(null);
        }
        for(ConfigurationParameter configurationParameter:configuration.getConfigurationParameters())
        {
            if("".equals(configurationParameter.getValue()))
            {
                configurationParameter.setValue(null);
            }

            if("".equals(configurationParameter.getDescription()))
            {
                configurationParameter.setDescription(null);
            }
        }
        
        getHibernateTemplate().saveOrUpdate(configuration);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#deleteConfiguration(org.ikasan.framework.configuration.model.Configuration)
     */
    public void delete(Configuration configuration)
    {
        getHibernateTemplate().delete(configuration);
    }

}
