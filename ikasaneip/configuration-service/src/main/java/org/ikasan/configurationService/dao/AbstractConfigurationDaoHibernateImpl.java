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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.ikasan.configurationService.model.DefaultConfiguration;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationParameter;

import java.util.List;

/**
 * Implementation of the ConfigurationDao interface providing
 * the Hibernate persistence for configuration instances.
 * 
 * @author Ikasan Development Team
 */
public abstract class AbstractConfigurationDaoHibernateImpl implements ConfigurationDao<List<ConfigurationParameter>>
{
    public abstract EntityManager getEntityManager();

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#findConfiguration(java.lang.String)
     */
    public Configuration findByConfigurationId(String configurationId)
    {
        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Configuration> criteriaQuery = builder.createQuery(Configuration.class);
        Root<DefaultConfiguration> root = criteriaQuery.from(DefaultConfiguration.class);


        criteriaQuery.select(root)
            .where(builder.equal(root.get("configurationId"),configurationId));

        TypedQuery<Configuration> query = this.getEntityManager().createQuery(criteriaQuery);
        List<Configuration> list = query.getResultList();
        if(list!=null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#saveConfiguration(org.ikasan.framework.configuration.window.Configuration)
     */
    public void save(Configuration<List<ConfigurationParameter>> configuration)
    {
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


            if("".equals(configurationParameter.getDescription()))
            {
                configurationParameter.setDescription(null);
            }
        });

        // hibernate mutates the object and amends configurations Params with Id
        this.getEntityManager().persist(this.getEntityManager().contains(configuration)
            ? configuration : this.getEntityManager().merge(configuration));
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#deleteConfiguration(org.ikasan.framework.configuration.window.Configuration)
     */
    public void delete(Configuration<List<ConfigurationParameter>> configuration)
    {
        this.getEntityManager().remove(this.getEntityManager().contains(configuration)
            ? configuration : this.getEntityManager().merge(configuration));
    }
}
