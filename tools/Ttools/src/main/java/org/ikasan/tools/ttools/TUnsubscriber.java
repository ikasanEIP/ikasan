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
package org.ikasan.tools.ttools;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.jms.Session;
import org.apache.log4j.Logger;

/**
 * This is a standalone client for unsubscribing 
 * durable subscriptions from JMS Topics
 * 
 * Ensure the classpath is as follows
 * CLASSPATH=$JBOSS_HOME/client/jboss-common-client.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/client/commons-logging.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/client/jboss-messaging-client.jar
 * 
 * Usage: deregister (unsubscribe) durable subscriptions
 *      args must have:
 *          -clientid <client-id>
 *          -subscriptionname <subscription-name>
 *          -durable
 *          
 * @author Ikasan Development Team
 */
public class TUnsubscriber 
    extends AbstractJMSHandler
{ 

    /** Logger for this class */
    private static final Logger logger = Logger.getLogger(TUnsubscriber.class);
    
    /**
     * Main method, kicks off an unsubscription.
     *
     * @param args
     * @throws NamingException
     */
    public static void main(String[] args)
        throws NamingException 
    {
        TUnsubscriber unsubscriber = new TUnsubscriber();
        //Initialise the unsubscriber instance
        unsubscriber.init(args);
        //Unsubscribe
        unsubscriber.unsubscribe();
    }

    /**
     * Unsubscribe from a message queue
     */
    public void unsubscribe()
    {
        Session session = null;
        Connection con = null;
        String clientID = JMSToolsUtils.getClientID();
        String subscriptionName = JMSToolsUtils.getSubscriptionName();
        
        try {
//            Context ctx = JMSToolsUtils.getContext();
//            ResourceBundle bundle = JMSToolsUtils.getBundle();
//            String connectionFactory = bundle.getString(JMSConstants.APP_SERVER_CONNECTIONFACTORY_JNDI_KEY);
            
            if (!JMSToolsUtils.isQueue())
            {
                //Only durable subscriptions can be unsubscribed
                //Removes ALL subscriptions with ID clientID.subsciptionName from any topic.
                //According to JMS specification:
                //It is erroneous for a client to delete a durable subscription while there is an 
                //active TopicSubscriber for the subscription, or while a consumed message is part 
                //of a pending transaction or has not been acknowledged in the session.
                //A JMSException will be thrown
//                TopicConnectionFactory fac =(TopicConnectionFactory) ctx.lookup(connectionFactory);
//
//                if(JMSToolsUtils.isAuthenticated())
//                {
//                    // create standard credentials
//                    IkasanPasswordCredential ipc = getCredential();
//                    con = fac.createTopicConnection(ipc.getUsername(), ipc.getPassword());
//                }
//                else
//                    con = fac.createTopicConnection();
//
                con = getConnection(JMSToolsUtils.isAuthenticated());

                con.setClientID(clientID);
                session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
                session.unsubscribe(subscriptionName);
                logger.info(" Durable subscription ["+clientID +"."+subscriptionName+"] unsubscribed.");  //$NON-NLS-1$
            }
        }
        catch (JMSException e)
        {
            logger.fatal("Caught JMSException: [" + e.getMessage() + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        } 
        catch (NamingException e)
        {
            logger.fatal("Caught NamingException: [" + e.getMessage() + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
        finally
        {
            //close the connection to topic when no further messages are to be received.
            try
            { 
                if (con != null)
                {
                    con.close();
                    logger.info(" Closed Connection"); //$NON-NLS-1$
                } else
                {
                    logger.info(" Connection was null, therefore already closed."); //$NON-NLS-1$
                }
            }
            catch (Exception e)
            {
                logger.fatal("Caught exception: [" + e.getMessage() + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
    
}
