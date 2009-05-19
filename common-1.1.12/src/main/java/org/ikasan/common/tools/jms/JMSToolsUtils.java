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
package org.ikasan.common.tools.jms;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.InitialContext;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import org.ikasan.common.component.Encoding;
import org.ikasan.common.component.Spec;

/**
 * This is a utility class to aid JMS tools
 * 
 * @author Martijn Verburg
 */
public class JMSToolsUtils { 

    /** Logger for this class */
    private static Logger logger = Logger.getLogger(JMSToolsUtils.class);
    
    /** The resource bundle representing the properties */
    private static ResourceBundle bundle = null;
    
    /** The properties file */
    private static String propertiesFile = null;
    
    /** The name of the server instance */
    private static String serverInstanceName = null;
    
    /** The port */
    private static String port = null;

    /** The topic name */
    private static String topicName = null;

    /** The file name that holds the event we're publishing */
    private static String eventsFile = null;
    
    /** The payload name we're publishing */
    private static String payloadName = null;
    
    /** The srcSystem of the payload */
    private static String payloadSrcSystem = null;
    
    /** The payload encoding */
    private static String payloadEncoding = Encoding.NOENC.toString();
    
    /** The payload spec */
    private static String payloadSpec = Spec.TEXT_XML.toString();
    
    /** The number of times to publish this payload */
    private static int publishNumber = 1;
    
    /** Whether or not we're actually talking to a queue */
    private static boolean isQueue = false;
    
    /** The JMS Client ID*/
    private static String clientID = null;
    
    /** The subscriber's name*/
    private static String subscriptionName = null;
    
    /** Whether the subscriber to be created is durable or not */
    private static boolean isDurable = false;
    
    /** username for secured JMS */
    private static String username;
    
    /** password for secured JMS */
    private static String password;
    
    /** authentication policy name for secured JMS */
    private static String policyName;
    
    /** does the JMS server require authentication credentials */
    private static boolean isAuthenticated;
    
    /** subscriber wait time - default of no waiting */
    private static int wait = JMSConstants.NO_WAIT;
    
    /** default - publish as a map message */
    private static String jmsMsgType = JMSConstants.JMS_MAP_MESSAGE_TYPE;
    
    /** content to publish from command line param */
    private static String payloadContent;
    
    /**
     * Initialise the Utils
     * @param args The command line args
     */
    public static void init(String[] args)
    {
        propertiesFile = JMSConstants.DEFAULT_PROPERTIES_FILE;
        // Read in the args to get the properties file if its there
        processArgs(args);
        // Prepare the resource bundle
        prepareResourceBundle();
        // Override any properties from the command line
        processProperties();
        // Re-read the args to over rdie the properties 
        processArgs(args);
        // Log the values after all of the processing
        checkRuntimeValues();
        // Start a publisher instance and publish a message
    }
    
    /**
     * prepare the resource bundle
     */
    public static void prepareResourceBundle()
    {
        Locale locale = Locale.getDefault();
        bundle = (ResourceBundle.getBundle(propertiesFile, locale));
    }
    
    /**
     * Process the properties in the runtime properties file
     */
    public static void processProperties()
    {
        serverInstanceName = bundle.getString(JMSConstants.SERVER_INSTANCE_NAME);
        port = bundle.getString(JMSConstants.SERVER_INSTANCE_PORT);
    }
    
