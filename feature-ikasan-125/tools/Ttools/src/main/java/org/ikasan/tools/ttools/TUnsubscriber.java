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
 * This is a standalone client for unsubscribing durable subscriptions from JMS Topics
 * 
 * Ensure the classpath is as follows CLASSPATH=$JBOSS_HOME/client/jboss-common-client.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/client/commons-logging.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/client/jboss-messaging-client.jar
 * 
 * Usage: deregister (unsubscribe) durable subscriptions args must have: -clientid <client-id> -subscriptionname
 * <subscription-name> -durable
 * 
 * @author Ikasan Development Team
 */
public class TUnsubscriber extends AbstractJMSHandler
{
    /** Logger for this class */
    private static final Logger logger = Logger.getLogger(TUnsubscriber.class);

    /**
     * Main method, kicks off an un-subscription.
     * 
     * @param args
     * @throws NamingException
     */
    public static void main(String[] args) throws NamingException
    {
        TUnsubscriber unsubscriber = new TUnsubscriber();
        unsubscriber.init(args);
        unsubscriber.unsubscribe();
    }

    /**
     * Un-subscribe from a message queue/topic
     */
    public void unsubscribe()
    {
        Session session = null;
        Connection connection = null;
        String clientID = JMSToolsUtils.getClientID();
        String subscriptionName = JMSToolsUtils.getSubscriptionName();
        try
        {
            if (!JMSToolsUtils.isQueue())
            {
                boolean authenticated = JMSToolsUtils.isAuthenticated();
                connection = getConnection(authenticated);
                connection.setClientID(clientID);
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                session.unsubscribe(subscriptionName);
                logger.info(" Durable subscription [" + clientID + "." + subscriptionName + "] unsubscribed.");
            }
        }
        catch (JMSException e)
        {
            logger.fatal("Caught JMSException: [" + e.getMessage() + "]", e);
        }
        catch (NamingException e)
        {
            logger.fatal("Caught NamingException: [" + e.getMessage() + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
        finally
        {
            try
            {
                if (connection != null)
                {
                    connection.close();
                    logger.info(" Closed Connection");
                }
                else
                {
                    logger.info(" Connection was null, therefore is already closed.");
                }
            }
            catch (Exception e)
            {
                logger.warn("Caught exception: [" + e.getMessage() + "] trying to close the connection", e);
            }
        }
    }
}
