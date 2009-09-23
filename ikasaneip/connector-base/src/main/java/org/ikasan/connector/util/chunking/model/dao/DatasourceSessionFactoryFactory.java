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
package org.ikasan.connector.util.chunking.model.dao;

import java.util.List;

import javax.resource.ResourceException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.FactoryBean;

import org.ikasan.connector.ConnectorContext;
import org.ikasan.connector.ResourceLoader;

/**
 * 
 * A factory class for instantiating Hibernate SessionFactories on demand
 * 
 * The configuration for the session factories is currently programatically
 * hardcoded internal to this class
 * 
 * @author Ikasan Development Team
 * 
 */
public class DatasourceSessionFactoryFactory implements FactoryBean
{

    /** Connector context is hidden behind an interface */
    protected ConnectorContext context = ResourceLoader.getInstance().newContext();

    /**
     * JNDI path to the datasource
     */
    private String datasourceJNDIPath;

    /**
     * List of fully qualified path names for mapped classes
     */
    private List<String> mappedClassnames;

    /**
     * Constructor
     */
    public DatasourceSessionFactoryFactory()
    {
        // Empty constructor
    }

    /*
     * (non-Javadoc) Instantiates Hibernate SessionFactory, using the datasource
     * configured through JNDI and attempting to map the classes enumerated in
     * the mappedClasses property.
     * 
     * All other configuration is hardcoded internal to this method. This could
     * be externalised if/when such functionality or additional configurability
     * is required
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception
    {
        SessionFactory sessionFactory = null;
        try
        {
            Configuration cfg = new Configuration();
            for (String className : mappedClassnames)
            {
                Class<?> clazz = Class.forName(className);
                cfg.addClass(clazz);
            }
            cfg.setProperty("hibernate.connection.datasource", datasourceJNDIPath);
            cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.SybaseDialect");
            cfg.setProperty("hibernate.current_session_context_class", "thread");
            cfg.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
            sessionFactory = cfg.buildSessionFactory();
        }
        catch (ClassNotFoundException e)
        {
            throw new ResourceException(e);
        }

        return sessionFactory;
    }

    public Class<?> getObjectType()
    {
        return SessionFactory.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    // /**
    // * Setter method by which the JNDI path to the underlying datasourc is
    // supplied
    // * @return datasourceJNDIPath
    // */
    // public String getDatasourceJNDIPath()
    // {
    // return datasourceJNDIPath;
    // }

    /**
     * Setter method by which the JNDI path to the underlying datasource is
     * supplied
     * 
     * @param datasourceJNDIPath
     */
    public void setDatasourceJNDIPath(String datasourceJNDIPath)
    {
        this.datasourceJNDIPath = datasourceJNDIPath;
    }

    // public List<String> getMappedClassnames()
    // {
    // return mappedClassnames;
    // }

    /**
     * Setter method by which the JNDI path to the underlying datasource is
     * supplied
     * 
     * @param mappedClassnames
     */
    public void setMappedClassnames(List<String> mappedClassnames)
    {
        this.mappedClassnames = mappedClassnames;
    }
}
