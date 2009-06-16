package org.ikasan.framework.messaging.jms;

import java.util.Map;

import javax.jms.Destination;
import javax.naming.NamingException;

/**
 * Interface definition for factory bean that provides runtime JNDI lookup for <code>SDestination</code>s
 * 
 * @author Ikasan Development Team
 *
 */
public interface JndiDestinationFactory
{
    /**
     * Retrieves the configured <code>Destination</code> if possible
     *
     * @param allowCachedResult - allows the implementation to return a previous result if one exists
     * @return <code>Destination</code> specified by configured jndiName and server
     * 
     * @throws NamingException
     */
    public Destination getDestination(boolean allowCachedResult) throws NamingException;
    
    /**
     * Accessor for environment
     * 
     * @return environment
     */
    public Map<?,?> getEnvironment();
    
    /**
     * Accessor for jndiName
     * 
     * @return jndiName
     */
    public String getJndiName();
    
}