    /**
     * This method parses the arguments passed in from the command line and
     * creates appropriate hooks for accessing the values.
     * 
     * @param args
     */
    public static void processArgs(String args[])
    {
        Options opt = new Options();
        opt.addOption("h", false, "Help"); //$NON-NLS-1$//$NON-NLS-2$
        opt.addOption(JMSConstants.PROPERTIES_ARG, true, "Name (without .properties extension) of the jms tool properties file."); //$NON-NLS-1$        
        opt.addOption(JMSConstants.SERVER_INSTANCE_NAME_ARG, true, "Name of the Server Instance."); //$NON-NLS-1$
        opt.addOption(JMSConstants.PORT_ARG, true, "Name of the Port."); //$NON-NLS-1$
        opt.addOption(JMSConstants.TOPIC_NAME_ARG, true, "Name of the Topic/Queue."); //$NON-NLS-1$
        opt.addOption(JMSConstants.EVENTS_FILE_NAME_ARG, true, "Name of the file that holds the event we're publishing."); //$NON-NLS-1$
        opt.addOption(JMSConstants.PAYLOAD_NAME_ARG, true, "Name of the payload."); //$NON-NLS-1$
        opt.addOption(JMSConstants.PAYLOAD_ENCODING_ARG, true, "Encoding of the payload."); //$NON-NLS-1$
        opt.addOption(JMSConstants.PAYLOAD_SPEC_ARG, true, "Spec of the payload."); //$NON-NLS-1$
        opt.addOption(JMSConstants.PAYLOAD_SRC_SYSTEM_ARG, true, "Source system of the payload."); //$NON-NLS-1$
        opt.addOption(JMSConstants.PAYLOAD_CONTENT_ARG, true, "Content of the payload to publish."); //$NON-NLS-1$
        opt.addOption(JMSConstants.PUBLISH_NUMBER_ARG, true, "Number of times to publish the payload."); //$NON-NLS-1$
        opt.addOption(JMSConstants.QUEUE_ARG, false, "Whether we're actually talking to a Queue."); //$NON-NLS-1$
        opt.addOption(JMSConstants.CLIENT_ID_ARG, true, "Client ID");//$NON-NLS-1$
        opt.addOption(JMSConstants.SUBSCRIPTION_NAME_ARG, true, "Subscription name");//$NON-NLS-1$
        opt.addOption(JMSConstants.DURABLE_ARG, false, "Whether the subscriber to be created is durable or not.");//$NON-NLS-1$
        opt.addOption(JMSConstants.USERNAME_ARG, true, "Username for secured JMS");//$NON-NLS-1$
        opt.addOption(JMSConstants.PASSWORD_ARG, true, "Password for secured JMS");//$NON-NLS-1$
        opt.addOption(JMSConstants.POLICY_NAME_ARG, true, "Authentication policy name for secured JMS");//$NON-NLS-1$
        opt.addOption(JMSConstants.AUTHENTICATE_ARG, false, "Whether this client must provide authentication details to the JMS server or not.");//$NON-NLS-1$
        opt.addOption(JMSConstants.WAIT_TIME_ARG, true, "Subscribers only. Defines the wait period for subscription. '-1'=noWait; '0'=waitForever; any other number details millis to wait.");//$NON-NLS-1$
        opt.addOption(JMSConstants.JMS_MAP_MESSAGE_TYPE, false, "Publish as a JMS Map Message.");//$NON-NLS-1$
        opt.addOption(JMSConstants.JMS_TEXT_MESSAGE_TYPE, false, "Publish as a JMS Text Message.");//$NON-NLS-1$
        BasicParser parser = new BasicParser();
        
        CommandLine cl = null;
        try
        {
            cl = parser.parse(opt, args);
            if (cl.hasOption('h'))
            {
                HelpFormatter f = new HelpFormatter();
                f.printHelp("TPublisher", opt); //$NON-NLS-1$
                System.exit(0);
            }
            else
            {
                if (cl.hasOption(JMSConstants.PROPERTIES_ARG))
                {
                    propertiesFile = cl.getOptionValue(JMSConstants.PROPERTIES_ARG);
                }
                if (cl.hasOption(JMSConstants.SERVER_INSTANCE_NAME_ARG))
                {
                    serverInstanceName = cl.getOptionValue(JMSConstants.SERVER_INSTANCE_NAME_ARG);
                }
                if (cl.hasOption(JMSConstants.PORT_ARG))
                {
                    port = cl.getOptionValue(JMSConstants.PORT_ARG);
                }
                // Topic has to be specified
                topicName = cl.getOptionValue(JMSConstants.TOPIC_NAME_ARG);
                // TODO split this from subscriber work
                if (cl.hasOption(JMSConstants.EVENTS_FILE_NAME_ARG))
                {
                    eventsFile = cl.getOptionValue(JMSConstants.EVENTS_FILE_NAME_ARG);
                }
                if (cl.hasOption(JMSConstants.PAYLOAD_NAME_ARG))
                {
                    payloadName = cl.getOptionValue(JMSConstants.PAYLOAD_NAME_ARG);
                }
                if (cl.hasOption(JMSConstants.PAYLOAD_ENCODING_ARG))
                {
                    payloadEncoding = cl.getOptionValue(JMSConstants.PAYLOAD_ENCODING_ARG);
                }
                if (cl.hasOption(JMSConstants.PAYLOAD_SPEC_ARG))
                {
                    payloadSpec = cl.getOptionValue(JMSConstants.PAYLOAD_SPEC_ARG);
                }
                if (cl.hasOption(JMSConstants.PAYLOAD_SRC_SYSTEM_ARG))
                {
                    payloadSrcSystem = cl.getOptionValue(JMSConstants.PAYLOAD_SRC_SYSTEM_ARG);
                }
                if (cl.hasOption(JMSConstants.PAYLOAD_CONTENT_ARG))
                {
                    payloadContent = cl.getOptionValue(JMSConstants.PAYLOAD_CONTENT_ARG);
                }
                if (cl.hasOption(JMSConstants.PUBLISH_NUMBER_ARG))
                {
                    String publishNumberStr = 
                        cl.getOptionValue(JMSConstants.PUBLISH_NUMBER_ARG);
                    try
                    {
                        publishNumber = Integer.parseInt(publishNumberStr);
                    }
                    catch(NumberFormatException e)
                    {
                        logger.fatal("PublishNumber arg [" + publishNumberStr  //$NON-NLS-1$
                                + "] must be numeric.", e); //$NON-NLS-1$
                        System.exit(-1);
                    }
                }
                if (cl.hasOption(JMSConstants.QUEUE_ARG))
                {
                    isQueue = true;
                }
                if (cl.hasOption(JMSConstants.CLIENT_ID_ARG))
                {
                    clientID = cl.getOptionValue(JMSConstants.CLIENT_ID_ARG);
                }
                if (cl.hasOption(JMSConstants.SUBSCRIPTION_NAME_ARG))
                {
                    subscriptionName = cl.getOptionValue(JMSConstants.SUBSCRIPTION_NAME_ARG);
                }
                if (cl.hasOption(JMSConstants.DURABLE_ARG))
                {
                    isDurable = true;
                }
                if (cl.hasOption(JMSConstants.AUTHENTICATE_ARG))
                {
                    isAuthenticated = true;

                    // supplementary authentication details
                    if (cl.hasOption(JMSConstants.POLICY_NAME_ARG))
                    {
                        policyName = cl.getOptionValue(JMSConstants.POLICY_NAME_ARG);
                    }
                    if (cl.hasOption(JMSConstants.USERNAME_ARG))
                    {
                        username = cl.getOptionValue(JMSConstants.USERNAME_ARG);
                    }
                    if (cl.hasOption(JMSConstants.PASSWORD_ARG))
                    {
                        password = cl.getOptionValue(JMSConstants.PASSWORD_ARG);
                    }
                }
                if (cl.hasOption(JMSConstants.WAIT_TIME_ARG))
                {
                    String waitStr = cl.getOptionValue(JMSConstants.WAIT_TIME_ARG);
                    try
                    {
                        wait = Integer.parseInt(waitStr);
                    }
                    catch(NumberFormatException e)
                    {
                        logger.error("Subcriber wait time [" + waitStr 
                                + "] must be an integer. "
                                + "Defaulting subscriber to [" 
                                + JMSConstants.NO_WAIT + "] no wait", e);
                        wait = JMSConstants.NO_WAIT;
                    }
                }
            }
            if (cl.hasOption(JMSConstants.JMS_TEXT_MESSAGE_TYPE))
                jmsMsgType = JMSConstants.JMS_TEXT_MESSAGE_TYPE;
            else
                jmsMsgType = JMSConstants.JMS_MAP_MESSAGE_TYPE;
        }
        catch (ParseException e)
        {
            logger.fatal("Failed to parse the command line args: [" + args + "]", e);  //$NON-NLS-1$//$NON-NLS-2$
            System.exit(-1);
        }
    }
    
