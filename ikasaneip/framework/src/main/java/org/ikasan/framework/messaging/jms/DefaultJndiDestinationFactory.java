/*
 * $Id: 
 * $URL:
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
package org.ikasan.framework.messaging.jms;

import java.util.Hashtable;
import java.util.Map;

import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * Default implementation of <code>JndiDestinationFactory</code>
 * 
 * Attempts to provide the <code>Destination</code> implied through configuration.
 * 
 * Allows lookup to return cached instance if exists, and may also attempt an initial lookup on start
 * 
 * @author Ikasan Development Team
 *
 */
public class DefaultJndiDestinationFactory implements JndiDestinationFactory

{
    /**
     * Logger instance
     */
    private static final Logger logger = Logger.getLogger(DefaultJndiDestinationFactory.class);
    
    /**
     * jndiName of the desired <code>Destination</code>
     */
    private String jndiName;
    /**
     * environment parameters for creating the <code>InitialContext</code>
     */
    private Hashtable<String, String>environment;
    
    /**
     * Constructor 
     * 
     * @param jndiName - jndiName of the desired <code>Destination</code>
     * @param environment - environment parameters for creating the <code>InitialContext</code>
     */
    public DefaultJndiDestinationFactory(String jndiName, Map<String, String> environment)
    {
        super();
        logger.info("constructor called with environment:"+environment);
        this.jndiName = jndiName;
        this.environment = new Hashtable<String, String>(environment);
        
    }
    
    /**
     * Cached instance of the target <code>Destination</code>
     */
    private Destination destination = null;
    
    /**
     * Constructor 
     * 
     * @param jndiName - jndiName of the desired <code>Destination</code>
     * @param environment - environment parameters for creating the <code>InitialContext</code>
     */
    public DefaultJndiDestinationFactory(String jndiName, Map<String, String> environment, boolean lookupOnCreation)
    {
        this(jndiName, environment);
        
        if (lookupOnCreation){
            try{
                getDestination(false);
            } catch(NamingException namingException){
                logger.warn("failed to find Destination on creation. "+namingException.getMessage());
            }
        }
    }
 
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.messaging.jms.JndiDestinationFactory#getDestination(boolean)
     */
    public Destination getDestination(boolean allowCachedResult) throws NamingException
    {
        if (destination==null||!allowCachedResult){
            Context context = new InitialContext(environment);
            destination = (Destination) context.lookup(jndiName);
        }

        return destination;
    }
    
    /**
     * Accessor for environment
     * 
     * @return environment
     */
    public Map<?,?> getEnvironment(){
        return environment;
    }
    
    /**
     * Accessor for jndiName
     * 
     * @return jndiName
     */
    public String getJndiName(){
        return jndiName;
    }

}
