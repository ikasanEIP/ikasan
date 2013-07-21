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

/**
 * This class holds constants used throughout the monitor code base
 * 
 * @author Ikasan Development Team
 */
public class JMSConstants
{
    /* ******************** Specific Properties ************************ */
    /** A key for helping us get a context for JNDI lookups */
    public final static String JAVA_NAMING_FACTORY_INITIAL = "java.naming.factory.initial"; //$NON-NLS-1$
    /** Another key for helping us get a context for JNDI lookups */
    public final static String JAVA_NAMING_FACTORY_URL_PKGS = "java.naming.factory.url.pkgs"; //$NON-NLS-1$
    /** The key that holds the URL to the JNDI tree */
    public final static String JAVA_NAMING_PROVIDER_URL = "java.naming.provider.url"; //$NON-NLS-1$
    /** The key that holds the URL to the JNDI tree */
    public final static String APP_SERVER_CONNECTIONFACTORY_JNDI_KEY = "appserver.connectionfactory.jndi.key"; //$NON-NLS-1$
    /** The key that holds the server instance */
    public final static String SERVER_INSTANCE_NAME = "server.instance.name"; //$NON-NLS-1$
    /** The key that holds the server instance port */
    public final static String SERVER_INSTANCE_PORT = "server.instance.port"; //$NON-NLS-1$
    /** Properties argument */
    public final static String PROPERTIES_ARG = "properties"; //$NON-NLS-1$
    /** Server Instance Name argument */
    public final static String SERVER_INSTANCE_NAME_ARG = "serverinstancename"; //$NON-NLS-1$
    /** Port argument */
    public final static String PORT_ARG = "port"; //$NON-NLS-1$
    /** Server Instance Name argument */
    public final static String DESTINATION_NAME_ARG = "destinationname"; //$NON-NLS-1$
    /** Events file name argument */
    public final static String EVENTS_FILE_NAME_ARG = "eventsfile"; //$NON-NLS-1$
    /** Payload name argument */
    public final static String PAYLOAD_NAME_ARG = "payloadName"; //$NON-NLS-1$
    /** Payload encoding argument */
    public final static String PAYLOAD_ENCODING_ARG = "payloadEncoding"; //$NON-NLS-1$
    /** Payload spec argument */
    public final static String PAYLOAD_SPEC_ARG = "payloadSpec"; //$NON-NLS-1$
    /** Payload srcSystem argument */
    public final static String PAYLOAD_SRC_SYSTEM_ARG = "payloadSrcSystem"; //$NON-NLS-1$
    /** Payload content */
    public final static String PAYLOAD_CONTENT_ARG = "payloadContent"; //$NON-NLS-1$
    /** Number of times the payload should be published */
    public final static String PUBLISH_NUMBER_ARG = "publishNumber"; //$NON-NLS-1$
    /** Server Instance Name argument */
    public final static String DEFAULT_PROPERTIES_FILE = "jmsTool"; //$NON-NLS-1$
    /** Test message name */
    public final static String TEST_MESSAGE_NAME = "testMsg"; //$NON-NLS-1$
    /** JNDI Topic key prefix */
    public final static String TOPIC_KEY_PREFIX = "/topic/"; //$NON-NLS-1$
    /** JNDI Queue key prefix */
    public final static String QUEUE_ARG = "queue"; //$NON-NLS-1$
    /** JNDI Queue key prefix */
    public final static String QUEUE_KEY_PREFIX = "/queue/"; //$NON-NLS-1$
    /** Client Id argument */
    public final static String CLIENT_ID_ARG = "clientid"; //$NON-NLS-1$
    /** Subscription name argument */
    public final static String SUBSCRIPTION_NAME_ARG = "subscriptionname";//$NON-NLS-1$
    /** Whether subscriber to be created is durable or not */
    public final static String DURABLE_ARG = "durable"; //$NON-NLS-1$
    /** username for secured JMS */
    public final static String USERNAME_ARG = "username"; //$NON-NLS-1$
    /** password for secured JMS */
    public final static String PASSWORD_ARG = "password"; //$NON-NLS-1$
    /** authentication policy name */
    public final static String POLICY_NAME_ARG = "policyName"; //$NON-NLS-1$
    /** authenticate flag */
    public final static String AUTHENTICATE_ARG = "auth"; //$NON-NLS-1$
    /** subscriber wait time */
    public final static String WAIT_TIME_ARG = "wait"; //$NON-NLS-1$
    /** subscriber default no wait time */
    public final static int NO_WAIT = -1; //$NON-NLS-1$
    /** subscriber default indefinite wait time */
    public final static int WAIT_FOREVER = 0; //$NON-NLS-1$
    /** JMS text msg type */
    public final static String JMS_TEXT_MESSAGE_TYPE = "textMessage"; //$NON-NLS-1$
    /** JMS map msg type */
    public final static String JMS_MAP_MESSAGE_TYPE = "mapMessage"; //$NON-NLS-1$
}