    /**
     * Return a context
     * 
     * @return The Context
     * @throws NamingException
     */
    public static Context getContext()
            throws NamingException
    {
        Properties props = new Properties();
        props.put(JMSConstants.JAVA_NAMING_FACTORY_INITIAL, bundle
            .getString(JMSConstants.JAVA_NAMING_FACTORY_INITIAL));
        props.put(JMSConstants.JAVA_NAMING_FACTORY_URL_PKGS, bundle
            .getString(JMSConstants.JAVA_NAMING_FACTORY_URL_PKGS));
        String url = new String("jnp://" + serverInstanceName + ":" + port); //$NON-NLS-1$ //$NON-NLS-2$
        props.put(JMSConstants.JAVA_NAMING_PROVIDER_URL, url);
        logger.debug("Context is: " + url); //$NON-NLS-1$
        return new InitialContext(props);
    }
    
    /**
     * Logs the runtime values
     */
    public static void checkRuntimeValues()
    {
        if (propertiesFile == null)
        {
            logger.fatal("Properties File was null"); //$NON-NLS-1$
            System.exit(-1);
        }
        logger
            .info("Properties File [" + propertiesFile + "]."); //$NON-NLS-1$//$NON-NLS-2$

        if (serverInstanceName == null)
        {
            logger.fatal("Server Instance Name was null"); //$NON-NLS-1$
            System.exit(-1);
        }
        logger
            .info("Server Instance [" + serverInstanceName + "]."); //$NON-NLS-1$//$NON-NLS-2$

        
        if (port == null)
        {
            logger.fatal("Server Instance Port was null"); //$NON-NLS-1$
            System.exit(-1);
        }
        logger
            .info("Port [" + port + "]."); //$NON-NLS-1$//$NON-NLS-2$

        if (topicName == null)
        {
            logger.warn("Target Topic was null"); //$NON-NLS-1$
        }
        logger
            .info("Topic Name [" + topicName + "]."); //$NON-NLS-1$//$NON-NLS-2$

        if (eventsFile == null)
        {
            logger.warn("Events File was null"); //$NON-NLS-1$
        }
        logger
            .info("Events File Name [" + eventsFile + "]."); //$NON-NLS-1$//$NON-NLS-2$
        if (payloadName == null)
        {
            logger.warn("Payload name was null"); //$NON-NLS-1$
        }
        logger
            .info("Payload Name [" + payloadName + "]."); //$NON-NLS-1$//$NON-NLS-2$
        if (payloadEncoding == null)
        {
            logger.warn("Payload encoding was null"); //$NON-NLS-1$
        }
        logger
            .info("Payload Encoding [" + payloadEncoding + "]."); //$NON-NLS-1$//$NON-NLS-2$
        if (payloadSpec == null)
        {
            logger.warn("Payload spec was null"); //$NON-NLS-1$
        }
        logger.info("Payload Content [" + payloadContent + "].");
        logger.info("Client ID was [" + clientID + "].");
        logger.info("Subscription name was [" + subscriptionName +"].");
        logger.info("Durable [" + isDurable +"].");
        if (isDurable())
        {
            if (clientID == null || subscriptionName == null)
            {
                logger.fatal("clientID and/or subscriptionName must not be null when creating durable subscriber");
                System.exit(-1);
            }
        }
        logger.info("PublishNumber [" + publishNumber + "]."); //$NON-NLS-1$//$NON-NLS-2$
        logger.info("Payload Spec [" + payloadSpec + "]."); //$NON-NLS-1$//$NON-NLS-2$

        logger.info("Authentication [" + isAuthenticated + "]."); //$NON-NLS-1$//$NON-NLS-2$
        if (isAuthenticated())
        {
            logger.info("Policy Name [" + policyName + "]."); //$NON-NLS-1$//$NON-NLS-2$
            logger.info("Username [" + username + "]."); //$NON-NLS-1$//$NON-NLS-2$
            logger.info("Password [" + password + "]."); //$NON-NLS-1$//$NON-NLS-2$
        }

        logger.info("Subscriber wait time [" + wait + "]."); //$NON-NLS-1$//$NON-NLS-2$
        logger.info("JMS Msg Type [" + jmsMsgType + "]."); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Return the Resource Bundle
     * @return resource bundle
     */
    public static ResourceBundle getBundle()
    {
        return bundle;
    }
    
    /**
     * Get the topic name
     * @return The topic name
     */
    public static String getTopicName()
    {
        return topicName;
    }

    /**
     * Get events file
     * @return events file
     */
    public static String getEventsFile()
    {
        return eventsFile;
    }

    /**
     * Get payload name
     * @return payload name
     */
    public static String getPayloadName()
    {
        return payloadName;
    }

    /**
     * Get payload encoding
     * @return payload encoding
     */
    public static String getPayloadEncoding()
    {
        return payloadEncoding;
    }

    /**
     * Get payload spec
     * @return payload spec
     */
    public static String getPayloadSpec()
    {
        return payloadSpec;
    }

    /**
     * Get payload srcSystem
     * @return payload srcSystem
     */
    public static String getPayloadSrcSystem()
    {
        return payloadSrcSystem;
    }

    /**
     * Get payload content
     * @return payload content
     */
    public static String getPayloadContent()
    {
        return payloadContent;
    }

    /**
     * Get JMS Msg Type
     * @return JMS Msg Type
     */
    public static String getJmsMsgType()
    {
        return jmsMsgType;
    }

    /**
     * Get payload publish number
     * @return payload publish number
     */
    public static int getPayloadPublishNumber()
    {
        return publishNumber;
    }
    /**
     * Get isQueue flag
     * @return boolean isQueue
     */
    public static boolean isQueue()
    {
        return isQueue;
    }
    
    /**
     * Get Client ID
     * @return String clientID
     */
    public static String getClientID()
    {
        return clientID;
    }
    
    /**
     * Get Subscription name
     * @return String subscriptionName
     */
    public static String getSubscriptionName()
    {
        return subscriptionName;
    }

    /**
     * Get isDurable flag
     * @return boolean isDurable
     */
    public static boolean isDurable()
    {
        return isDurable;
    }

    /**
     * Get username
     * @return String username
     */
    public static String getUsername()
    {
        return username;
    }

    /**
     * Get password
     * @return String password
     */
    public static String getPassword()
    {
        return password;
    }

    /**
     * Get policyName
     * @return String policyName
     */
    public static String getPolicyName()
    {
        return policyName;
    }

    /**
     * Get isAuthenticated flag
     * @return boolean isAuthenticated
     */
    public static boolean isAuthenticated()
    {
        return isAuthenticated;
    }

    /**
     * Get wait time for subscriber
     * @return int wait
     */
    public static int getWait()
    {
        return wait;
    }
}
