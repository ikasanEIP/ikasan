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
package org.ikasan.component.endpoint.jms.spring.consumer;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a consumer configuration for a Spring JMS consumer.
 * 
 * @author Ikasan Development Team
 */
public class SpringMessageConsumerConfiguration
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

    /** name of durable subscription */
    private String durableSubscriptionName;

    /** is a durable subscriber */
    private Boolean durable;

    /** session transacted */
    private Boolean sessionTransacted;

    /** do we automatically pull the payload content from the JMS message */
    private boolean autoContentConversion = false;

    /** use batching or not */
    private boolean batchMode;

    /** batching consumer configuration limit */
    private int batchSize;

    /** whether to split any batched JMS message list into individual message instances and invoke the flow for each instance */
    private boolean autoSplitBatch = true;

    /** the maximum number of concurrent consumers on this message listener container. */
    private int maxConcurrentConsumers = 1;

    /** the initial number of concurrent consumers on this message listener container. */
    private int concurrentConsumers = 1;

    /** the cache level of the underlying message listener container.
     *     CACHE_NONE = 0;
     *     CACHE_CONNECTION = 1;
     *     CACHE_SESSION = 2;
     *     CACHE_CONSUMER = 3;
     *     CACHE_AUTO = 4;
     **/
    private int cacheLevel = 1;

    public String getDestinationJndiName()
    {
        return destinationJndiName;
    }

    public Map<String, String> getConnectionFactoryJndiProperties()
    {
        return connectionFactoryJndiProperties;
    }

    public void setConnectionFactoryJndiProperties(Map<String, String> connectionFactoryJndiProperties)
    {
        this.connectionFactoryJndiProperties = connectionFactoryJndiProperties;
    }

    public void setDestinationJndiName(String destinationJndiName)
    {
        this.destinationJndiName = destinationJndiName;
    }

    public boolean getPubSubDomain()
    {
        return pubSubDomain;
    }

    public void setPubSubDomain(boolean pubSubDomain)
    {
        this.pubSubDomain = pubSubDomain;
    }

    public String getDurableSubscriptionName()
    {
        return durableSubscriptionName;
    }

    public void setDurableSubscriptionName(String durableSubscriptionName)
    {
        this.durableSubscriptionName = durableSubscriptionName;
    }

    public Boolean getDurable()
    {
        return durable;
    }

    public void setDurable(Boolean durable)
    {
        this.durable = durable;
    }

    public Boolean getSessionTransacted()
    {
        return sessionTransacted;
    }

    public void setSessionTransacted(Boolean sessionTransacted)
    {
        this.sessionTransacted = sessionTransacted;
    }

    public Map<String, String> getDestinationJndiProperties()
    {
        return destinationJndiProperties;
    }

    public void setDestinationJndiProperties(Map<String, String> destinationJndiProperties)
    {
        this.destinationJndiProperties = destinationJndiProperties;
    }

    public String getConnectionFactoryName()
    {
        return connectionFactoryName;
    }

    public void setConnectionFactoryName(String connectionFactoryName)
    {
        this.connectionFactoryName = connectionFactoryName;
    }

    public String getConnectionFactoryUsername()
    {
        return connectionFactoryUsername;
    }

    public void setConnectionFactoryUsername(String connectionFactoryUsername)
    {
        this.connectionFactoryUsername = connectionFactoryUsername;
    }

    public String getConnectionFactoryPassword()
    {
        return connectionFactoryPassword;
    }

    public void setConnectionFactoryPassword(String connectionFactoryPassword)
    {
        this.connectionFactoryPassword = connectionFactoryPassword;
    }

    public void setPubSubDomain(Boolean pubSubDomain)
    {
        this.pubSubDomain = pubSubDomain;
    }

    public boolean isAutoContentConversion()
    {
        return autoContentConversion;
    }

    public void setAutoContentConversion(boolean autoContentConversion)
    {
        this.autoContentConversion = autoContentConversion;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    public boolean isAutoSplitBatch()
    {
        return autoSplitBatch;
    }

    public void setAutoSplitBatch(boolean autoSplitBatch)
    {
        this.autoSplitBatch = autoSplitBatch;
    }

    public boolean isBatchMode()
    {
        return batchMode;
    }

    public void setBatchMode(boolean batchMode)
    {
        this.batchMode = batchMode;
    }

    public int getConcurrentConsumers()
    {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(int concurrentConsumers)
    {
        this.concurrentConsumers = concurrentConsumers;
    }

    public int getMaxConcurrentConsumers()
    {
        return maxConcurrentConsumers;
    }

    public void setMaxConcurrentConsumers(int maxConcurrentConsumers)
    {
        this.maxConcurrentConsumers = maxConcurrentConsumers;
    }

    public int getCacheLevel()
    {
        return cacheLevel;
    }

    public void setCacheLevel(int cacheLevel)
    {
        this.cacheLevel = cacheLevel;
    }

    @Override
    public String toString()
    {
        return "SpringMessageConsumerConfiguration{" +
                "destinationJndiProperties=" + destinationJndiProperties +
                ", destinationJndiName='" + destinationJndiName + '\'' +
                ", connectionFactoryJndiProperties=" + connectionFactoryJndiProperties +
                ", connectionFactoryName='" + connectionFactoryName + '\'' +
                ", connectionFactoryUsername='" + connectionFactoryUsername + '\'' +
                ", connectionFactoryPassword='" + connectionFactoryPassword + '\'' +
                ", pubSubDomain=" + pubSubDomain +
                ", durableSubscriptionName='" + durableSubscriptionName + '\'' +
                ", durable=" + durable +
                ", sessionTransacted=" + sessionTransacted +
                ", autoContentConversion=" + autoContentConversion +
                ", batchMode=" + batchMode +
                ", batchSize=" + batchSize +
                ", autoSplitBatch=" + autoSplitBatch +
                ", maxConcurrentConsumers=" + maxConcurrentConsumers +
                ", concurrentConsumers=" + concurrentConsumers +
                ", cacheLevel=" + cacheLevel +
                '}';
    }
}
