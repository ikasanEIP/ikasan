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
package org.ikasan.common;

import javax.naming.NamingException;

/**
 * Common context provides the base interface for all context access.
 * 
 * @author Jeff Mitchell
 */  
public interface CommonContext
{ 
    /** endpoint prefix */
    public String ENDPOINT_NAME_PREFIX = "jboss.j2ee:binding=message-inflow-driven-bean,jndiName=";
    /** endpoint suffix */
    public String ENDPOINT_NAME_SUFFIX = ",plugin=invoker,service=EJB";
    /** stop method */
    public String STOP_METHOD = "stop";
    /** destroy method */
    public String DESTROY_METHOD = "destroy";
    
    /** standard location of the Transaction Manager */
    public String TRANSACTION_MANAGER =
        ResourceLoader.getInstance().getProperty("transaction.manager"); //$NON-NLS-1$
    /** standard location of the User Transaction */
    public String USER_TRANSACTION =
        ResourceLoader.getInstance().getProperty("user.transaction"); //$NON-NLS-1$
    
    /** standard JNDI for non-managed clients */
    public final String JAVA_NAMING_FACTORY_INITIAL_CONST = "java.naming.factory.initial"; //$NON-NLS-1$
    /** Initial Naming Factory */
    public String JAVA_NAMING_FACTORY_INITIAL =
        ResourceLoader.getInstance().getProperty(JAVA_NAMING_FACTORY_INITIAL_CONST); //$NON-NLS-1$

    /** standard JNDI for factory of URL packages */
    public final String JAVA_NAMING_FACTORY_URL_PKGS_CONST = "java.naming.factory.url.pkgs";
    /** Initial Factory for URL Packages */
    public String JAVA_NAMING_FACTORY_URL_PKGS =
        ResourceLoader.getInstance().getProperty(JAVA_NAMING_FACTORY_URL_PKGS_CONST); //$NON-NLS-1$

    /** JNDI URL prefix constant */
    public final String JAVA_NAMING_PROVIDER_URL_PREFIX_CONST = "java.naming.provider.url.prefix";
    /** Initial JNDI URL prefix */
    public String JAVA_NAMING_PROVIDER_URL_PREFIX =
        ResourceLoader.getInstance().getProperty(JAVA_NAMING_PROVIDER_URL_PREFIX_CONST); //$NON-NLS-1$

    /** standard location of the Global JMS connection factory */
    public String GLOBAL_JMS_CONNECTION_FACTORY =
        ResourceLoader.getInstance().getProperty("global.jms.connectionFactory"); //$NON-NLS-1$
    /** standard location of the Global XA JMS connection factory */
    public String GLOBAL_JMS_XA_CONNECTION_FACTORY =
        ResourceLoader.getInstance().getProperty("global.jms.xa.connectionFactory"); //$NON-NLS-1$

    /**
     * Lookup the objectName from the given object context
     * @param object
     * @param objectName
     * @return referenced object
     * @throws NamingException
     */
    public Object lookup(Object object, String objectName)
        throws NamingException;

    /**
     * Lookup the objectName from the default context
     * @param objectName
     * @return Object bound to this name
     * @throws NamingException
     */
    public Object lookup(String objectName)
        throws NamingException;

    /**
     * Bind the objectValue to the given objectName in the default context
     * @param objectName
     * @param objectValue
     * @throws NamingException
     */
    public void bind(String objectName, Object objectValue)
        throws NamingException;

    /**
     * Bind the objectValue to the given objectName in the given context
     * @param object
     * @param objectName
     * @param objectValue
     * @throws NamingException
     */
    public void bind(Object object, String objectName, Object objectValue)
        throws NamingException;

    /**
     * Unbind the objectName from the default context
     * @param objectName
     * @throws NamingException
     */
    public void unbind(String objectName)
        throws NamingException;

    /**
     * Unbind the objectName from the given context
     * @param object
     * @param objectName
     * @throws NamingException
     */
    public void unbind(Object object, String objectName)
        throws NamingException;

    /**
     * Rebind the objectValue as the objectName in the default context
     * @param objectName
     * @param objectValue
     * @throws NamingException
     */
    public void rebind(String objectName, Object objectValue)
        throws NamingException;

    /**
     * Rebind the objectValue as the objectName in the given context
     * @param object
     * @param objectName
     * @param objectValue
     * @throws NamingException
     */
    public void rebind(Object object, String objectName, Object objectValue)
        throws NamingException;
}