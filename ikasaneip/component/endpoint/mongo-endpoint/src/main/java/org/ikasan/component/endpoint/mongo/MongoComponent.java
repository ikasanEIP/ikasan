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
package org.ikasan.component.endpoint.mongo;


import com.mongodb.*;
import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

import java.util.*;

/**
 * Abstract Mongo component which can be managed and configured.
 * @author Ikasan Development Team
 */
public abstract class MongoComponent implements ManagedResource, ConfiguredResource<MongoClientConfiguration>
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(MongoComponent.class);

    /** configured resource id */
    private String configuredResourceId;

    /** configuration */
    protected MongoClientConfiguration configuration;

    /** is this a critical resource for startup of the flow */
    private boolean isCriticalOnStartup;

    /** instantiated mongoclient */
    protected MongoClient mongoClient;

    /** handle to all referenced collections */
    protected Map<String,DBCollection> collections;

    /** handle to mongoDB instance */
    protected DB mongoDatabase;

    @Override
    public void startManagedResource()
    {
        this.configuration.validate();

        List<ServerAddress> addresses = configuration.getServerAddresses();
        if(addresses.size() == 0)
        {
            throw new RuntimeException("No Mongo server addresses specified!");
        }

        MongoClientOptions mongoClientOptions = buildMongoClientOptions(configuration);

        if(configuration.isAuthenticated())
        {
            MongoCredential mongoCredential =
                    MongoCredential.createMongoCRCredential(
                            configuration.getUsername(),
                            configuration.getDatabaseName(),
                            (configuration.getPassword() != null) ? configuration.getPassword().toCharArray() : null );

            mongoClient = getClient(addresses, mongoCredential, mongoClientOptions);
        }
        else
        {
            mongoClient = getClient(addresses, mongoClientOptions);
        }

        mongoDatabase = mongoClient.getDB(configuration.getDatabaseName());

        collections = new HashMap<String,DBCollection>();
        for(Map.Entry<String,String> entry : configuration.getCollectionNames().entrySet())
        {
            DBCollection dbCollection = mongoDatabase.getCollection(entry.getValue());
            if(dbCollection == null)
            {
                throw new RuntimeException("DBCollection[" + entry.getValue()
                        + "] not found in database[" + configuration.getDatabaseName() + "]");
            }

            collections.put(entry.getKey(), dbCollection);
        }
    }

    /**
     * Factory method to support testing
     * @param addresses
     * @param mongoCredential
     * @param mongoClientOptions
     * @return
     */
    protected MongoClient getClient(List<ServerAddress> addresses, MongoCredential mongoCredential, MongoClientOptions mongoClientOptions)
    {
        return new com.mongodb.MongoClient(addresses, Arrays.asList(mongoCredential), mongoClientOptions);
    }

    /**
     * Factory method to support testing
     * @param addresses
     * @param mongoClientOptions
     * @return
     */
    protected MongoClient getClient(List<ServerAddress> addresses, MongoClientOptions mongoClientOptions)
    {
        return new com.mongodb.MongoClient(addresses, mongoClientOptions);
    }

    /**
     * Mongo option builder method
     * @param configuration
     * @return
     */
    private MongoClientOptions buildMongoClientOptions(MongoClientConfiguration configuration)
    {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        if(configuration.getAcceptableLatencyDiff() != null)
        {
            builder.acceptableLatencyDifference(configuration.getAcceptableLatencyDiff().intValue());
        }

        if(configuration.getAlwaysUseMBeans() != null)
        {
            builder.alwaysUseMBeans(configuration.getAlwaysUseMBeans());
        }

        if(configuration.getConnectionsPerHost() != null)
        {
            builder.connectionsPerHost(configuration.getConnectionsPerHost());
        }

        if(configuration.getConnectionTimeout() != null)
        {
            builder.connectTimeout(configuration.getConnectionTimeout());
        }

        if(configuration.getCursorFinalizerEnabled() != null)
        {
            builder.cursorFinalizerEnabled(configuration.getCursorFinalizerEnabled());
        }

        if(configuration.getDescription() != null)
        {
            builder.description(configuration.getDescription());
        }

        if(configuration.getHeartbeatConnectRetryFrequency() != null)
        {
            builder.heartbeatConnectRetryFrequency(configuration.getHeartbeatConnectRetryFrequency());
        }

        if(configuration.getHeartbeatConnectTimeout() != null)
        {
            builder.heartbeatConnectTimeout(configuration.getHeartbeatConnectTimeout());
        }

        if(configuration.getHeartbeatFrequency() != null)
        {
            builder.heartbeatFrequency(configuration.getHeartbeatFrequency());
        }

        if(configuration.getHeartbeatSocketTimeout() != null)
        {
            builder.heartbeatSocketTimeout(configuration.getHeartbeatSocketTimeout());
        }

        if(configuration.getLegacyDefaults() != null && configuration.getLegacyDefaults())
        {
            builder.legacyDefaults();
        }

        if(configuration.getHeartbeatThreadCount() != null)
        {
            builder.heartbeatThreadCount(configuration.getHeartbeatThreadCount());
        }

        if(configuration.getMaxConnectionIdleTime() != null)
        {
            builder.maxConnectionIdleTime(configuration.getMaxConnectionIdleTime());
        }

        if(configuration.getMaxConnectionLifeTime() != null)
        {
            builder.maxConnectionLifeTime(configuration.getMaxConnectionLifeTime());
        }

        if(configuration.getMaxWaitTime() != null)
        {
            builder.maxWaitTime(configuration.getMaxWaitTime());
        }

        if(configuration.getSocketTimeout() != null)
        {
            builder.socketTimeout(configuration.getSocketTimeout());
        }

        if(configuration.getMinConnectionsPerHost() != null)
        {
            builder.minConnectionsPerHost(configuration.getMinConnectionsPerHost());
        }

        if(configuration.getRequiredReplicaSetName() != null)
        {
            builder.requiredReplicaSetName(configuration.getRequiredReplicaSetName());
        }

        if(configuration.getSocketKeepAlive() != null)
        {
            builder.socketKeepAlive(configuration.getSocketKeepAlive());
        }

        if(configuration.getThreadsAllowedToBlockForConnectionMultiplier() != null)
        {
            builder.threadsAllowedToBlockForConnectionMultiplier(configuration.getThreadsAllowedToBlockForConnectionMultiplier());
        }

        if(configuration.getReadPreference() != null)
        {
            builder.readPreference(configuration.getReadPreference());
        }

        if(configuration.getWriteConcern() != null)
        {
            builder.writeConcern(configuration.getWriteConcern());
        }

        return builder.build();
    }

    @Override
    public void stopManagedResource()
    {
        if(this.mongoClient != null)
        {
            this.mongoClient.close();
            this.mongoClient = null;
        }
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        // do we need this ?
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return this.isCriticalOnStartup;
    }

    @Override
    public void setCriticalOnStartup(boolean isCriticalOnStartup)
    {
        this.isCriticalOnStartup = isCriticalOnStartup;
    }

    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public MongoClientConfiguration getConfiguration()
    {
        return this.configuration;
    }

    @Override
    public void setConfiguration(MongoClientConfiguration configuration)
    {
        this.configuration = configuration;
    }
}
