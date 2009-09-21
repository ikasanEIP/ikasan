/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.connector.util.chunking.model.dao;

import java.util.List;

import javax.resource.ResourceException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.ikasan.connector.ConnectorContext;
import org.ikasan.connector.ResourceLoader;
import org.springframework.beans.factory.FactoryBean;

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
