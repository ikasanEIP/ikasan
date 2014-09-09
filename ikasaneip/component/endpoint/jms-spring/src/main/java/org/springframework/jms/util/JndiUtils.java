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
package org.springframework.jms.util;

import org.ikasan.component.endpoint.jms.AuthenticatedConnectionFactory;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.NamingException;
import java.util.Map;
import java.util.Properties;

/**
 * Utilities for accessing resources from the JNDI via Spring.
 *
 * @author Ikasan Development Team
 */
public class JndiUtils
{

    /**
     * Get an instance of a Destination
     * @param jndiProperties
     * @param destinationJndiName
     * @return
     */
    public static Destination getDestination(Map<String,String> jndiProperties, String destinationJndiName)
    {
        JndiTemplate jndiTemplate = getJndiTemplate(jndiProperties);
        JndiObjectFactoryBean jndiObjectFactoryBean = getObjectFactoryInstance(jndiTemplate, destinationJndiName);
        Object jndiObject = jndiObjectFactoryBean.getObject();
        if(jndiObject instanceof Destination)
        {
            return (Destination)jndiObject;
        }

        throw new RuntimeException("Expected a Destination class, Found a [" + jndiObject.getClass().getName() + "]");
    }

    /**
     * Get an instance of an authenticated connection factory
     * @param jndiProperties
     * @param connectionFactoryName
     * @param username
     * @param password
     * @return
     */
    public static ConnectionFactory getAuthenicatedConnectionFactory(Map<String,String> jndiProperties, String connectionFactoryName, String username, String password)
    {
        ConnectionFactory connectionFactory = getConnectionFactory(jndiProperties, connectionFactoryName);
        AuthenticatedConnectionFactory authenticatedConnectionFactory = new AuthenticatedConnectionFactory();
        authenticatedConnectionFactory.setConnectionFactory(connectionFactory);
        authenticatedConnectionFactory.setUsername(username);
        authenticatedConnectionFactory.setPassword(password);
        return authenticatedConnectionFactory;
    }

    /**
     * Get an instance of a connection factory
     * @param jndiProperties
     * @param connectionFactoryName
     * @return
     */
    public static ConnectionFactory getConnectionFactory(Map<String,String> jndiProperties, String connectionFactoryName)
    {
        JndiTemplate jndiTemplate = getJndiTemplate(jndiProperties);
        JndiObjectFactoryBean jndiObjectFactoryBean = getObjectFactoryInstance(jndiTemplate, connectionFactoryName);
        Object jndiObject = jndiObjectFactoryBean.getObject();
        if(jndiObject instanceof ConnectionFactory)
        {
            return (ConnectionFactory) jndiObject;
        }

        throw new RuntimeException("Expected a ConnectionFactory class, Found a [" + jndiObject.getClass().getName() + "]");
    }


    /**
     * Get an instance of a JNDI Object Factory bean
     * @param jndiTemplate
     * @param jndiObjectName
     * @return
     */
    protected static JndiObjectFactoryBean getObjectFactoryInstance(JndiTemplate jndiTemplate, String jndiObjectName)
    {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiTemplate(jndiTemplate);
        jndiObjectFactoryBean.setJndiName(jndiObjectName);

        try
        {
            jndiObjectFactoryBean.afterPropertiesSet();
            return jndiObjectFactoryBean;
        }
        catch(NamingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return an instance of a jndiTemplate from a given map of properties.
     * If the map is null or empty then return the default jndiTemplate.
     * @param map
     * @return
     */
    protected static JndiTemplate getJndiTemplate(Map<String,String> map)
    {
        if(map == null || map.size() == 0)
        {
            return new org.springframework.jndi.JndiTemplate();
        }

        Properties properties = new Properties();
        properties.putAll(map);
        JndiTemplate jndiTemplate = new org.springframework.jndi.JndiTemplate();
        jndiTemplate.setEnvironment(properties);
        return jndiTemplate;
    }
}
