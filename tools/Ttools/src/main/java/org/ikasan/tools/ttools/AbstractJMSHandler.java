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
 * Free Software Foundation Europe e.V. Talstraße 110, 40217 Düsseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.tools.ttools;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;

import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.security.AuthenticationPolicy;
import org.ikasan.common.security.AuthenticationPolicyFactory;
import org.ikasan.common.security.CredentialFactory;
import org.ikasan.common.security.IkasanPasswordCredential;
import org.ikasan.common.security.PolicyConfigurator;

/**
 * Abstract class grouping all common aspects of the standalone
 * JMS interaction.
 *
 * @author Ikasan Development Team
 */
public abstract class AbstractJMSHandler { 

    /** The context */
    protected Context ctx;
    
    /** Resource bundle with the connection details */
    protected ResourceBundle bundle;
    
    /** The connection factory */
    protected String connectionFactory;
    
    /**
     * Initialises the JMS handler.
     * 
     * @param properties 
     * @throws NamingException
     */
    protected void init(final Properties properties) 
        throws NamingException
    {
        // create a string array from the properties name:value pairs
        int arraySize = properties.size() * 2;
        String[] args = new String[ arraySize ];

        try
        {
            Enumeration<?> propertyNames = properties.propertyNames();
    
            for(int i=0; propertyNames.hasMoreElements(); i++) 
            {
                String propName = (String)propertyNames.nextElement();
                String propValue = (String)properties.get(propName);
                args[i] = propName;
                args[++i] = propValue;
            }
        }
        catch(ClassCastException e)
        {
            throw new CommonRuntimeException("Properties must be of type String", e);
        }
        
        this.init(args);
    }

    /**
     * Initialises the JMS handler.
     * 
     * @param args 
     * @throws NamingException
     */
    protected void init(String[] args) 
        throws NamingException
    {
        // Set the default of the properties file(jmsTools.properties)
        //Initialise using user arguments
        JMSToolsUtils.init(args);
        
        this.ctx = JMSToolsUtils.getContext();
        this.bundle = JMSToolsUtils.getBundle();
        connectionFactory = bundle.getString(JMSConstants.APP_SERVER_CONNECTIONFACTORY_JNDI_KEY);
    }

    /**
     * Abstract getConnection to create an authenticated connection if secured,
     * or an unauthenticated connection if not secured.
     * 
     * @param requiresAuthentication 
     * @return A connection
     * 
     * @throws NamingException
     * @throws JMSException
     */
    protected Connection getConnection(final boolean requiresAuthentication)
        throws NamingException, JMSException
    {
        ConnectionFactory factory = 
            (ConnectionFactory) this.ctx.lookup(connectionFactory);

        if(requiresAuthentication)
        {
            // create standard credentials
            IkasanPasswordCredential ipc = getCredential();
            return factory.createConnection(ipc.getUsername(), ipc.getPassword());
        }
        // Default else
        return factory.createConnection();
    }
    
    /**
     * Create a standard Ikasan Password credential instance. 
     * If no policy name exists then assume password is clear text, otherwise,
     * apply the policy to decode and return the decoded password.
     * @return IkasanPasswordCredential or null
     */
    protected IkasanPasswordCredential getCredential()
    {
        IkasanPasswordCredential ipc = 
            CredentialFactory.getPasswordCredential(JMSToolsUtils.getUsername(), 
                    JMSToolsUtils.getPassword());

        // if no authentication policy then just return the ipc
        if(JMSToolsUtils.getPolicyName() == null)
            return ipc; 

        AuthenticationPolicy ap = 
            AuthenticationPolicyFactory.getAuthenticationPolicy(JMSToolsUtils.getPolicyName());

        // we have a policy name so lets deal with it
        try
        {
            PolicyConfigurator policyConfig = new PolicyConfigurator();
            policyConfig.initPolicy(ap, ipc);
            ipc.setPassword( policyConfig.decode(ipc.getPassword()) );
        }
        catch(Exception e)
        {
            throw new CommonRuntimeException(e);
        }

        return ipc;
    }
    
}
