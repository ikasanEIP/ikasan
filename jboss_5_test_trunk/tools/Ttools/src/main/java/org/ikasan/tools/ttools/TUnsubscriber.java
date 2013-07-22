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
