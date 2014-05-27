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
package org.ikasan.component.endpoint.jms.producer;

import javax.jms.Session;
import java.util.Map;

/**
 * Implementation of a producer configuration for a generic JMS producer.
 *
 * @author Ikasan Development Team
 */
public class GenericJmsProducerConfiguration
{
    /** optional JNDI provider URL */
    private String providerURL;

    /** optional JNDI intial context factory */
    private String initialContextFactory;

    /** optional JNDI URL packages prefix */
    private String urlPackagePrefixes;

    /** need connectionFactory name if not provided on the constructor */
    private String connectionFactoryName;

    /** need destination name if destination/destinationResolver not provided in constructor */
    private String destinationName;

    /** optional boolean no local - can messages be delivered by this consumers connection */
    private boolean remoteJNDILookup = false;

    /** username credential where authentication is required */
    private String username;

    /** password credential where authentication is required */
    private String password;

    /** is this session transacted */
    private boolean transacted = false;

    /** type of session message acknowledgement */
    private int acknowledgement = Session.AUTO_ACKNOWLEDGE;

    public String getProviderURL() {
        return providerURL;
    }

    public void setProviderURL(String providerURL) {
        this.providerURL = providerURL;
    }

    public String getInitialContextFactory() {
        return initialContextFactory;
    }

    public void setInitialContextFactory(String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    public String getUrlPackagePrefixes() {
        return urlPackagePrefixes;
    }

    public void setUrlPackagePrefixes(String urlPackagePrefixes) {
        this.urlPackagePrefixes = urlPackagePrefixes;
    }

    public String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public boolean isRemoteJNDILookup() {
        return remoteJNDILookup;
    }

    public void setRemoteJNDILookup(boolean remoteJNDILookup) {
        this.remoteJNDILookup = remoteJNDILookup;
    }

    /** message properties */
    private Map<String,String> properties;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public boolean isTransacted()
    {
        return transacted;
    }

    public void setTransacted(boolean transacted)
    {
        this.transacted = transacted;
    }

    public int getAcknowledgement()
    {
        return acknowledgement;
    }

    public void setAcknowledgement(int acknowledgement)
    {
        this.acknowledgement = acknowledgement;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }
    
}
