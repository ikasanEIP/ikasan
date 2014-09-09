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
package org.ikasan.component.endpoint.jms.spring.producer;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a producer configuration for a Spring JMS producer.
 * 
 * @author Ikasan Development Team
 */
public class SpringMessageProducerConfiguration
{
    /** destination JNDI properties */
    private Map<String,String> destinationJndiProperties = new HashMap<String,String>();

    /** destination jndi name */
    private String destinationJndiName;

    /** connectionFactory JNDI properties */
    private Map<String,String> connectionFactoryJndiProperties = new HashMap<String,String>();

    /** connection factory JNDI name */
    private String connectionFactoryName;

    /** principal for authenticated connection factory */
    private String connectionFactoryUsername;

    /** credential for authenticated connection factory */
    private String connectionFactoryPassword;

    /** false if queue; true if topic - this is how Spring categorises */
    private Boolean pubSubDomain = Boolean.FALSE;

    /** delivery persistent */
    private Boolean deliveryPersistent;

    /** delivery mode - default is persistent */
    private Integer deliveryMode;

    /** session transacted */
    private Boolean sessionTransacted;

    private Boolean explicitQosEnabled;

    private Boolean messageIdEnabled;

    private Boolean messageTimestampEnabled;

    private Integer priority;

    private Boolean pubSubNoLocal;

    private Long receiveTimeout;

    private Integer sessionAcknowledgeMode;

    private String sessionAcknowledgeModeName;

    private Long timeToLive;

    public Map<String, String> getDestinationJndiProperties() {
        return destinationJndiProperties;
    }

    public void setDestinationJndiProperties(Map<String, String> destinationJndiProperties) {
        this.destinationJndiProperties = destinationJndiProperties;
    }

    public String getDestinationJndiName() {
        return destinationJndiName;
    }

    public void setDestinationJndiName(String destinationJndiName) {
        this.destinationJndiName = destinationJndiName;
    }

    public Map<String, String> getConnectionFactoryJndiProperties() {
        return connectionFactoryJndiProperties;
    }

    public void setConnectionFactoryJndiProperties(Map<String, String> connectionFactoryJndiProperties) {
        this.connectionFactoryJndiProperties = connectionFactoryJndiProperties;
    }

    public String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }

    public String getConnectionFactoryUsername() {
        return connectionFactoryUsername;
    }

    public void setConnectionFactoryUsername(String connectionFactoryUsername) {
        this.connectionFactoryUsername = connectionFactoryUsername;
    }

    public String getConnectionFactoryPassword() {
        return connectionFactoryPassword;
    }

    public void setConnectionFactoryPassword(String connectionFactoryPassword) {
        this.connectionFactoryPassword = connectionFactoryPassword;
    }

    public Boolean getPubSubDomain() {
        return pubSubDomain;
    }

    public void setPubSubDomain(Boolean pubSubDomain) {
        this.pubSubDomain = pubSubDomain;
    }

    public Boolean getDeliveryPersistent() {
        return deliveryPersistent;
    }

    public void setDeliveryPersistent(Boolean deliveryPersistent) {
        this.deliveryPersistent = deliveryPersistent;
    }

    public Integer getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(Integer deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public Boolean getSessionTransacted() {
        return sessionTransacted;
    }

    public void setSessionTransacted(Boolean sessionTransacted) {
        this.sessionTransacted = sessionTransacted;
    }

    public Boolean getExplicitQosEnabled() {
        return explicitQosEnabled;
    }

    public void setExplicitQosEnabled(Boolean explicitQosEnabled) {
        this.explicitQosEnabled = explicitQosEnabled;
    }

    public Boolean getMessageIdEnabled() {
        return messageIdEnabled;
    }

    public void setMessageIdEnabled(Boolean messageIdEnabled) {
        this.messageIdEnabled = messageIdEnabled;
    }

    public Boolean getMessageTimestampEnabled() {
        return messageTimestampEnabled;
    }

    public void setMessageTimestampEnabled(Boolean messageTimestampEnabled) {
        this.messageTimestampEnabled = messageTimestampEnabled;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getPubSubNoLocal() {
        return pubSubNoLocal;
    }

    public void setPubSubNoLocal(Boolean pubSubNoLocal) {
        this.pubSubNoLocal = pubSubNoLocal;
    }

    public Long getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(Long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public Integer getSessionAcknowledgeMode() {
        return sessionAcknowledgeMode;
    }

    public void setSessionAcknowledgeMode(Integer sessionAcknowledgeMode) {
        this.sessionAcknowledgeMode = sessionAcknowledgeMode;
    }

    public String getSessionAcknowledgeModeName() {
        return sessionAcknowledgeModeName;
    }

    public void setSessionAcknowledgeModeName(String sessionAcknowledgeModeName) {
        this.sessionAcknowledgeModeName = sessionAcknowledgeModeName;
    }

    public Long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public String toString() {
        return "SpringMessageProducerConfiguration{" +
                "destinationJndiProperties=" + destinationJndiProperties +
                ", destinationJndiName='" + destinationJndiName + '\'' +
                ", connectionFactoryJndiProperties=" + connectionFactoryJndiProperties +
                ", connectionFactoryName='" + connectionFactoryName + '\'' +
                ", connectionFactoryUsername='" + connectionFactoryUsername + '\'' +
                ", connectionFactoryPassword='" + connectionFactoryPassword + '\'' +
                ", pubSubDomain=" + pubSubDomain +
                ", deliveryPersistent=" + deliveryPersistent +
                ", deliveryMode=" + deliveryMode +
                ", sessionTransacted=" + sessionTransacted +
                ", explicitQosEnabled=" + explicitQosEnabled +
                ", messageIdEnabled=" + messageIdEnabled +
                ", messageTimestampEnabled=" + messageTimestampEnabled +
                ", priority=" + priority +
                ", pubSubNoLocal=" + pubSubNoLocal +
                ", receiveTimeout=" + receiveTimeout +
                ", sessionAcknowledgeMode=" + sessionAcknowledgeMode +
                ", sessionAcknowledgeModeName='" + sessionAcknowledgeModeName + '\'' +
                ", timeToLive=" + timeToLive +
                '}';
    }
}
