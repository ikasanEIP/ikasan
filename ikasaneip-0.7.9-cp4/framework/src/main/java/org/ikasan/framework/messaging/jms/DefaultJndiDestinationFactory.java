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
