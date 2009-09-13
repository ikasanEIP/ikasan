/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common;

import javax.naming.NamingException;

/**
 * Common context provides the base interface for all context access.
 * 
 * @author Ikasan Development Team
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